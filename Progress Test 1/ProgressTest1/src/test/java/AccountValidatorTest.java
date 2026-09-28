
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.stream.Stream;

import fu.de200053.AccountValidator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

@DisplayName("AccountValidator")
class AccountValidatorTest {

    // ---------- Username (BR-REG-02) ----------
    @Nested
    @DisplayName("isValidUsername")
    class Username {

        @ParameterizedTest(name = "[{index}] \"{0}\" hợp lệ")
        @ValueSource(strings = {"alice", "Alice_01", "Z____", "bob_the_builder", "abcdefghij0123456789"})
        void isValidUsername_ValidValues_ReturnsTrue(String username) {
            assertTrue(AccountValidator.isValidUsername(username));
        }

        @ParameterizedTest(name = "[{index}] \"{0}\" không hợp lệ")
        @ValueSource(strings = {"ab_1", "1alice", "_alice", "ali ce", "alice!", "alice-01", "álice", "aaaaaaaaaaaaaaaaaaaaa"})
        void isValidUsername_InvalidValues_ReturnsFalse(String username) {
            assertFalse(AccountValidator.isValidUsername(username));
        }

        @ParameterizedTest(name = "[{index}] null/rỗng/blank: \"{0}\"")
        @NullAndEmptySource
        @ValueSource(strings = {" ", "     "})
        void isValidUsername_NullEmptyBlank_ReturnsFalse(String username) {
            assertFalse(AccountValidator.isValidUsername(username));
        }

        @ParameterizedTest(name = "[{index}] độ dài {0} -> {1}")
        @MethodSource("lab2.account.AccountValidatorTest#usernameLengths")
        void isValidUsername_BoundaryLength(int length, boolean expected) {
            String username = "a".repeat(length);
            assertEquals(expected, AccountValidator.isValidUsername(username));
        }
    }

    static Stream<Arguments> usernameLengths() {
        return Stream.of(
                Arguments.of(4, false), Arguments.of(5, true), Arguments.of(6, true),
                Arguments.of(19, true), Arguments.of(20, true), Arguments.of(21, false));
    }

    // ---------- Email (BR-REG-04) ----------
    @Nested
    @DisplayName("isValidEmail")
    class Email {

        @ParameterizedTest(name = "[{index}] {0} -> {1}")
        @CsvSource({
                "alice@example.com,       true",
                "a.b+tag@mail.fpt.edu.vn, true",
                "ALICE@EXAMPLE.COM,       true",
                "alice@example.c,         false",
                "alice@example,           false",
                "alice.example.com,       false",
                "@example.com,            false",
                "alice@.com,              false",
                "alice@example..com,      false",
                "alice@exa mple.com,      false",
                "alice@example.c0m,       false"
        })
        void isValidEmail_Partitions(String email, boolean expected) {
            assertEquals(expected, AccountValidator.isValidEmail(email));
        }

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {"   "})
        void isValidEmail_NullEmptyBlank_ReturnsFalse(String email) {
            assertFalse(AccountValidator.isValidEmail(email));
        }

        @ParameterizedTest(name = "[{index}] độ dài {0} -> {1}")
        @CsvSource({"99, true", "100, true", "101, false"})
        void isValidEmail_BoundaryLength(int totalLength, boolean expected) {
            String suffix = "@example.com"; // 12 ký tự
            String email = "a".repeat(totalLength - suffix.length()) + suffix;
            assertEquals(totalLength, email.length());
            assertEquals(expected, AccountValidator.isValidEmail(email));
        }
    }

    // ---------- Password (BR-REG-06) ----------
    @Nested
    @DisplayName("isValidPassword")
    class Password {

        @ParameterizedTest(name = "[{index}] {3}")
        @CsvSource(delimiter = '|', value = {
                "Secret@123   | alice_01 | true  | hợp lệ đủ 4 nhóm",
                "Abcdef1=     | alice_01 | true  | ký tự đặc biệt '='",
                "secret@123   | alice_01 | false | thiếu chữ hoa",
                "SECRET@123   | alice_01 | false | thiếu chữ thường",
                "Secret@abc   | alice_01 | false | thiếu chữ số",
                "Secret1234   | alice_01 | false | thiếu ký tự đặc biệt",
                "'Secret @123'| alice_01 | false | chứa khoảng trắng",
                "Secret@123~  | alice_01 | false | ký tự ngoài tập cho phép",
                "Xalice_01@1  | alice_01 | false | chứa username",
                "XALICE_01@1a | alice_01 | false | chứa username khác hoa/thường",
                "Xalice_01@1  |          | true  | username null -> bỏ qua điều kiện"
        })
        void isValidPassword_Partitions(String password, String username, boolean expected, String desc) {
            assertEquals(expected, AccountValidator.isValidPassword(password, username));
        }

        @ParameterizedTest(name = "[{index}] độ dài {0} -> {1}")
        @CsvSource({"7, false", "8, true", "9, true", "31, true", "32, true", "33, false"})
        void isValidPassword_BoundaryLength(int length, boolean expected) {
            String password = "Aa1!" + "b".repeat(length - 4);
            assertEquals(expected, AccountValidator.isValidPassword(password, null));
        }

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {"        "})
        void isValidPassword_NullEmptyBlank_ReturnsFalse(String password) {
            assertFalse(AccountValidator.isValidPassword(password, "alice_01"));
        }
    }

    // ---------- Phone (BR-REG-09) ----------
    @Nested
    @DisplayName("isValidPhone")
    class Phone {

        @ParameterizedTest
        @ValueSource(strings = {"0312345678", "0512345678", "0712345678", "0812345678", "0912345678"})
        void isValidPhone_ValidPrefixes_ReturnsTrue(String phone) {
            assertTrue(AccountValidator.isValidPhone(phone));
        }

        @ParameterizedTest
        @ValueSource(strings = {"0112345678", "0412345678", "0612345678", "091234567", "09123456789",
                "091234567a", "+84912345678", "9123456789", " 0912345678"})
        void isValidPhone_InvalidValues_ReturnsFalse(String phone) {
            assertFalse(AccountValidator.isValidPhone(phone));
        }

        @ParameterizedTest
        @NullAndEmptySource
        void isValidPhone_NullOrEmpty_ReturnsFalse(String phone) {
            // validator chỉ kiểm định dạng; tính "tùy chọn" do AccountService xử lý
            assertFalse(AccountValidator.isValidPhone(phone));
        }
    }

    // ---------- Age (BR-REG-08) ----------
    @ParameterizedTest(name = "[{index}] sinh {0}, hôm nay {1} -> {2} tuổi")
    @CsvSource({
            "2008-09-28, 2026-09-28, 18",   // đúng sinh nhật 18
            "2008-09-29, 2026-09-28, 17",   // 18 tuổi trừ 1 ngày
            "2008-09-27, 2026-09-28, 18",
            "2008-02-29, 2026-02-28, 17",   // năm nhuận
            "2008-02-29, 2026-03-01, 18",
            "2026-09-28, 2026-09-28, 0"
    })
    void calculateAge_Boundaries(LocalDate dob, LocalDate today, int expected) {
        assertEquals(expected, AccountValidator.calculateAge(dob, today));
    }
}