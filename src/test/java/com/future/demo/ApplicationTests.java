package com.future.demo;

import com.future.common.auth.dto.LoginSuccessDto;
import com.future.common.auth.dto.UserDto;
import com.future.common.auth.service.TokenService;
import com.future.common.auth.service.VerificationCodeService;
import com.future.common.constant.ErrorCodeConstant;
import com.future.common.exception.BusinessException;
import com.future.common.http.ObjectResponse;
import com.future.common.phone.RandomPhoneGeneratorUtil;
import com.future.demo.feign.ApplicationFeign;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.UUID;

/**
 * 应用集成测试用例
 */
@RunWith(SpringRunner.class)
@SpringBootTest(
        classes = {Application.class},
        webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestPropertySource("classpath:application-test.properties")
public class ApplicationTests {
    @Autowired
    ApplicationFeign applicationFeign;
    @SpyBean
    VerificationCodeService verificationCodeService;
    @SpyBean
    TokenService tokenService;

    @Test
    public void test_register_and_login() throws BusinessException, InterruptedException {
        // mock指定的验证码
        String verificationCode = "1111";
        Mockito.doReturn(verificationCode).when(this.verificationCodeService).generateRandomCode();

        String phone = RandomPhoneGeneratorUtil.getRandom();
        ObjectResponse<Integer> response =
                this.applicationFeign.getVerificationCode(phone);
        Assert.assertEquals(VerificationCodeService.VerificationCodeTTLInSeconds, response.getData().intValue());

        // 恢复验证码mock
        Mockito.doCallRealMethod().when(this.verificationCodeService).generateRandomCode();

        String password = UUID.randomUUID().toString();
        ObjectResponse<String> responseRegister =
                this.applicationFeign.register(phone, phone, password, verificationCode);
        Assert.assertEquals("注册成功", responseRegister.getData());

        ObjectResponse<LoginSuccessDto> responseLogin = this.applicationFeign.login(phone, password);
        Assert.assertEquals(phone, responseLogin.getData().getPhone());

        String accessToken = responseLogin.getData().getAccessToken();

        ObjectResponse<UserDto> getInfoResponse = this.applicationFeign.getInfo(accessToken);
        Assert.assertEquals(phone, getInfoResponse.getData().getPhone());

        // 测试多个spring-security配置是否会冲突
        Assert.assertEquals("Hello world!", this.applicationFeign.test1().getData());
        Assert.assertEquals(phone, this.applicationFeign.test2(accessToken).getData());

        //region 测试需要登录的接口不登录就请求和不需要登录的接口却登录请求的情况
        verificationCode = "1111";
        Mockito.doReturn(verificationCode).when(this.verificationCodeService).generateRandomCode();

        phone = RandomPhoneGeneratorUtil.getRandom();

        response = this.applicationFeign.getVerificationCode(phone, accessToken);
        Assert.assertEquals(VerificationCodeService.VerificationCodeTTLInSeconds, response.getData().intValue());

        // 恢复验证码mock
        Mockito.doCallRealMethod().when(this.verificationCodeService).generateRandomCode();

        password = UUID.randomUUID().toString();
        responseRegister =
                this.applicationFeign.register(phone, phone, password, verificationCode, accessToken);
        Assert.assertEquals("注册成功", responseRegister.getData());

        responseLogin = this.applicationFeign.login(phone, password, accessToken);
        Assert.assertEquals(phone, responseLogin.getData().getPhone());

        accessToken = responseLogin.getData().getAccessToken();

        try {
            this.applicationFeign.getInfo();
            Assert.fail("预期异常没有抛出");
        } catch (BusinessException ex) {
            Assert.assertEquals(ErrorCodeConstant.ErrorCodeLoginRequired, ex.getErrorCode());
            Assert.assertEquals("您未登陆", ex.getMessage());
        }

        Assert.assertEquals("Hello world!", this.applicationFeign.test1(accessToken).getData());
        try {
            this.applicationFeign.test2();
            Assert.fail("预期异常没有抛出");
        } catch (BusinessException ex) {
            Assert.assertEquals(ErrorCodeConstant.ErrorCodeLoginRequired, ex.getErrorCode());
            Assert.assertEquals("您未登陆", ex.getMessage());
        }

        //endregion

        //region 不能使用refresh token调用非refresh外的接口
        String refreshToken = responseLogin.getData().getRefreshToken();
        try {
            this.applicationFeign.getInfo(refreshToken);
            Assert.fail("预期异常没有抛出");
        } catch (BusinessException ex) {
            Assert.assertEquals(ErrorCodeConstant.ErrorCodeCommon, ex.getErrorCode());
            Assert.assertEquals("不存在token", ex.getErrorMessage());
        }
        //endregion

        //region 测试refresh access token
        try {
            this.applicationFeign.refreshAccessToken(refreshToken);
            Assert.fail("预期异常没有抛出");
        } catch (BusinessException ex) {
            Assert.assertEquals(ErrorCodeConstant.ErrorCodeCommon, ex.getErrorCode());
            Assert.assertEquals("不能提前刷新access token", ex.getErrorMessage());
        }

        Mockito.doReturn(1).when(this.tokenService).getTtlAccessToken();
        Thread.sleep(2000);
        accessToken = this.applicationFeign.refreshAccessToken(refreshToken).getData();
        Mockito.doCallRealMethod().when(this.tokenService).getTtlAccessToken();
        Assert.assertEquals(phone, this.applicationFeign.getInfo(accessToken).getData().getPhone());

        // 测试不能使用access token请求refresh接口
        try {
            this.applicationFeign.refreshAccessToken(accessToken);
            Assert.fail("预期异常没有抛出");
        } catch (BusinessException ex) {
            Assert.assertEquals(ErrorCodeConstant.ErrorCodeCommon, ex.getErrorCode());
            Assert.assertEquals("不存在token", ex.getErrorMessage());
        }

        //endregion

        //region 测试token过期
        Mockito.doReturn(1).when(this.tokenService).getTtlAccessToken();
        Mockito.doReturn(1).when(this.tokenService).getTtlRefreshTokenInSeconds();
        Thread.sleep(2000);
        try {
            this.applicationFeign.getInfo(accessToken);
            Assert.fail("预期异常没有抛出");
        } catch (BusinessException ex) {
            Assert.assertEquals(ErrorCodeConstant.ErrorCodeTokenExpired, ex.getErrorCode());
            Assert.assertEquals("token已过期", ex.getMessage());
        }

        Assert.assertEquals("Hello world!", this.applicationFeign.test1(accessToken).getData());
        Assert.assertEquals("Hello world!", this.applicationFeign.test1().getData());

        try {
            this.applicationFeign.test2(accessToken);
            Assert.fail("预期异常没有抛出");
        } catch (BusinessException ex) {
            Assert.assertEquals(ErrorCodeConstant.ErrorCodeTokenExpired, ex.getErrorCode());
            Assert.assertEquals("token已过期", ex.getMessage());
        }

        try {
            this.applicationFeign.refreshAccessToken(refreshToken);
            Assert.fail("预期异常没有抛出");
        } catch (BusinessException ex) {
            Assert.assertEquals(ErrorCodeConstant.ErrorCodeTokenExpired, ex.getErrorCode());
            Assert.assertEquals("token已过期", ex.getMessage());
        }

        //endregion
    }

}
