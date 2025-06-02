package com.renatmirzoev.moviebookingservice.mapper;

import com.renatmirzoev.moviebookingservice.model.entity.Booking;
import com.renatmirzoev.moviebookingservice.rest.model.booking.CreateBookingRequest;
import com.renatmirzoev.moviebookingservice.rest.model.booking.GetBookingResponse;
import org.mapstruct.Mapper;

@Mapper(config = MapperConfig.class)
public interface BookingMapper {

    Booking toBooking(CreateBookingRequest request);

    GetBookingResponse toGetBookingResponse(Booking booking);
}
