package vn.nextcore.device.service.provider;

import vn.nextcore.device.dto.req.ProviderRequest;
import vn.nextcore.device.dto.resp.ProviderResponse;

import java.util.List;

public interface IProviderService {
    List<ProviderResponse> getAllProvider(String providerName, Boolean isSelectList);

    ProviderResponse createProvider(ProviderRequest req);

    ProviderResponse updateProvider(String id, ProviderRequest req);

    ProviderResponse getDetailProvider(String id);

    ProviderResponse deleteProvider(String id);
}
