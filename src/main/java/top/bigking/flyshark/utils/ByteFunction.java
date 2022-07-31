package top.bigking.flyshark.utils;

import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

@Component
public class ByteFunction {
    public static Boolean bcontains(byte[] targetObject, byte[] callObject) {
        int l1 = 0;
        int l2 = 0;
        while (true) {
            if (l1 >= targetObject.length) return Boolean.TRUE;
            if (l2 >= callObject.length) return Boolean.FALSE;
            if (targetObject[l1] == callObject[l2]) {
                l1++;
                l2++;
            } else {
                l1 = 0;
                l2++;
            }
        }
    }
    public static Boolean bcontains(String s, byte[] callObject) {
        return ByteFunction.bcontains(s.getBytes(StandardCharsets.UTF_8), callObject);
//        return Boolean.TRUE;
//        boolean equals = Arrays.equals(s.getBytes(StandardCharsets.UTF_8), callObject);
//        return equals;
    }

    public static void main(String[] args) {
        Boolean bcontains = ByteFunction.bcontains(new byte[]{1, 2, 3}, new byte[]{4, 5, 6, 1, 2, 3, 5, 9});
        System.out.println(bcontains);
    }
}
