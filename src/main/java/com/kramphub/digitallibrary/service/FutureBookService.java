package com.kramphub.digitallibrary.service;

import com.kramphub.digitallibrary.dao.Book;
import com.kramphub.digitallibrary.dao.BookList;
import com.kramphub.digitallibrary.dao.BookVolumeInfo;
import com.kramphub.digitallibrary.dao.LibraryItem;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

/**
 *  FutureBookService Class contains the logic to retrieve Book information from Google Upstream service as Future objects
 * *@author Kaveesha Baddage
 * *
 */
public class FutureBookService implements Callable<List<LibraryItem>> {
    private final HttpClient httpClient;
    private final HttpGet request;

    public FutureBookService(HttpClient httpClient, HttpGet requestURL) {
        this.httpClient = httpClient;
        this.request = requestURL;
    }

    private LibraryItem getBookListFromResponse(Book bookObject) {
        return new LibraryItem(bookObject.getBookVolumeInfo().getTitle(),
                bookObject.getBookVolumeInfo().getAuthors(), "Book");
    }


    @Override
    public List<LibraryItem> call() throws IOException, JSONException {

        List<LibraryItem> list = new ArrayList<>();

        BookList bookListResponse = new BookList();

        HttpResponse response = httpClient.execute(request);
        HttpEntity entity = response.getEntity();

        int statusCode = response.getStatusLine().getStatusCode();

        if (statusCode == 200) {
            String retSrc = EntityUtils.toString(entity);
            JSONObject result = new JSONObject(retSrc);
            int totalItems = result.getInt("totalItems");
            if(totalItems>0){
                JSONArray booksInResponse = result.getJSONArray("items");

                ArrayList<Book> retrievedBooks = new ArrayList<>();

                for (int i = 0; i < booksInResponse.length(); i++) {

                    JSONObject volumeInfo = booksInResponse.getJSONObject(i).getJSONObject("volumeInfo");
                    String title ;
                    JSONArray authors ;

                    //Validate relevant data exist in the upstream service's response and retrieve them after the verification
                    if(volumeInfo.has("title")){
                        title = volumeInfo.getString("title");
                    }else{
                        title = "";
                    }

                    if(volumeInfo.has("authors")){
                        authors = volumeInfo.getJSONArray("authors");
                    }else{
                        authors = new JSONArray();
                    }


                    ArrayList<String> authorsNames = new ArrayList<>();
                    for (int k = 0; k < authors.length(); k++) {
                        String authorName = authors.getString(k);
                        authorsNames.add(authorName);
                    }
                    BookVolumeInfo bookVolumeInfo = new BookVolumeInfo();
                    bookVolumeInfo.setTitle(title);
                    bookVolumeInfo.setAuthors(authorsNames);
                    Book book = new Book();
                    book.setBookVolumeInfo(bookVolumeInfo);
                    retrievedBooks.add(book);
                }

                bookListResponse.setBookList(retrievedBooks);
            }else{
                bookListResponse = null;
            }
        }else{
            bookListResponse = null;
        }


        if (bookListResponse != null && !CollectionUtils.isEmpty(bookListResponse.getBookList())) {
            List<LibraryItem> books = bookListResponse.getBookList().stream()
                    .map(this::getBookListFromResponse).collect(Collectors.toList());
            list.addAll(books);
        }
        return list;
    }
}
