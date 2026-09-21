package fu.de200053;

public class InsuranceClaim {
    private final String claimId;
    private double amount;
    private String claimStatus;
    public InsuranceClaim(String id, double claimAmount) {
        if (id == null || id.isEmpty()) {
            throw new IllegalArgumentException("Claim ID cannot be null or empty");
        }
        if (claimAmount <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }
        this.claimId = id;
        this.amount = claimAmount;
        this.claimStatus = "Pending";
    }
    public boolean processClaim(String newStatus) {
        if (newStatus == null) {

            throw new IllegalArgumentException("Status cannot be null");
        }
        if (!"Pending".equals(this.claimStatus)) {
            return false;
        }
        this.claimStatus = newStatus;
        return true;
    }

    public double calculatePayout() {
        if ("Approved".equals(claimStatus)) {
            return amount * 0.85;   // trừ 15% phí
        }
        return 0;
    }

    public void updateClaimAmount(double newAmount) {
        if (newAmount <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }
        this.amount = newAmount;
    }

    public String getClaimId() { return claimId; }
    public double getAmount() { return amount; }
    public String getClaimStatus() { return claimStatus; }

    @Override
    public String toString() {
        return "InsuranceClaim{" +
                "claimId='" + claimId + '\'' +
                ", amount=" + amount +
                ", claimStatus='" + claimStatus + '\'' +
                '}';
    }
}
