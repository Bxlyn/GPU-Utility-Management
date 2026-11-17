public class Student extends User{
    private String email;
    private String phone;
    private boolean approved;
    private int allocatedMinutes;
    private int usedMinutes;
    private int reservedMinutes;
    
    public Student(String username, String password, String name, String email, String phone) {
        super(username, password, name);
        this.email = email;
        this.phone = phone;
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
        int availableMinutes = allocatedMinutes - usedMinutes - reservedMinutes;

        return availableMinutes;
    }

    public void grantMinutes(int minutes) {
        if(!(minutes <= 0)) {
            allocatedMinutes += minutes;
        }
    }

    public boolean reserveMinutes(int minutes) {
        if (minutes <= 0 || minutes > getAvailableMinutes()) {
            return false;
        }

        reservedMinutes += minutes;
        return true;
    }

    public void settleMinutes(int reserved, int charged) {
        if (reserved <= 0 || charged < 0 || charged > reserved
                || reserved > reservedMinutes) {
             System.out.println("Error");
        }

        reservedMinutes -= reserved;
        usedMinutes += charged;
    }
}
