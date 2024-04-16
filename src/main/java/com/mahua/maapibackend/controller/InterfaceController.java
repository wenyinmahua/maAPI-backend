package com.mahua.maapibackend.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.gson.Gson;
import com.mahua.maapibackend.annotation.AuthCheck;
import com.mahua.maapibackend.common.BaseResponse;
import com.mahua.maapibackend.common.DeleteRequest;
import com.mahua.maapibackend.common.ErrorCode;
import com.mahua.maapibackend.common.ResultUtils;
import com.mahua.maapibackend.constant.CommonConstant;
import com.mahua.maapibackend.exception.BusinessException;
import com.mahua.maapibackend.model.dto.interfaceinfo.*;
import com.mahua.maapibackend.model.entity.InterfaceInfo;
import com.mahua.maapibackend.model.entity.User;
import com.mahua.maapibackend.model.enums.InterfaceInfoStatusEnum;
import com.mahua.maapibackend.service.InterfaceInfoService;
import com.mahua.maapibackend.service.UserService;
import com.mahua.mahuaclientsdk.client.MaHuaAPIClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 帖子接口
 *
 * @author mahua
 */
@RestController
@RequestMapping("/interfaceInfo")
@Slf4j
public class InterfaceController {

    @Resource
    private InterfaceInfoService interfaceinfoService;

    @Resource
    private UserService userService;

    @Resource
    private MaHuaAPIClient maHuaAPIClient;

    // region 增删改查

