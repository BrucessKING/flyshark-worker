package top.bigking.flyshark.entity;

import lombok.Data;
import org.springframework.util.MultiValueMap;

import java.util.Map;

@Data
public class MyResponse {
    private MyURL url;
    private String method;
    private Integer status;
    private MultiValueMap<String, Object> headers;
    private String content_type;
    private byte[] body;
}
