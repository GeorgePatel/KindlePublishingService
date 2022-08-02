package com.amazon.ata.kindlepublishingservice.activity;

import com.amazon.ata.kindlepublishingservice.dao.CatalogDao;
import com.amazon.ata.kindlepublishingservice.dao.PublishingStatusDao;
import com.amazon.ata.kindlepublishingservice.dynamodb.models.CatalogItemVersion;
import com.amazon.ata.kindlepublishingservice.dynamodb.models.PublishingStatusItem;
import com.amazon.ata.kindlepublishingservice.enums.PublishingRecordStatus;
import com.amazon.ata.kindlepublishingservice.exceptions.BookNotFoundException;
import com.amazon.ata.kindlepublishingservice.exceptions.KindlePublishingClientException;
import com.amazon.ata.kindlepublishingservice.models.requests.SubmitBookForPublishingRequest;
import com.amazon.ata.kindlepublishingservice.models.response.SubmitBookForPublishingResponse;
import com.amazon.ata.kindlepublishingservice.publishing.BookPublishRequest;
import com.amazon.ata.kindlepublishingservice.publishing.BookPublishRequestManager;
import com.amazon.ata.recommendationsservice.types.BookGenre;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.PaginatedQueryList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class SubmitBookForPublishingActivityTest {

    @Mock
    private PublishingStatusDao publishingStatusDao;

    @Mock
    private CatalogDao catalogDao;

    @Mock
    private BookPublishRequestManager bookPublishRequestManager;

    @Mock
    private PaginatedQueryList<CatalogItemVersion> list;

    @Mock
    private DynamoDBMapper dynamoDbMapper;

    @InjectMocks
    private SubmitBookForPublishingActivity activity;

    @BeforeEach
    public void setup() {
        initMocks(this);
    }

    @Test
    public void execute_activeBookIdInRequest_bookQueuedForPublishing() throws KindlePublishingClientException {
        // GIVEN
        String activeBookId = "book.061bd15f-1a23-4bb0-a733-9f43859ca4e7";
        SubmitBookForPublishingRequest request = SubmitBookForPublishingRequest.builder()
                .withAuthor("Author")
                .withTitle("Title")
                .withBookId(activeBookId)
                .withGenre(BookGenre.FANTASY.name())
                .build();

        PublishingStatusItem item = new PublishingStatusItem();
        item.setPublishingRecordId("publishing.123");

        // an active CatalogItemVersion
        CatalogItemVersion book = new CatalogItemVersion();
        book.setInactive(false);
        book.setBookId(activeBookId);
        book.setVersion(1);

        // return that Book
        when(dynamoDbMapper.query(eq(CatalogItemVersion.class), any(DynamoDBQueryExpression.class))).thenReturn(list);
        when(list.isEmpty()).thenReturn(false);
        when(list.get(0)).thenReturn(book);

        // KindlePublishingUtils generates a random publishing status ID for us
        when(publishingStatusDao.setPublishingStatus(anyString(),
                eq(PublishingRecordStatus.QUEUED),
                eq(request.getBookId()))).thenReturn(item);
        // WHEN
        SubmitBookForPublishingResponse response = activity.execute(request);

        // THEN
        verify(catalogDao).validateBookExists(activeBookId); // Checks to see if Book exists in Catalog
        verify(bookPublishRequestManager).addBookPublishRequest(any(BookPublishRequest.class)); // BookPublishRequest is added to the bookPublishRequestManager
        assertEquals("publishing.123", response.getPublishingRecordId(), "Expected response to return a publishing" +
                "record id.");
    }

    @Test
    public void execute_inactiveBookIdInRequest_inactiveBookQueuedForPublishing() throws KindlePublishingClientException {
        // GIVEN
        String inactiveBookId = "book.1b12b4ed-1b2c-4b33-a823-fbfa393d2404";
        SubmitBookForPublishingRequest request = SubmitBookForPublishingRequest.builder()
                .withAuthor("Author")
                .withTitle("Title")
                .withBookId(inactiveBookId)
                .withGenre(BookGenre.FANTASY.name())
                .build();

        PublishingStatusItem item = new PublishingStatusItem();
        item.setPublishingRecordId("publishing.123");

        // an inactive CatalogItemVersion
        CatalogItemVersion book = new CatalogItemVersion();
        book.setInactive(true);
        book.setBookId(inactiveBookId);
        book.setVersion(1);

        // return that Book
        when(dynamoDbMapper.query(eq(CatalogItemVersion.class), any(DynamoDBQueryExpression.class))).thenReturn(list);
        when(list.isEmpty()).thenReturn(false);
        when(list.get(0)).thenReturn(book);

        // KindlePublishingUtils generates a random publishing status ID for us
        when(publishingStatusDao.setPublishingStatus(anyString(),
                eq(PublishingRecordStatus.QUEUED),
                eq(request.getBookId()))).thenReturn(item);
        // WHEN
        SubmitBookForPublishingResponse response = activity.execute(request);

        // THEN
        verify(catalogDao).validateBookExists(inactiveBookId); // Checks to see if Book exists in Catalog
        verify(bookPublishRequestManager).addBookPublishRequest(any(BookPublishRequest.class)); // BookPublishRequest is added to the bookPublishRequestManager
        assertEquals("publishing.123", response.getPublishingRecordId(), "Expected response to return a publishing" +
                "record id.");
    }

    @Test
    public void execute_invalidBookIdInRequest_throwsKindlePublishingClientException() throws KindlePublishingClientException {
        // GIVEN
        String invalidBookId = "nonExistantBookId";
        SubmitBookForPublishingRequest request = SubmitBookForPublishingRequest.builder()
                .withAuthor("Author")
                .withTitle("Title")
                .withBookId(invalidBookId)
                .withGenre(BookGenre.FANTASY.name())
                .build();

        // WHEN + THEN
        /*verify(bookPublishRequestManager, times(1)).addBookPublishRequest(any(BookPublishRequest.class)); // BookPublishRequest is added to the bookPublishRequestManager
        verify(publishingStatusDao).setPublishingStatus(anyString(), // KindlePublishingUtils generates a random publishing status ID for us
                eq(PublishingRecordStatus.QUEUED),
                eq(request.getBookId()));*/
        doThrow(BookNotFoundException.class).when(catalogDao).validateBookExists(invalidBookId); // Checks to see if Book exists in Catalog*/
        assertThrows(KindlePublishingClientException.class, () -> activity.execute(request));
    }

    @Test
    public void execute_noBookIdInRequest_bookQueuedForPublishing() throws KindlePublishingClientException {
        // GIVEN
        SubmitBookForPublishingRequest request = SubmitBookForPublishingRequest.builder()
                .withAuthor("Author")
                .withTitle("Title")
                .withGenre(BookGenre.FANTASY.name())
                .build();

        PublishingStatusItem item = new PublishingStatusItem();
        item.setPublishingRecordId("publishing.123");
        when(publishingStatusDao.setPublishingStatus(anyString(),
                eq(PublishingRecordStatus.QUEUED),
                isNull())).thenReturn(item);

        // WHEN
        SubmitBookForPublishingResponse response = activity.execute(request);

        // THEN
        verify(bookPublishRequestManager).addBookPublishRequest(any(BookPublishRequest.class)); // BookPublishRequest is added to the bookPublishRequestManager
        assertEquals("publishing.123", response.getPublishingRecordId(), "Expected response to return a publishing" +
                "record id.");
    }


}
