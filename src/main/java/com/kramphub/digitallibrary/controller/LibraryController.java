package com.kramphub.digitallibrary.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

import com.kramphub.digitallibrary.dao.LibraryItem;
import com.kramphub.digitallibrary.service.EntityService;

/**
 * REST Controller class to handle REST API endpoints to retrieve Book and album
 * * @author Kaveesha Baddage
 * *
 */

@RestController
@RequestMapping("/library")
@Tag(name = "Digital Library API", description = "Expose REST API endpoint to retrieve book and album information from Google and iTunes" +
        " upstream services. ")
public class LibraryController {

    @Autowired
    EntityService entityService;

    @Operation(summary = "Find Album/Book information by term",
            description = "Search results will be sorted by the title alphabetically. " +
                    "Number of record from each entity(Album/Book) will be depends on pre configured value in application properties."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Successful API call will fetch data from upstream service and list down all the album and book information.",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = LibraryItem.class)))),
            @ApiResponse(responseCode = "400",
                    description = "Failed API call will return HTTP 400 status code.",
                    content = @Content())})
    @GetMapping(value = "/itemList", produces = {"application/json"})
    private List<LibraryItem> searchByParam(@Parameter(description = "Term of the Album/Book. Cannot be empty.",
            required = true) @RequestParam(required = true) String term) throws Exception {
        return entityService.findByKeyword(term);
    }

}
