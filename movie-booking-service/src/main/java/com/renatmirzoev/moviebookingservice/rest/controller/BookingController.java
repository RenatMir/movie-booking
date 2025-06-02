package com.renatmirzoev.moviebookingservice.rest.controller;

import com.renatmirzoev.moviebookingservice.exception.NotFoundException;
import com.renatmirzoev.moviebookingservice.mapper.BookingMapper;
import com.renatmirzoev.moviebookingservice.model.entity.Booking;
import com.renatmirzoev.moviebookingservice.rest.model.booking.CreateBookingRequest;
import com.renatmirzoev.moviebookingservice.rest.model.booking.CreateBookingResponse;
import com.renatmirzoev.moviebookingservice.rest.model.booking.GetBookingResponse;
import com.renatmirzoev.moviebookingservice.service.BookingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.Optional;

@RestController
@RequestMapping("/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;
    private final BookingMapper bookingMapper;

    @GetMapping("/{id}")
    public ResponseEntity<GetBookingResponse> getBooking(@PathVariable("id") long id) {
        Optional<Booking> bookingOptional = bookingService.getBookingById(id);
        if (bookingOptional.isEmpty()) {
            throw new NotFoundException("Booking with id %s not found".formatted(id));
        }

        Booking booking = bookingOptional.get();
        GetBookingResponse response = bookingMapper.toGetBookingResponse(booking);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<CreateBookingResponse> createBooking(@Valid @RequestBody CreateBookingRequest request) {
        Booking booking = bookingMapper.toBooking(request);
        long id = bookingService.saveBooking(booking);

        CreateBookingResponse response = new CreateBookingResponse()
            .setId(id);

        URI location = ServletUriComponentsBuilder
            .fromCurrentRequest()
            .path("/{id}")
            .buildAndExpand(id)
            .toUri();
        return ResponseEntity.created(location).body(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBooking(@PathVariable("id") long id) {
        bookingService.deleteBooking(id);
        return ResponseEntity.noContent().build();
    }
}
