package com.kramphub.digitallibrary.dao;

import lombok.Data;
import java.io.Serializable;


/**
 * Book class represents the Book object  which contains book information retrieved from Google Book API
 * @author Kaveesha Baddage
 * *
 */
@Data
public class Book implements Serializable {

    private BookVolumeInfo bookVolumeInfo;

}
