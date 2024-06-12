package com.future.demo.feign;

import com.future.common.auth.dto.LoginSuccessDto;
import com.future.common.auth.dto.UserDto;
import com.future.common.exception.BusinessException;
import com.future.common.http.ObjectResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(
        contextId = "applicationFeign",
        value = "future-common-test",
        path = "/api/v1")
public interface ApplicationFeign {

    @GetMapping("future/auth/verificationCode/get")
    ObjectResponse<Integer> getVerificationCode(
            @RequestParam(value = "phone", defaultValue = "") String phone) throws BusinessException;

    @GetMapping("future/auth/verificationCode/get")
    ObjectResponse<Integer> getVerificationCode(
            @RequestParam(value = "phone", defaultValue = "") String phone,
            @RequestHeader(value = HttpHeaders.AUTHORIZATION) String accessToken) throws BusinessException;


    @PostMapping(value = "future/auth/register")
    ObjectResponse<String> register(
            @RequestParam(name = "phone", defaultValue = "") String phone,
            @RequestParam(name = "nickname", defaultValue = "") String nickname,
            @RequestParam(name = "password", defaultValue = "") String password,
            @RequestParam(name = "verificationCode", defaultValue = "") String verificationCode) throws BusinessException;

    @PostMapping(value = "future/auth/register")
    ObjectResponse<String> register(
            @RequestParam(name = "phone", defaultValue = "") String phone,
            @RequestParam(name = "nickname", defaultValue = "") String nickname,
            @RequestParam(name = "password", defaultValue = "") String password,
            @RequestParam(name = "verificationCode", defaultValue = "") String verificationCode,
            @RequestHeader(value = HttpHeaders.AUTHORIZATION) String accessToken) throws BusinessException;

    @PostMapping(value = "future/auth/login")
    ObjectResponse<LoginSuccessDto> login(
            @RequestParam(name = "phone", defaultValue = "") String phone,
            @RequestParam(name = "password", defaultValue = "") String password) throws BusinessException;

    @PostMapping(value = "future/auth/login")
    ObjectResponse<LoginSuccessDto> login(
            @RequestParam(name = "phone", defaultValue = "") String phone,
            @RequestParam(name = "password", defaultValue = "") String password,
            @RequestHeader(value = HttpHeaders.AUTHORIZATION) String accessToken) throws BusinessException;

    @GetMapping("future/auth/getInfo")
    ObjectResponse<UserDto> getInfo(@RequestHeader(value = HttpHeaders.AUTHORIZATION) String accessToken) throws BusinessException;

    @GetMapping("future/auth/getInfo")
    ObjectResponse<UserDto> getInfo() throws BusinessException;

    @GetMapping("test/test1")
    ObjectResponse<String> test1() throws BusinessException;

    @GetMapping("test/test1")
    ObjectResponse<String> test1(@RequestHeader(value = HttpHeaders.AUTHORIZATION) String accessToken) throws BusinessException;

    @GetMapping("test/test2")
    ObjectResponse<String> test2(@RequestHeader(value = HttpHeaders.AUTHORIZATION) String accessToken) throws BusinessException;

    @GetMapping("test/test2")
    ObjectResponse<String> test2() throws BusinessException;

    @PostMapping(value = "future/auth/refreshAccessToken")
    ObjectResponse<String> refreshAccessToken(@RequestParam(value = "refreshToken", defaultValue = "") String refreshToken) throws BusinessException;
}