package model;
// Student account with approval and GPU-minute balances.
public class Student extends User {
    private String email;
    private String phone;
    private boolean approved;
    private int allocatedMinutes;
    private int usedMinutes;
    private int reservedMinutes;

    public Student(String username, String password, String name,
                   String email, String phone) {
        super(username, password, name);
        this.email = email;
        this.phone = phone;
        this.approved = false;
        this.allocatedMinutes = 0;
        this.usedMinutes = 0;
        this.reservedMinutes = 0;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public boolean isApproved() {
        return approved;
    }

    public int getAllocatedMinutes() {
        return allocatedMinutes;
    }

    public int getUsedMinutes() {
        return usedMinutes;
    }

    public int getReservedMinutes() {
        return reservedMinutes;
    }

    public int getAvailableMinutes() {
        return allocatedMinutes - usedMinutes - reservedMinutes;
    }
    
    // An approved student starts with 100 GPU minutes.
    public void approve() {
        if (!approved) {
            approved = true;
            allocatedMinutes = 100;
        }
    }

    public void grantMinutes(int minutes) {
        if (minutes > 0 && approved) {
            allocatedMinutes += minutes;
        }
    }

    public boolean reserveMinutes(int minutes) {
        if (!approved || minutes <= 0 ||
            minutes > getAvailableMinutes()) {
            return false;
        }

        reservedMinutes += minutes;
        return true;
    }

    public void settleMinutes(int reserved, int charged) {
        if (reserved <= 0 || charged < 0 ||
            charged > reserved || reserved > reservedMinutes) {
            throw new IllegalArgumentException(
                "Invalid reservation or charge"
            );
        }

        reservedMinutes -= reserved;
        usedMinutes += charged;
    }


    public void setAllocatedMinutes(int minutes) {
        if (!approved) {
            throw new IllegalStateException(
                "Student must be approved first"
            );
        }

        if (minutes < usedMinutes + reservedMinutes) {
            throw new IllegalArgumentException(
                "Allocation cannot be less than used and reserved minutes"
            );
        }

        allocatedMinutes = minutes;
    }


    @Override
    public String toString() {
        return "Student: " + getName() +
               "\nID: " + getUsername() +
               "\nEmail: " + email +
               "\nApproved: " + approved +
               "\nAllocated: " + allocatedMinutes +
               "\nUsed: " + usedMinutes +
               "\nReserved: " + reservedMinutes +
               "\nAvailable: " + getAvailableMinutes();
    }
}
