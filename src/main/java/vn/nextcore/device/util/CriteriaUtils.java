package vn.nextcore.device.util;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.http.HttpStatus;
import vn.nextcore.device.enums.ErrorCodeEnum;
import vn.nextcore.device.enums.Operator;
import vn.nextcore.device.exception.HandlerException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class CriteriaUtils {
    public static Predicate createQueryByOperator(CriteriaBuilder cb, Root<?> root, String field, Operator operator, List<String> values, SimpleDateFormat dateFormat) throws ParseException, HandlerException {
        switch (operator) {
            case LIKE:
                Expression<String> unaccentedField = cb.function("unaccent", String.class, cb.lower(root.get(field)));
                Expression<String> unaccentedValue = cb.function("unaccent", String.class, cb.literal("%" + values.get(0).toLowerCase() + "%"));
                return cb.like(unaccentedField, unaccentedValue);
//                return cb.like(cb.lower(root.get(field)), "%" + values.get(0).toLowerCase() + "%");
            case EQ:
                Object parsedValue = ParseUtils.parseValue(field, values.get(0), dateFormat);
                if (parsedValue instanceof Date) {
                    Expression<Date> truncatedField = cb.function("date_trunc", Date.class, cb.literal("day"), root.get(field));
                    return cb.equal(truncatedField, parsedValue);
                } else {
                    return cb.equal(root.get(field), parsedValue);
                }
            case GTE:
                return cb.greaterThanOrEqualTo(root.get(field), (Comparable) ParseUtils.parseValue(field, values.get(0), dateFormat));
            case LTE:
                return cb.lessThanOrEqualTo(root.get(field), (Comparable) ParseUtils.parseValue(field, values.get(0), dateFormat));
            case BETWEEN:
                if (values.size() == 2) {
                    return cb.between(root.get(field),
                            (Comparable) ParseUtils.parseValue(field, values.get(0), dateFormat),
                            (Comparable) ParseUtils.parseValue(field, values.get(1), dateFormat));
                } else {
                    throw new HandlerException(ErrorCodeEnum.ER056.getCode(), ErrorCodeEnum.ER056.getMessage(), HttpStatus.BAD_REQUEST);
                }
            default:
                throw new HandlerException(ErrorCodeEnum.ER052.getCode(), ErrorCodeEnum.ER052.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}
