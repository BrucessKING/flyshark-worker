package top.bigking.flyshark.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import top.bigking.flyshark.entity.MyURL;

import java.util.Objects;

@Slf4j
public class MyURLFunction {
    public static Boolean wait(Integer second, MyURL myURL) throws InterruptedException {
        return Boolean.FALSE;
//        Thread.sleep(second * 0);
//        String prefix = myURL.getUrl().getHost().split(".xxxxxxx.xx")[0];
//        WebClient webClient = WebClient.create("http://xxxxxxx.xx");
//        Mono<ResponseEntity<String>> mono = webClient
//                .method(HttpMethod.GET)
//                .uri("/vector/check?prefix={prefix}&token=token&type=public", prefix)
//                .exchangeToMono(response -> response.toEntity(String.class));
//        ResponseEntity<String> block = mono.block();
//        assert block != null;
//        if (block.getStatusCodeValue() == 200 && Objects.requireNonNull(block.getBody()).contains("\"status\": 200") && block.getBody().contains("dns")) {
//            log.info(block.getBody());
//            return Boolean.TRUE;
//        }
//        return Boolean.FALSE;
    }
}
