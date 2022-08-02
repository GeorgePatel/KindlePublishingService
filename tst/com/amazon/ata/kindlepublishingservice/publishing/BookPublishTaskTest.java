package com.amazon.ata.kindlepublishingservice.publishing;

import com.amazon.ata.kindlepublishingservice.dao.CatalogDao;
import com.amazon.ata.kindlepublishingservice.dao.PublishingStatusDao;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.mockito.MockitoAnnotations.initMocks;

public class BookPublishTaskTest {
    @Mock
    private BookPublishRequestManager bookPublishRequestManager;
    @Mock
    private PublishingStatusDao publishingStatusDao;
    @Mock
    private CatalogDao catalogDao;

    @InjectMocks
    private BookPublishTask bookPublishTask;

    @BeforeEach
    public void setUp() {
        initMocks(this);
    }

    @Test
    public void run_newBookRequest_createCatalogItemVersionAndUpdatePublishingStatusItemToSuccessful() {
        // GIVEN
        // WHEN
        // THEN
    }

    @Test
    public void run_newVersionRequest_updateCatalogItemVersionAndUpdatePublishingStatusItemToSuccessful() {
        // GIVEN
        // WHEN
        // THEN
    }

    @Test
    public void run_invalidBookIdInNewVersionRequest_updatePublishingStatusItemToFailed() {
        // GIVEN
        // WHEN
        // THEN
    }
}
