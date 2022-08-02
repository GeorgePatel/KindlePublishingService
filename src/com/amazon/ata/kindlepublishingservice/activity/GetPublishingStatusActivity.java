package com.amazon.ata.kindlepublishingservice.activity;

import com.amazon.ata.kindlepublishingservice.converters.PublishingStatusCoralConverter;
import com.amazon.ata.kindlepublishingservice.dao.PublishingStatusDao;
import com.amazon.ata.kindlepublishingservice.dynamodb.models.PublishingStatusItem;
import com.amazon.ata.kindlepublishingservice.exceptions.KindlePublishingClientException;
import com.amazon.ata.kindlepublishingservice.exceptions.PublishingStatusNotFoundException;
import com.amazon.ata.kindlepublishingservice.models.requests.GetPublishingStatusRequest;
import com.amazon.ata.kindlepublishingservice.models.response.GetPublishingStatusResponse;

import javax.inject.Inject;
import java.util.List;

public class GetPublishingStatusActivity {

    private PublishingStatusDao publishingStatusDao;

    @Inject
    public GetPublishingStatusActivity(PublishingStatusDao publishingStatusDao) {
        this.publishingStatusDao = publishingStatusDao;
    }

    public GetPublishingStatusResponse execute(GetPublishingStatusRequest publishingStatusRequest) throws KindlePublishingClientException {
        String publishingStatusId = publishingStatusRequest.getPublishingRecordId();
        List<PublishingStatusItem> statusList;
        try {
            statusList = publishingStatusDao.getPublishingStatusList(publishingStatusId);
        } catch (PublishingStatusNotFoundException e) {
            throw new KindlePublishingClientException(e.getMessage(), e);
        }
        return GetPublishingStatusResponse.builder()
                .withPublishingStatusHistory(PublishingStatusCoralConverter.toCoral(statusList))
                .build();
    }
}
