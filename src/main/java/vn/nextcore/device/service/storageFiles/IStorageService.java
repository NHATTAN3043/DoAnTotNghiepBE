package vn.nextcore.device.service.storageFiles;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;
import java.util.stream.Stream;

public interface IStorageService {
    void init();

    void store(MultipartFile file);

    public String saveFile(MultipartFile file) throws IOException;

    Stream<Path> loadAll();

    Path load(String filename);

    public boolean delete(String filename);

    Resource loadAsResource(String filename);

    void deleteAll();
}
