package top.bigking.flyshark.entity;

import lombok.Data;

import java.util.*;

@Data
public class PocYaml {
    private String name;
    private String title;
    private LinkedHashMap<String, Object> set;
    private LinkedHashMap<String, LinkedHashSet<Object>> payloads;
    private LinkedList<Map<String, Object>> rules;
    private HashMap<String, Object> groups;
    // something useless
    private HashMap<String, Object> detail;
    private HashMap<String, String> test;
    public Boolean checkPoc() {
        if (rules != null && groups != null) {
            return Boolean.FALSE;
        }
        return Boolean.TRUE;
    }
}
