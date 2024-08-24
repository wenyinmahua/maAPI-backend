# 项目介绍


API 开放接口：提供 API 接口供开发者调用的平台，基于 Spring Boot 后端的微服务项目。

市场上的相关项目：搏天 API（https://api.btstu.cn/ ）

管理员可以接入并发布接口、统计分析各接口调用情况；用户可以注册登录并开通接口调用权限、浏览接口、在线调试，还能使用 客户端 SDK 轻松在代码中调用接口。

项目模块：

1. 公共模块：提供项目中公共的部分，减少代码量，提高开发效率

   1. 定义了公共实体类
   2. 定义了公共的方法（查询接口信息、查询公钥是否分配给用户、接口调用次数 + 1）
   3. 公共模块作为子模块引入需要相关功能的模块中，
      - 公共的方法在公共模块中存在的形式是接口，在相关模块中可以通过接口 + Dubbo 实现远程调用，接口的具体实现在后端模块中。

2. 网关模块：

   1. 路由转发：发送到网关的请求再转发给真实的后端地址

   2. 流量染色：增加特殊的请求头，以表示该请求来自于网关，可以使某些接口不被非法调用，防止用户跳过网关直接访问接口。（这一部分在接口模块的开发中，从请求头中取出网关中加入的请求头，并判断请求头是否合法，如果合法，就放过，非法操作直接异常处理）

   3. 统一身份认证：通过 Dubbo RPC 提高的远程调用的查询数据库的方法，得到用户的数据，并校验用户的身份是否合法。ak、sk是否以及分配、签名是否正确。还要判断接口是否存在。

   4. 黑白名单，判断用户的主机地址是否在黑白名单中

   5. 防止回放：时间戳+随机数

   6. 处理公共业务：如果调用成功，那么调用次数 + 1，不用在各个调用请求的方法中判断并改变请求调用的次数。

3. 客户端 SDK 模块：

   1. 客户端：
      1. 客户端需要传入 AK 和 SK 才能创建
      2. 客户端通过 Hutool 工具将请求发给网关，在请求头中添加nonce和时间戳、公钥、签名、请求数据（参数）等
   2. 实体类：请求参数实体类
   3. 签名算法工具：m5对请求参数进行 API 签名
   4. 客户端配置：快速实现在配置文件中补充 accessKey 和 SecretKey
   5. 为啥需要客户端？因为可能不止这一个地方用，其他人也可能用

4. 接口模块：

   1. 控制层：里面含有接口的操作信息，还可以有服务层、数据访问层（创建属于自己的API，将数据存放在数据库中，实现自己的api 接口）
   2. 启动类：无特殊情况

5. 后端模块：

   1. 服务层：服务层有些服务类实现了公共模块的代码

   



# Dubbo

## Dubbo 的使用

Dubbo 提供了一种透明化的远程方法调用（RPC）方式，使得开发者能够像调用本地方法一样调用远程服务，极大地简化了分布式系统中服务间的调用复杂度。

Dubbo 帮助解决微服务组件之间的通信问题，提供了基于 HTTP、HTTP/2、TCP 等的多种高性能通信协议实现，并支持序列化协议扩展，在实现上解决网络连接管理、数据传输等基础问题。

由于 Dubbo 的使用需要注册中心，项目中选择了 Nacos 作为注册中心；



### 1. 项目的必备模块

项目模块最少需要三个模块

- 服务接口模块
- 服务提供者模块
- 服务消费者模块

> 项目的服务提供者和服务消费者是两个独立的模块，将服务接口模块定义成公共模块，在服务的提供者和服务消费者引入服务接口模块。

> 服务提供者和服务消费者的启动类上要添加 `@EnableDubbo` 注解

```java
@SpringBootApplication
@EnableDubbo
public class ConsumerApplication {

    public static void main(String[] args) {
        SpringApplication.run(ConsumerApplication.class, args);
    }
}
```



### 2. Dobbo + Nacos Maven 依赖

```xml
<dependency>
    <groupId>org.apache.dubbo</groupId>
    <artifactId>dubbo</artifactId>
    <version>3.0.9</version>
</dependency>
<dependency>
    <groupId>com.alibaba.nacos</groupId>
    <artifactId>nacos-client</artifactId>
    <version>2.1.0</version>
</dependency>
```

> Nacos 应用下载的版本号为 `2.1.1` ，高版本可能会出错
>
> - Nacos的运行建议至少在2C4G 60G的机器配置下运行，低于这个配置可能会导致 Nacos 启动报错。



### 3. 注册中心 Nacos 的使用

需要切换到 Nacos 的 bin 目录下

```shell
sh startup.sh -m standalone   # 启动命令(standalone代表着单机模式运行，非集群模式)
sh shutdown.sh # 关闭服务器
```



