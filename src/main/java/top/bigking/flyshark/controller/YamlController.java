package top.bigking.flyshark.controller;

import org.springframework.util.Base64Utils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.representer.Representer;
import top.bigking.flyshark.consumer.Consumer;
import top.bigking.flyshark.entity.PocYaml;
import top.bigking.flyshark.entity.Vulnerability;
import top.bigking.flyshark.service.PocYamlService;

import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("yaml")
public class YamlController {
    @Resource
    private PocYamlService pocYamlService;
    @Resource
    private Consumer consumer;
//    @PostMapping("request")
//    public String generateRequest(Integer taskId, String pocBase64, String domain) {
//        byte[] decode = Base64Utils.decode(pocBase64.getBytes(StandardCharsets.UTF_8));
//        String poc_str = new String(decode);
//        Representer representer = new Representer();
//        representer.getPropertyUtils().setSkipMissingProperties(true);
//        Yaml yaml = new Yaml(representer);
//        PocYaml pocYaml = yaml.loadAs(poc_str, PocYaml.class);
//        if (pocYaml.checkPoc()) { // 验证PoC的合法性
//            HashMap<String, Object> paramsMap = new HashMap<>();
//            paramsMap.put("pocTitle", pocYaml.getTitle());
////            if (pocYaml.getSet() != null) {pocService.parseSet(pocYaml, paramsMap);}
//            if (pocYaml.getGroups() != null) {
//                pocYamlService.parseGroups(taskId, pocYaml, domain, paramsMap, Boolean.TRUE);
//            }
//            if (pocYaml.getRules() != null) {
//                pocYamlService.parseRules2(taskId, pocYaml, domain, paramsMap, Boolean.TRUE);
//            }
//            return "success";
//        } else {
//            return "不合法的PoC";
//        }
//    }
    @PostMapping("testTarget")
    public Vulnerability testTarget(String pocBase64, String domain, String proxyHost, Integer proxyPort) {
        byte[] decode = Base64Utils.decode(pocBase64.getBytes(StandardCharsets.UTF_8));
        String poc_str = new String(decode);
        Representer representer = new Representer();
        representer.getPropertyUtils().setSkipMissingProperties(true);
        Yaml yaml = new Yaml(representer);
        PocYaml pocYaml = yaml.loadAs(poc_str, PocYaml.class);
        if (pocYaml.checkPoc()) { // 验证PoC的合法性
            HashMap<String, Object> paramsMap = new HashMap<>();
            paramsMap.put("pocTitle", pocYaml.getTitle());
//            if (pocYaml.getSet() != null) {pocService.parseSet(pocYaml, paramsMap);}
            if (pocYaml.getGroups() != null) {
                Map<String, Object> rulesMessage = pocYamlService.parseGroups(-1, pocYaml, domain, paramsMap, Boolean.FALSE);
                // todo 发送请求
                Vulnerability vulnerability = consumer.onMessage(rulesMessage, proxyHost, proxyPort);
//                System.out.println(rulesMessage);
                return vulnerability;
            }
            if (pocYaml.getRules() != null) {
                List<Map<String, Object>> result = pocYamlService.parseRules2(-1, pocYaml, domain, paramsMap, Boolean.FALSE);
                // todo 发送请求
                for (Map<String, Object> rulesMessage : result) {
                    Vulnerability vulnerability = consumer.onMessage(rulesMessage, proxyHost, proxyPort);
                    if (vulnerability != null && vulnerability.getVuln() != null && vulnerability.getVuln().length() > 0) {
                        return vulnerability;
                    }
                }
            }
            return new Vulnerability();
        } else {
            return new Vulnerability();
        }
    }
}
