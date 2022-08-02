package com.amazon.ata.kindlepublishingservice.dao;

import com.amazon.ata.kindlepublishingservice.dynamodb.models.CatalogItemVersion;
import com.amazon.ata.kindlepublishingservice.exceptions.BookNotFoundException;
import com.amazon.ata.kindlepublishingservice.publishing.KindleFormattedBook;
import com.amazon.ata.kindlepublishingservice.utils.KindlePublishingUtils;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;

import javax.inject.Inject;
import java.util.List;

public class CatalogDao {

    private final DynamoDBMapper dynamoDbMapper;

    /**
     * Instantiates a new CatalogDao object.
     *
     * @param dynamoDbMapper The {@link DynamoDBMapper} used to interact with the catalog table.
     */
    @Inject
    public CatalogDao(DynamoDBMapper dynamoDbMapper) {
        this.dynamoDbMapper = dynamoDbMapper;
    }

    /**
     * Returns the latest version of the book from the catalog corresponding to the specified book id.
     * Throws a BookNotFoundException if the latest version is not active or no version is found.
     * @param bookId Id associated with the book.
     * @return The corresponding CatalogItem from the catalog table.
     */
    public CatalogItemVersion getBookFromCatalog(String bookId) {
        CatalogItemVersion book = getLatestVersionOfBook(bookId);

        if (book == null || book.isInactive()) {
            throw new BookNotFoundException(String.format("No book found for id: %s", bookId));
        }

        return book;
    }

    // Returns null if no version exists for the provided bookId
    private CatalogItemVersion getLatestVersionOfBook(String bookId) {
        CatalogItemVersion book = new CatalogItemVersion();
        book.setBookId(bookId);

        DynamoDBQueryExpression<CatalogItemVersion> queryExpression = new DynamoDBQueryExpression()
            .withHashKeyValues(book)
            .withScanIndexForward(false)
            .withLimit(1);

        List<CatalogItemVersion> results = dynamoDbMapper.query(CatalogItemVersion.class, queryExpression);
        if (results.isEmpty()) {
            return null;
        }
        return results.get(0);
    }


    public CatalogItemVersion removeBookFromCatalog(String bookId) {
        CatalogItemVersion book = getLatestVersionOfBook(bookId);

        if (book == null || book.isInactive()) {
            throw new BookNotFoundException(String.format("No book found for id: %s", bookId));
        }

        book.setInactive(true);
        dynamoDbMapper.save(book);

        return book;
    }

    public void validateBookExists(String bookId) {
        CatalogItemVersion book = this.getLatestVersionOfBook(bookId);

        if (book == null) {
            throw new BookNotFoundException(String.format("No book found for id: %s", bookId));
        }
    }

    public CatalogItemVersion createOrUpdateBook(KindleFormattedBook kindleFormattedBook) {
        if (kindleFormattedBook.getBookId() == null) {
            return createBook(kindleFormattedBook);
        } else {
            return updateBook(kindleFormattedBook);
        }
    }

    private CatalogItemVersion createBook(KindleFormattedBook kindleFormattedBook) {
        CatalogItemVersion book = new CatalogItemVersion();
        book.setBookId(KindlePublishingUtils.generateBookId());
        book.setVersion(1);
        book.setInactive(false);
        book.setTitle(kindleFormattedBook.getTitle());
        book.setAuthor(kindleFormattedBook.getAuthor());
        book.setText(kindleFormattedBook.getText());
        book.setGenre(kindleFormattedBook.getGenre());

        dynamoDbMapper.save(book);

        return book;
    }

    private CatalogItemVersion updateBook(KindleFormattedBook kindleFormattedBook) {
        CatalogItemVersion existingBook = getLatestVersionOfBook(kindleFormattedBook.getBookId());

        if (existingBook == null) {
            throw new BookNotFoundException(String.format("No book found for id: %s", kindleFormattedBook.getBookId()));
        }

        existingBook.setInactive(true);
        dynamoDbMapper.save(existingBook);

        CatalogItemVersion newVersion = new CatalogItemVersion();
        newVersion.setBookId(existingBook.getBookId());
        newVersion.setVersion(existingBook.getVersion() + 1);
        newVersion.setInactive(false);
        newVersion.setTitle(kindleFormattedBook.getTitle());
        newVersion.setAuthor(kindleFormattedBook.getAuthor());
        newVersion.setText(kindleFormattedBook.getText());
        newVersion.setGenre(kindleFormattedBook.getGenre());

        dynamoDbMapper.save(newVersion);

        return newVersion;
    }
}
