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
import vn.nextcore.device.repository.DeviceRepository;
import vn.nextcore.device.repository.ProviderRepository;
import vn.nextcore.device.util.ParseUtils;
import vn.nextcore.device.validation.HandlerValidateParams;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class ProviderService implements IProviderService {
    @Autowired
    private ProviderRepository providerRepository;

    @Autowired
    private DeviceRepository deviceRepository;

    @Override
    public List<ProviderResponse> getAllProvider(String providerName, Boolean isSelectList) {
        List<ProviderResponse> results = new ArrayList<>();
        try {
            List<Provider> providerList;
            if (isSelectList || providerName == null || providerName.isEmpty()) {
                providerList = providerRepository.findProviderByDeletedAtIsNull();
            } else {
                String queryParam = "%" + providerName + "%";
                providerList = providerRepository.findProviderByNameIsLikeIgnoreCaseAndDeletedAtIsNull(queryParam);
            }
            for (Provider provider : providerList) {
                ProviderResponse item = ParseUtils.convertProviderToProviderResponse(provider, isSelectList);
                results.add(item);
            }
            return results;
        } catch (Exception e) {
            e.printStackTrace();
            throw new HandlerException(ErrorCodeEnum.ER005.getCode(), ErrorCodeEnum.ER005.getMessage(), PathEnum.PROVIDER_PATH.getPath(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
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
            throw new HandlerException(ErrorCodeEnum.ER005.getCode(), ErrorCodeEnum.ER005.getMessage(), PathEnum.PROVIDER_PATH.getPath(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public ProviderResponse updateProvider(String id, ProviderRequest req) {
        try {
            HandlerValidateParams.validateInt(id, ErrorCodeEnum.ER146);
            Provider provider = providerRepository.findProviderByIdAndDeletedAtIsNull(Long.parseLong(id));

            if (provider == null) {
                throw new HandlerException(ErrorCodeEnum.ER126.getCode(), ErrorCodeEnum.ER126.getMessage(), PathEnum.PROVIDER_PATH.getPath(), HttpStatus.BAD_REQUEST);
            }
            provider.setName(req.getName());
            provider.setPhoneNumber(req.getPhoneNumber());
            provider.setAddress(req.getAddress());
            provider.setUpdatedAt(new Date());

            providerRepository.save(provider);

            return new ProviderResponse(provider.getId());
        } catch (HandlerException handlerException) {
            throw new HandlerException(handlerException.getCode(), handlerException.getMessage(), PathEnum.DEVICE_PATH.getPath(), handlerException.getStatus());
        } catch (Exception e) {
            throw new HandlerException(ErrorCodeEnum.ER005.getCode(), ErrorCodeEnum.ER005.getMessage(), PathEnum.PROVIDER_PATH.getPath(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public ProviderResponse getDetailProvider(String id) {
        try {
            HandlerValidateParams.validateInt(id, ErrorCodeEnum.ER146);
            Provider provider = providerRepository.findProviderByIdAndDeletedAtIsNull(Long.parseLong(id));
            if (provider == null) {
                throw new HandlerException(ErrorCodeEnum.ER126.getCode(), ErrorCodeEnum.ER126.getMessage(), PathEnum.PROVIDER_PATH.getPath(), HttpStatus.BAD_REQUEST);
            }

            return ParseUtils.convertProviderToProviderResponse(provider, true);
        } catch (HandlerException handlerException) {
            throw new HandlerException(handlerException.getCode(), handlerException.getMessage(), PathEnum.DEVICE_PATH.getPath(), handlerException.getStatus());
        } catch (Exception e) {
            throw new HandlerException(ErrorCodeEnum.ER005.getCode(), ErrorCodeEnum.ER005.getMessage(), PathEnum.PROVIDER_PATH.getPath(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public ProviderResponse deleteProvider(String id) {
        try {
            HandlerValidateParams.validateInt(id, ErrorCodeEnum.ER146);
            Provider provider = providerRepository.findProviderByIdAndDeletedAtIsNull(Long.parseLong(id));

            if (provider == null) {
                throw new HandlerException(ErrorCodeEnum.ER126.getCode(), ErrorCodeEnum.ER126.getMessage(), PathEnum.PROVIDER_PATH.getPath(), HttpStatus.BAD_REQUEST);
            }
            if (deviceRepository.existsDeviceByProviderIdAndDeletedAtIsNull(Long.parseLong(id))) {
                throw new HandlerException(ErrorCodeEnum.ER147.getCode(), ErrorCodeEnum.ER147.getMessage(), PathEnum.PROVIDER_PATH.getPath(), HttpStatus.BAD_REQUEST);
            }
            provider.setDeletedAt(new Date());

            providerRepository.save(provider);
            return new ProviderResponse(provider.getId());
        } catch (HandlerException handlerException) {
            throw new HandlerException(handlerException.getCode(), handlerException.getMessage(), PathEnum.DEVICE_PATH.getPath(), handlerException.getStatus());
        } catch (Exception e) {
            throw new HandlerException(ErrorCodeEnum.ER005.getCode(), ErrorCodeEnum.ER005.getMessage(), PathEnum.PROVIDER_PATH.getPath(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
