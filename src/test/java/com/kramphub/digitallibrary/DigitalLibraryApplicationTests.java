package com.kramphub.digitallibrary;

import com.kramphub.digitallibrary.service.EntityServiceImpl;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@SpringBootTest(classes = DigitalLibraryApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class DigitalLibraryApplicationTests {


    @Autowired
    EntityServiceImpl entityService;

    @Test
    void testSortLibraryItem() {

        List<String> unorderedList = Arrays.asList("The Weekend", "Mother Love Bon", "Michael Phillips");
        List<String> orderedList = Arrays.asList("Michael Phillips", "Mother Love Bon", "The Weekend");
        Collections.sort(unorderedList);

        Assert.assertEquals(orderedList, unorderedList);
    }

}
