package org.syemon;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

@Builder
@Getter
@ToString
public class HttpResponse {

    private final int statusCode;
    private final Object body;
    private final List<String> headers;
}
