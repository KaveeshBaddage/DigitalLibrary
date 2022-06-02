package com.kramphub.digitallibrary.dao;

import lombok.Data;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * BookList class use to represent collection of {@link Book}s in a {@link List}
 * @author Kaveesha Baddage
 * *
 */
@Data
public class BookList implements Serializable {

    private List<Book> bookList;

    public BookList(){
        bookList = new ArrayList<>();
    }
}