### 4. 配置 Dubbo 和 Nacos 注册中心

通过 Spring Boot 的方式配置 Dubbo 的一些基础信息。

在服务提供者和服务消费者两个，定义了 Dubbo 的应用名、Dubbo 协议信息、Dubbo 使用的注册中心地址，定义如下：

```yaml
dubbo:
  application:
    name: dubbo-springboot-provider  # Dubbo 的应用名
  protocol: 						 # Dubbo 的协议信息
    name: dubbo
    port: -1
  registry:							 # Dubbo 使用的注册中心地址
    id: nacos-registry
    address: nacos://Nacos 所在服务器的 ip 地址:8848
```

###  5. 定义服务接口

服务接口需要定义在服务接口模块中

服务接口是 Dubbo 中沟通消费端和服务端的桥梁。

后续服务端发布的服务，消费端订阅的服务都是围绕着 `服务接口` 展开的。

> - 服务接口模块作为子模块被引入到 服务提供者模块和服务消费者模块；
> - 服务接口模块中的服务接口在服务提供者中具体实现；
> - 服务消费者直接调用服务接口模块中定义的服务接口。



### 6. 定义服务端实现

> 这里定义的服务端一般指的是服务提供者。

定义了服务接口之后，可以在服务端这一侧定义对应的实现，这部分的实现相对于消费端来说是远端的实现，本地没有相关的信息。

- 需要在服务提供者实现的接口实现类上加上 `@DubboService` 注解

>@DubboService 是Dubbo框架中的一个注解，主要用于标记在Spring框架中的服务提供者（Provider）接口或者实现类。当一个类或方法被此注解标记时，Dubbo会将其识别为需要暴露给其他服务消费者（Consumer）调用的服务。



### 7. 配置消费端请求任务

在需要服务接口模块提供的服务接口时，需要注入相关的服务接口的对象，加上 `@DubboReference` ，从 Dubbo 获取了一个 RPC 订阅，订阅的服务可以像本地调用一样直接调用。

>@DubboReference 是 Dubbo 框架提供的另一个重要注解，用于 Spring 框架中服务消费者的客户端代理。当在消费者应用中使用此注解时，Dubbo 会自动创建一个代理对象，该对象背后封装了对远程服务的调用逻辑。这意味着开发者可以像调用本地方法一样调用远程服务。



### 8. 启动应用

先启动服务的提供者模块，然后启动服务的消费者模块。



### 总结

- 在某个模块使用 Dubbo ，引入相关依赖，配置相关配置，需要在启动类上添加 `@EnableDubbo` 注解；

- 在项目的服务消费者注入的 Dubbo 服务对象上增加 `@DubboReference` 注解；
- 在项目的服务提供者的服务接口实现类上增加 `@DubboService` 注解。



### 项目中这些地方用到了 Dubbo

#### 1.服务接口模块

在项目中，公共模块相当于服务接口模块，提供了以下三个接口：

- 查询接口信息
- 查询公钥是否分配给用户
- 接口调用次数 + 1

接口中定义了相关的方法。

```java
public interface InnerInterfaceService {

    /**
     * 从数据库中查询接口是否存在（请求路径、请求方法）
     * @param path
     * @param method
     * @return 接口信息，为空表示接口不存在
     */
    InterfaceInfo getInterfaceInfo(String path, String method);
}
```



#### 2.服务提供者模块

由于服务接口需要操作数据库，而后端模块已经实现了相关的操作，一因此这里就将后端作为消息的提供者。

```java
@DubboService  	// 这个是关键
@Service
public class InnerInterfaceServiceInfo implements InnerInterfaceService {
    @Resource
    InterfaceInfoMapper interfaceInfoMapper;
    @Override
    public InterfaceInfo getInterfaceInfo(String path, String method) {
       if (StringUtils.isAllEmpty(path,method)){
          throw new BusinessException(ErrorCode.PARAMS_ERROR,"请求参数不能为空");
       }
       QueryWrapper<InterfaceInfo> queryWrapper = new QueryWrapper<>();
       queryWrapper.eq("url",path);
       queryWrapper.eq("method",method);
       InterfaceInfo interfaceInfo = interfaceInfoMapper.selectOne(queryWrapper);
       if (interfaceInfo == null){
          throw new BusinessException(ErrorCode.PARAMS_ERROR,"请求参数错误");
       }
       return interfaceInfo;
    }
}
```



#### 3. 服务消费者

由于网关需要使用这些接口进行鉴权和执行公共业务等操作，这里的网关模块相当于服务的消费者。

以下是网关的过滤器中的相关内容。

