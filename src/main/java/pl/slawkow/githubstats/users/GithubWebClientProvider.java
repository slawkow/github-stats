package pl.slawkow.githubstats.users;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;
import java.util.Collections;

@Configuration
public class GithubWebClientProvider {

    private static final String CONTENT_TYPE_HEADER_NAME = "Content-Type";
    private static final String ACCEPT_HEADER_NAME = "Accept";

    @Bean("githubWebClient")
    public WebClient createDataSourceWebClient(@Value("${github.api.timeout}") int timeout,
                                               @Value("${github.api.url}") String url) {

        return jsonClientBuilder(timeout)
                .baseUrl(url)
                .build();
    }

    private WebClient.Builder jsonClientBuilder(int timeout) {
        return WebClient.builder()
                .defaultHeaders(httpHeaders -> {
                    httpHeaders.put(CONTENT_TYPE_HEADER_NAME, Collections.singletonList(MediaType.APPLICATION_JSON.toString()));
                    httpHeaders.put(ACCEPT_HEADER_NAME, Collections.singletonList(MediaType.APPLICATION_JSON.toString()));
                })
                .clientConnector(new ReactorClientHttpConnector(createHttpClient(timeout)));
    }

    private HttpClient createHttpClient(int timeout) {
        return HttpClient.create().responseTimeout(Duration.ofMillis(timeout));
    }
}
