package tw.edu.ntub.imd.birc.practice.util;

public class IsbnUtils {

    private IsbnUtils() {
    }

    public static String clean(String isbn) {
        if (isbn == null) return null;
        return isbn.replaceAll("[\\s-]", "");
    }

    public static boolean isValid(String isbn) {
        if (isbn == null) return false;
        String cleanIsbn = clean(isbn);
        if (cleanIsbn.length() != 13 || !cleanIsbn.matches("\\d{13}")) return false;

        int sum = 0;
        for (int i = 0; i < 12; i++) {
            int digit = Character.getNumericValue(cleanIsbn.charAt(i));
            sum += (i % 2 == 0) ? digit : digit * 3;
        }
        int checkDigit = (10 - (sum % 10)) % 10;
        return checkDigit == Character.getNumericValue(cleanIsbn.charAt(12));
    }
}
