
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.stream.Stream;

import fu.de200053.Account;
import fu.de200053.AccountService;
import fu.de200053.AccountStatus;
import fu.de200053.ResultCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

class AccountServiceTest {

    static final String USER = "alice_01";
    static final String EMAIL = "alice@example.com";
    static final String PASS = "Secret@123";
    static final String WRONG = "Wrong@123";
    static final String PHONE = "0912345678";
    static final LocalDate DOB = LocalDate.of(1995, 5, 15);   // luôn >= 18 tuổi, không phụ thuộc thời gian

    AccountService service;

    @BeforeEach
    void setUp() {
        service = new AccountService();          // mỗi test một service mới -> test độc lập
    }

    /** Đăng ký user mẫu, bắt buộc thành công. */
    void registerSampleUser() {
        assertEquals(ResultCode.SUCCESS, service.register(USER, EMAIL, PASS, PASS, DOB, PHONE));
    }

    Account account() {
        return service.findByUsername(USER).orElseThrow();
    }

    // ============================ DỮ LIỆU CHO REGISTER ============================
    // (@MethodSource trong lớp @Nested phải trỏ tới method static ở lớp ngoài)

    static Stream<Arguments> invalidRegisterInputs() {
        LocalDate future = LocalDate.now().plusDays(1);
        LocalDate seventeen = LocalDate.now().minusYears(17);
        return Stream.of(
                // --- mỗi quy tắc một dòng ---
                Arguments.of("REG-01 confirm rỗng", USER, EMAIL, PASS, "", DOB, PHONE, ResultCode.INVALID_INPUT),
                Arguments.of("REG-01 dob null", USER, EMAIL, PASS, PASS, null, PHONE, ResultCode.INVALID_INPUT),
                Arguments.of("REG-01 dob ở tương lai", USER, EMAIL, PASS, PASS, future, PHONE, ResultCode.INVALID_INPUT),
                Arguments.of("REG-02 username bắt đầu bằng số", "1alice", EMAIL, PASS, PASS, DOB, PHONE, ResultCode.INVALID_USERNAME),
                Arguments.of("REG-02 username quá ngắn", "ab_1", EMAIL, PASS, PASS, DOB, PHONE, ResultCode.INVALID_USERNAME),
                Arguments.of("REG-02 username có dấu gạch ngang", "alice-01", EMAIL, PASS, PASS, DOB, PHONE, ResultCode.INVALID_USERNAME),
                Arguments.of("REG-04 email thiếu @", USER, "bad", PASS, PASS, DOB, PHONE, ResultCode.INVALID_EMAIL),
                Arguments.of("REG-04 email thiếu TLD", USER, "alice@example", PASS, PASS, DOB, PHONE, ResultCode.INVALID_EMAIL),
                Arguments.of("REG-06 mật khẩu yếu", USER, EMAIL, "weak", "weak", DOB, PHONE, ResultCode.WEAK_PASSWORD),
                Arguments.of("REG-06 mật khẩu chứa username", USER, EMAIL, "Xalice_01@1", "Xalice_01@1", DOB, PHONE, ResultCode.WEAK_PASSWORD),
                Arguments.of("REG-07 confirm lệch", USER, EMAIL, PASS, "Secret@124", DOB, PHONE, ResultCode.PASSWORD_MISMATCH),
                Arguments.of("REG-08 chưa đủ 18 tuổi", USER, EMAIL, PASS, PASS, seventeen, PHONE, ResultCode.UNDERAGE),
                Arguments.of("REG-09 phone sai đầu số", USER, EMAIL, PASS, PASS, DOB, "0212345678", ResultCode.INVALID_PHONE),
                Arguments.of("REG-09 phone toàn khoảng trắng", USER, EMAIL, PASS, PASS, DOB, "   ", ResultCode.INVALID_PHONE),
                Arguments.of("REG-09 phone thiếu số", USER, EMAIL, PASS, PASS, DOB, "091234567", ResultCode.INVALID_PHONE),
                // --- thứ tự ưu tiên: vi phạm nhiều quy tắc cùng lúc ---
                Arguments.of("ƯU TIÊN: username sai + email sai", "1alice", "bad", PASS, PASS, DOB, PHONE, ResultCode.INVALID_USERNAME),
                Arguments.of("ƯU TIÊN: email sai + mk yếu", USER, "bad", "weak", "weak", DOB, PHONE, ResultCode.INVALID_EMAIL),
                Arguments.of("ƯU TIÊN: mk yếu + confirm lệch", USER, EMAIL, "weak", "x", DOB, PHONE, ResultCode.WEAK_PASSWORD),
                Arguments.of("ƯU TIÊN: confirm lệch + chưa đủ tuổi", USER, EMAIL, PASS, "Secret@124", seventeen, PHONE, ResultCode.PASSWORD_MISMATCH),
                Arguments.of("ƯU TIÊN: chưa đủ tuổi + phone sai", USER, EMAIL, PASS, PASS, seventeen, "0212345678", ResultCode.UNDERAGE),
                Arguments.of("ƯU TIÊN: input rỗng + username sai", "1alice", EMAIL, PASS, "", DOB, PHONE, ResultCode.INVALID_INPUT));
    }

