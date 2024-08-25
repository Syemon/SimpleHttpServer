package org.syemon;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Optional;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class RequestReaderTest {

    RequestReader sut = new RequestReader();

    @ParameterizedTest
    @MethodSource("decodeDataProvider")
    void decode(HttpMethod httpMethod, String path) {
        // given
        String request = "%s %s HTTP/1.1\r\n".formatted(httpMethod, path);

        InputStream inputStream = new ByteArrayInputStream(request.getBytes());

        // when
        Optional<HttpRequest> actual = sut.decode(inputStream);

        // then
        assertThat(actual).isPresent();
        assertThat(actual.get().getMethod()).isEqualTo(httpMethod);
        assertThat(actual.get().getPath()).isEqualTo(path);
    }

    public static Stream<Arguments> decodeDataProvider() {
        return Stream.of(
                Arguments.of(HttpMethod.GET, "/loremipsum1"),
                Arguments.of(HttpMethod.POST, "/loremipsum2"),
                Arguments.of(HttpMethod.PUT, "/loremipsum3"),
                Arguments.of(HttpMethod.DELETE, "/loremipsum4")
        );
    }

}