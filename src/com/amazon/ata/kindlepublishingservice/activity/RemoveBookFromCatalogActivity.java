package com.amazon.ata.kindlepublishingservice.activity;

import com.amazon.ata.kindlepublishingservice.converters.CatalogItemConverter;
import com.amazon.ata.kindlepublishingservice.dao.CatalogDao;
import com.amazon.ata.kindlepublishingservice.dynamodb.models.CatalogItemVersion;
import com.amazon.ata.kindlepublishingservice.models.requests.RemoveBookFromCatalogRequest;
import com.amazon.ata.kindlepublishingservice.models.response.RemoveBookFromCatalogResponse;

import javax.inject.Inject;

/**
 * Implementation of the RemoveBookFromCatalogActivity for the ATACurriculumKindlePublishingService's
 * RemoveBook API.
 *
 * This API allows the client to remove a book.
 */
public class RemoveBookFromCatalogActivity {
    private CatalogDao catalogDao;

    /**
     * Instantiates a new RemoveBookFromCatalogActivity object.
     *
     * @param catalogDao CatalogDao to access the Catalog table.
     */
    @Inject
    public RemoveBookFromCatalogActivity(CatalogDao catalogDao) {
        this.catalogDao = catalogDao;
    }

    /**
     * Soft delete the book associated with the provided book id.
     *
     * @param removeBookFromCatalogRequest Request object containing the book ID associated with the book to remove from the Catalog.
     * @return RemoveBookFromCatalogResponse Response object containing the removed book.
     */
    public RemoveBookFromCatalogResponse execute(RemoveBookFromCatalogRequest removeBookFromCatalogRequest) {
        CatalogItemVersion book = catalogDao.removeBookFromCatalog(removeBookFromCatalogRequest.getBookId());

        return RemoveBookFromCatalogResponse.builder()
                .withBook(CatalogItemConverter.toBook(book))
                .build();
    }
}
