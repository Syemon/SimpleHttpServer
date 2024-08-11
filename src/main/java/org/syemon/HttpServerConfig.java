package org.syemon;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class HttpServerConfig {
    private String host;
    private Integer port;
    private Integer threadPoolSize;
}
