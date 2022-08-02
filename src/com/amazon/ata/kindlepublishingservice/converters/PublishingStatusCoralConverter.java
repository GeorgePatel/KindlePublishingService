package com.amazon.ata.kindlepublishingservice.converters;

import com.amazon.ata.coral.converter.CoralConverterUtil;
import com.amazon.ata.kindlepublishingservice.dynamodb.models.PublishingStatusItem;
import com.amazon.ata.kindlepublishingservice.models.PublishingStatusRecord;

import java.util.List;

/**
 * Converters for PublishingStatus related objects.
 */
public class PublishingStatusCoralConverter {

    private PublishingStatusCoralConverter() {}

    /**
     * Converts the given PublishingStatusItem list into the corresponding Coral PublishingStatusRecords object.
     *
     * @param publishingStatusItems PublishingStatusItem list to convert.
     * @return Coral PublishingStatusRecords list.
     */
    public static List<PublishingStatusRecord> toCoral(List<PublishingStatusItem>
                                                           publishingStatusItems) {
        return CoralConverterUtil.convertList(publishingStatusItems, PublishingStatusCoralConverter::toCoral);
    }

    /**
     * Converts the given PublishingStatusItem object to the corresponding Coral PublishingStatusRecord object.
     * @param publishingStatusItem PublishingStatusItem object to convert
     * @return Coral PublishingStatusRecord object.
     */
    public static PublishingStatusRecord toCoral(PublishingStatusItem
                                                     publishingStatusItem) {
        return PublishingStatusRecord.builder()
                .withBookId(publishingStatusItem.getBookId())
                .withStatus(publishingStatusItem.getStatus().toString())
                .withStatusMessage(publishingStatusItem.getStatusMessage())
                .build();
    }
}
