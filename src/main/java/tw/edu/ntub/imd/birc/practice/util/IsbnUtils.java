package tw.edu.ntub.imd.birc.practice.util;

public class IsbnUtils {

    private IsbnUtils() {
    }

    public static String clean(String isbn) {
        if (isbn == null) return null;
        return isbn.replaceAll("-", "").replaceAll(" ", "");
    }
}