    // ================================ REGISTER ================================

    @Nested
    class Register {

        @Test
        void register_ValidInput_ReturnsSuccessAndStoresActiveAccount() {
            ResultCode result = service.register(USER, "Alice@Example.COM", PASS, PASS, DOB, PHONE);

            assertEquals(ResultCode.SUCCESS, result);
            Account acc = account();
            assertEquals(AccountStatus.ACTIVE, acc.getStatus());
            assertEquals(0, acc.getFailedAttempts());
            assertFalse(acc.isLocked());
            assertFalse(service.isLocked(USER));
            assertNotEquals(PASS, acc.getCurrentPasswordHash());
            assertEquals(64, acc.getCurrentPasswordHash().length());
            assertEquals("alice@example.com", acc.getEmail());        // email lưu lowercase
            assertEquals(USER, acc.getUsername());
            assertEquals(DOB, acc.getDateOfBirth());
            assertEquals(PHONE, acc.getPhone());
            assertEquals(1, acc.getPasswordHistory().size());
        }

        @Test
        void register_TwoUsersSamePassword_UseDifferentSaltsAndHashes() {
            assertEquals(ResultCode.SUCCESS, service.register("alice_01", "a@example.com", PASS, PASS, DOB, null));
            assertEquals(ResultCode.SUCCESS, service.register("bob_02", "b@example.com", PASS, PASS, DOB, null));

            Account a = service.findByUsername("alice_01").orElseThrow();
            Account b = service.findByUsername("bob_02").orElseThrow();
            assertNotEquals(a.getSalt(), b.getSalt());
            assertNotEquals(a.getCurrentPasswordHash(), b.getCurrentPasswordHash());
        }

        @ParameterizedTest(name = "[{index}] {0}")
        @MethodSource("lab2.account.AccountServiceTest#invalidRegisterInputs")
        void register_InvalidInput_ReturnsExpectedCode(String desc, String u, String e, String p, String c,
                                                       LocalDate dob, String phone, ResultCode expected) {
            assertEquals(expected, service.register(u, e, p, c, dob, phone));
            assertTrue(service.findByUsername(u).isEmpty());          // thất bại -> không tạo tài khoản
        }

        @ParameterizedTest(name = "[{index}] username = \"{0}\"")
        @NullAndEmptySource
        @ValueSource(strings = {" "})
        void register_BlankUsername_ReturnsInvalidInput(String username) {
            assertEquals(ResultCode.INVALID_INPUT, service.register(username, EMAIL, PASS, PASS, DOB, PHONE));
            assertTrue(service.findByUsername(username).isEmpty());
            assertTrue(service.findByUsername(USER).isEmpty());
        }

        @ParameterizedTest(name = "[{index}] email = \"{0}\"")
        @NullAndEmptySource
        @ValueSource(strings = {" "})
        void register_BlankEmail_ReturnsInvalidInput(String email) {
            assertEquals(ResultCode.INVALID_INPUT, service.register(USER, email, PASS, PASS, DOB, PHONE));
            assertTrue(service.findByUsername(USER).isEmpty());
        }

