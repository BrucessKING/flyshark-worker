package top.bigking.flyshark.consumer;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import top.bigking.flyshark.entity.Vulnerability;
import top.bigking.flyshark.utils.CustomerClient;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
//@RocketMQMessageListener(nameServer = "staging-cnbj2-rocketmq.namesrv.api.xiaomi.net:9876", topic = "${rocketmq.consumer.topic}", selectorExpression = "${rocketmq.consumer.selector-expression}", consumerGroup = "${rocketmq.consumer.group}", consumeThreadMax = 64, enableMsgTrace = true, maxReconsumeTimes = 3)
//public class Consumer implements RocketMQListener<Map<String, Object>> {
public class Consumer {
    private final Logger logger = LoggerFactory.getLogger(Consumer.class);
    @Resource
    private CustomerClient customerClient;

//    @Override
    public Vulnerability onMessage(Map<String, Object> map, String proxyHost, Integer proxyPort) {
        if (map.get("paramsMap") instanceof HashMap paramsMap) {
            if (map.get("rules") instanceof List rules) {
                Boolean expression = sendRules(paramsMap, rules, proxyHost, proxyPort);
                if (expression) {
                    Vulnerability vulnerability = new Vulnerability();
                    vulnerability.setTarget((String)((Map)rules.get(0)).get("domain"));
                    vulnerability.setVuln((String) paramsMap.get("pocTitle"));
//                    customerClient.sendVulnerability(vulnerability);
                    logger.info(vulnerability.getTarget() + "存在" + vulnerability.getVuln());
                    return vulnerability;
                }
            } else if (map.get("groups") instanceof List groups) {
                for (Object group : groups) {
                    if (group instanceof List rules) {
                        Boolean expression = sendRules(paramsMap, rules, proxyHost, proxyPort);
                        if (expression) {
                            Vulnerability vulnerability = new Vulnerability();
                            vulnerability.setTarget((String)((Map)rules.get(0)).get("domain"));
                            vulnerability.setVuln((String) paramsMap.get("pocTitle"));
//                            customerClient.sendVulnerability(vulnerability);
                            logger.info(vulnerability.getTarget() + "存在" + vulnerability.getVuln());
                            return vulnerability;
                        }
                    }
                }
            } else if (map.get("groupsList") instanceof List groupsList) {
                for (Object groups : groupsList) {
                    if (groups instanceof List groups2) {
                        for (Object group : groups2) {
                            if (group instanceof List rules) {
                                Boolean expression = sendRules(paramsMap, rules, proxyHost, proxyPort);
                                if (expression) {
                                    Vulnerability vulnerability = new Vulnerability();
                                    vulnerability.setTarget((String)((Map)rules.get(0)).get("domain"));
                                    vulnerability.setVuln((String) paramsMap.get("pocTitle"));
//                                    customerClient.sendVulnerability(vulnerability);
                                    logger.info(vulnerability.getTarget() + "存在" + vulnerability.getVuln());
                                    return vulnerability;
                                }
                            }
                        }
                    }
                }
            }
        } else {
            logger.info(map.toString());
        }
        return null;
    }

    private Boolean sendRules(HashMap paramsMap, List rules, String proxyHost, Integer proxyPort) {
        int i = 0;
        for (i = 0; i < rules.size(); i++) {
            Map<String, Object> rule = (Map<String, Object>) rules.get(i);
            Boolean expression = customerClient.sendHttpClient(paramsMap, rule, proxyHost, proxyPort);
            if (!expression) {
                break;
            }
        }
        if (i >= rules.size()) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }


//    @Override
//    public void onMessage(MessageExt messageExt) {
//        logger.info("收到消息，消息ID为：" + messageExt.getMsgId());
//        String messageBase64 = new String(messageExt.getBody());
//        byte[] decode = Base64.getDecoder().decode(messageBase64);
//        String message = new String(decode);
//        ObjectMapper objectMapper = new ObjectMapper();
//        if (message.startsWith("[[[")) {
//            try {
//                List<List<List<Map<String, Object>>>> payloadsGroups = objectMapper.readValue(message, new TypeReference<List<List<List<Map<String, Object>>>>>() {});
//                payloadsGroups.forEach(groups -> {
//                    groups.forEach(rules -> {
//                        int i = 0;
//                        for (i = 0; i < rules.size(); i++) {
//                            Map<String, Object> rule = rules.get(i);
//                            Boolean expression = customerClient.sendHttpClient(rule);
//                            if (!expression) {
//                                break;
//                            }
//                        }
//                        if (i >= rules.size()) {
//                            logger.info("检测出漏洞！");
//                        }
////                        rules.forEach(rule -> {
////                            CustomerClient.sendHttpClient(rule);
////                        });
//                    });
//                });
//                logger.info("收到消息："  + payloadsGroups.toString());
//            } catch (JsonProcessingException e) {
//                logger.error("转换json异常");
//                e.printStackTrace();
//            }
//        } else if (message.startsWith("[[")) {
//            try {
//                List<LinkedList<Map<String, Object>>> groups = objectMapper.readValue(message, new TypeReference<List<LinkedList<Map<String, Object>>>>() {});
//                logger.info("收到消息："  + groups.toString());
//                // groups 是rule规则组，即一个groups是一个List<Rule>
//                groups.forEach(rules -> {
//                    // 一个rule可能包含多个请求，因此是一个LinkedList
//                    int i = 0;
//                    for (i = 0; i < rules.size(); i++) {
//                        Map<String, Object> rule = rules.get(i);
//                        Boolean expression = customerClient.sendHttpClient(rule);
//                        if (!expression) {
//                            break;
//                        }
//                    }
//                    if (i >= rules.size()) {
//                        logger.info("检测出漏洞！");
//                    }
//
//                });
//            } catch (JsonProcessingException e) {
//                logger.error("转换json异常");
//                e.printStackTrace();
//            }
//        } else if (message.startsWith("[")) {
//            try {
//                List<Map<String, Object>> rules = objectMapper.readValue(message, new TypeReference<List<Map<String, Object>>>() {});
//                logger.info("收到消息："  + rules.toString());
//                int i = 0;
//                for (i = 0; i < rules.size(); i++) {
//                    Map<String, Object> rule = rules.get(i);
//                    Boolean expression = customerClient.sendHttpClient(rule);
//                    if (!expression) {
//                        break;
//                    }
//                }
//                if (i >= rules.size()) {
//                    logger.info("检测出漏洞！");
//                }
////                rules.forEach(rule -> {
////                    CustomerClient.sendHttpClient(rule);
////                });
//            } catch (JsonProcessingException e) {
//                logger.error("转换json异常");
//                e.printStackTrace();
//            }
//        } else {
//            logger.info(message);
//        }
//
//    }
}
