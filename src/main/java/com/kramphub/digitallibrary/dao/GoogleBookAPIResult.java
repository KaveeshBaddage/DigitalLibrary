package com.kramphub.digitallibrary.dao;

import lombok.Data;
import java.io.Serializable;
import java.util.ArrayList;


/**
 * GoogleBookAPIResult class represents the Book object  which contains book information retrieved from Google Book API
 * @author Kaveesha Baddage
 * *
 */
@Data
public class GoogleBookAPIResult implements Serializable {

    private String kind;
    private long totalItems;
    private ArrayList<GoogleBookItem> items;
}
