package vn.nextcore.device.enums;

public enum StatusRequest {
    REQUEST_PENDING("pending"),

    REQUEST_APPROVED("approved"),

    REQUEST_REJECTED("rejected"),
    REQUEST_PROGRESS("progress"),
    REQUEST_DONE("done");

    private final String status;

    StatusRequest(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }
}
