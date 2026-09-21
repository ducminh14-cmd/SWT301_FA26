import fu.de200053.InsuranceClaim;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;

class InsuranceClaimTest {

    private InsuranceClaim claim;

    @BeforeEach
    void setUp() {
        // Arrange chung cho mọi test
        claim = new InsuranceClaim("C001", 1000.0);
    }

    @Test
    @DisplayName("Constructor khởi tạo đúng giá trị")
    void constructor_ValidInput_InitializesValues() {
        // Assert
        assertEquals("C001", claim.getClaimId());
        assertEquals(1000.0, claim.getAmount());
        assertEquals("Pending", claim.getClaimStatus());
    }

    @Test
    @DisplayName("Constructor ném exception khi amount không hợp lệ")
    void constructor_InvalidAmount_Throws() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class,
                () -> new InsuranceClaim("C002", -500));
    }

    @Test
    @DisplayName("Constructor ném exception khi claimId null")
    void constructor_NullClaimId_Throws() {
        assertThrows(IllegalArgumentException.class,
                () -> new InsuranceClaim(null, 1000.0));
    }

    @Test
    @DisplayName("processClaim cập nhật status khi đang Pending")
    void processClaim_StatusPending_UpdatesStatus() {
        // Act
        boolean result = claim.processClaim("Approved");

        // Assert
        assertTrue(result);
        assertEquals("Approved", claim.getClaimStatus());
    }

    @Test
    @DisplayName("processClaim trả về false khi đã không còn Pending")
    void processClaim_StatusNotPending_ReturnsFalse() {
        // Arrange
        claim.processClaim("Approved");

        // Act
        boolean result = claim.processClaim("Rejected");

        // Assert
        assertFalse(result);
        assertEquals("Approved", claim.getClaimStatus());
    }

    @Test
    @DisplayName("processClaim ném exception khi truyền null")
    void processClaim_NullInput_Throws() {
        assertThrows(IllegalArgumentException.class,
                () -> claim.processClaim(null));
    }
    @Test
    @DisplayName("calculatePayout trả về 85% khi Approved")
    void calculatePayout_StatusApproved_Returns85Percent() {
        // Arrange
        claim.processClaim("Approved");

        // Act
        double payout = claim.calculatePayout();

        // Assert
        assertEquals(850.0, payout, 0.001);
    }

    @Test
    @DisplayName("calculatePayout trả về 0 khi không phải Approved")
    void calculatePayout_StatusNotApproved_ReturnsZero() {
        assertEquals(0, claim.calculatePayout());
    }

    @Test
    @DisplayName("updateClaimAmount cập nhật thành công")
    void updateClaimAmount_ValidValue_Updates() {
        // Act
        claim.updateClaimAmount(2000.0);

        // Assert
        assertEquals(2000.0, claim.getAmount());
    }

    @Test
    @DisplayName("updateClaimAmount ném exception khi <= 0")
    void updateClaimAmount_Invalid_Throws() {
        assertThrows(IllegalArgumentException.class,
                () -> claim.updateClaimAmount(0));
    }

    @ParameterizedTest(name = "status={0} → payout={1}")
    @CsvSource({
            "Approved, 850.0",
            "Rejected, 0",
            "Pending,  0"
    })
    @DisplayName("Parameterized: calculatePayout cho các trạng thái khác nhau")
    void calculatePayout_VariousStatuses(String status, double
            expectedPayout) {
        // Arrange
        if (!"Pending".equals(status)) {
            claim.processClaim(status);
        }

        // Act
        double payout = claim.calculatePayout();

        // Assert
        assertEquals(expectedPayout, payout, 0.001);
    }
    @Test
    @DisplayName("toString trả về đúng định dạng")
    void toString_ReturnsExpectedFormat() {
// Act
        String output = claim.toString();
// Assert
        assertAll(
                () -> assertTrue(output.contains("InsuranceClaim")),
                () -> assertTrue(output.contains("claimId='C001'")),
                () -> assertTrue(output.contains("amount=1000.0")),
                () -> assertTrue(output.contains("claimStatus='Pending'"))
        );
    }
}
