package com.venus.meetspace.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Knife4jConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("MeetSpace 活动管理平台 API")
                        .version("1.0.0")
                        .description("MeetSpace 是一个线上到线下活动管理平台，提供活动创建、报名、搜索、推荐等功能。")
                        .contact(new Contact()
                                .name("Void Yang")
                                .email("void@meetspace.com"))
                        .license(new License()
                                .name("MIT")
                                .url("https://opensource.org/licenses/MIT")));
    }
}
