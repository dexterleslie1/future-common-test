package com.future.demo;


import com.future.common.auth.security.CustomizeAuthentication;
import com.future.common.auth.service.UserService;
import com.future.common.exception.BusinessException;
import com.future.common.http.ObjectResponse;
import com.future.common.http.ResponseUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/v1/test")
@Slf4j
public class TestController {

    @Autowired
    UserService userService;

    /**
     * 允许匿名访问的接口
     *
     * @return
     * @throws BusinessException
     */
    @GetMapping("test1")
    public ObjectResponse<String> test1() throws BusinessException {
        return ResponseUtils.successObject("Hello world!");
    }

    /**
     * 不允许匿名访问接口
     *
     * @param authentication
     * @return
     * @throws BusinessException
     */
    @GetMapping("test2")
    public ObjectResponse<String> test2(CustomizeAuthentication authentication) throws BusinessException {
        return ResponseUtils.successObject(this.userService.get(authentication.getUser().getUserId()).getPhone());
    }
}
