package vn.nextcore.device.repository.criteria.request;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;
import vn.nextcore.device.dto.req.FilterRequest;
import vn.nextcore.device.dto.resp.ListRequestResponse;
import vn.nextcore.device.dto.resp.ReqResponse;
import vn.nextcore.device.entity.Request;
import vn.nextcore.device.entity.User;
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
public class RequestCriteriaRepository implements IRequestCriteriaRepository{

    private final String ASC = "ASC";
    private final String DESC = "DESC";
    @PersistenceContext
    private EntityManager entityManager;

    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

    @Override
    public ListRequestResponse listRequestsCriteria(String title, String status, String exceptStatus, String type, Long createdBy, String sortCreatedDate, String sortApprovedDate, Integer offset, Integer limit, List<FilterRequest> dateFilters) {
        ListRequestResponse response = new ListRequestResponse();
        try {
            CriteriaBuilder cb = entityManager.getCriteriaBuilder();
            CriteriaQuery<Request> createQuery = cb.createQuery(Request.class);
            Root<Request> requestRoot = createQuery.from(Request.class);

            Join<Request, User> userJoin = requestRoot.join("createdBy", JoinType.LEFT);

            List<Predicate> predicates = new ArrayList<>();

            if (title != null) {
            Expression<String> unaccentedField = cb.function("unaccent", String.class, cb.lower(requestRoot.get("title")));
            Expression<String> unaccentedValue = cb.function("unaccent", String.class, cb.literal("%" + title.toLowerCase() + "%"));
            predicates.add(cb.like(unaccentedField, unaccentedValue));
            }

            if (status != null) {
                predicates.add(cb.equal(cb.lower(requestRoot.get("status")), status.toLowerCase()));
            }

            if (exceptStatus != null) {
                predicates.add(cb.notEqual(cb.lower(requestRoot.get("status")), exceptStatus.toLowerCase()));
            }

            if (type != null) {
                predicates.add(cb.equal(cb.lower(requestRoot.get("requestType")), type.toLowerCase()));
            }

            if (createdBy != null) {
                predicates.add(cb.equal(userJoin.get("id"), createdBy));
            }

            for (FilterRequest filter : dateFilters) {
                if (filter.getValues().size() >= 1 && CheckerUtils.checkIsNotEmptyValues(filter.getValues())) {
                    Predicate predicate = handleFilterRequest(cb, requestRoot, filter, dateFormat);
                    if (predicate != null) {
                        predicates.add(predicate);
                    }
                }
            }

            // using query
            createQuery.where(predicates.toArray(new Predicate[0]));

            // group by deviceId
            createQuery.groupBy(requestRoot.get("id"));

            // Create List order by
            List<Order> orderList = new ArrayList<>();
            if (sortCreatedDate == null) {
                addOrderCondition(cb, requestRoot, "createdDate", "desc", orderList);
            } else {
                addOrderCondition(cb, requestRoot, "createdDate", sortCreatedDate, orderList);
            }
            if (sortApprovedDate != null) {
                addOrderCondition(cb, requestRoot, "approvedDate", sortApprovedDate, orderList);
            }

            // Default sort by deviceId if no sorting is specified
            if (orderList.isEmpty()) {
                createQuery.orderBy(cb.asc(cb.min(requestRoot.get("id"))));
            } else {
                createQuery.orderBy(orderList);
            }

            TypedQuery<Request> query = entityManager.createQuery(createQuery);
            List<Request> requestsBeforePag = query.getResultList();

            // pagination
            query.setFirstResult(offset);
            query.setMaxResults(limit);

            // get result list request
            List<Request> requests = query.getResultList();

            // convert to DeviceResponse
            List<ReqResponse> result = new ArrayList<>();

            for (Request request : requests) {
                ReqResponse reqResponse = ParseUtils.convertRequestToReqResponse(request, null);
                result.add(reqResponse);
            }

            response.setTotalRecords(requestsBeforePag.size());
            response.setOffset(offset);
            response.setLimit(limit);
            response.calculatePagination();
            response.setRequest(result);
            return response;
        } catch (Exception e) {
            e.printStackTrace();
            throw new HandlerException(ErrorCodeEnum.ER005.getCode(), ErrorCodeEnum.ER005.getMessage(), PathEnum.REQUEST_PATH.getPath(), HttpStatus.INTERNAL_SERVER_ERROR);
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

    private Predicate handleFilterRequest(CriteriaBuilder cb, Root<Request> root, FilterRequest filter, SimpleDateFormat dateFormat) throws ParseException, HandlerException {
        Operator operator = Operator.fromString(filter.getOperator());
        String field = filter.getField();
        List<String> values = filter.getValues();

        return CriteriaUtils.createQueryByOperator(cb, root, field, operator, values, dateFormat);
    }
}
