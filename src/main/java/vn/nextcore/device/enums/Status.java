package vn.nextcore.device.enums;

public enum Status {
    REQUEST_PENDING("pending"),
    REQUEST_APPROVED("approved"),
    REQUEST_REJECTED("rejected"),
    REQUEST_PROGRESS("progress"),
    REQUEST_DONE("done"),
    DEVICE_STOCK("stock"),
    DEVICE_MAINTENANCE("maintenance"),
    DEVICE_ACTIVE("active"),
    ACTION_ALLOCATE("allocate"),
    ACTION_MAINTENANCE("maintenance"),
    DEVICE_SCRAP("scrap"),
    ACTION_RETRIEVE("retrieve");

    private final String status;

    Status(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }
}
