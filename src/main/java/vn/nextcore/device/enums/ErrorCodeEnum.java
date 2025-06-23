package vn.nextcore.device.enums;

public enum ErrorCodeEnum {
    ER007("ER007", "Wrong email or password"),
    ER001("ER001", "Email not blank"),
    ER002("ER002", "Password not blank"),
    ER003("ER003", "Email format invalid"),
    ER004("ER004", "The password length >= 8 and <= 50 characters."),
    ER005("ER005", "Internal server error. Please try again later."),
    ER025("ER025", "createBy not exists"),
    ER029("ER029", "File size maximum 10MB"),
    ER030("ER030", "Invalid file type. Only JPG, PNG, JPEG, and GIF are allowed."),
    ER031("ER031", "Upload maximum 5 files"),
    ER032("ER032", "Upload image failed!"),
    ER033("ER033", "specificationName not blank"),
    ER034("ER034", "specificationValue not Blank"),
    ER035("ER035", "dateMaintenance > dateBuy"),
    ER036("ER036", "Specifications is empty!"),
    ER037("ER037", "Sort by dateBuy  must  be 'asc' or 'desc' "),
    ER038("ER038", "Sort by dateMaintenance  must  be 'asc' or 'desc' "),
    ER039("ER039", "groupId must be positive Integer"),
    ER040("ER040", "providerId must be positive Integer"),
    ER041("ER041", "Offset must be Integer"),
    ER042("ER042", "limit must be positive Integer"),
    ER043("ER043", "List device is empty!"),
    ER044("ER044", "Group is empty!"),
    ER045("ER045", "Provider is empty!"),
    ER046("ER046", "dateBuy invalid format dd/MM/yyyy"),
    ER047("ER047", "dateMaintenance invalid format dd/MM/yyyy"),
    ER048("ER048", "dateBuy invalid date"),
    ER049("ER049", "dateMaintenance invalid date"),
    ER050("ER050", "Start Date must <= End Date"),
    ER051("ER051", "Filters contain field {0} not allowed"),
    ER052("ER052", "Filters contain operator not allowed (must be like, eq, gte, lte, between)"),
    ER053("ER053", "Length of filters.values search must > 0 && < 3"),
    ER054("ER054", "Status must be stock, active or maintenance"),
    ER055("ER055", "Param filters invalid format"),
    ER056("ER056", "Searching with between then startDate and endDate cannot be empty"),
    ER057("ER057", "deviceId not found"),
    ER058("ER058", "deviceId must be positive integer"),
    ER059("ER059", "ImageDelete contains id not found"),
    ER060("ER060", "imagesDelete wrong format [\"number\", \"number2\", ...]"),
    ER061("ER061", "specificationsDelete  format [\"number\", \"number2\", ...]"),
    ER062("ER062", "specifications invalid format JSON"),
    ER063("ER063", "specificationsDelete contains id not found"),
    ER100("ER100", "Refresh token invalid"),
    ER101("ER101", "Invalid token authentication"),
    ER102("ER102", "you are not authentication"),
    ER103("ER103", "You do not have permission to access"),
    ER104("ER104", "Invalid JSON format or request body is malformed."),
    ER105("ER105", "Invalid JSON format of specifications"),
    ER106("ER106", "Image not found"),
    ER107("ER107", "Invalid request JSON format"),
    ER108("ER108", "Email not found"),
    ER109("ER109", "The OTP must be a 6-digit numeric code"),
    ER110("ER110", "Invalid OTP for email!"),
    ER111("ER111", "OTP has expired!"),
    ER112("ER112", "password not mapping with repeatPassword"),
    ER113("ER113", "repeatPassword not blank"),
    ER114("ER114", "password length must be > = 8 char"),
    ER135("ER135", "RequestId not exists"),
    ER136("ER136", "DeliveryNoteId not exists"),
    ER123("ER123", "projectId not blank"),
    ER125("ER125", "groupId invalid"),
    ER126("ER126", "providerId not exists"),
    ER127("ER127", "roleId not exists"),
    ER138("ER138", "departmentId not exists"),
    ER139("ER139", "The user still has devices that have not been returned"),
    ER140("ER140", "userId must be positive integer"),
    ER141("ER141", "year not null"),
    ER142("ER142", "year invalid"),
    ER143("ER143", "date param invalid format dd/MM/yyyy"),
    ER144("ER144", "date param invalid"),
    ER145("ER145", "device can't update because not in stock"),
    ER146("ER146", "providerId must be positive integer"),
    ER147("ER147", "provider contain devices"),
    ER148("ER148", "user not exists"),
    ER149("ER149", "id not valid"),
    ER150("ER150", "notification not found"),
    ER151("ER151", "groupId not found"),
    ER152("ER152", "old password not match"),
    ER153("ER153", "departmentId not found"),
    ER154("ER154", "department contain user"),
    ER404("ER404", "Not found"),
    ER403("ER403", "FOR BIDDEN");

    private final String code;

    private final String message;

    ErrorCodeEnum(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public String getMessage(String fieldName) {
        return message.replace("{0}", fieldName);
    }
}
