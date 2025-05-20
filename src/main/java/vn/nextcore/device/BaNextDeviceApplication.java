package vn.nextcore.device;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import vn.nextcore.device.service.storageFiles.StorageService;

@SpringBootApplication
@EnableConfigurationProperties
@EnableScheduling
public class BaNextDeviceApplication {
    public static void main(String[] args) {
        SpringApplication.run(BaNextDeviceApplication.class, args);
    }

    @Bean
    CommandLineRunner init(StorageService storageService) {
        return (args) -> {
            storageService.init();
        };
    }
}
