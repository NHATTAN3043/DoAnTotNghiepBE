package vn.nextcore.device.service.provider;

import vn.nextcore.device.dto.resp.ProviderResponse;

import java.util.List;

public interface IProviderService {
    List<ProviderResponse> getAllProvider();
}
