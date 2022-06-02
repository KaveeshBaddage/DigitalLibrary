package com.kramphub.digitallibrary.dao;

import lombok.Data;
import java.util.Date;

/**
 * ItunesAlbumItem class represents the single Album   which contains album information retrieved from Itunes Album API
 * @author Kaveesha Baddage
 * *
 */

@Data
public class ItunesAlbumItem {
    private String wrapperType;
    private String kind;
    private int artistId;
    private int collectionId;
    private int trackId;
    private String artistName;
    private String collectionName;
    private String trackName;
    private String collectionCensoredName;
    private String trackCensoredName;
    private String artistViewUrl;
    private String collectionViewUrl;
    private String trackViewUrl;
    private String previewUrl;
    private String artworkUrl30;
    private String artworkUrl60;
    private String artworkUrl100;
    private double collectionPrice;
    private double trackPrice;
    private Date releaseDate;
    private String collectionExplicitness;
    private String trackExplicitness;
    private int discCount;
    private int discNumber;
    private int trackCount;
    private String copyright;
    private int trackNumber;
    private int trackTimeMillis;
    private String country;
    private String currency;
    private String primaryGenreName;
    private boolean isStreamable;
    private String contentAdvisoryRating;
}
