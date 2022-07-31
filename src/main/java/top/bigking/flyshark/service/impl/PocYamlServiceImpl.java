package top.bigking.flyshark.service.impl;

import org.springframework.stereotype.Service;
import top.bigking.flyshark.entity.PocYaml;
import top.bigking.flyshark.service.PocYamlService;
import top.bigking.flyshark.utils.ELUtil;
import top.bigking.flyshark.utils.PocUtil;

import javax.annotation.Resource;
import java.util.*;

@Service
public class PocYamlServiceImpl implements PocYamlService {
    @Override
    public String getPocName(PocYaml pocYaml) {
        return pocYaml.getName();
    }

    @Override
    public String getTitle(PocYaml pocYaml) {
        return pocYaml.getTitle();
    }

    @Override
    public LinkedHashMap<String, LinkedHashSet<Object>> parsePayloads(PocYaml pocYaml, HashMap<String, Object> paramsMap) {
        LinkedHashMap<String, LinkedHashSet<Object>> payloads = pocYaml.getPayloads();
        if (payloads != null) {
            paramsMap.putAll(payloads);
            return payloads;
        }
        return null;
    }

    @Override
    public LinkedHashMap<String, Object> parseSet(PocYaml pocYaml, HashMap<String, Object> paramsMap) {
        assert pocYaml.getSet() != null;
        LinkedHashMap<String, Object> set = pocYaml.getSet();
        // todo 存入 request 或者 放入controller层做
//        paramsMap.put("request", "");
        Map<String, Object> evaluate = ELUtil.parseSet(set, paramsMap);

        return set;
    }

//    @Override
//    public Map<String, Object> parseRules(PocYaml pocYaml, String domain, HashMap<String, Object> paramsMap, Boolean sendMQ) {
//        LinkedList<Map<String, Object>> rules = pocYaml.getRules();
//        if (pocYaml.getPayloads() != null) {
//            List<List<Map<String, Object>>> evaluateRulesList = PocUtil.evaluatePayloadsRules(pocYaml.getPayloads(), rules, pocYaml.getSet(),domain, paramsMap);
//            Map<String, Object> rulesMessage = new HashMap<>();
//            rulesMessage.put("paramsMap", paramsMap);
//            rulesMessage.put("groups", evaluateRulesList);
//            if (sendMQ) producer.sendGroups(rulesMessage);
//            return rulesMessage;
//        } else {
//            List<Map<String, Object>> evaluateRules = PocUtil.evaluateRules(rules, pocYaml.getSet(), domain, paramsMap);
//            Map<String, Object> rulesMessage = new HashMap<>();
//            rulesMessage.put("paramsMap", paramsMap);
//            rulesMessage.put("rules", evaluateRules);
//            if (sendMQ) producer.sendRules(rulesMessage);
//            return rulesMessage;
//        }
////        return null;
//    }

    @Override
    public List<Map<String, Object>> parseRules2(Integer taskId, PocYaml pocYaml, String domain, HashMap<String, Object> paramsMap, Boolean sendMQ) {
        LinkedList<Map<String, Object>> rules = pocYaml.getRules();
        List<Map<String, Object>> result = new ArrayList<>();
        if (pocYaml.getPayloads() != null) {
            List<List> evaluateRulesList = PocUtil.evaluatePayloadsRules2(pocYaml.getPayloads(), rules, pocYaml.getSet(),domain, paramsMap);
            evaluateRulesList.forEach(r -> {
                Map<String, Object> rulesMessage = new HashMap<>();
                rulesMessage.put("paramsMap", r.get(0));
                rulesMessage.put("rules", r.get(1));
//                if (sendMQ) producer.sendRules(taskId, rulesMessage);
                result.add(rulesMessage);
            });
            return result;
        } else {
            List<Map<String, Object>> evaluateRules = PocUtil.evaluateRules(rules, pocYaml.getSet(), domain, paramsMap);
            Map<String, Object> rulesMessage = new HashMap<>();
            rulesMessage.put("paramsMap", paramsMap);
            rulesMessage.put("rules", evaluateRules);
//            if (sendMQ) producer.sendRules(taskId, rulesMessage);
            result.add(rulesMessage);
            return result;
        }
//        return null;
    }

    @Override
    public Map<String, Object> parseGroups(Integer taskId, PocYaml pocYaml, String domain, HashMap<String, Object> paramsMap, Boolean sendMQ) {
        HashMap<String, Object> groups = pocYaml.getGroups();
        if (pocYaml.getSet() != null) {
            ELUtil.parseSet(pocYaml.getSet(), paramsMap);}
        if (pocYaml.getPayloads() != null) {
            List<List<List<Map<String, Object>>>> evaluateGroupsList = PocUtil.evaluatePayloadsGroups(pocYaml.getPayloads(), groups, pocYaml.getSet(),domain, paramsMap);
            evaluateGroupsList.forEach(g -> {
                Map<String, Object> rulesMessage = new HashMap<>();
                rulesMessage.put("paramsMap", paramsMap);
                rulesMessage.put("groupsList", g);
//                if (sendMQ) producer.sendRules(taskId, rulesMessage);
            });
            Map<String, Object> rulesMessage2 = new HashMap<>();
            rulesMessage2.put("paramsMap", paramsMap);
            rulesMessage2.put("groupsList", evaluateGroupsList);
            return rulesMessage2;
        } else {
            List<List<Map<String, Object>>> evaluateGroups = PocUtil.evaluateGroups(groups, pocYaml.getSet(),domain, paramsMap);
            Map<String, Object> rulesMessage = new HashMap<>();
            rulesMessage.put("paramsMap", paramsMap);
            rulesMessage.put("groups", evaluateGroups);
//            if (sendMQ) producer.sendGroups(taskId, rulesMessage);
            return rulesMessage;
        }
//        return null;
    }

    @Override
    public HashMap<String, Object> getDetail(PocYaml pocYaml) {
        return pocYaml.getDetail();
    }

    @Override
    public HashMap<String, String> getTest(PocYaml pocYaml) {
        return pocYaml.getTest();
    }
}
