package vn.nextcore.device.service.device;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.multipart.MultipartFile;
import vn.nextcore.device.dto.req.DeviceRequest;
import vn.nextcore.device.dto.resp.DeviceResponse;
import vn.nextcore.device.dto.resp.ListDeviceResponse;

import java.util.Map;

public interface IDeviceService {
    DeviceResponse createDevice(DeviceRequest deviceRequest, String specificationJson, MultipartFile[] images, HttpServletRequest request);

    DeviceResponse updateDevice(String id, DeviceRequest deviceRequest, String specificationJson, MultipartFile[] images, String imagesDelete, String specificationDelete);

    ListDeviceResponse getAllDevices(Map<String, String> allParams);

    DeviceResponse getInfoDevice(String id);

    DeviceResponse deleteDevice(String id);
}
