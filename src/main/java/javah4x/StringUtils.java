package javah4x;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class StringUtils {

    private StringUtils() {}

    /**
     * Convert the given camelCase string into snake_case.
     * @param s the string in camelCase.
     * @return the string converted to snake_case.
     */
    public static String toSnakeCase(String s) {
        Pattern pat = Pattern.compile("([A-Z])");
        Matcher matcher = pat.matcher(s);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            String upCase = matcher.group(1);
            matcher.appendReplacement(sb, '_' + upCase.toLowerCase());
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    public static String mangle(String symbol) {
        return symbol.replace("_", "_1")
                .replace(";", "_2")
                .replace("[", "_3")
                .replace('.', '_');
    }
}
