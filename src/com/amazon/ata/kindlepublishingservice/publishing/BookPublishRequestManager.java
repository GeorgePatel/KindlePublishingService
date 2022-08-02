package com.amazon.ata.kindlepublishingservice.publishing;

import javax.inject.Inject;
import java.util.concurrent.ConcurrentLinkedQueue;

public class BookPublishRequestManager {
    private ConcurrentLinkedQueue<BookPublishRequest> bookPublishRequestQueue;

    @Inject
    public BookPublishRequestManager(ConcurrentLinkedQueue<BookPublishRequest> bookPublishRequestQueue) {
        this.bookPublishRequestQueue = bookPublishRequestQueue;
    }

    public ConcurrentLinkedQueue<BookPublishRequest> getBookPublishRequestQueue() {
        return this.bookPublishRequestQueue;
    }

    public void addBookPublishRequest(BookPublishRequest bookPublishRequest) {
        this.bookPublishRequestQueue.add(bookPublishRequest);
    }

    public BookPublishRequest getBookPublishRequestToProcess() {
        return this.bookPublishRequestQueue.poll();
    }
}
