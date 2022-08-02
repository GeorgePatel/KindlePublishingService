package com.amazon.ata.kindlepublishingservice.publishing;

import com.amazon.ata.recommendationsservice.types.BookGenre;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.ConcurrentLinkedQueue;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class BookPublishRequestManagerTest {

    private BookPublishRequestManager bookPublishRequestManager;
    private ConcurrentLinkedQueue<BookPublishRequest> bookPublishRequestQueue;

    @BeforeEach
    public void setUp() {
        bookPublishRequestQueue = new ConcurrentLinkedQueue<>();
        bookPublishRequestManager = new BookPublishRequestManager(bookPublishRequestQueue);
    }

    @Test
    public void addBookPublishRequest_givenBookPublishRequests_addsToEndOfQueue() {
        //GIVEN
        BookPublishRequest bookPublishRequest1 = BookPublishRequest.builder()
                .withPublishingRecordId("publishingRecordId1")
                .withBookId("bookId1")
                .withTitle("title1")
                .withAuthor("author1")
                .withText("text1")
                .withGenre(BookGenre.FANTASY)
                .build();

        BookPublishRequest bookPublishRequest2 = BookPublishRequest.builder()
                .withPublishingRecordId("publishingRecordId2")
                .withBookId("bookId2")
                .withTitle("title2")
                .withAuthor("author2")
                .withText("text2")
                .withGenre(BookGenre.HISTORICAL_FICTION)
                .build();

        //WHEN
        bookPublishRequestManager.addBookPublishRequest(bookPublishRequest1);
        bookPublishRequestManager.addBookPublishRequest(bookPublishRequest2);

        //THEN
        assertEquals(bookPublishRequestQueue.peek(), bookPublishRequest1,
                "Expected bookPublishRequest1 to be the first request in Queue.");
        assertEquals(bookPublishRequestQueue.size(), 2, "Expected Queue to contain two requests.");
    }

    @Test
    public void getBookPublishRequestToProcess_emptyQueue_returnsNull() {
        //WHEN
        BookPublishRequest result = bookPublishRequestManager.getBookPublishRequestToProcess();

        //THEN
        assertNull(result, "Empty Queue should've returned null.");
    }

    @Test
    public void getBookPublishRequestToProcess_queueWithRequests_returnsFirstRequestInQueue() {
        //GIVEN
        BookPublishRequest bookPublishRequest1 = BookPublishRequest.builder()
                .withPublishingRecordId("publishingRecordId1")
                .withBookId("bookId1")
                .withTitle("title1")
                .withAuthor("author1")
                .withText("text1")
                .withGenre(BookGenre.FANTASY)
                .build();

        BookPublishRequest bookPublishRequest2 = BookPublishRequest.builder()
                .withPublishingRecordId("publishingRecordId2")
                .withBookId("bookId2")
                .withTitle("title2")
                .withAuthor("author2")
                .withText("text2")
                .withGenre(BookGenre.HISTORICAL_FICTION)
                .build();

        bookPublishRequestQueue.add(bookPublishRequest1);
        bookPublishRequestQueue.add(bookPublishRequest2);

        //WHEN
        BookPublishRequest result = bookPublishRequestManager.getBookPublishRequestToProcess();

        //THEN
        assertEquals(bookPublishRequest1.getPublishingRecordId(), result.getPublishingRecordId(),
                "Expected PublishingRecordId of the first request in Queue.");
        assertEquals(bookPublishRequest1.getBookId(), result.getBookId(),
                "Expected BookId of the first request in Queue.");
        assertEquals(bookPublishRequest1.getTitle(), result.getTitle(),
                "Expected Title of the first request in Queue.");
        assertEquals(bookPublishRequest1.getAuthor(), result.getAuthor(),
                "Expected Author of the first request in Queue.");
        assertEquals(bookPublishRequest1.getText(), result.getText(),
                "Expected Text of the first request in Queue.");
        assertEquals(bookPublishRequest1.getGenre(), result.getGenre(),
                "Expected Genre of the first request in Queue.");
    }
}
