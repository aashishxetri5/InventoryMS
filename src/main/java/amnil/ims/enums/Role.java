package amnil.ims.enums;

public enum Role {
    ADMIN, EMPLOYEE;

    public static Role fromString(String role) {
        return Role.valueOf(role.replace("ROLE_", "").toUpperCase());
    }
}
