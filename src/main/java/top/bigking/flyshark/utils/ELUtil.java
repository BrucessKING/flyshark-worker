package top.bigking.flyshark.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.expression.MapAccessor;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.SpelEvaluationException;
import org.springframework.expression.spel.SpelParseException;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.yaml.snakeyaml.Yaml;
import top.bigking.flyshark.entity.PocYaml;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Slf4j
public class ELUtil {
    private static final ExpressionParser parser = new SpelExpressionParser();
    private static final StandardEvaluationContext standardEvaluationContext = new StandardEvaluationContext();
    static {
        standardEvaluationContext.addPropertyAccessor(new MapAccessor());
    }
    public static Map<String, Object> parseSet(Map<String, Object> pocSet, HashMap<String, Object> paramsMap) {
        for (Map.Entry<String, Object> entry : pocSet.entrySet()) {
            if (entry.getValue().equals("newReverse()")) {
                paramsMap.put(entry.getKey(), PocFunction.newReverse());
                continue;
            }
            try {
//                String out = eval(entry.getValue().toString(), paramsMap);
                Object out = eval(entry.getValue().toString(), paramsMap);
                paramsMap.put(entry.getKey(), out);
            } catch (SpelEvaluationException e) {
                paramsMap.put(entry.getKey(), entry.getValue());
                log.error("parse failed");
            } catch (NoSuchMethodException e) {
                log.error("error method");
            }
        }
        return pocSet;
    }


    public static Map<String, Object> parseRule(Map<String, Object> rule, HashMap<String, Object> paramsMap) {
        standardEvaluationContext.setRootObject(paramsMap);
        Map<String, Object> result = new HashMap<>();
        for (Map.Entry<String, Object> entry : rule.entrySet()) {
            if (entry.getValue().equals("newReverse()")) {
                result.put(entry.getKey(), PocFunction.newReverse());
                continue;
            }
            try {
                if ("headers".equals(entry.getKey())) {
                    LinkedHashMap<String, Object> linkedHashMap = new LinkedHashMap<>();
                    linkedHashMap.putAll((LinkedHashMap) entry.getValue());
                    for (Map.Entry<String, Object> entry1 : linkedHashMap.entrySet()) {
//                        String out = null;
                        Object out = null;
                        try {
//                            out = parser.parseExpression(entry1.getValue().toString(), new CustomTemplateParseContext()).getValue(standardEvaluationContext, String.class);
                            out = parser.parseExpression(entry1.getValue().toString(), new CustomTemplateParseContext()).getValue(standardEvaluationContext, Object.class);
                        } catch (SpelEvaluationException e) {
                            linkedHashMap.put(entry1.getKey(), entry1.getValue().toString());
                            continue;
                        }
                        if (out instanceof Byte[] bytes) {
                            byte[] outTmp = new byte[bytes.length];
                            for (int i = 0; i < bytes.length; i++) {
                                outTmp[i] = bytes[i];
                            }
                            linkedHashMap.put(entry1.getKey(), new String(outTmp, StandardCharsets.UTF_8));
                        } else {
                            linkedHashMap.put(entry1.getKey(), out);
                        }
                    }
                    result.put("headers", linkedHashMap);
                    continue;
                }
                String out = parser.parseExpression(entry.getValue().toString(), new CustomTemplateParseContext()).getValue(standardEvaluationContext, String.class);
//                String out = eval(entry.getValue().toString(), paramsMap);
                result.put(entry.getKey(), out);
            } catch (SpelEvaluationException e) {
                result.put(entry.getKey(), entry.getValue().toString());
                log.error("evaluate failed");
            } catch (SpelParseException e) {
                log.error("parse failed");
            }
        }
        return result;
    }
    public static Object eval(String expression, HashMap<String, Object> paramsMap) throws NoSuchMethodException {
        standardEvaluationContext.setRootObject(paramsMap);
        standardEvaluationContext.addMethodResolver(new CustomMethodResolver());
        Object out = parser.parseExpression(expression).getValue(standardEvaluationContext, Object.class);
//        log.info(out);
        return out;
    }

    public static void main(String[] args) throws JsonProcessingException {
        Yaml yaml = new Yaml();
        InputStream resourceAsStream = ELUtil.class.getClassLoader().getResourceAsStream("poc/yaml/apache-druid-rce.yml");
        PocYaml pocYaml = yaml.loadAs(resourceAsStream, PocYaml.class);
        System.out.println(pocYaml.toString());
        standardEvaluationContext.setRootObject(pocYaml);
        HashMap<String, Object> paramsMap = new HashMap<>();
        log.info("------parse poc.payloads-----");
        paramsMap.putAll(pocYaml.getPayloads());
        log.info("------parse poc.set-----");
        Map<String, Object> evaluate = parseSet(pocYaml.getSet(), paramsMap);


        for (Map.Entry<String, Object> map : paramsMap.entrySet()) {
            log.info(map.getKey() + ":" + map.getValue());
        }

    }
}
