package com.kramphub.digitallibrary.dao;

import lombok.Data;
import java.io.Serializable;
import java.util.Comparator;
import java.util.List;

/**
 * BookVolumeInfo class represents the single response entity of the LibraryController
 * * @author Kaveesha Baddage
 * *
 */
@Data
public class LibraryItem implements Serializable, Comparable<LibraryItem> {

    private String itemTitle;

    private List<String> itemAuthors;

    private String itemType;

    public LibraryItem(String itemTitle,List<String> itemAuthor,String itemType){
        this.itemTitle = itemTitle;
        this.itemAuthors = itemAuthor;
        this.itemType = itemType;
    }

    //Create comparator which is null-friendly comparator and considers null values as the last ones.
    private static Comparator<LibraryItem> itemTitleComparator = Comparator.comparing(LibraryItem::getItemTitle,
            Comparator.nullsLast(String::compareToIgnoreCase));


    @Override
    public int compareTo(LibraryItem libraryItem) {
        return itemTitleComparator.compare(this,libraryItem);
    }



}
