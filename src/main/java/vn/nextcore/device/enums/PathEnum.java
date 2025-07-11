package vn.nextcore.device.enums;

public enum PathEnum {
    LOGIN_PATH("/auth/login"),
    DEVICE_PATH("/api/device"),
    SPECIFICATION_PATH("/api/specification"),
    GROUP_PATH("/api/group"),
    PROJECT_PATH("/api/project"),
    PROVIDER_PATH("/api/provider"),
    VERIFY_EMAIL_PATH("/api/forgotPassword/verifyMail"),
    REFRESH_TOKEN_PATH("/auth/refreshtoken"),
    DELIVERY_PATH("/auth/delivery"),
    USER_PATH("/api/user"),
    STATISTICS_PATH("/api/statistics"),
    DEPARTMENT_PATH("/api/department"),
    REQUEST_PATH("/api/request");


    private final String path;

    PathEnum(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }
}
