package vn.nextcore.device.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import vn.nextcore.device.dto.req.FilterRequest;
import vn.nextcore.device.enums.ErrorCodeEnum;
import vn.nextcore.device.enums.PathEnum;
import vn.nextcore.device.exception.HandlerException;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class JsonUtils {
    private static final ObjectMapper objectMapper = new ObjectMapper()
            .configure(com.fasterxml.jackson.core.JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true)
            .configure(com.fasterxml.jackson.core.JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);

    public static <T> Set<T> parseJsonToSet(String json, TypeReference<Set<T>> typeReference, ErrorCodeEnum codeEnum) {
        try {
            json = json.substring(json.indexOf("["));
            return objectMapper.readValue(json, typeReference);
        } catch (StringIndexOutOfBoundsException ex) {
            throw new HandlerException(ErrorCodeEnum.ER105.getCode(), ErrorCodeEnum.ER105.getMessage(), PathEnum.DEVICE_PATH.getPath(), HttpStatus.BAD_REQUEST);
        } catch (JsonProcessingException jsonProcessingException) {
            throw new HandlerException(codeEnum.getCode(), codeEnum.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    public static List<FilterRequest> decodeAndList(String encoded) {
        try {
            String decodedJson = URLDecoder.decode(encoded, StandardCharsets.UTF_8);
            List<FilterRequest> filterRequests = objectMapper.readValue(decodedJson, new TypeReference<>() {
            });

            return new ArrayList<>(filterRequests);
        } catch (Exception e) {
            throw new HandlerException(ErrorCodeEnum.ER043.getCode(), ErrorCodeEnum.ER043.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    public static String decodeURL(String encode) {
        return URLDecoder.decode(encode, StandardCharsets.UTF_8);
    }

    public static String getValueByKey(Map<String, String> allParams, String key) {
        return allParams.get(key);
    }
}