```java
	@DubboReference
	private InnerUserService innerUserService;

	@DubboReference
	private InnerInterfaceService innerInterfaceService;

	@DubboReference
	private UserInterfaceInfoService userInterfaceInfoService;

	private static final List<String> IP_WHITE_LIST = Arrays.asList("127.0.0.1");
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
		Long FIVE_MINUTES = 60 * 5l;
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

		// 从数据库中查出 secretKey，经过签名算法，通过用户的身份和密钥生成签名。
		String serverSign = SignUtil.getSign(body,invokeUser.getSecretKey());
		if(!serverSign.equals(sign)){
			return handleNoAuth(response);
		}
		// 5.从数据库中查询，请求的模拟接口是否存在，以及请求参数是否匹配
		InterfaceInfo interfaceInfo = null;

		try{
			interfaceInfo = innerInterfaceService.getInterfaceInfo(path, method);
		}catch (Exception e){
			log.error("getInvokeUser error", e);
		}
		// 6.请求转发，调用模拟接口 + 响应日志
		log.info("响应："+response.getStatusCode());
		return handleResponse(exchange,chain,interfaceInfo.getId(),invokeUser.getId());
}
```



# 80万行古诗并发插入数据库

```java
@SpringBootTest
@Slf4j
public class PoemInsertTest {

    @Resource
    private PoemService poemService;

    private ExecutorService executorService = new ThreadPoolExecutor(60,100,100, TimeUnit.SECONDS,new ArrayBlockingQueue<>(800000));

    /**
     * 最简单的读
     * <p>
     * 1. 创建excel对应的实体对象 参照{@link Poem}
     * <p>
     * 3. 直接读即可
     */
    @Test
    public void simpleRead() {

//     // 写法2：
//     // 匿名内部类 不用额外写一个DemoDataListener
       String fileName = "C:\\Users\\50184\\Desktop\\poem.xlsx";
       // 这里 需要指定读用哪个class去读，然后读取第一个sheet 文件流会自动关闭
       StopWatch stopWatch = new StopWatch();
       stopWatch.start();
       EasyExcel.read(fileName, Poem.class, new ReadListener<Poem>() {
          /**
           * 单次缓存的数据量
           */
          public static final int BATCH_COUNT = 100;
          /**
           *临时存储
           */
          private List<Poem> cachedDataList = ListUtils.newArrayListWithExpectedSize(BATCH_COUNT);

          @Override
          public void invoke(Poem data, AnalysisContext context) {
             cachedDataList.add(data);
             if (cachedDataList.size() >= BATCH_COUNT) {
                saveData();
                // 存储完成清理 list
                cachedDataList = ListUtils.newArrayListWithExpectedSize(BATCH_COUNT);
             }
          }

          @Override
          public void doAfterAllAnalysed(AnalysisContext context) {
             saveData();
             stopWatch.stop();
             System.out.println(stopWatch.getTotalTimeMillis());
          }

          /**
           * 加上存储数据库
           */
          private void saveData() {
             for (Poem poem : cachedDataList){
                poemService.save(poem);
             }
          }
       }).sheet().doRead();

    }

	// 并行插入
    @Test
    public void doConcurrencyRead(){
       String fileName = "C:\\Users\\50184\\Desktop\\poem.xlsx";

       // 这里 需要指定读用哪个class去读，然后读取第一个sheet 文件流会自动关闭
       StopWatch stopWatch = new StopWatch();
       stopWatch.start();
       List<CompletableFuture<Void>> futureList = new ArrayList<>();
       try {
          EasyExcel.read(fileName, Poem.class, new ReadListener<Poem>() {
             public static final int BATCH_COUNT = 10;
             private List<Poem> cachedDataList = ListUtils.newArrayListWithExpectedSize(BATCH_COUNT);
             @Override
             public void invoke(Poem data, AnalysisContext context) {
                cachedDataList.add(data);
                if (cachedDataList.size() >= BATCH_COUNT) {
                   CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                      poemService.saveBatch(cachedDataList,BATCH_COUNT);
                      cachedDataList = ListUtils.newArrayListWithExpectedSize(BATCH_COUNT);
                   },executorService);
                   futureList.add(future);
                   // 存储完成清理 list
                }
             }
             @Override
             public void doAfterAllAnalysed(AnalysisContext context) {

             }

          }).sheet().doRead();
       } finally {
          CompletableFuture.allOf(futureList.toArray(new CompletableFuture[]{})).join();
          stopWatch.stop();
          System.out.println(stopWatch.getTotalTimeMillis());
       }

    }
}
```



简单读，每一次插入向数据库插入一行古诗，80 万行古诗插入需要 24 分 05 秒，使用多线程并行插入 + Mybatis-Plus 的 saveBatch，每次插入10行，时间减少为 1 分 24秒左右，超过 10 行也是 1 分 24 秒左右，但是第一次插入的前 n 条的id 顺序跨度很大，

