package top.bigking.flyshark.utils;

import java.util.regex.Pattern;

public class RawFunction {
    public static Boolean bmatches(byte[] bytes, String callObject) {
//        return Boolean.FALSE;
        String content = new String(bytes);
        Pattern compile = Pattern.compile(callObject);
        return compile.matcher(content).lookingAt();
    }
    public static Boolean contains(String content, String callObject) {
        if (callObject.contains(content)) {
            return Boolean.TRUE;
        } else {
            return Boolean.FALSE;
        }
    }

}
