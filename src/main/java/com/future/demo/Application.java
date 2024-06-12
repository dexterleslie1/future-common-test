package com.future.demo;

import com.future.common.auth.EnableFutureAuthorization;
import com.future.common.exception.EnableFutureExceptionHandler;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
// 启用future的全局异常处理特性
@EnableFutureExceptionHandler
// 启用 auth 插件
@EnableFutureAuthorization
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
