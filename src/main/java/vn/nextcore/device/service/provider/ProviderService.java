package vn.nextcore.device.service.provider;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import vn.nextcore.device.dto.req.ProviderRequest;
import vn.nextcore.device.dto.resp.ProviderResponse;
import vn.nextcore.device.entity.Provider;
import vn.nextcore.device.enums.ErrorCodeEnum;
import vn.nextcore.device.enums.PathEnum;
import vn.nextcore.device.exception.HandlerException;
import vn.nextcore.device.repository.ProviderRepository;

import java.util.ArrayList;
import java.util.List;

@Service
public class ProviderService implements IProviderService {
    @Autowired
    private ProviderRepository providerRepository;

    @Override
    public List<ProviderResponse> getAllProvider() {
        List<ProviderResponse> results = new ArrayList<>();
        try {
            List<Provider> providerList = providerRepository.findAll();
            for (Provider provider : providerList) {
                ProviderResponse item = new ProviderResponse();
                item.setId(provider.getId());
                item.setName(provider.getName());
                item.setAddress(provider.getAddress());
                item.setPhoneNumber(provider.getPhoneNumber());

                results.add(item);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new HandlerException(ErrorCodeEnum.ER005.getCode(), ErrorCodeEnum.ER005.getMessage(), PathEnum.GROUP_PATH.getPath(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        if (results.isEmpty())
            throw new HandlerException(ErrorCodeEnum.ER045.getCode(), ErrorCodeEnum.ER045.getMessage(), PathEnum.GROUP_PATH.getPath(), HttpStatus.BAD_REQUEST);

        return results;
    }

    @Override
    public ProviderResponse createProvider(ProviderRequest req) {
        try {
            Provider provider = new Provider();
            provider.setName(req.getName());
            provider.setAddress(req.getAddress());
            provider.setPhoneNumber(req.getPhoneNumber());
            providerRepository.save(provider);
            ProviderResponse result = new ProviderResponse();
            result.setId(provider.getId());
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            throw new HandlerException(ErrorCodeEnum.ER005.getCode(), ErrorCodeEnum.ER005.getMessage(), PathEnum.GROUP_PATH.getPath(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
