package com.kramphub.digitallibrary.dao;

import lombok.Data;
import java.io.Serializable;
import java.util.List;


/**
 * BookVolumeInfo class represents the  embedded  book object in the response of Google Book API
 * * @author Kaveesha Baddage
 * *
 */


@Data
public class BookVolumeInfo implements Serializable {

    private String title;
    private List<String> authors;
}
