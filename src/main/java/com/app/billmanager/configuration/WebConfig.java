package com.app.billmanager.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/features").setViewName("features");
        registry.addViewController("/localization").setViewName("localization");
        registry.addViewController("/terms").setViewName("terms");
        registry.addViewController("/privacy").setViewName("privacy");
        registry.addViewController("/team").setViewName("team");
        registry.addViewController("/donate").setViewName("donate");
    }
}