> 解决方案：
>
> 当 id 顺序不会影响项目的问题时；
>
> 设置并发插入的条数设置为 1 条时，插入的数据 id 顺序仍然是乱的，时间花费也比较大，为 2 分03秒左右，但是这时 id 的顺序除了第一条比较乱，其他的都是正常递增的，只需要修改最后的 n 条数据的 id，即可保证 id 的从 1 开始的自增顺序。 





# 开发一个简单易用的SDK

理想情况，开发者只需要关心调用哪些接口、传递哪些参数，就跟调用自己写的代码一样简单

开发start的好处：开发者引入后，可以直接在application.yml中写配置，自动创建客户端

spring-boot-configuration-processor的作用是自动生成配置的代码提示

> 初始化一个新的简单易用的SDK需要以下步骤：
>
> 1. 初始化一个 Spring 项目，引入 Lombok和 spring-boot-configuration-processor 依赖
>
> 2. 去掉项目中的启动类
>
> 3. 创建客户端、pojo类、工具类等等
>
> 4. 创建客户端配置类
>
>    1. 注解Configuration、ComponentScan、ConfigurationProperties("有什么配置信息")、Data（Lombok）
>    2. 返回一个新的客户端对象
>
> 5. 在java/resource目录下创建`META-INF`文件夹，存放`spring.factroies`文件
>
>    ```tex
>    # SpringBoot starter 配置类所在位置
>    org.springframework.boot.autoconfigure.EnableAutoConfiguration=com.mahua.mahuaclientsdk.MahuaAPIClientConfig
>    ```
>
> 6. 将配置文件中的 mahuaapi.client 部分映射到一个配置类，然后通过 @Bean 注解创建一个 MaHuaAPIClient 实例，这个实例的创建依赖于配置文件中的 accessKey 和 secretKey.
>
> ```java
> @Configuration
> @Data
> @ComponentScan
> @ConfigurationProperties("mahuaapi.client")
> public class MahuaAPIClientConfig {
> 
> 	private String accessKey;
> 	private String secretKey;
> 
> 	@Bean
> 	public MaHuaAPIClient maHuaAPIClient(){
> 		return new MaHuaAPIClient(accessKey,secretKey);	}
> }
> ```
>
> 



## 如何使用?

因为 SpringBoot 的自动配置原理，只需要配置相关的信息。



在引入 SDK 模块相关模块的的 `resources` 资源文件夹下建立 `application.yml` 配置文件，在这个配置文件中填写如下信息：

```yaml
mahuaapi:
  client:
    access-key: mahua
    secret-key: 123456
```

这里的 AK 和 SK 都是系统生成的，否则在验签的时候会出现错误，因为签名是使用 Ed25519 非对称签名算法，公钥和私钥都是需要匹配的。

# 签名算法

### API签名

API 签名算法是对请求数据进行签名。具体来说，签名过程是为了确保API请求的完整性和来源的可信性，防止数据在传输过程中被篡改，同时验证请求发起者拥有合法的权限。以下是签名算法对API请求数据进行签名的详细说明：

1. 请求参数：
   签名算法通常针对 API 请求中包含的所有关键参数进行签名。这些参数可能包括但不限于：访问令牌（Access Token）、请求方法（GET、POST等）、请求路径（URL）、查询参数、请求体（JSON、XML等格式的数据）、时间戳、nonce（一次性随机值，用于防止重放攻击）等。
   参数通常按照一定的规则（如字母序、参数重要性）进行排序，确保双方（客户端和服务端）对签名数据的处理方式一致。

2. 签名密钥：
   签名过程需要用到一个或多个密钥。这些密钥可能是对称密钥（如HMAC-SHA256签名中使用的密钥）或非对称密钥对（如RSA、ECDSA签名中使用的私钥和公钥）。密钥通常由服务提供商分配给API使用者，或者由使用者根据服务提供商的规范自行生成，并在安全通道上传递给服务提供商。

3. 签名生成：
   客户端（API使用者）将排序后的请求参数拼接成一个字符串或序列化为二进制数据，然后使用指定的签名算法（如HMAC、RSA、ECDSA等）和对应的密钥对这个数据进行签名运算，生成一个固定长度的签名值（通常为一串十六进制或Base64编码的字符串）。

4. 签名传递：
   客户端将生成的签名值附加到API请求中，通常作为请求头的一个字段（如Authorization、X-Signature、Signature等）发送给服务端。同时，原始请求参数也随请求一同发送。

