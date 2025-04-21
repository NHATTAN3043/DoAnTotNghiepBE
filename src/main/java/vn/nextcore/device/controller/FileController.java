package vn.nextcore.device.controller;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.util.StreamUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import vn.nextcore.device.enums.ErrorCodeEnum;
import vn.nextcore.device.exception.HandlerException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

@RestController
@Validated
public class FileController {
    @Value("${file.upload-dir}")
    private String UPLOAD_DIR;

    private String currentDirectory = System.getProperty("user.dir");

    @GetMapping(value = "images/{filename}", produces = MediaType.IMAGE_JPEG_VALUE)
    public void getImage(@PathVariable String filename, HttpServletResponse response) throws IOException {
        File file = new File(currentDirectory + UPLOAD_DIR + filename);

        if (!file.exists()) {
            throw new HandlerException(ErrorCodeEnum.ER106.getCode(), ErrorCodeEnum.ER106.getMessage(), "/images", HttpStatus.BAD_REQUEST);
        }

        try (InputStream inputStream = new FileInputStream(file)) {
            response.setContentType(MediaType.IMAGE_JPEG_VALUE);
            StreamUtils.copy(inputStream, response.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
            throw new HandlerException(ErrorCodeEnum.ER005.getCode(), ErrorCodeEnum.ER005.getMessage(), "/images", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
