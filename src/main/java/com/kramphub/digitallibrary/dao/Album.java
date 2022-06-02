package com.kramphub.digitallibrary.dao;

import lombok.Data;
import java.io.Serializable;

/**
 * Album class represents the Album object  which contains album information retrieved from itunes API
 * * @author Kaveesha Baddage
 * *
 */

@Data
public class Album implements Serializable {

    private String trackName;
    private String  artistName;
}
