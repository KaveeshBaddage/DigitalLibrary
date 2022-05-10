package com.kramphub.digitallibrary.service;

import com.kramphub.digitallibrary.dao.Album;
import com.kramphub.digitallibrary.dao.Book;
import com.kramphub.digitallibrary.dao.LibraryItem;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;


/**
 *   EntityService Interface abstracts the service method which implements the logic to retrieve {@link Album} and
 *   {@link Book} data from upstream services
 * * @author Kaveesha Baddage
 * *
 */

public interface EntityService {

    public List<LibraryItem> findByKeyword(String keyword) throws URISyntaxException, IOException;
}
