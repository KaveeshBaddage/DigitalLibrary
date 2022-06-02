package com.kramphub.digitallibrary.dao;

import lombok.Data;
import java.io.Serializable;
import java.util.ArrayList;


/**
 * ITunesAlbumAPIResult class represents the Album object  which contains albums information retrieved from ITUnes Album API
 *
 * @author Kaveesha Baddage
 * *
 */
@Data
public class ITunesAlbumAPIResult implements Serializable {

    private int resultCount;
    private ArrayList<ItunesAlbumItem> results;
}
