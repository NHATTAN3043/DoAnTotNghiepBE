package vn.nextcore.device.enums;

import org.springframework.http.HttpStatus;
import vn.nextcore.device.exception.HandlerException;

public enum Operator {
    EQ, LIKE, GTE, LTE, BETWEEN;

    public static Operator fromString(String operator) {
        switch (operator.toLowerCase()) {
            case "eq": return EQ;
            case "like": return LIKE;
            case "gte": return GTE;
            case "lte": return LTE;
            case "between": return BETWEEN;
            default: throw new HandlerException(ErrorCodeEnum.ER052.getCode(), ErrorCodeEnum.ER052.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}