        @ParameterizedTest(name = "[{index}] password = \"{0}\"")
        @NullAndEmptySource
        @ValueSource(strings = {" "})
        void register_BlankPassword_ReturnsInvalidInput(String password) {
            assertEquals(ResultCode.INVALID_INPUT, service.register(USER, EMAIL, password, PASS, DOB, PHONE));
            assertTrue(service.findByUsername(USER).isEmpty());
        }

        @ParameterizedTest(name = "[{index}] phone = \"{0}\" (tùy chọn) -> SUCCESS")
        @NullAndEmptySource
        @ValueSource(strings = {"0312345678", "0512345678", "0712345678", "0812345678", "0912345678"})
        void register_OptionalOrValidPhone_ReturnsSuccess(String phone) {
            assertEquals(ResultCode.SUCCESS, service.register(USER, EMAIL, PASS, PASS, DOB, phone));
            assertTrue(service.findByUsername(USER).isPresent());
        }

        @ParameterizedTest(name = "[{index}] trùng username: {0}")
        @ValueSource(strings = {"alice_01", "ALICE_01", "Alice_01"})
        void register_DuplicateUsernameAnyCase_ReturnsDuplicateUsername(String username) {
            registerSampleUser();

            ResultCode result = service.register(username, "other@example.com", PASS, PASS, DOB, null);

            assertEquals(ResultCode.DUPLICATE_USERNAME, result);
            assertTrue(service.findByUsername("other@example.com").isEmpty());
            assertEquals(EMAIL, account().getEmail());                // tài khoản gốc không bị ghi đè
        }

        @ParameterizedTest(name = "[{index}] trùng email: {0}")
        @ValueSource(strings = {"alice@example.com", "ALICE@EXAMPLE.COM", "Alice@Example.Com"})
        void register_DuplicateEmailAnyCase_ReturnsDuplicateEmail(String email) {
            registerSampleUser();

            ResultCode result = service.register("bob_02", email, PASS, PASS, DOB, null);

            assertEquals(ResultCode.DUPLICATE_EMAIL, result);
            assertTrue(service.findByUsername("bob_02").isEmpty());   // thất bại -> không tạo tài khoản
        }

        @Test
        void register_DuplicateUsernameAndEmail_UsernameCheckedFirst() {
            registerSampleUser();

            assertEquals(ResultCode.DUPLICATE_USERNAME,
                    service.register(USER, EMAIL, PASS, PASS, DOB, PHONE));   // ƯU TIÊN BR-REG-03 trước 05
        }

        @Test
        void register_InvalidPhoneAndDuplicateUsername_PhoneCheckedFirst() {
            registerSampleUser();

            assertEquals(ResultCode.INVALID_PHONE,
                    service.register(USER, "other@example.com", PASS, PASS, DOB, "0212345678"));   // 09 trước 03
        }

        @ParameterizedTest(name = "[{index}] today - {0} năm + {1} ngày -> {2}")
        @CsvSource({
                "18, 0, SUCCESS",       // đúng sinh nhật 18
                "18, 1, UNDERAGE",      // 18 tuổi trừ 1 ngày
                "18, -1, SUCCESS",      // qua sinh nhật 18 một ngày
                "17, 0, UNDERAGE",
                "19, 0, SUCCESS",
                "0, 0, UNDERAGE",       // sinh hôm nay
                "0, 1, INVALID_INPUT"   // sinh ngày mai (tương lai)
        })
        void register_AgeBoundary_ReturnsExpected(int yearsAgo, int plusDays, ResultCode expected) {
            LocalDate dob = LocalDate.now().minusYears(yearsAgo).plusDays(plusDays);

            assertEquals(expected, service.register(USER, EMAIL, PASS, PASS, dob, null));
            assertEquals(expected == ResultCode.SUCCESS, service.findByUsername(USER).isPresent());
        }

        @Test
        void register_AfterFailure_SameUsernameAndEmailCanRegisterAgain() {
            assertEquals(ResultCode.WEAK_PASSWORD, service.register(USER, EMAIL, "weak", "weak", DOB, PHONE));

            assertEquals(ResultCode.SUCCESS, service.register(USER, EMAIL, PASS, PASS, DOB, PHONE));
        }
    }
}
