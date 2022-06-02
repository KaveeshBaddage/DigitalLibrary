package com.kramphub.digitallibrary.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.json.JSONObject;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import com.kramphub.digitallibrary.dao.*;
import com.kramphub.digitallibrary.exception.NoRecordException;

/**
 * EntityServiceImpl Class implements the methods defined in  {@link EntityService} Interface.
 * This service uses Google Book API and iTunes API as upstream services to retrieve {@link Album} and {@link Book} data
 * * @author Kaveesha Baddage
 * *
 */

@Slf4j
@Service
public class EntityServiceImpl implements EntityService {

    private static final String GOOGLE_BOOK_API_RESULT_LIMIT_PARAM = "maxResults";
    private static final String GOOGLE_BOOK_API_QUERY_PARAM = "q";
    private static final String ITUNES_API_RESULT_LIMIT_PARAM = "limit";
    private static final String ITUNES_ALBUM_API_QUERY_PARAM = "term";
    private static final String ITUNES_API_ENTITY_QUERY_PARAM = "entity";
    private static final String ITUNES_ALBUM_API_ALBUM_ENTITY = "album";
    private HttpClient client;

    private BeanFactory beanFactory;
    private final WebClient.Builder webClientBuilder;
    private
    @Value("${google.book.url}")
    String googleBookAPIUrl;

    @Value("${apple.album.url}")
    String itunesAlbumAPIUrl;

    @Value("${response.entityLimit}")
    Integer resultLimit;


    @Value("${response.waitTime}")
    int waitTIme;

    public EntityServiceImpl(WebClient.Builder webClientBuilder, BeanFactory beanFactory) {
        this.webClientBuilder = webClientBuilder;
        this.beanFactory = beanFactory;

    }


    @Override
    public List<LibraryItem> findByKeyword(String keyword) throws URISyntaxException, IOException {

        log.info("Received keyword for Book|Album search service: " + keyword);

        // Creates a ExecutorService object having a pool of 10 threads  to process requests on threads asynchronously.
        ExecutorService executorService = Executors.newFixedThreadPool(10);

        //Create data structures to save data received from API requests and processed data
        final List<Future<List<LibraryItem>>> futureLibraryItemList = new ArrayList<>();
        final List<LibraryItem> libraryItemList = new ArrayList<>();


        // Create HTTP request to call Google Book API
        HttpGet googleRequest = new HttpGet(googleBookAPIUrl);


        URI bookSearchURI = new URIBuilder(googleRequest.getURI())
                .addParameter(GOOGLE_BOOK_API_QUERY_PARAM, keyword)
                .addParameter(GOOGLE_BOOK_API_RESULT_LIMIT_PARAM, String.valueOf(resultLimit))
                .build();


        //Get bookList from Google Book API in synchronous way
        Mono<GoogleBookAPIResult> asyncBookItems = (Mono<GoogleBookAPIResult>) beanFactory.getBean("asyncWebClient", bookSearchURI);
        asyncBookItems.subscribe(bookItems -> {
            log.info("Num of items in Google Book API: {}", bookItems.getTotalItems());
            if (bookItems.getTotalItems() > 0) {
                ArrayList<GoogleBookItem> booksData = bookItems.getItems();
                for (GoogleBookItem book : booksData) {
                    GoogleBookItem.VolumeInfo volume = book.getVolumeInfo();
                    LibraryItem libraryItem = new LibraryItem(volume.getTitle(), volume.getAuthors(), "Book");
                    libraryItemList.add(libraryItem);
                }
            }
        });

        // Create  HTTP request  to call iTunes Album API
        HttpGet itunesRequest = new HttpGet(itunesAlbumAPIUrl);

        URI albumSearchURI = new URIBuilder(itunesRequest.getURI())
                .addParameter(ITUNES_ALBUM_API_QUERY_PARAM, keyword)
                .addParameter(ITUNES_API_RESULT_LIMIT_PARAM, String.valueOf(resultLimit))
                .addParameter(ITUNES_API_ENTITY_QUERY_PARAM, ITUNES_ALBUM_API_ALBUM_ENTITY)
                .build();

        // itunesRequest.setURI(albumSearchURI);

        //Get albumList from iTunes API
        Mono<String> asyncAlbumItems = (Mono<String>)
                beanFactory.getBean("asyncItunesWebClient", albumSearchURI);
        asyncAlbumItems.subscribe(albumItems -> {
            log.info("Items in iTunes API: {}", albumItems);
            JSONObject result = new JSONObject(albumItems);
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            ITunesAlbumAPIResult iTunesAlbumAPIResult;
            try {
                iTunesAlbumAPIResult = objectMapper.readValue(result.toString(), ITunesAlbumAPIResult.class);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
            if (iTunesAlbumAPIResult.getResultCount() == 0) {
                log.error("No data retrieved from the Itunes API");
            } else {
                for (ItunesAlbumItem itunesAlbum : iTunesAlbumAPIResult.getResults()) {
                    LibraryItem libraryItem = new LibraryItem(itunesAlbum.getCollectionName(),
                            Arrays.asList(itunesAlbum.getArtistName().split(",")),
                            "Album");
                    libraryItemList.add(libraryItem);
                }
            }

        });

        //Wait the main thread until predefined time period to complete upstream services
        try {
            Thread.sleep(waitTIme);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }


        if (libraryItemList.isEmpty()) {
            throw new NoRecordException("Data not found for input term : " + keyword);
        }

        //Sort results
        Collections.sort(libraryItemList);
        log.info("Num of retrieved data for keyword:" + keyword + " = " + libraryItemList.size());

        return libraryItemList;

    }
}
