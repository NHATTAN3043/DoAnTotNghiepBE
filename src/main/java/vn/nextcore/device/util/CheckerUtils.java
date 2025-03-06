package vn.nextcore.device.util;

import java.util.List;

public class CheckerUtils {
    public static boolean checkIsNotEmptyValues(List<String> values) {
        return values != null && values.stream()
                .allMatch(value -> value != null && !value.isEmpty());
    }
}
