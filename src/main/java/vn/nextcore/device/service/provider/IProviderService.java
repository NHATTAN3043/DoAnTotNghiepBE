package vn.nextcore.device.service.provider;

import vn.nextcore.device.dto.req.ProviderRequest;
import vn.nextcore.device.dto.resp.ProviderResponse;

import java.util.List;

public interface IProviderService {
    List<ProviderResponse> getAllProvider();

    ProviderResponse createProvider(ProviderRequest req);
}