5. 签名验证：
   服务端收到请求后，首先提取请求头中的签名值和请求中的所有相关参数。接着，按照与客户端相同的规则重新计算这些参数的签名。如果重新计算得到的签名与接收到的签名值匹配，说明请求数据在传输过程中未被篡改，且请求来自持有正确密钥的合法客户端。

   综上所述，API签名算法是对API请求数据（包括请求参数）进行签名，目的是确保请求的完整性和来源的可信性。签名过程涉及到请求参数的规范化、密钥的使用、签名值的生成与传递以及服务端的签名验证。通过签名，服务端可以有效地鉴别请求的真伪，保障API接口的安全性。





### Java 使用 Ed25519 算法进行签名和验签

1.引入依赖

使用EdDSA（Edwards-curve Digital Signature Algorithm）进行签名操作，需要借助于BouncyCastle库，因为它提供了对 EdDSA 算法的支持。添加了BouncyCastle作为JCE（Java Cryptography Extension）的提供者。

```xml
<dependency>
    <groupId>org.bouncycastle</groupId>
    <artifactId>bcprov-jdk15on</artifactId>
    <version>最新版本号</version>
</dependency>
```

2.编写EdDSA签名、Ed25519

生成 公钥 和 私钥 并保存在数据库中

1. 添加BouncyCastle作为JCE提供者
2. 生成密钥对
3. 获取私钥和公钥
4. 将 公钥 和 私钥 转换为 byte 数组，经过 Base64 编码之后转换为字符串，方便存储在数据库中

签名和验签：

1. 从数据库中取出公钥和私钥
2. 使用私钥对**请求数据**进行签名，之后转发给其他请求
3. 在另外的服务器中使用公钥验签。



### 1. 定义密钥对实体类

```java
@Data
@ToString
public class KeyPair {
    /**
     * 公钥
     */
    private String publicKey;
    /**
     * 私钥
     */
    private String privateKey;
}
```



### 2. 编写工具类

```java
/**
 * Ed25519 签名工具类
 */
public class EncryptUtil {

	/**
	 * 生成密钥对，包含公钥和私钥
	 * @return 返回密钥对
	 */
	public static KeyPair getKeys(){
		KeyPair keyPair = new KeyPair();
		Security.addProvider(new BouncyCastleProvider());
		AsymmetricCipherKeyPair generateKeyPair = generateEd25519KeyPair();
		Ed25519PrivateKeyParameters privateKeyParams = (Ed25519PrivateKeyParameters) generateKeyPair.getPrivate();
		Ed25519PublicKeyParameters publicKeyParams = (Ed25519PublicKeyParameters) generateKeyPair.getPublic();
		byte[] privateKeyBytes = privateKeyParams.getEncoded();
		byte[] publicKeyBytes = publicKeyParams.getEncoded();
		// Base64 编码将 byte 数组转换成字符串，方便存储在数据库中
		keyPair.setPrivateKey(Base64.getEncoder().encodeToString(privateKeyBytes));
		keyPair.setPublicKey(Base64.getEncoder().encodeToString(publicKeyBytes));
		return keyPair;
	}

	/**
	 * 对请求数据使用私钥钥进行签名
	 * @param param 请求数据
	 * @param privateKey 密钥
	 * @return
	 */
	public static String getSign(String param, String privateKey) {
		byte[] retrievedPrivateKeyBytes = Base64.getDecoder().decode(privateKey);
		Ed25519PrivateKeyParameters retrievedPrivateKeyParams = new Ed25519PrivateKeyParameters(retrievedPrivateKeyBytes, 0);
		// 要签名的数据(请求参数)
		byte[] message = param.getBytes();
		Ed25519Signer signer = new Ed25519Signer();
		signer.init(true, retrievedPrivateKeyParams);
		signer.update(message, 0, message.length);
		byte[] bytes = signer.generateSignature();
		String sign = new String(bytes);
		return sign;
	}

	/**
	 * 对请求参数进行验签
	 * @param publicKey 公钥
	 * @param sign 需要验证的签名
	 * @param param 需要验证的请求参数
	 * @return
	 */
	public static boolean verifySign(String publicKey, String sign, String param){
		byte[] retrievedPublicKeyBytes = Base64.getDecoder().decode(publicKey);
		// 反序列化为Ed25519PublicKeyParameters对象
		Ed25519PublicKeyParameters retrievedPublicKeyParams = new Ed25519PublicKeyParameters(retrievedPublicKeyBytes, 0);

		// 使用私钥对数据进行签名
		byte[] signature = Base64.getDecoder().decode(sign);
		Ed25519Signer verifier = new Ed25519Signer();
		verifier.init(false, retrievedPublicKeyParams);
		// 要签名的数据(请求参数)
		byte[] message = param.getBytes();
		verifier.update(message, 0, message.length);
		return verifier.verifySignature(signature);
	}


	private static AsymmetricCipherKeyPair generateEd25519KeyPair() {
		Ed25519KeyPairGenerator generator = new Ed25519KeyPairGenerator();
		generator.init(new Ed25519KeyGenerationParameters(null));
		return generator.generateKeyPair();
	}
}
```





