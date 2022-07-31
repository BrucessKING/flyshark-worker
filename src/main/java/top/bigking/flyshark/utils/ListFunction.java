package top.bigking.flyshark.utils;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class ListFunction {
    public static Boolean contains(String content, ArrayList callObject) {
        for (int i = 0; i < callObject.size(); i++) {
            if (callObject.get(i) instanceof String s) {
                if (s.contains(content)) {
                    return Boolean.TRUE;
                }
            }
        }
        return Boolean.FALSE;
    }
}