    /**
     * 创建
     *
     * @param interfaceAddRequest
     * @param request
     * @return
     */
    @PostMapping("/add")
    public BaseResponse<Long> addInterface(@RequestBody InterfaceAddRequest interfaceAddRequest, HttpServletRequest request) {
        if (interfaceAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        InterfaceInfo interfaceInfo = new InterfaceInfo();
        BeanUtils.copyProperties(interfaceAddRequest, interfaceInfo);
        // 校验
        interfaceinfoService.validInterface(interfaceInfo, true);
        User loginUser = userService.getLoginUser(request);
        interfaceInfo.setUserId(loginUser.getId());
        boolean result = interfaceinfoService.save(interfaceInfo);
        if (!result) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR);
        }
        long newInterfaceId = interfaceInfo.getId();
        return ResultUtils.success(newInterfaceId);
    }

    /**
     * 删除
     *
     * @param deleteRequest
     * @param request
     * @return
     */
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteInterface(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = userService.getLoginUser(request);
        long id = deleteRequest.getId();
        // 判断是否存在
        InterfaceInfo oldInterface = interfaceinfoService.getById(id);
        if (oldInterface == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        // 仅本人或管理员可删除
        if (!oldInterface.getUserId().equals(user.getId()) && !userService.isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        boolean b = interfaceinfoService.removeById(id);
        return ResultUtils.success(b);
    }

    /**
     * 更新
     *
     * @param interfaceUpdateRequest
     * @param request
     * @return
     */
    @PostMapping("/update")
    public BaseResponse<Boolean> updateInterface(@RequestBody InterfaceUpdateRequest interfaceUpdateRequest,
                                            HttpServletRequest request) {
        if (interfaceUpdateRequest == null || interfaceUpdateRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        InterfaceInfo interfaceInfo = new InterfaceInfo();
        BeanUtils.copyProperties(interfaceUpdateRequest, interfaceInfo);
        // 参数校验
        interfaceinfoService.validInterface(interfaceInfo, false);
        User user = userService.getLoginUser(request);
        long id = interfaceUpdateRequest.getId();
        // 判断是否存在
        InterfaceInfo oldInterface = interfaceinfoService.getById(id);
        if (oldInterface == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        // 仅本人或管理员可修改
        if (!oldInterface.getUserId().equals(user.getId()) && !userService.isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        boolean result = interfaceinfoService.updateById(interfaceInfo);
        return ResultUtils.success(result);
    }

    /**
     * 根据 id 获取
     *
     * @param id
     * @return
     */
    @GetMapping("/get")
    public BaseResponse<InterfaceInfo> getInterfaceById(long id) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        InterfaceInfo interfaceInfo = interfaceinfoService.getById(id);
        return ResultUtils.success(interfaceInfo);
    }

    /**
     * 获取列表（仅管理员可使用）
     *
     * @param interfaceQueryRequest
     * @return
     */
    @AuthCheck(mustRole = "admin")
    @GetMapping("/list")
    public BaseResponse<List<InterfaceInfo>> listInterface(InterfaceQueryRequest interfaceQueryRequest) {
        InterfaceInfo interfaceQuery = new InterfaceInfo();
        if (interfaceQueryRequest != null) {
            BeanUtils.copyProperties(interfaceQueryRequest, interfaceQuery);
        }
        QueryWrapper<InterfaceInfo> queryWrapper = new QueryWrapper<>(interfaceQuery);
        List<InterfaceInfo> interfaceList = interfaceinfoService.list(queryWrapper);
        return ResultUtils.success(interfaceList);
    }

    /**
     * 分页获取列表
     *
     * @param interfaceQueryRequest
     * @param request
     * @return
     */
    @GetMapping("/list/page")
    public BaseResponse<Page<InterfaceInfo>> listInterfaceByPage(InterfaceQueryRequest interfaceQueryRequest, HttpServletRequest request) {
        if (interfaceQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        InterfaceInfo interfaceQuery = new InterfaceInfo();
        BeanUtils.copyProperties(interfaceQueryRequest, interfaceQuery);
        long current = interfaceQueryRequest.getCurrent();
        long size = interfaceQueryRequest.getPageSize();
        String sortField = interfaceQueryRequest.getSortField();
        String sortOrder = interfaceQueryRequest.getSortOrder();
        String name = interfaceQuery.getName();
        // content 需支持模糊搜索
        interfaceQuery.setName(null);
        // 限制爬虫
        if (size > 50) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        QueryWrapper<InterfaceInfo> queryWrapper = new QueryWrapper<>(interfaceQuery);
        queryWrapper.like(StringUtils.isNotBlank(name), "name", name);
        queryWrapper.orderBy(StringUtils.isNotBlank(sortField),
                sortOrder.equals(CommonConstant.SORT_ORDER_ASC), sortField);
        Page<InterfaceInfo> interfacePage = interfaceinfoService.page(new Page<>(current, size), queryWrapper);
        return ResultUtils.success(interfacePage);
    }

    // endregion

    /**
     * 发布
     *
     * @param interfaceOnlineOrOfflineRequest
     * @return
     */
    @PostMapping("/online")
    @AuthCheck(mustRole = "admin")
    public BaseResponse<Boolean> onlineInterface(@RequestBody InterfaceOnlineOrOfflineRequest interfaceOnlineOrOfflineRequest) {
        if (interfaceOnlineOrOfflineRequest == null || interfaceOnlineOrOfflineRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        InterfaceInfo interfaceInfo = new InterfaceInfo();
        BeanUtils.copyProperties(interfaceOnlineOrOfflineRequest, interfaceInfo);
        long id = interfaceOnlineOrOfflineRequest.getId();
        // 判断是否存在
        InterfaceInfo oldInterface = interfaceinfoService.getById(id);
        if (oldInterface == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        // 判断接口是否可以调用
        com.mahua.mahuaclientsdk.model.User user = new com.mahua.mahuaclientsdk.model.User();
        user.setName("mahua");
        String username = maHuaAPIClient.getNameByPostJson(user);
        if(StringUtils.isBlank(username)){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"接口验证失败");
        }
        InterfaceInfo onlineInterfaceInfo = new InterfaceInfo();
        onlineInterfaceInfo.setId(id);
        onlineInterfaceInfo.setStatus(InterfaceInfoStatusEnum.ONLINE.getValue());
        boolean result = interfaceinfoService.updateById(onlineInterfaceInfo);
        return ResultUtils.success(result);
    }

    /**
     * 下线
     *
     * @param interfaceOnlineOrOfflineRequest
     * @return
     */
    @PostMapping("/offline")
    @AuthCheck(mustRole = "admin")
    public BaseResponse<Boolean> offlineInterface(@RequestBody InterfaceOnlineOrOfflineRequest interfaceOnlineOrOfflineRequest) {
        if (interfaceOnlineOrOfflineRequest == null || interfaceOnlineOrOfflineRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        InterfaceInfo interfaceInfo = new InterfaceInfo();
        BeanUtils.copyProperties(interfaceOnlineOrOfflineRequest, interfaceInfo);
        long id = interfaceOnlineOrOfflineRequest.getId();
        // 判断是否存在
        InterfaceInfo oldInterface = interfaceinfoService.getById(id);
        if (oldInterface == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        // 判断接口是否可以调用
        com.mahua.mahuaclientsdk.model.User user = new com.mahua.mahuaclientsdk.model.User();
        user.setName("test");
        String username = maHuaAPIClient.getNameByPostJson(user);
        if(StringUtils.isBlank(username)){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"接口验证失败");
        }
        InterfaceInfo offlineInterfaceInfo = new InterfaceInfo();
        offlineInterfaceInfo.setId(id);
        offlineInterfaceInfo.setStatus(InterfaceInfoStatusEnum.OFFLINE.getValue());
        boolean result = interfaceinfoService.updateById(offlineInterfaceInfo);
        return ResultUtils.success(result);
    }


    /**
     * 测试调用
     *
     * @param interfaceInfoInvokeRequest
     * @return
     */
    @PostMapping("/invoke")
    @AuthCheck(mustRole = "admin")
    public BaseResponse<String> invokeInterface(@RequestBody InterfaceInfoInvokeRequest interfaceInfoInvokeRequest, HttpServletRequest request) {
        if (interfaceInfoInvokeRequest == null || interfaceInfoInvokeRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        long id = interfaceInfoInvokeRequest.getId();
        String userRequestParams = interfaceInfoInvokeRequest.getUserRequestParams();
        // 判断是否存在
        InterfaceInfo oldInterface = interfaceinfoService.getById(id);
        if (oldInterface == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        if (oldInterface.getStatus().intValue() == InterfaceInfoStatusEnum.OFFLINE.getValue()){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"接口已下线");
        }
        User loginUser = userService.getLoginUser(request);

        String accessKey = loginUser.getAccessKey();
        String secretKey = loginUser.getSecretKey();
        MaHuaAPIClient userAPIClient = new MaHuaAPIClient(accessKey,secretKey);
        Gson gson = new Gson();
        com.mahua.mahuaclientsdk.model.User user = gson.fromJson(userRequestParams, com.mahua.mahuaclientsdk.model.User.class);
        String nameByPostJson = userAPIClient.getNameByPostJson(user);
        return ResultUtils.success(nameByPostJson);
    }


}
