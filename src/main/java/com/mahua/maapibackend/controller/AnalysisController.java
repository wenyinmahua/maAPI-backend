package com.mahua.maapibackend.controller;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.mahua.maapibackend.annotation.AuthCheck;
import com.mahua.maapibackend.common.BaseResponse;
import com.mahua.maapibackend.common.ErrorCode;
import com.mahua.maapibackend.common.ResultUtils;
import com.mahua.maapibackend.constant.UserConstant;
import com.mahua.maapibackend.exception.BusinessException;
import com.mahua.maapibackend.mapper.UserInterfaceInfoMapper;
import com.mahua.maapibackend.model.vo.InterfaceInfoVO;
import com.mahua.maapibackend.service.InterfaceInfoService;
import com.mahua.maapicommon.model.entity.InterfaceInfo;
import com.mahua.maapicommon.model.entity.UserInterfaceInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/analysis")
@Slf4j
public class AnalysisController {

	@Resource
	private UserInterfaceInfoMapper userInterfaceInfoMapper;

	@Resource
	private InterfaceInfoService interfaceInfoService;
	@GetMapping("/top/interface/invoke")
	@AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
	public BaseResponse<List<InterfaceInfoVO>> getTopInterfaceInvoke(){
		// 得到的是某个接口的总调用次数
		List<UserInterfaceInfo> userInterfaceInfoList = userInterfaceInfoMapper.listTopInvokeInterfaceInfo(3);
		// 根据 id 进行分组，存储数据
		Map<Long, List<UserInterfaceInfo>> interfaceInfoIdMap =
				userInterfaceInfoList.stream().collect(Collectors.groupingBy(UserInterfaceInfo::getInterfaceInfoId));
		// 根据 id 查询某个接口的信息
		QueryWrapper<InterfaceInfo> queryWrapper = new QueryWrapper<>();
		Set<Long> interfaceInfoIds = interfaceInfoIdMap.keySet();
		queryWrapper.in("id", interfaceInfoIds);
		List<InterfaceInfo> interfaceInfoList = interfaceInfoService.list(queryWrapper);
		if (CollectionUtil.isEmpty(interfaceInfoList)){
			throw new BusinessException(ErrorCode.SYSTEM_ERROR,"查询数据库失败");
		}
		// 将接口信息 和 接口总调用次数 封装在一起
		List<InterfaceInfoVO> interfaceInfoVOList = interfaceInfoList.stream().map(interfaceInfo -> {
			InterfaceInfoVO interfaceInfoVO = new InterfaceInfoVO();
			BeanUtils.copyProperties(interfaceInfo, interfaceInfoVO);
			interfaceInfoVO.setTotalNum(interfaceInfoIdMap.get(interfaceInfo.getId()).get(0).getTotalNum());
			return interfaceInfoVO;
		}).collect(Collectors.toList());


		// 返回封装后的数据

		return ResultUtils.success(interfaceInfoVOList);
	}
}