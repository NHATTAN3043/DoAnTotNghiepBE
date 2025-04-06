package vn.nextcore.device.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {
    @Value("${app.domain}")
    private String domainName;

    @Value("${app.license}")
    private String urlLicense;

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI().info(new Info()
                        .title("Api Next Device").version("v1.0.0").description("description")
                        .license(new License().name("Api license").url(urlLicense)))
                .servers(List.of(new Server().url(domainName).description("server 1")))
                // Config authentication swagger
                .components(new Components()
                        .addSecuritySchemes("bearer-key",
                                new SecurityScheme().type(SecurityScheme.Type.HTTP).scheme("bearer").bearerFormat("JWT")))
                .addSecurityItem(new SecurityRequirement().addList("bearer-key"));
    }

    @Bean
    public GroupedOpenApi groupApi() {
        return GroupedOpenApi.builder()
                .group("api-group")
                .pathsToMatch("/api/group/**")
                .build();
    }

    @Bean
    public GroupedOpenApi helloApi() {
        return GroupedOpenApi.builder()
                .group("api-hello")
                .pathsToMatch("/hello/**")
                .build();
    }

    @Bean
    public GroupedOpenApi authApi() {
        return GroupedOpenApi.builder()
                .group("api-auth")
                .pathsToMatch("/auth/**")
                .build();
    }

    @Bean
    public GroupedOpenApi deviceApi() {
        return GroupedOpenApi.builder()
                .group("api-device")
                .pathsToMatch("/api/device/**")
                .build();
    }

    @Bean
    public GroupedOpenApi specificationApi() {
        return GroupedOpenApi.builder()
                .group("api-specification")
                .pathsToMatch("/api/specification/**")
                .build();
    }

    @Bean
    public GroupedOpenApi providerApi() {
        return GroupedOpenApi.builder()
                .group("api-provider")
                .pathsToMatch("/api/provider/**")
                .build();
    }

    @Bean
    public GroupedOpenApi forgotPassWordApi() {
        return GroupedOpenApi.builder()
                .group("api-forgot-password")
                .pathsToMatch("/api/forgotPassword/**")
                .build();
    }

    @Bean
    public GroupedOpenApi requestApi() {
        return GroupedOpenApi.builder()
                .group("api-request")
                .pathsToMatch("/api/request/**")
                .build();
    }

    @Bean
    public GroupedOpenApi projectApi() {
        return GroupedOpenApi.builder()
                .group("api-project")
                .pathsToMatch("/api/project/**")
                .build();
    }
}