# 麻花 API 

## 业务流程：

用户请求 backend，backend 请求 sdk，验证用户的身份，转发到网关，网关再请求实际地址

公钥请求头转发，密钥并不是通过请求头直接发送的，而是通过用户的某个固定的属性，结果非对称加密（Ed25519）之后转换为签名发送的，调用接口时通过签名算法进行验签，判断用户的签名是否正确。

请求数据在客户端进行签名。



## 1.初期准备



### 1. 项目介绍

做一个提供 API 接口调用的平台，用户可以注册登录，开通接口调用权限，用户可以使用接口，并且每次调用会进行统计。管理员可以发布接口，下线接口，介入接口，以及可视化接口的调用情况、数据。

### 2. 业务流程

管理员->发布接口、下线接口、接口设置

用户->浏览接口、开通接口、调用接口->API网关（统计次数、计费、接口保护、鉴权认证、授权（用户登录）、日志、跨域）

第三方调用的SDK（工具类，别人给你更方便使用接口的源码包）

### 3.技术选型

后端

Java Spring Boot

Spring Boot start（SDK开发）

Dubbo （RPC 远程服务调用）

Spring Cloud GateWay（网关、统一鉴权、流量染色、日志实现）



### 4. 需求分析

1. 管理员可以对接口信息进行增删改查
2. 用户可以查看接口信息、访问接口



### 5. 项目脚手架

#### 数据库表设计

##### 接口信息表

id

name 接口名称

description 描述

url 接口地址

type 请求类型

requestHeader 请求头

responseHeader 响应头

status 接口状态 0 - 关闭 1 - 开启

isDelete 

createTime

updateTime







# 项目计划

## 第一期——初始化和展示

项目介绍、设计、技术选型

基础项目的搭建

接口管理

用户查看接口



## 第二期——接口调用

1. 开发虚拟 API 接口
2. 开发调用这个接口的代码
3. 保证调用的安全性（API签名认证）
4. 客户端 SDK 开发
5. 管理员接口 **发布** 与调度
6. 接口文档展示、接口在线调度



### 调用接口

几种 HTTP 调用方式：

