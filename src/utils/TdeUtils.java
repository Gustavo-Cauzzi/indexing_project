package utils;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

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

    public static void iterateOverFile(RandomAccessFile file, Consumer<String> consumer) throws IOException {
        String line;
        do {
            line = file.readLine();
            if (line != null) consumer.accept(line);
        } while (line != null);
    }

    public enum MATCH_RESULT {
        WHITE('w', "White"), BLACK('b', "Black"), DRAW('d', "Draw"), UNKNOWN('u', "Unknown");
        private final Character code;
        private final String result;

        MATCH_RESULT(Character c, String result) {
            this.code = c;
            this.result = result;
        }

        public Character getCode() {
            return code;
        }

        public String getResult() {
            return result;
        }


        public static MATCH_RESULT getResult(String s) {
            return getResult(s.charAt(0));
        }
        public static MATCH_RESULT getResult (Character c) {
            return Arrays.stream(MATCH_RESULT.values()).filter(matchResult -> matchResult.code.equals(c)).findFirst().orElse(MATCH_RESULT.UNKNOWN);
        }
    }
}
