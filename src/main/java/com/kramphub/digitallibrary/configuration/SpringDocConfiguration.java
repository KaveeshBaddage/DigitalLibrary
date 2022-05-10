package com.kramphub.digitallibrary.configuration;

import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * Configuration class for springdoc-openapi
 * @return bean of OpenAPI
 * @author Kaveesha Baddage
 * *
 */

@Configuration
public class SpringDocConfiguration {

    @Bean
    public OpenAPI customOpenAPI() {
        OpenAPI digital_library_application_api = new OpenAPI()
                .components(new Components())
                .info(new Info()
                        .title("Digital Library Application API")
                        .description("This is a Spring Boot REST Architecture based web service which expose an API to retrieve Book" +
                                " and Album information. \n This application use Google Book API and iTunes API as upstream services.")
                        .version("1.0.0")
                        .license(new License().name("Apache 2.0")));
        return digital_library_application_api;
    }


}






