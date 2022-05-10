package com.kramphub.digitallibrary.dao;

import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * AlbumList class use to represent collection of {@link Album}s in a {@link List}
 * @author Kaveesha Baddage
 * *
 */
@Data
public class AlbumList implements Serializable {

    private List<Album> albumList;

    public AlbumList(){
        albumList = new ArrayList<>();
    }
}
