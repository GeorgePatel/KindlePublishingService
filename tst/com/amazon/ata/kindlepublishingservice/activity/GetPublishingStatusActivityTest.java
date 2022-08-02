package com.amazon.ata.kindlepublishingservice.activity;

import com.amazon.ata.kindlepublishingservice.dao.PublishingStatusDao;
import com.amazon.ata.kindlepublishingservice.dynamodb.models.PublishingStatusItem;
import com.amazon.ata.kindlepublishingservice.enums.PublishingRecordStatus;
import com.amazon.ata.kindlepublishingservice.exceptions.KindlePublishingClientException;
import com.amazon.ata.kindlepublishingservice.exceptions.PublishingStatusNotFoundException;
import com.amazon.ata.kindlepublishingservice.models.requests.GetPublishingStatusRequest;
import com.amazon.ata.kindlepublishingservice.models.response.GetPublishingStatusResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class GetPublishingStatusActivityTest {

    @Mock
    private PublishingStatusDao publishingStatusDao;

    @InjectMocks
    private GetPublishingStatusActivity getPublishingStatusActivity;

    @BeforeEach
    public void setUp() {
        initMocks(this);
    }

    @Test
    public void execute_successfulPublishingProcessForExistingBook_returnsPublishingStatusItemsWithBookIds() throws KindlePublishingClientException {
        // GIVEN
        String publishingStatusId = "publishingstatus.bdd319cb-05eb-494b-983f-6e1b983c4c46";
        String bookId = "book.b3750190-2a30-4ca8-ae1b-73d0d202dc41";
        GetPublishingStatusRequest request = GetPublishingStatusRequest.builder().withPublishingRecordId(publishingStatusId).build();

        PublishingStatusItem queuedItem = new PublishingStatusItem();
        queuedItem.setPublishingRecordId(publishingStatusId);
        queuedItem.setStatus(PublishingRecordStatus.QUEUED);
        queuedItem.setStatusMessage("Queued for publishing at 2022-05-06 23:04:05.32");
        queuedItem.setBookId(bookId);

        PublishingStatusItem inProgressItem = new PublishingStatusItem();
        inProgressItem.setPublishingRecordId(publishingStatusId);
        inProgressItem.setStatus(PublishingRecordStatus.IN_PROGRESS);
        inProgressItem.setStatusMessage("Processing started at 2022-05-06 23:04:05.894");
        inProgressItem.setBookId(bookId);

        PublishingStatusItem successfulItem = new PublishingStatusItem();
        successfulItem.setPublishingRecordId(publishingStatusId);
        successfulItem.setStatus(PublishingRecordStatus.SUCCESSFUL);
        successfulItem.setStatusMessage("Book published at 2022-05-05 15:36:14.127");
        successfulItem.setBookId(bookId);

        List<PublishingStatusItem> statusList = new ArrayList<>();
        statusList.add(queuedItem);
        statusList.add(inProgressItem);
        statusList.add(successfulItem);

        // WHEN
        when(publishingStatusDao.getPublishingStatusList(publishingStatusId)).thenReturn(statusList);
        GetPublishingStatusResponse result = getPublishingStatusActivity.execute(request);

        // THEN
        assertEquals(bookId, result.getPublishingStatusHistory().get(0).getBookId(), "Expected first PublishingStatusItem's bookId to be: " + bookId);
        assertEquals(bookId, result.getPublishingStatusHistory().get(1).getBookId(), "Expected second PublishingStatusItem's bookId to be: " + bookId);
        assertEquals(bookId, result.getPublishingStatusHistory().get(2).getBookId(), "Expected third PublishingStatusItem's bookId to be: " + bookId);

        assertTrue(result.getPublishingStatusHistory().get(0).getStatusMessage().contains("Queued for publishing"), "Expected first PublishingStatusItem's statusMessage to contain the phrase: Queued for publishing");
        assertTrue(result.getPublishingStatusHistory().get(1).getStatusMessage().contains("Processing started at"), "Expected second PublishingStatusItem's statusMessage to contain the phrase: Processing started at");
        assertTrue(result.getPublishingStatusHistory().get(2).getStatusMessage().contains("Book published at"), "Expected third PublishingStatusItem's statusMessage to contain the phrase: Book published at");

        assertEquals("QUEUED", result.getPublishingStatusHistory().get(0).getStatus(), "Expected first PublishingStatusItem's status to be QUEUED.");
        assertEquals("IN_PROGRESS", result.getPublishingStatusHistory().get(1).getStatus(), "Expected second PublishingStatusItem's status to be IN_PROGRESS.");
        assertEquals("SUCCESSFUL", result.getPublishingStatusHistory().get(2).getStatus(), "Expected third PublishingStatusItem's status to be SUCCESSFUL.");
    }

    @Test
    public void execute_successfulPublishingProcessForNewVersion_returnsSuccessfulPublishingStatusItemWithBookId() throws KindlePublishingClientException {
        // GIVEN
        String publishingStatusId = "publishingstatus.2bc206a1-5b41-4782-a260-976c0a291825";
        String newBookId = "book.b3750190-2a30-4ca8-ae1b-73d0d202dc41";
        GetPublishingStatusRequest request = GetPublishingStatusRequest.builder().withPublishingRecordId(publishingStatusId).build();

        PublishingStatusItem queuedItem = new PublishingStatusItem();
        queuedItem.setPublishingRecordId(publishingStatusId);
        queuedItem.setStatus(PublishingRecordStatus.QUEUED);
        queuedItem.setStatusMessage("Queued for publishing at 2022-05-06 23:04:05.32");

        PublishingStatusItem inProgressItem = new PublishingStatusItem();
        inProgressItem.setPublishingRecordId(publishingStatusId);
        inProgressItem.setStatus(PublishingRecordStatus.IN_PROGRESS);
        inProgressItem.setStatusMessage("Processing started at 2022-05-06 23:04:05.894");

        PublishingStatusItem successfulItem = new PublishingStatusItem();
        successfulItem.setPublishingRecordId(publishingStatusId);
        successfulItem.setStatus(PublishingRecordStatus.SUCCESSFUL);
        successfulItem.setStatusMessage("Book published at 2022-05-05 15:36:14.127");
        successfulItem.setBookId(newBookId);

        List<PublishingStatusItem> statusList = new ArrayList<>();
        statusList.add(queuedItem);
        statusList.add(inProgressItem);
        statusList.add(successfulItem);

        // WHEN
        when(publishingStatusDao.getPublishingStatusList(publishingStatusId)).thenReturn(statusList);
        GetPublishingStatusResponse result = getPublishingStatusActivity.execute(request);

        // THEN
        assertNull(result.getPublishingStatusHistory().get(0).getBookId(), "Expected first PublishingStatusItem's bookId to be null");
        assertNull(result.getPublishingStatusHistory().get(0).getBookId(), "Expected second PublishingStatusItem's bookId to be null");
        assertEquals(newBookId, result.getPublishingStatusHistory().get(2).getBookId(), "Expected the third PublishingStatusItem to contain a new bookId of: " + newBookId);

        assertTrue(result.getPublishingStatusHistory().get(0).getStatusMessage().contains("Queued for publishing"), "Expected first PublishingStatusItem's statusMessage to contain the phrase: Queued for publishing");
        assertTrue(result.getPublishingStatusHistory().get(1).getStatusMessage().contains("Processing started at"), "Expected second PublishingStatusItem's statusMessage to contain the phrase: Processing started at");
        assertTrue(result.getPublishingStatusHistory().get(2).getStatusMessage().contains("Book published at"), "Expected third PublishingStatusItem's statusMessage to contain the phrase: Book published at");

        assertEquals("QUEUED", result.getPublishingStatusHistory().get(0).getStatus(), "Expected first PublishingStatusItem's status to be QUEUED.");
        assertEquals("IN_PROGRESS", result.getPublishingStatusHistory().get(1).getStatus(), "Expected second PublishingStatusItem's status to be IN_PROGRESS.");
        assertEquals("SUCCESSFUL", result.getPublishingStatusHistory().get(2).getStatus(), "Expected third PublishingStatusItem's status to be SUCCESSFUL.");
    }

    @Test
    public void execute_failedPublishingProcess_returnsFailedPublishingStatusItem() throws KindlePublishingClientException {
        // GIVEN
        String failedPublishingStatusId = "publishingstatus.4bd41646-b1b2-4627-8304-5180c9b54e00";
        String bookId = "book.69c16130-60b5-485a-8326-7f79d3feb36d";
        GetPublishingStatusRequest request = GetPublishingStatusRequest.builder().withPublishingRecordId(failedPublishingStatusId).build();

        PublishingStatusItem queuedItem = new PublishingStatusItem();
        queuedItem.setPublishingRecordId(failedPublishingStatusId);
        queuedItem.setStatus(PublishingRecordStatus.QUEUED);
        queuedItem.setStatusMessage("Queued for publishing at 2022-05-06 23:04:05.32");

        PublishingStatusItem inProgressItem = new PublishingStatusItem();
        inProgressItem.setPublishingRecordId(failedPublishingStatusId);
        inProgressItem.setStatus(PublishingRecordStatus.IN_PROGRESS);
        inProgressItem.setStatusMessage("Processing started at 2022-05-06 23:04:05.894");

        PublishingStatusItem failedItem = new PublishingStatusItem();
        failedItem.setPublishingRecordId(failedPublishingStatusId);
        failedItem.setStatus(PublishingRecordStatus.FAILED);
        failedItem.setStatusMessage("Book publish failed at 2020-02-28 13:04:22.911 Additional Notes: Failed to convert text.");
        failedItem.setBookId(bookId);

        List<PublishingStatusItem> statusList = new ArrayList<>();
        statusList.add(queuedItem);
        statusList.add(inProgressItem);
        statusList.add(failedItem);

        // WHEN
        when(publishingStatusDao.getPublishingStatusList(failedPublishingStatusId)).thenReturn(statusList);
        GetPublishingStatusResponse result = getPublishingStatusActivity.execute(request);

        // THEN
        assertNull(result.getPublishingStatusHistory().get(0).getBookId(), "Expected first PublishingStatusItem's bookId to be null");
        assertNull(result.getPublishingStatusHistory().get(0).getBookId(), "Expected second PublishingStatusItem's bookId to be null");
        assertEquals(bookId, result.getPublishingStatusHistory().get(2).getBookId(), "Expected third PublishingStatusItem's bookId to be: " + bookId);

        assertTrue(result.getPublishingStatusHistory().get(0).getStatusMessage().contains("Queued for publishing"), "Expected first PublishingStatusItem's statusMessage to contain the phrase: Queued for publishing");
        assertTrue(result.getPublishingStatusHistory().get(1).getStatusMessage().contains("Processing started at"), "Expected second PublishingStatusItem's statusMessage to contain the phrase: Processing started at");
        assertTrue(result.getPublishingStatusHistory().get(2).getStatusMessage().contains("Book publish failed at"), "Expected third PublishingStatusItem's statusMessage to contain the phrase: Book publish failed at");

        assertEquals("QUEUED", result.getPublishingStatusHistory().get(0).getStatus(), "Expected first PublishingStatusItem's status to be QUEUED.");
        assertEquals("IN_PROGRESS", result.getPublishingStatusHistory().get(1).getStatus(), "Expected second PublishingStatusItem's status to be IN_PROGRESS.");
        assertEquals("FAILED", result.getPublishingStatusHistory().get(2).getStatus(), "Expected third PublishingStatusItem's status to be FAILED.");
    }

    @Test
    public void execute_noItemsFound_throwsPublishingStatusNotFoundException() {
        // GIVEN
        String invalidPublishingStatusId = "notAPublishingStatusId";
        GetPublishingStatusRequest request = GetPublishingStatusRequest.builder().withPublishingRecordId(invalidPublishingStatusId).build();

        // WHEN
        when(publishingStatusDao.getPublishingStatusList(invalidPublishingStatusId)).thenThrow(PublishingStatusNotFoundException.class);

        // THEN
        assertThrows(KindlePublishingClientException.class, () -> getPublishingStatusActivity.execute(request));
    }

}
