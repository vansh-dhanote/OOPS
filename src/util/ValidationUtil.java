package util;

public class ValidationUtil {
    private ValidationUtil() {
    }

    public static void requireText(String value, String fieldName) throws PortalException {
        if (value == null || value.trim().isEmpty()) {
            throw new PortalException(fieldName + " cannot be empty.");
        }
    }
}
