package com.onlineshop.Online.shop.Swagger;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.tags.Tag;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class CustomSwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Online Shop API")
                        .version("1.0")
                        .description("API dokumentacija za Online Shop projekat.")
                        .contact(new Contact()
                                .name("Nikola Dzomic")
                                .email("nikoladzomic123@gmail.com")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8080")
                                .description("Lokalni server")))
                .addSecurityItem(new SecurityRequirement().addList("BearerAuth"))
                .components(new Components()
                        .addSecuritySchemes("BearerAuth", new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")));
    }
}