package com.lwj.house.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.*;

/**
 * 前端资源配置类
 *
 * @author Administrator
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    /**
     * springBoot2.X之后，默认静态资源访问路径将会被拦截，
     * 需要在这里配置访问路径
     * @param registry
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/static/**").addResourceLocations("classpath:/static/");
    }

    @Bean
    public WebMvcConfigurer webMvcConfigurer() {
        WebMvcConfigurer webMvcConfigurer = new WebMvcConfigurer() {
            @Override
            public void addViewControllers(ViewControllerRegistry registry) {
                registry.addViewController("/").setViewName("login");
                registry.addViewController("/index.html").setViewName("login");
                registry.addViewController("/main.html").setViewName("dashboard");
            }

//            //注册登陆拦截器
//            @Override
//            public void addInterceptors(InterceptorRegistry registry) {
//                registry.addInterceptor(new LoginHandlerInterceptor()).addPathPatterns("/**")
//                        .excludePathPatterns("/index.html", "/", "/user/login", "/asserts/**", "/webjars/**");
//            }
        };
        return webMvcConfigurer;
    }

//    @Autowired
//    private UserSecurityInterceptor securityInterceptor;
//
//
//    @Override
//    public void addInterceptors(InterceptorRegistry registry) {
//        InterceptorRegistration addInterceptor = registry.addInterceptor(securityInterceptor);
//        // 排除配置
//        addInterceptor.excludePathPatterns("/error");
//        //排除静态资源
//        addInterceptor.excludePathPatterns("/static/**");
//        addInterceptor.excludePathPatterns("/view/login");
//        addInterceptor.excludePathPatterns("/login/check");
//        // 拦截配置
//        addInterceptor.addPathPatterns("/**");
//    }

}
