package com.future.demo.config;

import com.future.common.feign.CustomizeErrorDecoder;
import com.future.demo.feign.ApplicationFeign;
import feign.codec.ErrorDecoder;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableFeignClients(
        clients = {
                ApplicationFeign.class
        }
)
public class TestSupportConfig {
    /**
     * openfeign支持自动检查并抛出业务异常不需要编写代码判断errorCode是否不等于0
     *
     * @return
     */
    @Bean
    ErrorDecoder errorDecoder() {
        return new CustomizeErrorDecoder();
    }
}
