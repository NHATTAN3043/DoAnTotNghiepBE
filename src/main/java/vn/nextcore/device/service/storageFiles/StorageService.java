package vn.nextcore.device.service.storageFiles;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;
import java.util.stream.Stream;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import vn.nextcore.device.enums.ErrorCodeEnum;
import vn.nextcore.device.enums.PathEnum;
import vn.nextcore.device.exception.HandlerException;

@Component
public class StorageService implements IStorageService {
    @Value("${file.upload-dir}")
    private String uploadImgDeviceDir;

    String currentDirectory = System.getProperty("user.dir");

    private Path rootLocation;

    @Override
    @PostConstruct
    public void init() {
        this.rootLocation = Paths.get(currentDirectory + uploadImgDeviceDir);
        try {
            if (!Files.exists(rootLocation)) {
                Files.createDirectories(rootLocation);
            }
        } catch (IOException e) {
            throw new HandlerException(ErrorCodeEnum.ER032.getCode(), ErrorCodeEnum.ER032.getMessage(), PathEnum.DEVICE_PATH.getPath(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public String saveFile(MultipartFile file) throws IOException {
        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
        Path filePath = Paths.get(currentDirectory + uploadImgDeviceDir, fileName);
        file.transferTo(filePath.toFile());

        return fileName;
    }

    @Override
    public void store(MultipartFile file) {
        try {

            Path destinationFile = this.rootLocation.resolve(
                            Paths.get(file.getOriginalFilename()))
                    .normalize().toAbsolutePath();

            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, destinationFile,
                        StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new HandlerException(ErrorCodeEnum.ER032.getCode(), ErrorCodeEnum.ER032.getMessage(), PathEnum.DEVICE_PATH.getPath(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public Stream<Path> loadAll() {
        return null;
    }

    @Override
    public Path load(String filename) {
        return null;
    }

    @Override
    public boolean delete(String filename) {
        try {
            Path file = rootLocation.resolve(filename);
            return Files.deleteIfExists(file);
        } catch (IOException e) {
            throw new RuntimeException("Error: " + e.getMessage());
        }
    }

    @Override
    public Resource loadAsResource(String filename) {
        return null;
    }

    @Override
    public void deleteAll() {
    }
}
