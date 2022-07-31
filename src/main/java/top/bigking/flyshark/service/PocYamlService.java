package top.bigking.flyshark.service;

import top.bigking.flyshark.entity.PocYaml;

import java.util.*;

public interface PocYamlService {
    /**
     * Poc中的变量需要被解析
     *
     */
    String getPocName(PocYaml pocYaml);
    String getTitle(PocYaml pocYaml);
    LinkedHashMap<String, LinkedHashSet<Object>> parsePayloads(PocYaml pocYaml, HashMap<String, Object> paramsMap);
    LinkedHashMap<String, Object> parseSet(PocYaml pocYaml, HashMap<String, Object> paramsMap);
//    Map<String, Object> parseRules(PocYaml pocYaml, String domain, HashMap<String, Object> paramsMap, Boolean sendMQ);
    List<Map<String, Object>> parseRules2(Integer taskId, PocYaml pocYaml, String domain, HashMap<String, Object> paramsMap, Boolean sendMQ);
    Map<String, Object> parseGroups(Integer taskId, PocYaml pocYaml, String domain, HashMap<String, Object> paramsMap, Boolean sendMQ);


    HashMap<String, Object> getDetail(PocYaml pocYaml);
    HashMap<String, String> getTest(PocYaml pocYaml);


}
