package vn.nextcore.device.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebMvc
public class WebConfig implements WebMvcConfigurer {
    @Value("${file.upload-dir}")
    private String uploadImgDeviceDir;

    String  currentDirectory = System.getProperty("user.dir");

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/uploadImgDevice/**")
                .addResourceLocations(currentDirectory + uploadImgDeviceDir);
    }
}
