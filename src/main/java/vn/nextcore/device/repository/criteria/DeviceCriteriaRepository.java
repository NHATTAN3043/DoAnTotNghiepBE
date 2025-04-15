package vn.nextcore.device.repository.criteria;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;
import vn.nextcore.device.dto.req.FilterRequest;
import vn.nextcore.device.dto.resp.DeviceResponse;
import vn.nextcore.device.dto.resp.ListDeviceResponse;
import vn.nextcore.device.entity.*;
import vn.nextcore.device.enums.ErrorCodeEnum;
import vn.nextcore.device.enums.Operator;
import vn.nextcore.device.enums.PathEnum;
import vn.nextcore.device.exception.HandlerException;
import vn.nextcore.device.util.CheckerUtils;
import vn.nextcore.device.util.CriteriaUtils;
import vn.nextcore.device.util.ParseUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

@Repository
public class DeviceCriteriaRepository implements IDeviceCriteriaRepository {
    private final String PROVIDER = "provider";
    private final String GROUP = "group";
    private final String IMAGES = "images";
    private final String SPECIFICATIONS = "specifications";
    private final String STATUS = "status";
    private final String ID = "id";
    private final String DATE_BUY = "dateBuy";
    private final String DATE_MAINTENANCE = "dateMaintenance";
    private final String ASC = "ASC";
    private final String DESC = "DESC";
    private final String PROVIDER_ID = "providerId";
    private final String GROUP_ID = "groupId";
    private final String DELETED_AT = "deletedAt";
    private final String NAME = "name";

    @PersistenceContext
    private EntityManager entityManager;

    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

    @Override
    public ListDeviceResponse listDeviceCriteria(String status, String sortDateBuy, String sortDateMaintenance, Integer offset, Integer limit, List<FilterRequest> filters) {
        ListDeviceResponse response = new ListDeviceResponse();
        try {
            CriteriaBuilder cb = entityManager.getCriteriaBuilder();
            CriteriaQuery<Device> createQuery = cb.createQuery(Device.class);
            Root<Device> deviceRoot = createQuery.from(Device.class);

            // join Provider, Group, Image
            Join<Device, Provider> providerJoin = deviceRoot.join(PROVIDER, JoinType.LEFT);
            Join<Device, Group> groupJoin = deviceRoot.join(GROUP, JoinType.LEFT);
            Join<Device, Image> imageJoin = deviceRoot.join(IMAGES, JoinType.LEFT);
            Join<Device, Specification> specificationJoin = deviceRoot.join(SPECIFICATIONS, JoinType.LEFT);

            List<Predicate> predicates = new ArrayList<>();
            // get device with deletedAt is null
            predicates.add(cb.isNull(deviceRoot.get(DELETED_AT)));
            // get by status
            if (status != null && !status.isEmpty()) {
                predicates.add(cb.equal(cb.lower(deviceRoot.get(STATUS)), status.toLowerCase()));
            }

            // handle search by filters
            for (FilterRequest filter : filters) {
                if (filter.getValues().size() >= 1 && CheckerUtils.checkIsNotEmptyValues(filter.getValues())) {
                    Predicate predicate = handleFilterRequest(cb, deviceRoot, groupJoin, providerJoin, specificationJoin, filter, dateFormat);
                    if (predicate != null) {
                        predicates.add(predicate);
                    }
                }
            }

            // using query
            createQuery.where(predicates.toArray(new Predicate[0]));

            // group by deviceId
            createQuery.groupBy(deviceRoot.get(ID));

            // Create List order by
            List<Order> orderList = new ArrayList<>();
            addOrderCondition(cb, deviceRoot, DATE_BUY, sortDateBuy, orderList);
            addOrderCondition(cb, deviceRoot, DATE_MAINTENANCE, sortDateMaintenance, orderList);

            // Default sort by deviceId if no sorting is specified
            if (orderList.isEmpty()) {
                createQuery.orderBy(cb.asc(cb.min(deviceRoot.get(ID))));
            } else {
                createQuery.orderBy(orderList);
            }

            TypedQuery<Device> query = entityManager.createQuery(createQuery);
            List<Device> devicesBeforePag = query.getResultList();

            // pagination
            query.setFirstResult(offset);
            query.setMaxResults(limit);

            // get result list devices
            List<Device> devices = query.getResultList();

            // convert to DeviceResponse
            List<DeviceResponse> result = new ArrayList<>();
            for (Device device : devices) {
                DeviceResponse deviceResponse = ParseUtils.convertDeviceToDeviceRes(device, "list");
                result.add(deviceResponse);
            }

            response.setTotalRecords(devicesBeforePag.size());
            response.setOffset(offset);
            response.setLimit(limit);
            response.calculatePagination();
            response.setDevices(result);
            return response;
        } catch (HandlerException handlerException) {
            throw new HandlerException(handlerException.getCode(), handlerException.getMessage(), PathEnum.DEVICE_PATH.getPath(), handlerException.getStatus());
        } catch (Exception e) {
            e.printStackTrace();
            throw new HandlerException(ErrorCodeEnum.ER005.getCode(), ErrorCodeEnum.ER005.getMessage(), PathEnum.DEVICE_PATH.getPath(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    // add list order by
    private void addOrderCondition(CriteriaBuilder cb, Root<?> root, String sortField, String sortDirection, List<Order> orderList) {
        if (sortDirection != null && !sortDirection.isEmpty()) {
            if (sortDirection.equalsIgnoreCase(ASC)) {
                orderList.add(cb.asc(cb.min(root.get(sortField))));
            } else if (sortDirection.equalsIgnoreCase(DESC)) {
                orderList.add(cb.desc(cb.min(root.get(sortField))));
            }
        }
    }

    private Predicate handleFilterRequest(CriteriaBuilder cb, Root<Device> root, Join<Device, ?> groupJoin,
                                          Join<Device, ?> providerJoin, Join<Device, ?> specificationJoin, FilterRequest filter, SimpleDateFormat dateFormat) throws ParseException, HandlerException {
        Operator operator = Operator.fromString(filter.getOperator());
        String field = filter.getField();
        List<String> values = filter.getValues();

        if (GROUP_ID.equals(field)) {
            return operator == Operator.EQ ? cb.equal(groupJoin.get(ID), values.get(0)) : null;
        }

        if (PROVIDER_ID.equals(field)) {
            return operator == Operator.EQ ? cb.equal(providerJoin.get(ID), values.get(0)) : null;
        }

        if (NAME.equals(field)) {
            String keyword = values.get(0).toLowerCase();
            String[] parts = keyword.split("\\s+");

            List<Predicate> andPredicates = new ArrayList<>();

            for (String part : parts) {
                String likePattern = "%" + part + "%";

                Expression<String> unaccentedPart = cb.function("unaccent", String.class, cb.literal(likePattern));

                Expression<String> deviceName = cb.function("unaccent", String.class, cb.lower(root.get("name")));
                Expression<String> specName = cb.function("unaccent", String.class, cb.lower(specificationJoin.get("name")));
                Expression<String> specValue = cb.function("unaccent", String.class, cb.lower(specificationJoin.get("value")));

                Predicate p1 = cb.like(deviceName, unaccentedPart);
                Predicate p2 = cb.like(specName, unaccentedPart);
                Predicate p3 = cb.like(specValue, unaccentedPart);

                andPredicates.add(cb.or(p1, p2, p3));
            }

            return cb.and(andPredicates.toArray(new Predicate[0]));
        }

        return CriteriaUtils.createQueryByOperator(cb, root, field, operator, values, dateFormat);
    }
}
