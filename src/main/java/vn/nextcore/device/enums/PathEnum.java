package vn.nextcore.device.enums;

public enum PathEnum {
    LOGIN_PATH("/auth/login"),
    DEVICE_PATH("/api/device"),
    SPECIFICATION_PATH("/api/specification"),
    GROUP_PATH("/api/group"),
    VERIFY_EMAIL_PATH("/api/forgotPassword/verifyMail"),
    REFRESH_TOKEN_PATH("/auth/refreshtoken"),
    REQUEST_PATH("/api/request");


    private final String path;

    PathEnum(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }
}
