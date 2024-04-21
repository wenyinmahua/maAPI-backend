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
import com.mahua.maapibackend.constant.UserConstant;
import com.mahua.maapibackend.exception.BusinessException;
import com.mahua.maapibackend.model.dto.userinterfaceinfo.UserInterfaceAddRequest;
import com.mahua.maapibackend.model.dto.userinterfaceinfo.UserInterfaceQueryRequest;
import com.mahua.maapibackend.model.dto.userinterfaceinfo.UserInterfaceUpdateRequest;
import com.mahua.maapibackend.model.entity.UserInterfaceInfo;
import com.mahua.maapibackend.model.entity.User;
import com.mahua.maapibackend.service.UserInterfaceInfoService;
import com.mahua.maapibackend.service.UserService;
import com.mahua.mahuaclientsdk.client.MaHuaAPIClient;
import lombok.extern.slf4j.Slf4j;
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
@RequestMapping("/userInterfaceInfo")
@Slf4j
public class UserInterfaceController {

    @Resource
    private UserInterfaceInfoService userUserInterfaceInfoService;

    @Resource
    private UserService userService;

    @Resource
    private MaHuaAPIClient maHuaAPIClient;

    // region 增删改查

    /**
     * 创建用户可以调用某个接口
     *
     * @param userInterfaceAddRequest
     * @param request
     * @return
     */
    @PostMapping("/add")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Long> addUserInterface(@RequestBody UserInterfaceAddRequest userInterfaceAddRequest, HttpServletRequest request) {
        if (userInterfaceAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        UserInterfaceInfo interfaceInfo = new UserInterfaceInfo();
        BeanUtils.copyProperties(userInterfaceAddRequest, interfaceInfo);
        // 校验
        userUserInterfaceInfoService.validUserInterface(interfaceInfo, true);
        User loginUser = userService.getLoginUser(request);
        interfaceInfo.setUserId(loginUser.getId());
        boolean result = userUserInterfaceInfoService.save(interfaceInfo);
        if (!result) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR);
        }
        long newUserInterfaceId = interfaceInfo.getId();
        return ResultUtils.success(newUserInterfaceId);
    }

    /**
     * 删除
     *
     * @param deleteRequest
     * @param request
     * @return
     */
    @PostMapping("/delete")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> deleteUserInterface(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = userService.getLoginUser(request);
        long id = deleteRequest.getId();
        // 判断是否存在
        UserInterfaceInfo oldUserInterface = userUserInterfaceInfoService.getById(id);
        if (oldUserInterface == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        // 仅本人或管理员可删除
        if (!oldUserInterface.getUserId().equals(user.getId()) && !userService.isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        boolean b = userUserInterfaceInfoService.removeById(id);
        return ResultUtils.success(b);
    }

    /**
     * 更新
     *
     * @param userInterfaceUpdateRequest
     * @param request
     * @return
     */
    @PostMapping("/update")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> updateUserInterface(@RequestBody UserInterfaceUpdateRequest userInterfaceUpdateRequest,
                                            HttpServletRequest request) {
        if (userInterfaceUpdateRequest == null || userInterfaceUpdateRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        UserInterfaceInfo interfaceInfo = new UserInterfaceInfo();
        BeanUtils.copyProperties(userInterfaceUpdateRequest, interfaceInfo);
        // 参数校验
        userUserInterfaceInfoService.validUserInterface(interfaceInfo, false);
        User user = userService.getLoginUser(request);
        long id = userInterfaceUpdateRequest.getId();
        // 判断是否存在
        UserInterfaceInfo oldUserInterface = userUserInterfaceInfoService.getById(id);
        if (oldUserInterface == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        // 仅本人或管理员可修改
        if (!oldUserInterface.getUserId().equals(user.getId()) && !userService.isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        boolean result = userUserInterfaceInfoService.updateById(interfaceInfo);
        return ResultUtils.success(result);
    }

    /**
     * 根据 id 获取
     *
     * @param id
     * @return
     */
    @GetMapping("/get")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<UserInterfaceInfo> getUserInterfaceById(long id) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        UserInterfaceInfo interfaceInfo = userUserInterfaceInfoService.getById(id);
        return ResultUtils.success(interfaceInfo);
    }

    /**
     * 获取列表（仅管理员可使用）
     *
     * @param userInterfaceQueryRequest
     * @return
     */
    @GetMapping("/list")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<List<UserInterfaceInfo>> listUserInterface(UserInterfaceQueryRequest userInterfaceQueryRequest) {
        UserInterfaceInfo interfaceQuery = new UserInterfaceInfo();
        if (userInterfaceQueryRequest != null) {
            BeanUtils.copyProperties(userInterfaceQueryRequest, interfaceQuery);
        }
        QueryWrapper<UserInterfaceInfo> queryWrapper = new QueryWrapper<>(interfaceQuery);
        List<UserInterfaceInfo> interfaceList = userUserInterfaceInfoService.list(queryWrapper);
        return ResultUtils.success(interfaceList);
    }

    /**
     * 分页获取列表
     *
     * @param userInterfaceQueryRequest
     * @param request
     * @return
     */
    @GetMapping("/list/page")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Page<UserInterfaceInfo>> listUserInterfaceByPage(UserInterfaceQueryRequest userInterfaceQueryRequest, HttpServletRequest request) {
        if (userInterfaceQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        UserInterfaceInfo interfaceQuery = new UserInterfaceInfo();
        BeanUtils.copyProperties(userInterfaceQueryRequest, interfaceQuery);
        long current = userInterfaceQueryRequest.getCurrent();
        long size = userInterfaceQueryRequest.getPageSize();
        String sortField = userInterfaceQueryRequest.getSortField();
        String sortOrder = userInterfaceQueryRequest.getSortOrder();
//        String name = interfaceQuery.getName();
        // content 需支持模糊搜索
//        interfaceQuery.setName(null);
        // 限制爬虫
        if (size > 50) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        QueryWrapper<UserInterfaceInfo> queryWrapper = new QueryWrapper<>(interfaceQuery);
//        queryWrapper.like(StringUtils.isNotBlank(name), "name", name);
        queryWrapper.orderBy(StringUtils.isNotBlank(sortField),
                sortOrder.equals(CommonConstant.SORT_ORDER_ASC), sortField);
        Page<UserInterfaceInfo> interfacePage = userUserInterfaceInfoService.page(new Page<>(current, size), queryWrapper);
        return ResultUtils.success(interfacePage);
    }

    // endregion

    /**
     * 发布
     *
     * @param interfaceOnlineOrOfflineRequest
     * @return
     */
//    @PostMapping("/online")
//    @AuthCheck(mustRole = "admin")
//    public BaseResponse<Boolean> onlineUserInterface(@RequestBody UserInterfaceOnlineOrOfflineRequest interfaceOnlineOrOfflineRequest) {
//        if (interfaceOnlineOrOfflineRequest == null || interfaceOnlineOrOfflineRequest.getId() <= 0) {
//            throw new BusinessException(ErrorCode.PARAMS_ERROR);
//        }
//        UserInterfaceInfo interfaceInfo = new UserInterfaceInfo();
//        BeanUtils.copyProperties(interfaceOnlineOrOfflineRequest, interfaceInfo);
//        long id = interfaceOnlineOrOfflineRequest.getId();
//        // 判断是否存在
//        UserInterfaceInfo oldUserInterface = userUserInterfaceInfoService.getById(id);
//        if (oldUserInterface == null) {
//            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
//        }
//        // 判断接口是否可以调用
//        com.mahua.mahuaclientsdk.model.User user = new com.mahua.mahuaclientsdk.model.User();
//        user.setName("mahua");
//        String username = maHuaAPIClient.getNameByPostJson(user);
//        if(StringUtils.isBlank(username)){
//            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"接口验证失败");
//        }
//        UserInterfaceInfo onlineUserInterfaceInfo = new UserInterfaceInfo();
//        onlineUserInterfaceInfo.setId(id);
//        onlineUserInterfaceInfo.setStatus(UserInterfaceInfoStatusEnum.ONLINE.getValue());
//        boolean result = userUserInterfaceInfoService.updateById(onlineUserInterfaceInfo);
//        return ResultUtils.success(result);
//    }

    /**
     * 下线
     *
     * @param interfaceOnlineOrOfflineRequest
     * @return
     */
//    @PostMapping("/offline")
//    @AuthCheck(mustRole = "admin")
//    public BaseResponse<Boolean> offlineUserInterface(@RequestBody UserInterfaceOnlineOrOfflineRequest interfaceOnlineOrOfflineRequest) {
//        if (interfaceOnlineOrOfflineRequest == null || interfaceOnlineOrOfflineRequest.getId() <= 0) {
//            throw new BusinessException(ErrorCode.PARAMS_ERROR);
//        }
//        UserInterfaceInfo interfaceInfo = new UserInterfaceInfo();
//        BeanUtils.copyProperties(interfaceOnlineOrOfflineRequest, interfaceInfo);
//        long id = interfaceOnlineOrOfflineRequest.getId();
//        // 判断是否存在
//        UserInterfaceInfo oldUserInterface = userUserInterfaceInfoService.getById(id);
//        if (oldUserInterface == null) {
//            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
//        }
//        // 判断接口是否可以调用
//        com.mahua.mahuaclientsdk.model.User user = new com.mahua.mahuaclientsdk.model.User();
//        user.setName("test");
//        String username = maHuaAPIClient.getNameByPostJson(user);
//        if(StringUtils.isBlank(username)){
//            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"接口验证失败");
//        }
//        UserInterfaceInfo offlineUserInterfaceInfo = new UserInterfaceInfo();
//        offlineUserInterfaceInfo.setId(id);
//        offlineUserInterfaceInfo.setStatus(UserInterfaceInfoStatusEnum.OFFLINE.getValue());
//        boolean result = userUserInterfaceInfoService.updateById(offlineUserInterfaceInfo);
//        return ResultUtils.success(result);
//    }
//

    /**
     * 测试调用
     *
     * @param interfaceInfoInvokeRequest
     * @return
     */
//    @PostMapping("/invoke")
//    public BaseResponse<String> invokeUserInterface(@RequestBody UserInterfaceInfoInvokeRequest interfaceInfoInvokeRequest, HttpServletRequest request) {
//        if (interfaceInfoInvokeRequest == null || interfaceInfoInvokeRequest.getId() <= 0) {
//            throw new BusinessException(ErrorCode.PARAMS_ERROR);
//        }
//        long id = interfaceInfoInvokeRequest.getId();
//        String userRequestParams = interfaceInfoInvokeRequest.getUserRequestParams();
//        // 判断是否存在
//        UserInterfaceInfo oldUserInterface = userUserInterfaceInfoService.getById(id);
//        if (oldUserInterface == null) {
//            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
//        }
//        if (oldUserInterface.getStatus().intValue() == UserInterfaceInfoStatusEnum.OFFLINE.getValue()){
//            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"接口已下线");
//        }
//        User loginUser = userService.getLoginUser(request);
//
//        String accessKey = loginUser.getAccessKey();
//        String secretKey = loginUser.getSecretKey();
//        MaHuaAPIClient userAPIClient = new MaHuaAPIClient(accessKey,secretKey);
//        Gson gson = new Gson();
//        com.mahua.mahuaclientsdk.model.User user = gson.fromJson(userRequestParams, com.mahua.mahuaclientsdk.model.User.class);
//        String nameByPostJson = userAPIClient.getNameByPostJson(user);
//        return ResultUtils.success(nameByPostJson);
//    }
//

}
