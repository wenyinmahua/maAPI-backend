package com.mahua.maapigateway;


import com.mahua.maapicommon.model.entity.InterfaceInfo;
import com.mahua.maapicommon.model.entity.User;
import com.mahua.maapicommon.service.InnerInterfaceService;
import com.mahua.maapicommon.service.InnerUserService;
import com.mahua.maapicommon.service.UserInterfaceInfoService;
import com.mahua.mahuaclientsdk.utils.EncryptUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.reactivestreams.Publisher;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 全局过滤器
 */
@Slf4j
@Component
public class CustomGlobalFilter implements GlobalFilter, Ordered {

	static Long FIVE_MINUTES = 60 * 5l;

	@DubboReference
	private InnerUserService innerUserService;

	@DubboReference
	private InnerInterfaceService innerInterfaceService;

	@DubboReference
	private UserInterfaceInfoService userInterfaceInfoService;

	private static final List<String> IP_WHITE_LIST = Arrays.asList("127.0.0.1","139.199.168.219");
	@Override
	public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
		// 1.用户发送请求到 API 网关
		// 2.请求日志
		ServerHttpRequest request = exchange.getRequest();
		String path = request.getPath().toString();
		String method = request.getMethod().toString();
		log.info("请求方式:"+method);
		log.info("请求参数:"+ request.getQueryParams());
		log.info("请求地址:"+path);
		log.info("请求源地址" + request.getRemoteAddress().getHostString());
		// 3.黑白名单
		ServerHttpResponse response = exchange.getResponse();
		if (!IP_WHITE_LIST.contains(request.getLocalAddress().getHostString())){
			return handleNoAuth(response);
		}
		// 4.用户鉴权
		HttpHeaders headers = request.getHeaders();
		String accessKey = headers.getFirst("accessKey");
		String nonce = headers.getFirst("nonce");
		String timestamp = headers.getFirst("timestamp");
		String sign = headers.getFirst("sign");
		String body = headers.getFirst("body");

		if(Long.valueOf(nonce) > 10000){
			return handleNoAuth(response);
		}
		// 时间和当前时间不能超过5分钟

		Long currentTimestamp = System.currentTimeMillis() /1000;
		if(currentTimestamp - Long.valueOf(timestamp) >= FIVE_MINUTES){
			throw new RuntimeException("无权限");
		}
		// 5. 实际情况是从数据库中查询是否已分配给用户
		User invokeUser = null;
		try{
			invokeUser = innerUserService.getInvokeUser(accessKey);
		}catch (Exception e){
			log.error("getInvokeUser error", e);
		}
		if (invokeUser == null){
			return handleInvokeError(response);
		}
		if (!accessKey.equals(invokeUser.getAccessKey())){
			return handleNoAuth(response);
		}

		// 从数据库中查出使用公钥，进行验签。
		boolean verifySignResult = EncryptUtils.verifySign(accessKey, sign, body);
		if(!verifySignResult){
			return handleNoAuth(response);
		}
		// 5.从数据库中查询，请求的模拟接口是否存在，以及请求参数是否匹配
		InterfaceInfo interfaceInfo = null;

		try{
			interfaceInfo = innerInterfaceService.getInterfaceInfo(path, method);
		}catch (Exception e){
			log.error("getInvokeUser error", e);
		}
		Long userId = invokeUser.getId();
		Long interfaceId = interfaceInfo.getId();
		boolean canCall = userInterfaceInfoService.validUserCallNumber(interfaceId, userId);
		if (!canCall){
			return handlerNoCallNumber(response);
		}
		// 6.请求转发，调用模拟接口 + 响应日志
		log.info("响应："+response.getStatusCode());
		return handleResponse(exchange,chain,interfaceInfo.getId(),invokeUser.getId());
}


	/**
	 *
	 * @param exchange
	 * @param chain
	 * @return
	 */
	public Mono<Void> handleResponse(ServerWebExchange exchange, GatewayFilterChain chain,long interfaceInfoId,long userId) {
		try {
			ServerHttpResponse originalResponse = exchange.getResponse();
			// 缓存数据的工厂
			DataBufferFactory bufferFactory = originalResponse.bufferFactory();
			// 拿到响应码
			HttpStatus statusCode = originalResponse.getStatusCode();
			if (statusCode == HttpStatus.OK) {
				// 装饰，增强能力
				ServerHttpResponseDecorator decoratedResponse = new ServerHttpResponseDecorator(originalResponse) {
					// 等调用完转发的接口后才会执行
					@Override
					public Mono<Void> writeWith(Publisher<? extends DataBuffer> body) {
						log.info("body instanceof Flux: {}", (body instanceof Flux));
						if (body instanceof Flux) {
							Flux<? extends DataBuffer> fluxBody = Flux.from(body);
							// 往返回值里写数据
							// 拼接字符串
							return super.writeWith(
									fluxBody.map(dataBuffer -> {
										// 7. 调用成功，接口调用次数 + 1 invokeCount
										try {
											userInterfaceInfoService.invokeCount(interfaceInfoId, userId);
										} catch (Exception e) {
											log.error("invokeCount error", e);
										}
										byte[] content = new byte[dataBuffer.readableByteCount()];
										dataBuffer.read(content);
										DataBufferUtils.release(dataBuffer);//释放掉内存
										// 构建日志
										StringBuilder sb2 = new StringBuilder(200);
										List<Object> rspArgs = new ArrayList<>();
										rspArgs.add(originalResponse.getStatusCode());
										String data = new String(content, StandardCharsets.UTF_8); //data
										sb2.append(data);
										// 打印日志
										log.info("响应结果：" + data);
										return bufferFactory.wrap(content);
									}));
						} else {
							// 8. 调用失败，返回一个规范的错误码
							log.error("<--- {} 响应code异常", getStatusCode());
						}
						return super.writeWith(body);
					}
				};
				// 设置 response 对象为装饰过的
				return chain.filter(exchange.mutate().response(decoratedResponse).build());
			}
			return chain.filter(exchange); // 降级处理返回数据
		} catch (Exception e) {
			log.error("网关处理响应异常" + e);
			return chain.filter(exchange);
		}
	}

	public Mono<Void> handleNoAuth(ServerHttpResponse response){
		response.setStatusCode(HttpStatus.FORBIDDEN);
		return response.setComplete();
	}

	public Mono<Void> handleInvokeError(ServerHttpResponse response){
		response.setStatusCode(HttpStatus.SERVICE_UNAVAILABLE);
		return response.setComplete();
	}

	public Mono<Void> handlerNoCallNumber(ServerHttpResponse response){
		response.setStatusCode(HttpStatus.FORBIDDEN);
		return response.setComplete();
	}

	@Override
	public int getOrder() {
		return -1;
	}
}

