package com.kramphub.digitallibrary.service;

import com.kramphub.digitallibrary.dao.Album;
import com.kramphub.digitallibrary.dao.Book;
import com.kramphub.digitallibrary.dao.LibraryItem;
import com.kramphub.digitallibrary.exception.NoRecordException;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.ssl.SSLContextBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 *  EntityServiceImpl Class implements the methods defined in  {@link EntityService} Interface.
 *  This service uses Google Book API and iTunes API as upstream services to retrieve {@link Album} and {@link Book} data
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

    @Value("${google.book.url}")
    String googleBookAPIUrl;

    @Value("${apple.album.url}")
    String itunesAlbumAPIUrl;

    @Value("${response.entityLimit}")
    Integer resultLimit;

    public EntityServiceImpl() throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
        if (client == null) {
            // Initiate http connection if it is not exist
            initiateConnection();
        }
    }

    private void initiateConnection() throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException {

        HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();
        SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial(null, new TrustStrategy() {
            @Override
            public boolean isTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
                return true;
            }
        }).build();
        httpClientBuilder.setSSLContext(sslContext);
        HostnameVerifier hostnameVerifier = SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER;
        SSLConnectionSocketFactory sslSocketFactory = new SSLConnectionSocketFactory(sslContext, hostnameVerifier);
        Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create().
                register("http", PlainConnectionSocketFactory.getSocketFactory()).register("https", sslSocketFactory).build();
        PoolingHttpClientConnectionManager connMgr = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
        httpClientBuilder.setConnectionManager(connMgr);
        client = httpClientBuilder.build();
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

        googleRequest.setURI(bookSearchURI);


        // Create  HTTP request  to call iTunes Album API
        HttpGet itunesRequest = new HttpGet(itunesAlbumAPIUrl);

        URI albumSearchURI = new URIBuilder(itunesRequest.getURI())
                .addParameter(ITUNES_ALBUM_API_QUERY_PARAM, keyword)
                .addParameter(ITUNES_API_RESULT_LIMIT_PARAM, String.valueOf(resultLimit))
                .addParameter(ITUNES_API_ENTITY_QUERY_PARAM, ITUNES_ALBUM_API_ALBUM_ENTITY)
                .build();

        itunesRequest.setURI(albumSearchURI);

        //Get bookList from Google Book API
        final Future<List<LibraryItem>> futureBooks = executorService.submit(new FutureBookService(client,
                googleRequest));
        futureLibraryItemList.add(futureBooks);

        //Get albumList from iTunes API
        final Future<List<LibraryItem>> futureAlbums = executorService.submit(new FutureAlbumService(client,
                itunesRequest));
        futureLibraryItemList.add(futureAlbums);

        //get result from Future
        for (final Future<List<LibraryItem>> futureItem : futureLibraryItemList) {
            try {
                libraryItemList.addAll(futureItem.get(5, TimeUnit.SECONDS));
            } catch (Exception ex) {
                log.error("Error while getting future objects ", ex);
                continue;
            }
        }

        if (libraryItemList.isEmpty()) {
            throw new NoRecordException("Data not found for input term : " + keyword);
        }

        //Sort results
        Collections.sort(libraryItemList);
        log.info("Num of retrieved data for keyword:" + keyword + " = " + libraryItemList.size() );

        return libraryItemList;

    }
}
