package com.kramphub.digitallibrary.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;
import java.net.URI;
import java.time.Duration;


@Configuration
public class ITunesAPIWebClientConfiguration {
    private final WebClient.Builder webClientBuilder;

    public ITunesAPIWebClientConfiguration(WebClient.Builder webClientBuilder) {
        this.webClientBuilder = webClientBuilder;
    }

    /*
     * Return GoogleBookAPIResult object which contains requested books information
     * This method works synchronously. Retries on failure, maximum 3 times
     * @param uri: an URL requesting books information  similar to the term
     * @return GoogleBookAPIResult object
     * */
    @Bean
    @Scope(value = "prototype")
    String getSyncItunesWebClient( URI uri){
        return  webClientBuilder.build()
                .get()
                .uri(uri)
                .retrieve()
                .bodyToMono(String.class)
                .retryWhen(Retry.fixedDelay(3, Duration.ofMillis(100)))
                .block();
    }

    /*
    * Return Mono wrapped GoogleBookAPIResult object which contains requested books information
    * This method works asynchronously.
    * @param uri: an URL requesting books information  similar to the term
    * @return Mono<GoogleBookAPIResult> object
    * */
    @Bean(name = "asyncItunesWebClient")
    @Scope(value = "prototype")
    Mono<String> getAsyncItunesWebClient(final URI uri){
        return  webClientBuilder.build()
                .get()
                .uri(uri)
                .retrieve()

                .bodyToMono(String.class);

    }



}
