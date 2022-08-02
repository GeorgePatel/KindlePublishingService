package com.amazon.ata.kindlepublishingservice.controllers;

import com.amazon.ata.kindlepublishingservice.*;
import com.amazon.ata.kindlepublishingservice.activity.GetBookActivity;
import com.amazon.ata.kindlepublishingservice.activity.GetPublishingStatusActivity;
import com.amazon.ata.kindlepublishingservice.activity.RemoveBookFromCatalogActivity;
import com.amazon.ata.kindlepublishingservice.activity.SubmitBookForPublishingActivity;
import com.amazon.ata.kindlepublishingservice.dagger.ApplicationComponent;
import com.amazon.ata.kindlepublishingservice.exceptions.BookNotFoundException;
import com.amazon.ata.kindlepublishingservice.exceptions.KindlePublishingClientException;
import com.amazon.ata.kindlepublishingservice.models.*;
import com.amazon.ata.kindlepublishingservice.models.requests.GetBookRequest;
import com.amazon.ata.kindlepublishingservice.models.requests.GetPublishingStatusRequest;
import com.amazon.ata.kindlepublishingservice.models.requests.RemoveBookFromCatalogRequest;
import com.amazon.ata.kindlepublishingservice.models.requests.SubmitBookForPublishingRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
public class Controller {
    private static final ApplicationComponent component = App.component;

    @GetMapping(value = "/books/{id}", produces = {"application/json"})
    public ResponseEntity<?> getBook(@PathVariable String id) {
        GetBookActivity bookActivity = component.provideGetBookActivity();
        GetBookRequest getBookRequest = GetBookRequest.builder().withBookId(id).build();
        return new ResponseEntity<>(bookActivity.execute(getBookRequest), HttpStatus.OK);
    }

    @DeleteMapping(value = "/books/{id}")
    public ResponseEntity<?> removeBook(@PathVariable String id) {
        RemoveBookFromCatalogActivity removeBookActivity = component.provideRemoveBookFromCatalogActivity();
        RemoveBookFromCatalogRequest removeBookRequest = RemoveBookFromCatalogRequest.builder()
                                        .withBookId(id).build();
        return new ResponseEntity<>(removeBookActivity.execute(removeBookRequest), HttpStatus.OK);
    }

    @PostMapping(value = "/books", consumes = {"application/json"}, produces = {"application/json"})
    public ResponseEntity<?> submitBookForPublishing(@Valid @RequestBody Book book) {
        SubmitBookForPublishingActivity submitActivity = component.provideSubmitBookForPublishingActivity();
        SubmitBookForPublishingRequest request = SubmitBookForPublishingRequest.builder()
                .withBookId(book.getBookId())
                .withTitle(book.getTitle())
                .withText(book.getText())
                .withAuthor(book.getAuthor())
                .withGenre(book.getGenre())
                .build();
        try {
            return new ResponseEntity<>(submitActivity.execute(request), HttpStatus.OK);
        } catch (KindlePublishingClientException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping(value = "/publishingStatus/{recordId}", produces = {"application/json"})
    public ResponseEntity<?> getPublishingStatus(@PathVariable String recordId) throws KindlePublishingClientException {
        GetPublishingStatusActivity getPublishingStatusActivity = component.provideGetPublishingStatusActivity();

        GetPublishingStatusRequest getPublishingStatusRequest = GetPublishingStatusRequest.builder()
                .withPublishingRecordId(recordId)
                .build();

        try {
            return new ResponseEntity<>(getPublishingStatusActivity.execute(getPublishingStatusRequest), HttpStatus.OK);
        } catch (KindlePublishingClientException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
}
