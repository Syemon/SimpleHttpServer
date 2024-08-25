package org.syemon;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class RequestReaderTest {

    RequestReader sut = new RequestReader();

    @Test
    void decode() {
        // given
        InputStream inputStream = new ByteArrayInputStream("GET / HTTP/1.1\r\n".getBytes());

        // when
        Optional<HttpRequest> actual = sut.decode(inputStream);

        // then
        assertThat(actual).isPresent();
        assertThat(actual.get().getMethod()).isEqualTo(HttpMethod.GET);
        assertThat(actual.get().getPath()).isEqualTo("/");
    }
}