package utils;

import java.util.ArrayList;
import java.util.List;

public class TdeUtils {
    private TdeUtils(){}
    public static <T> List<List<T>> splitInHalf(final List<T> list) {
        int midIndex = ((list.size() / 2) - (((list.size() % 2) > 0) ? 0 : 1));

        List<T> a = new ArrayList<>();
        List<T> b = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            if (i <= midIndex) {
                a.add(list.get(i));
            } else {
                b.add(list.get(i));
            }
        }

        return List.of(a, b);
    }

    public static Long extractId (final String line) {
        return Long.parseLong(line.split(",")[0]);
    }
}
