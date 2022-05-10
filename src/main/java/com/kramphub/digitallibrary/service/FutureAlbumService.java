package com.kramphub.digitallibrary.service;

import com.kramphub.digitallibrary.dao.Album;
import com.kramphub.digitallibrary.dao.AlbumList;
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
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

/**
 * FutureAlbumService Class contains the logic to retrieve Album information from iTunes Upstream service as Future objects
 * *@author Kaveesha Baddage
 * *
 */
public class FutureAlbumService implements Callable<List<LibraryItem>> {

    private final HttpClient httpClient;
    private final HttpGet request;

    public FutureAlbumService(HttpClient httpClient, HttpGet requestURL) {
        this.httpClient = httpClient;
        this.request = requestURL;
    }

    private LibraryItem getAlbumListFromResponse(Album albumObject) {
        List<String> artists = Arrays.asList(albumObject.getArtistName());
        return new LibraryItem(albumObject.getTrackName(), artists, "Album");
    }


    @Override
    public List<LibraryItem> call() throws IOException, JSONException {

        List<LibraryItem> list = new ArrayList<>();

        AlbumList albumListResponse = new AlbumList();

        HttpResponse response = httpClient.execute(request);
        HttpEntity entity = response.getEntity();

        int statusCode = response.getStatusLine().getStatusCode();

        if (statusCode == 200) {
            String retSrc = EntityUtils.toString(entity);
            JSONObject result = new JSONObject(retSrc);
            JSONArray albumsInResponse = result.getJSONArray("results");

            ArrayList<Album> retrievedAlbums = new ArrayList<>();

            if (albumsInResponse.length() > 0) {
                //Validate relevant data exist in the upstream service's response and retrieve them after the verification
                for (int i = 0; i < albumsInResponse.length(); i++) {
                    String artistName;
                    String trackName;
                    if (albumsInResponse.getJSONObject(i).has("artistName")) {
                        artistName = albumsInResponse.getJSONObject(i).getString("artistName");
                    } else {
                        artistName = "";
                    }

                    if (albumsInResponse.getJSONObject(i).has("artistName")) {
                        trackName = albumsInResponse.getJSONObject(i).getString("collectionName");
                    } else {
                        trackName = "";
                    }

                    Album album = new Album();
                    album.setArtistName(artistName);
                    album.setTrackName(trackName);
                    retrievedAlbums.add(album);
                }
                albumListResponse.setAlbumList(retrievedAlbums);
            } else {
                albumListResponse = null;
            }
        }


        if (albumListResponse != null && !CollectionUtils.isEmpty(albumListResponse.getAlbumList())) {
            List<LibraryItem> albums = albumListResponse.getAlbumList().stream()
                    .map(this::getAlbumListFromResponse).collect(Collectors.toList());
            list.addAll(albums);
        }
        return list;
    }
}
