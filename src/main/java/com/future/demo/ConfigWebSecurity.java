package com.future.demo;

import org.springframework.context.annotation.Configuration;

@Configuration
//@EnableWebSecurity
public class ConfigWebSecurity /*extends WebSecurityConfigurerAdapter*/ {

    // todo 如何避免覆盖插件的配置或者插件覆盖此security配置
//    @Override
//    protected void configure(HttpSecurity http) throws Exception {
//        // todo 尝试找找有没有更加好的方式
//        // todo 编写文档演示antMatcher函数的用法
//        http.antMatcher("/api/v1/test/**")
//                .authorizeRequests().antMatchers("/api/v1/test/test1").permitAll();
//    }

}