package com.amazon.ata.kindlepublishingservice.exceptions;

/**
 * Exception to be thrown when a Kindle cannot be published to the catalog.
 */
public class KindlePublishingClientException extends Exception {
    private static final long serialVersionUID = 8579665150752085679L;

    /**
     * Exception with a message, but no cause.
     * @param message A descriptive message for this exception.
     */
    public KindlePublishingClientException(String message) {
        super(message);
    }

    /**
     * Exception with message and cause.
     * @param message A descriptive message for this exception.
     * @param cause The original throwable resulting in this exception.
     */
    public KindlePublishingClientException(String message, Throwable cause) {
        super(message, cause);
    }
}
