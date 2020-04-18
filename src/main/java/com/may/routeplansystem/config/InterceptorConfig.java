package com.may.routeplansystem.config;

import com.may.routeplansystem.intercepter.LoginInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Configuration
public class InterceptorConfig implements WebMvcConfigurer {

    @Resource
    private LoginInterceptor loginIntercepter;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        List<String> urlPatterns = new ArrayList<>();
        urlPatterns.add("/**/finalSolution/**");
        urlPatterns.add("/**/node/**");
        urlPatterns.add("/**/question/**");
        urlPatterns.add("/**/vehicleSystem/**");
        urlPatterns.add("/**/algorithm/**");
        urlPatterns.add("/**/distance/**");
        urlPatterns.add("/**/demoNode/**");
//        registry.addInterceptor(loginIntercepter).addPathPatterns(urlPatterns);
    }

}
