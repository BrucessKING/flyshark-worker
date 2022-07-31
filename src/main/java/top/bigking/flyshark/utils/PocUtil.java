package top.bigking.flyshark.utils;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.yaml.snakeyaml.Yaml;
import top.bigking.flyshark.entity.PocYaml;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

@Slf4j
public class PocUtil {

    public PocYaml parseYaml(String poc) {
        Yaml yaml = new Yaml();
        return yaml.loadAs(poc, PocYaml.class);
    }

    public static List<List<Map<String, Object>>> evaluatePayloadsRules(LinkedHashMap<String, LinkedHashSet<Object>> payloads, LinkedList<Map<String, Object>> rules, LinkedHashMap<String, Object> set ,String domain, HashMap<String, Object> paramsMap) {
        List<List<Object>> arrayList = new ArrayList<>();
        payloads.forEach((key, value) -> {
            ArrayList<Object> o = new ArrayList<>(value);
            arrayList.add(o);
        });

        log.info(arrayList.toString());
        List<List<Object>> payloadList = new ArrayList<>();
        PocUtil.descartes(arrayList, payloadList, 0, new ArrayList<>());
        List<List<Map<String, Object>>> result = new LinkedList<>();
        payloadList.forEach(r -> {
            payloads.keySet().forEach(k -> {
                paramsMap.put(k, r.remove(0));
            });
            // todo for循环 parseSet()
//            if (set != null) { ELUtil.parseSet(set, paramsMap);}
            List<Map<String, Object>> evaluateRules = PocUtil.evaluateRules(rules, set, domain, paramsMap);
            result.add(evaluateRules);
        });
        return result;
    }

    public static List<List> evaluatePayloadsRules2(LinkedHashMap<String, LinkedHashSet<Object>> payloads, LinkedList<Map<String, Object>> rules, LinkedHashMap<String, Object> set ,String domain, HashMap<String, Object> paramsMap) {
        List<List<Object>> arrayList = new ArrayList<>();
        payloads.forEach((key, value) -> {
            ArrayList<Object> o = new ArrayList<>(value);
            arrayList.add(o);
        });

        log.info(arrayList.toString());
        List<List<Object>> payloadList = new ArrayList<>();
        PocUtil.descartes(arrayList, payloadList, 0, new ArrayList<>());
        List<List> result = new LinkedList<>();
        payloadList.forEach(r -> {
            HashMap<String, Object> tmpParamsMap = new HashMap<>(paramsMap);
            payloads.keySet().forEach(k -> {
                tmpParamsMap.put(k, r.remove(0));
            });
            if (set != null) {ELUtil.parseSet(set, tmpParamsMap);}
            List<Map<String, Object>> evaluateRules = PocUtil.evaluateRules(rules, set, domain, tmpParamsMap);
            List<Object> l = new ArrayList<>();
            l.add(tmpParamsMap);
            l.add(evaluateRules);
            result.add(l);
            // todo for循环 parseSet()
//            if (set != null) { ELUtil.parseSet(set, paramsMap);}
//            List<Map<String, Object>> evaluateRules = PocUtil.evaluateRules(rules, set, domain, paramsMap);
//            result.add(evaluateRules);
        });
        return result;
    }

        // 测试插入expression
    public static List<Map<String, Object>> evaluateRules(LinkedList<Map<String, Object>> rules, LinkedHashMap<String, Object> set, String domain, HashMap<String, Object> paramsMap) {
        List<Map<String, Object>> evaluateRules = new LinkedList<>();
        for (Map<String, Object> rule : rules) {
//            if (set != null) {ELUtil.parseSet(set, paramsMap);}
            Map<String, Object> evaluate = ELUtil.parseRule(rule, paramsMap);
            evaluate.put("domain", domain);
//            evaluate.put("paramsMap", paramsMap);
            evaluateRules.add(evaluate);
        }
        return evaluateRules;
    }

    public static List<List<Map<String, Object>>> evaluateGroups(HashMap<String, Object> groups, LinkedHashMap<String, Object> set,String domain, HashMap<String, Object> paramsMap) {
        List<List<Map<String, Object>>> groupsList = new ArrayList<>();
        groups.forEach((key, value) -> {
            LinkedList<Map<String, Object>> list = new LinkedList<>();
            ((ArrayList) value).forEach(arr -> {
                list.add((Map<String, Object>)arr);
            });
            List<Map<String, Object>> evaluateRules = PocUtil.evaluateRules(list, set, domain, paramsMap);
            groupsList.add(evaluateRules);
        });
        return groupsList;
    }

