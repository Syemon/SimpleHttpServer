package org.syemon;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Builder
@Getter
@ToString
public class HttpRequest {
    private final HttpMethod method;
    private final String path;
}
