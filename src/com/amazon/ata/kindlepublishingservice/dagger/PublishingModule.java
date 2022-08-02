package com.amazon.ata.kindlepublishingservice.dagger;

import com.amazon.ata.kindlepublishingservice.dao.CatalogDao;
import com.amazon.ata.kindlepublishingservice.dao.PublishingStatusDao;
import com.amazon.ata.kindlepublishingservice.publishing.BookPublishRequestManager;
import com.amazon.ata.kindlepublishingservice.publishing.BookPublishTask;
import com.amazon.ata.kindlepublishingservice.publishing.BookPublisher;
import dagger.Module;
import dagger.Provides;

import javax.inject.Singleton;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

@Module
public class PublishingModule {

    @Provides
    @Singleton
    public BookPublisher provideBookPublisher(ScheduledExecutorService scheduledExecutorService,
                                              BookPublishRequestManager bookPublishRequestManager,
                                              PublishingStatusDao publishingStatusDao,
                                              CatalogDao catalogDao) {
        return new BookPublisher(scheduledExecutorService, new BookPublishTask(bookPublishRequestManager,
                                                                               publishingStatusDao,
                                                                               catalogDao));
    }

    @Provides
    @Singleton
    public ScheduledExecutorService provideBookPublisherScheduler() {
        return Executors.newScheduledThreadPool(1);
    }

    @Provides
    @Singleton
    public BookPublishRequestManager provideBookPublishRequestManager() {
        return new BookPublishRequestManager(new ConcurrentLinkedQueue<>());
    }

    @Provides
    @Singleton
    public CatalogDao provideCatalogDao() {
        return new CatalogDao(new DataAccessModule().provideDynamoDBMapper());
    }

    @Provides
    @Singleton
    public PublishingStatusDao providePublishingStatusDao() {
        return new PublishingStatusDao(new DataAccessModule().provideDynamoDBMapper());
    }
}
