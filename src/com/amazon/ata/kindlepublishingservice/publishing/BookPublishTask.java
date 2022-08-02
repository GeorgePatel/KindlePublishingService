package com.amazon.ata.kindlepublishingservice.publishing;

import com.amazon.ata.kindlepublishingservice.dao.CatalogDao;
import com.amazon.ata.kindlepublishingservice.dao.PublishingStatusDao;
import com.amazon.ata.kindlepublishingservice.dynamodb.models.CatalogItemVersion;
import com.amazon.ata.kindlepublishingservice.enums.PublishingRecordStatus;

import javax.inject.Inject;
import java.util.Queue;

public class BookPublishTask implements Runnable {
    private final BookPublishRequestManager bookPublishRequestManager;
    private final PublishingStatusDao publishingStatusDao;
    private final CatalogDao catalogDao;

    @Inject
    public BookPublishTask(BookPublishRequestManager bookPublishRequestManager, PublishingStatusDao publishingStatusDao, CatalogDao catalogDao) {
        this.bookPublishRequestManager = bookPublishRequestManager;
        this.publishingStatusDao = publishingStatusDao;
        this.catalogDao = catalogDao;
    }

    public BookPublishRequestManager getBookPublishRequestManager() {
        return bookPublishRequestManager;
    }

    @Override
    public void run() {
        Queue<BookPublishRequest> bookPublishRequests = bookPublishRequestManager.getBookPublishRequestQueue();

        if (bookPublishRequests == null) {
            return;
        }

        while (!bookPublishRequests.isEmpty()) {
            BookPublishRequest bookPublishRequest = bookPublishRequestManager.getBookPublishRequestToProcess();

            publishingStatusDao.setPublishingStatus(bookPublishRequest.getPublishingRecordId(),
                                                    PublishingRecordStatus.IN_PROGRESS,
                                                    bookPublishRequest.getBookId());

            KindleFormattedBook kindleFormattedBook = KindleFormatConverter.format(bookPublishRequest);

            try {
                CatalogItemVersion createdOrUpdatedBook = catalogDao.createOrUpdateBook(kindleFormattedBook);
                publishingStatusDao.setPublishingStatus(bookPublishRequest.getPublishingRecordId(),
                                                        PublishingRecordStatus.SUCCESSFUL,
                                                        createdOrUpdatedBook.getBookId());
            } catch (Exception e) {
                publishingStatusDao.setPublishingStatus(bookPublishRequest.getPublishingRecordId(),
                                                        PublishingRecordStatus.FAILED,
                                                        bookPublishRequest.getBookId(),
                                                        e.getMessage());
            }
        }
    }
}
