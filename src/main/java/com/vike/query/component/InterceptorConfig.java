package com.vike.query.component;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: lsl
 * @Date: Create in 2018/10/13
 */
@Configuration
public class InterceptorConfig implements WebMvcConfigurer {


    @Bean
    public SysInterceptor getInterceptor(){ return new SysInterceptor(); }


    public void addInterceptors(InterceptorRegistry registry) {
        /** 放行路径 */
        List<String> unInterceptor = new ArrayList<>();
        unInterceptor.add("/**/*.png");
        unInterceptor.add("/**/*.jpg");
        unInterceptor.add("/**/*.jpeg");
        unInterceptor.add("/**/*.css");
        unInterceptor.add("/**/*.js");

        unInterceptor.add("/");
        unInterceptor.add("/wx");
        unInterceptor.add("/view/index");
        unInterceptor.add("/view/history");
        unInterceptor.add("/view/invite");
        unInterceptor.add("/view/query");
        unInterceptor.add("/view/agreement");
        unInterceptor.add("/view/query-summit");
        unInterceptor.add("/tag/**");
        unInterceptor.add("/query/gain");
        unInterceptor.add("/query/check");
        unInterceptor.add("/query/summit");
        unInterceptor.add("/manager/create-menu");

        registry.addInterceptor(getInterceptor()).addPathPatterns("/**").excludePathPatterns(unInterceptor);
//        registry.addInterceptor(getAdminInterceptor()).addPathPatterns("/admin/**").excludePathPatterns("/admin/operator/login");
    }


}
