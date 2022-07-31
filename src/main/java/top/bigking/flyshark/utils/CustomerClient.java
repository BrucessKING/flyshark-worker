package top.bigking.flyshark.utils;

import io.netty.channel.ChannelOption;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.expression.MapAccessor;
import org.springframework.expression.spel.SpelEvaluationException;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Mono;
import reactor.netty.ByteBufFlux;
import reactor.netty.http.client.HttpClient;
import reactor.netty.transport.ProxyProvider;
import top.bigking.flyshark.entity.MyResponse;

import javax.net.ssl.SSLException;
import javax.net.ssl.TrustManagerFactory;
import java.io.File;
import java.io.FileInputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Component
public class CustomerClient {

    public Boolean sendHttpClient(HashMap<String, Object> paramsMap, Map<String, Object> rule, String proxyHost, Integer proxyPort) {
        Map<String, Object> evaluate = ELUtil.parseRule(rule, paramsMap);

        MultiValueMap<String, String> multiValueMap = new LinkedMultiValueMap<>();
        if (evaluate.get("headers") != null) {
            LinkedHashMap<String, Object> headers = ((LinkedHashMap<String, Object>) evaluate.get("headers"));
            for (Map.Entry<String, Object> entry : headers.entrySet()) {
                multiValueMap.add(entry.getKey(), entry.getValue().toString());
            }
        }
        try {
            SslContext sslContext = SslContextBuilder.forClient().trustManager(InsecureTrustManagerFactory.INSTANCE).build();
//            SslContext sslContext = SslContextBuilder.forClient().trustManager(new File("cacert.pem")).build();
            HttpClient httpClient = null;
            if (proxyHost != null && proxyPort != null) {
                httpClient = HttpClient.create()
                        .secure(sslContextSpec -> sslContextSpec.sslContext(sslContext))
                        .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 10000)
                        .followRedirect(Boolean.parseBoolean((String) evaluate.get("follow_redirects")))
                        .responseTimeout(Duration.ofSeconds(10))
                        .headers(h -> {
                            multiValueMap.forEach(h::set);
                        })
                        .proxy(typeSpec ->
                                typeSpec.type(ProxyProvider.Proxy.HTTP)
                                        .host(proxyHost)
                                        .port(proxyPort));
//                                        .username(PROXY_USERNAME)
//                                        .password(password -> PROXY_PASSWORD));
            } else {
                httpClient = HttpClient.create()
                        .secure(sslContextSpec -> sslContextSpec.sslContext(sslContext))
                        .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 10000)
                        .followRedirect(Boolean.parseBoolean((String) evaluate.get("follow_redirects")))
                        .responseTimeout(Duration.ofSeconds(10))
                        .headers(h -> {
                            multiValueMap.forEach(h::set);
                        });
//                    .proxy(typeSpec -> typeSpec.type(ProxyProvider.Proxy.HTTP).host(HOST).port(PORT));
//            if (PROXY_ENABLE) {
//                httpClient.proxy(typeSpec -> typeSpec.type(ProxyProvider.Proxy.HTTP).host(HOST).port(PORT));
//            }
            }
            String domain = (String) evaluate.get("domain");
            String uri = (String) evaluate.get("path");
            try {
                new URI(uri);
            } catch (URISyntaxException e) {
                uri = URLEncoder.encode(uri, StandardCharsets.UTF_8);
            }
            MyResponse myResponse = new MyResponse();
            String block = httpClient.request(HttpMethod.valueOf(evaluate.get("method") instanceof String method ? method : "GET"))
                    .uri(domain.stripTrailing() + "/" + StringUtils.trimLeadingCharacter(uri, '/'))
                    .send(ByteBufFlux.fromString(Mono.just(evaluate.get("body") != null ? (String) evaluate.get("body") : "")))
                    .responseSingle((resp, bytes) -> {
                        assert resp != null;
                        myResponse.setStatus(resp.status().code());
                        MultiValueMap<String, Object> headersMap = new LinkedMultiValueMap<>();
                        resp.responseHeaders().forEach(h -> {
                            if (headersMap.containsKey(h.getKey())) {
                                headersMap.get(h.getKey()).add(h.getValue());
                            } else {
                                headersMap.addIfAbsent(h.getKey(), h.getValue());
                            }
                        });
                        if (headersMap.containsKey("content-length") && !headersMap.containsKey("Content-Length")) {
                            headersMap.put("Content-Length", headersMap.get("content-length"));
                        }
                        myResponse.setHeaders(headersMap);
                        myResponse.setContent_type(resp.responseHeaders().get("Content-Type"));
                        return bytes.asString();
                    })
                    .block();
            myResponse.setBody(block != null ? block.getBytes(StandardCharsets.UTF_8) : new byte[]{0});
            if (!evaluate.containsKey("expression")) {
                return Boolean.TRUE;
            }
            String expression = (String) evaluate.get("expression");
            if (paramsMap != null) {
                paramsMap.put("response", myResponse);
                // todo 解析expression
                SpelExpressionParser parser = new SpelExpressionParser();
                StandardEvaluationContext standardEvaluationContext = new StandardEvaluationContext();
                standardEvaluationContext.addPropertyAccessor(new MapAccessor());
                standardEvaluationContext.addMethodResolver(new CustomMethodResolver());
                standardEvaluationContext.setRootObject(paramsMap);
                Boolean value = parser.parseExpression(expression).getValue(standardEvaluationContext, Boolean.class);
                log.info("expression 解析的值为：" + value);
                // todo 增加search逻辑
                if (evaluate.get("search") instanceof String preSearch) {
                    String search = preSearch.replace("(?P<", "(?<").stripTrailing();


                    String searchVarName = "\\(\\?<([\\s\\S]+)>";
                    Pattern comp = Pattern.compile(searchVarName);
                    Matcher matcherVarName = comp.matcher(search);
                    if (matcherVarName.find()) {
                        String varName = matcherVarName.group(1);
                        // 下划线转驼峰命名
                        StringBuilder stringBuilder = new StringBuilder();
                        for (String s : varName.split("_")) {
                            if (!search.contains("_")) {
                                stringBuilder.append(s);
                                continue;
                            }
                            if (stringBuilder.length() == 0) {
                                stringBuilder.append(s.toLowerCase());
                            } else {
                                stringBuilder.append(s.substring(0, 1).toUpperCase());
                                stringBuilder.append(s.substring(1).toLowerCase());
                            }
                        }
                        Pattern compile = Pattern.compile(search.replace(varName, stringBuilder));
                        // todo 目前匹配的是body，以后需要匹配整个request
                        assert block != null;
                        Matcher matcher = compile.matcher(block);
                        if (matcher.find()) {
                            String varValue = matcher.group(varName);
                            paramsMap.put(varName, varValue);
                        }
                    }
                }
                return value;
            }
            return Boolean.TRUE;
        } catch (SSLException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (SpelEvaluationException e) {
            log.info("expression parse failed");
            return Boolean.FALSE;
        }
        return Boolean.FALSE;
    }

//    public void sendVulnerability(Vulnerability vulnerability) {
//        ObjectMapper objectMapper = new ObjectMapper();
//        try {
//            String jsonString = objectMapper.writeValueAsString(vulnerability);
//            HttpClient client = HttpClient.create()
//                    .headers(h -> { h.set("Content-Type", MediaType.APPLICATION_JSON); });
////                    .proxy(typeSpec -> typeSpec.type(ProxyProvider.Proxy.HTTP).host("127.0.0.1").port(7777));
//            client.post()
//                    .uri("http://10.46.21.35:8888/vulnerability/add")
//                    .send(ByteBufFlux.fromString(Mono.just(jsonString)))
//                    .response()
//                    .subscribe();
//        } catch (JsonProcessingException e) {
//            e.printStackTrace();
//        }
//    }
}