1. HTTPClient
2. ResultTemplate
3. 第三方库（OKHTTP、[Hutool](https://doc.hutool.cn/)）



### API 签名认证

本质：

1. 签发签名
2. 使用签名（校验签名）

为什么需要？

- 保证安全性，不能随便一个人调用

#### 怎么实现？

通过 http request header 头传递参数。

参数1：accessKey：调用的标识 userA，userB（复杂、无序、无规律）

参数2：secretKey：密钥（复杂、无序、无规律） 该参数不能放在请求头中

（类似于用户名和密码，区别：ak、sk是无状态的）

参数3：用户请求参数

> 自己写代码生成 ak 和 sk，千万不要把密钥直接在服务器之间传递，有可能被拦截被其他人获取，需要对密码进行加密

参数4：sign

加密方式：对称加密、非对称加密，md5加密

用户参数 + 密钥 => 签名生成算法 => 不可解密的值

服务端使用一样的参数和算法生成签名，只要和用户传的一致，就表示一致。

> 怎么防止重放？
>
> 参数5：加 nonce 随机数，只能用一次，服务器需要保存用过的随机数。
>
> 参数6：加timestamp时间戳，校验时间戳是否过期



API 签名认证是一个很灵活的设计，具体要哪些参数、参数名如何一定要根据场景来。（如userId、appId等等固定值）



### 开发一个简单易用的SDK

理想情况，开发者只需要关心调用哪些接口、传递哪些参数，就跟调用自己写的代码一样简单

开发start的好处：开发者引入后，可以直接在application.yml中写配置，自动创建客户端

spring-boot-configuration-processor的作用是自动生成配置的代码提示

> 初始化一个新的简单易用的SDK需要以下步骤：
>
> 1. 初始化一个 Spring 项目，引入 Lombok和 spring-boot-configuration-processor 依赖
>
> 2. 去掉项目中的启动类
>
> 3. 创建客户端、pojo类、工具类等等
>
> 4. 创建客户端配置类
>
>    1. 注解Configuration、ComponentScan、ConfigurationProperties("有什么配置信息")、Data（Lombok）
>    2. 返回一个新的客户端对象
>
> 5. 在java/resource目录下创建`META-INF`文件夹，存放`spring.factroies`文件
>
>    ```tex
>    # SpringBoot starter 配置类所在位置
>    org.springframework.boot.autoconfigure.EnableAutoConfiguration=com.mahua.mahuaclientsdk.MahuaAPIClientConfig
>    ```
>
> 
>
> ```java
> @Configuration
> @Data
> @ComponentScan
> @ConfigurationProperties("mahuaapi.client")
> public class MahuaAPIClientConfig {
> 
> 	private String accessKey;
> 	private String secretKey;
> 
> 	@Bean
> 	public MaHuaAPIClient maHuaAPIClient(){
> 		return new MaHuaAPIClient(accessKey,secretKey);	}
> }
> ```
>
> 



## 第三期——接口计费与保护

### 统计用户调用次数

1. 开发接口发布/下线的功能（管理员）
2. 统计用户调用接口的次数
3. 优化系统---API网关

### 开发接口发布/下线的功能（管理员）

后台接口：

发布接口：（仅管理员可操作）

1. 校验接口是否存在
2. 判断接口是否可以调用
3. 修改接口数据库字段的状态字段为1



下线接口：（仅管理员可操作）

1. 校验接口是否存在
2. 判断接口是否可以调用
3. 修改接口数据库字段的 isDelete 字段为0



用户需要走后端间接调用模拟接口，这样更安全、更规范。

流程：

1. 将用户输入的请求参数和要测试的接口 id 发给平台后端
2. （在调用前做一些校验）
3. 平台后端去调用模拟接口



## 第四期——管理、统计分析

提供可视化平台、用图标的方式创建所有接口的调用情况，便于调整业务。

1. 开发接口调用次数的统计
2. 优化整个系统的代码的架构（API网关）
   1. 网关是什么？
   2. 网关的作用？
   3. 网关的应用场景和实现？
   4. 结合业务去应用网关



### 接口调用次数统计

需求：

1. 用户每次调用接口成功，次数+1
2. 给用户分配或者用户主动申请接口调用次数



业务流程：

1. 用户调用接口
2. 修改数据库，调用次数 + 1；



设计库表：

哪个用户？哪个接口？

用户 => 接口（多对多）



用户接口管理表：

```sql
create table if not exists user_interface_info
(
    id             bigint auto_increment comment 'id'
        primary key,
    userId         bigint                             not null comment '调用者id',
    interfaceInfoId bigint                            not null comment '接口id',
    totalNum int default 0 not null comment '总调用次数',
    leftNum  int default 0 not null comment '剩余调用次数',
    status  int default 0 not null comment '0 - 正常 | 1 - 禁用',
    createTime     datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime     datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete       tinyint  default 0                 not null comment '是否删除(0-未删 1 - 已删)',
)
    comment '用户调用接口关系表';
```



### 网关

网关：可以理解成火车站的验票口，统一去验票。统一去进行一些操作、处理一些问题。

#### 网关的作用

1. 路由（转发请求到模拟接口项目）
2. 统一鉴权（accessKey、secretKey）
3. 跨域
4. 流量染色（加上网关请求头，记录请求是否从网关来的）
5. 访问控制
6. 统一业务处理（缓存、每次请求接口后，请求调用次数+1）
7. 发布控制
8. 脱敏
9. 负载均衡
10. 接口保护
    1. 限制请求
    2. 信息脱敏
    3. 降级（熔断）
    4. 限流
    5. 超时时间
11. 统一日志
12. 统一文档



> 路由：

起到转发的作用，比如有接口A和接口B，网关回记录这些信息，根据用户访问的地址和参数，转发请求到对应的接口（服务器/集群）

/a => 接口A

/b => 接口B



> 负载均衡

在路由的基础上

/c => 服务A/集群A （随机转发到其中的一个机器）



>统一鉴权

判断用户是否有权限进行操作，无论访问声明接口，都统一去判断权限，不用重复写



> 统一处理跨域

网关统一处理跨域，不用在每一个项目中单独处理



> 统一业务处理

把每一个项目中都要做的通用逻辑放在上层（网关），统一处理，比如统计次数



> 访问控制

黑白名单，比如限制 DDOS IP



> 发布控制

灰度发布，比如上线新接口、先给接口分配20%的流量，老接口80%，再慢慢调整比例



> 流量染色

给请求（流量）添加一些标识，一般设置在请求头中，添加新的请求头



> 统一接口保护







### 网关分类：

>- 全局网关（接入层网关）：作用是负载均衡、请求日志等，不和业务逻辑绑定
>- 业务网关（微服务网关）：由一些业务逻辑作用是将请求转发给不同的业务、项目、接口、服务



### 实现

1.Nginx（全局网关）、Kong网关（API网关）

Spring Cloud Gateway（替代Zuul）性能高、可以用Java代码来写逻辑



### 核心概念

路由（根据什么条件，请求转发到哪里）

断言（一组规则、条件，用来判断如何使用路由）

过滤器（对请求进行一系列处理，比如添加请求头、添加请求参数）



请求流程：

1. 客户端发起请求
2. Handler Mapping：根据断言，曲江请求转发给对应的路由
3. Web Handle：处理请求（一层层经过过滤器）
4. 实际调用服务



两种定义方式：

1. 配置式（方便、）
2. 编程式（灵活、相对比较麻烦）





### 过滤器

基本功能：对请求头、请求参数、响应头的增删改查

1. 添加请求头
2. 添加请求参数‘
3. 添加响应头
4. 降级
5. 限流
6. 重试







## 第五期

1. 实现统一的用户鉴权，统一的接口调用次数（把 API 网关也要用到项目中）
2. 完善功能



业务逻辑：

1. 用户发送请求到API网关
2. 请求日志
3. （黑白名单）
4. 用户鉴权（判断 ak、sk是否合法）
5. 请求的虚拟接口是否存在？
6. 请求转发，调用模拟接口
7. 响应日志
8. 调用成功，调用次数+1；
9. 调用失败，返回一个规范的错误码





具体实现：

##### 1.请求转发

使用前缀匹配断言：

用一个前缀匹配路由，所有路径为/api/\*\*转发到 http://localhost:8080/api/\*\*

```yaml
- id: api_route
  uri: http://localhost:8081/
  predicates:
    - Path=/api/name/{api_url}
```

#### 2.编写业务逻辑

使用了 GlobalFilter，全局请求拦截处理（类似于AOP）

因为网关没有引入MyBatis等操作数据库的类库，如果该操作较为复杂，可以由后端增删改查提供接口，网关直接调用，不用再重写逻辑。

- HTTP请求：HTTPClient、ResultTemplate、Fegin等
- RPC（Dubbo）



## 第六期

1. 补充完整网关的业务逻辑（怎么操作数据库、怎么复用之前的方法？RPC）
2. 完善系统、开发一个监督统计功能



### 网关业务逻辑

问题：网关项目比较纯净，没有操作数据库的包，并且还要调用之前写的代码，复制黏贴维护麻烦

理想，直接请求到其他项目的方法

#### 怎么调用其他项目的方法？

1. 复制代码和依赖、环境
2. HTTP请求（提供一个接口，供其他项目使用）
3. RPC
4. 把公共的代码打个 jar 包，供其他项目去引用（客户端SDK）



HTTP请求怎么调用？

1. 提供方开发一个接口（地址、请求方式、参数、返回值）
2. 调用方使用HTTPClient之类的代码去发送 HTTP 请求。





#### RPC

**作用：像调用本地方法一样调用远程方法。**

对开发者更透明，减少了很多沟通成本。

**RPC向远程服务器发送请求时，未必要使用 HTTP 协议，比如还可以使用 TCP/IP。性能更高，内部服务更适用**



#### Dubbo

底层为 Triple 协议

1. backend项目作为服务的提供者，提供三个方法：
   1. 去数据库中查询 ak 和 sk 是否已经分配给用户
   2. 从数据库中查询接口是否存在，以及请求方法是否匹配（还要校验请求参数）
   3. 调用成功，调用接口次数 + 1，invokeCount
2. gateway 作为服务的调用者，调用这三个方法





1. 服务接口必须要在同一个包下，建议是抽象出一个公共项目（存放接口、实体类等）
2. 设置注解（启动类的@EnableDubbo 和 接口实现类和 Bean 引入的注解）
3. 添加配置
4. 服务调用项目和提供者项目尽量引入相同的依赖和配置









## 第七期

1. 完成网关业务逻辑
2. 开发管理员分析的功能
3. 上线



#### 1.网关业务逻辑

1. 实际情况是去数据库中查看是否已分配给用户密钥（ak、sk是否合法）
2. 从数据库中查询虚拟接口是否存在，以及请求方式是否匹配（还可以校验请求参数）
3. 调用成功，接口调用次数+1；





#### 公共服务

目的是让方法、实体类在多个项目间复用，避免重复编写

1. 数据库中查是否分配给用户密钥
2. 从数据库重查询接口是否存在
3. 调用接口次数 + 1；

步骤：

1. 新建干净的maven 项目，只保留必要的依赖
2. 抽取 service 和 实体类
3. install 本地 maven 包
4. 让服务提供者引入 common 包，测试是否正常运行
5. 让服务消费者引入 common 包



#### 开发统计分析

##### 需求

获取某个用户调用接口次数的占比（饼图）

```sql
select interfacterInfo , sum(totalNum) as totalNum from userInterInfo group by interfaceInfoId order by total desc limit 3 
```