    public static URL generateReverseUrl(Integer length) {
//        int length = 8;
        String str = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random random = new Random();
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int number = random.nextInt(62);
            stringBuilder.append(str.charAt(number));
        }
        try {
            return new URL("http://" + stringBuilder + ".xxxxxxx.xx");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Boolean checkReverse(URL url) {
        RestTemplate restTemplate = new RestTemplate();
        System.out.println(url.getPath());
        ResponseEntity<String> responseEntity = restTemplate.getForEntity(url.toString(), String.class);
        if (responseEntity.getStatusCodeValue() == 200 && Objects.requireNonNull(responseEntity.getBody()).contains("dns"))
            return Boolean.TRUE;
        return Boolean.FALSE;
    }

    /**
     * Discription: 笛卡尔乘积算法
     * 把一个List{[1,2],[A,B],[a,b]} 转化成
     * List{[1,A,a],[1,A,b],[1,B,a],[1,B,b],[2,A,a],[2,A,b],[2,B,a],[2,B,b]} 数组输出
     *
     * @param dimensionValue
     *              原List
     * @param result
     *              通过乘积转化后的数组
     * @param layer
     *              中间参数
     * @param currentList
     *               中间参数
     */
    public static void descartes(List<List<Object>> dimensionValue, List<List<Object>> result, int layer, List<Object> currentList) {
        if (layer < dimensionValue.size() - 1) {
            if (dimensionValue.get(layer).size() == 0) {
                descartes(dimensionValue, result, layer + 1, currentList);
            } else {
                for (int i = 0; i < dimensionValue.get(layer).size(); i++) {
                    List<Object> list = new ArrayList<Object>(currentList);
                    list.add(dimensionValue.get(layer).get(i));
                    descartes(dimensionValue, result, layer + 1, list);
                }
            }
        } else if (layer == dimensionValue.size() - 1) {
            if (dimensionValue.get(layer).size() == 0) {
                result.add(currentList);
            } else {
                for (int i = 0; i < dimensionValue.get(layer).size(); i++) {
                    List<Object> list = new ArrayList<Object>(currentList);
                    list.add(dimensionValue.get(layer).get(i));
                    result.add(list);
                }
            }
        }
    }

    /**
     * 解析payloads变量，会进行循环操作
     * @param payloads
     * @param groups
     * @param domain
     * @param paramsMap
     */
    public static List<List<List<Map<String, Object>>>> evaluatePayloadsGroups(LinkedHashMap<String, LinkedHashSet<Object>> payloads, HashMap<String, Object> groups, LinkedHashMap<String,Object> set,String domain, HashMap<String, Object> paramsMap) {
        List<List<Object>> arrayList = new ArrayList<>();
        payloads.forEach((key, value) -> {
            ArrayList<Object> o = new ArrayList<>(value);
            arrayList.add(o);
        });

        System.out.println(arrayList);
        List<List<Object>> payloadsList = new ArrayList<>();
        PocUtil.descartes(arrayList, payloadsList, 0, new ArrayList<>());
        List<List<List<Map<String, Object>>>> result = new LinkedList<>();
        payloadsList.forEach(r -> {
            payloads.keySet().forEach(k -> {
                paramsMap.put(k, r.remove(0));
            });
            // todo for循环 parseSet()
//            if (set != null) {ELUtil.parseSet(set, paramsMap);}
            List<List<Map<String, Object>>> evaluateGroups = PocUtil.evaluateGroups(groups, set, domain, paramsMap);
            result.add(evaluateGroups);
        });
        return result;
    }


    public static void main(String[] args) {
        PocUtil pocUtil = new PocUtil();
        String pocName = "apache-druid-rce";
        InputStream inputStream = pocUtil.getClass().getClassLoader().getResourceAsStream("poc/yaml/" + pocName + ".yml");
        assert inputStream != null;
        Scanner s = new Scanner(inputStream).useDelimiter("\\A");
        String poc = s.hasNext() ? s.next() : "";
        PocYaml pocYaml = pocUtil.parseYaml(poc);
//        System.out.println(pocYaml);
        String domain = "http://baidu.com";
        LinkedList<Map<String, Object>> rules = pocYaml.getRules();

        HashMap<String, Object> paramsMap = new HashMap<>();
        paramsMap.putAll(pocYaml.getPayloads());
        log.info("------parse poc.set-----");
        Map<String, Object> evaluate = ELUtil.parseSet(pocYaml.getSet(), paramsMap);

        HashMap<String, Object> groups = pocYaml.getGroups();
        // 转化rules
//        PocUtil.parseYamlToRequest2(rules, domain, paramsMap);
        // 转化groups
//        List<List<Mono<ResponseEntity<String>>>> groupsList = PocUtil.parseGroupsToRequest(groups, domain, paramsMap);
//        groupsList.forEach(monos -> {
//            monos.forEach(mono -> {
//                mono.block();
//            });
//        });

//        PocUtil.parsePayloadsToRequest(pocYaml.getPayloads(), pocYaml.getGroups(), "http://baidu.com", paramsMap);

    }

}
