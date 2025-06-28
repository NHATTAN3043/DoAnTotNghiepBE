package vn.nextcore.device;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.annotation.EnableScheduling;
import vn.nextcore.device.service.storageFiles.StorageService;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

@SpringBootApplication
@EnableConfigurationProperties
@EnableScheduling
public class BaNextDeviceApplication {
    @Bean
    FirebaseMessaging firebaseMessaging() throws IOException {
        String path = System.getenv("FIREBASE_CONFIG_PATH");
        InputStream serviceAccount;

        if (path != null && !path.isBlank()) {
            serviceAccount = new FileInputStream(path);
        } else {
            serviceAccount = new ClassPathResource("nextdevice-2efec-firebase-adminsdk-fbsvc-803f9d189d.json").getInputStream();
        }

        GoogleCredentials googleCredentials = GoogleCredentials.fromStream(serviceAccount);
        FirebaseOptions firebaseOptions = FirebaseOptions.builder()
                .setCredentials(googleCredentials).build();
        FirebaseApp firebaseApp;
        if (FirebaseApp.getApps().isEmpty()) {
            firebaseApp = FirebaseApp.initializeApp(firebaseOptions);
        } else {
            firebaseApp = FirebaseApp.getInstance();
        }
        return FirebaseMessaging.getInstance(firebaseApp);

    }

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
