package fu.de200053;

import java.time.LocalDate;
import java.time.Period;
import java.util.Locale;
import java.util.regex.Pattern;

/** Các hàm kiểm tra hợp lệ (thuần, static). Tham số null luôn trả false, không ném exception. */
public final class AccountValidator {

    private static final Pattern USERNAME = Pattern.compile("^[A-Za-z][A-Za-z0-9_]{4,19}$");
    private static final Pattern EMAIL =
            Pattern.compile("^[A-Za-z0-9._%+-]+@(?:[A-Za-z0-9-]+\\.)+[A-Za-z]{2,}$");
    private static final Pattern PHONE = Pattern.compile("^0[35789]\\d{8}$");
    private static final String SPECIAL_CHARS = "!@#$%^&*()_+-=";
    private static final int EMAIL_MAX_LENGTH = 100;
    private static final int PASSWORD_MIN_LENGTH = 8;
    private static final int PASSWORD_MAX_LENGTH = 32;

    private AccountValidator() {
    }

    /** BR-REG-02: dài 5-20, bắt đầu bằng chữ cái, chỉ gồm [A-Za-z0-9_]. */
    public static boolean isValidUsername(String username) {
        return username != null && USERNAME.matcher(username).matches();
    }

    /** BR-REG-04: local@domain.tld, TLD >= 2 chữ cái, nhãn domain không rỗng, dài <= 100. */
    public static boolean isValidEmail(String email) {
        return email != null
                && email.length() <= EMAIL_MAX_LENGTH
                && EMAIL.matcher(email).matches();
    }

    /**
     * BR-REG-06: dài 8-32, đủ 4 nhóm ký tự, chỉ ký tự cho phép,
     * không chứa username (bỏ qua nếu username null/blank).
     */
    public static boolean isValidPassword(String password, String username) {
        if (password == null
                || password.length() < PASSWORD_MIN_LENGTH
                || password.length() > PASSWORD_MAX_LENGTH) {
            return false;
        }
        boolean upper = false, lower = false, digit = false, special = false;
        for (char c : password.toCharArray()) {
            if (c >= 'A' && c <= 'Z') upper = true;
            else if (c >= 'a' && c <= 'z') lower = true;
            else if (c >= '0' && c <= '9') digit = true;
            else if (SPECIAL_CHARS.indexOf(c) >= 0) special = true;
            else return false;                       // khoảng trắng / ký tự lạ
        }
        if (!(upper && lower && digit && special)) {
            return false;
        }
        if (username != null && !username.isBlank()) {
            return !password.toLowerCase(Locale.ROOT).contains(username.toLowerCase(Locale.ROOT));
        }
        return true;
    }

    /** Chỉ kiểm tra định dạng: 0[35789] + 8 chữ số. (Phone tùy chọn do register() xử lý.) */
    public static boolean isValidPhone(String phone) {
        return phone != null && PHONE.matcher(phone).matches();
    }

    /** Số năm tròn giữa dob và today. Hàm thuần; tham số null trả 0 (không ném NPE). */
    public static int calculateAge(LocalDate dob, LocalDate today) {
        if (dob == null || today == null) {
            return 0;
        }
        return Period.between(dob, today).getYears();
    }
}