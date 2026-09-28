package fu.de200053;

public enum ResultCode {
    SUCCESS,
    INVALID_INPUT,
    // Đăng ký
    INVALID_USERNAME,
    DUPLICATE_USERNAME,
    INVALID_EMAIL,
    DUPLICATE_EMAIL,
    WEAK_PASSWORD,
    PASSWORD_MISMATCH,
    UNDERAGE,
    INVALID_PHONE,
    // Đăng nhập
    INVALID_CREDENTIALS,
    ACCOUNT_LOCKED,
    ACCOUNT_DISABLED,
    // Đổi / đặt lại mật khẩu
    USER_NOT_FOUND,
    OLD_PASSWORD_INCORRECT,
    SAME_AS_OLD_PASSWORD,
    PASSWORD_REUSED,
    INVALID_TOKEN;

    public boolean isSuccess() {
        return this == SUCCESS;
    }
}
