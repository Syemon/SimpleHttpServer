package org.syemon;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Builder
@Getter
@ToString
public class HttpServerConfig {
    private String host;
    private Integer port;
    private Integer threadPoolSize;
}
