package com.kramphub.digitallibrary.dao;

import lombok.Data;
import java.util.ArrayList;
/**
 * GoogleBookItem class represents the single Book  which contains book information retrieved from Google Book API
 * @author Kaveesha Baddage
 * *
 */
@Data
public class GoogleBookItem {
    private VolumeInfo volumeInfo;
    private String kind;
    private String etag;

    @Data
    public class VolumeInfo{
       String title;
       ArrayList<String> authors;
    }

}
