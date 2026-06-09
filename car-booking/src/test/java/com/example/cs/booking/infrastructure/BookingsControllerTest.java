package com.example.cs.booking.infrastructure;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.cs.booking.application.CreateBookingUseCase;
import com.example.cs.booking.application.ReturnCarUseCase;
import com.example.cs.booking.domain.Booking;
import com.example.cs.booking.domain.BookingNotFoundException;
import com.example.cs.booking.domain.BookingPeriod;
import com.example.cs.booking.domain.BookingStatus;
import java.time.LocalDate;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(BookingsController.class)
class BookingsControllerTest {

  @Autowired MockMvc mockMvc;

  @MockitoBean CreateBookingUseCase createBookingUseCase;
  @MockitoBean ReturnCarUseCase returnCarUseCase;

  private final UUID bookingId = UUID.randomUUID();
  private final UUID carId = UUID.randomUUID();
  private final UUID borrowerId = UUID.randomUUID();
  private final LocalDate start = LocalDate.now();
  private final LocalDate end = start.plusDays(3);

  @Test
  void postBooking_withValidRequest_returns201WithFullBookingObject() throws Exception {
    var booking =
        Booking.reconstitute(
            bookingId, carId, borrowerId, BookingPeriod.of(start, end), BookingStatus.PENDING);
    when(createBookingUseCase.handle(any())).thenReturn(booking);

    mockMvc
        .perform(
            post("/bookings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                      "carId": "%s",
                      "borrowerId": "%s",
                      "startDate": "%s",
                      "endDate": "%s"
                    }
                    """
                        .formatted(carId, borrowerId, start, end)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").value(bookingId.toString()))
        .andExpect(jsonPath("$.carId").value(carId.toString()))
        .andExpect(jsonPath("$.borrowerId").value(borrowerId.toString()))
        .andExpect(jsonPath("$.startDate").value(start.toString()))
        .andExpect(jsonPath("$.endDate").value(end.toString()))
        .andExpect(jsonPath("$.status").value("PENDING"));
  }

  @Test
  void postBooking_withInvalidRequest_returns400WithErrorBody() throws Exception {
    when(createBookingUseCase.handle(any()))
        .thenThrow(new IllegalArgumentException("borrower is a debtor"));

    mockMvc
        .perform(
            post("/bookings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                      "carId": "%s",
                      "borrowerId": "%s",
                      "startDate": "%s",
                      "endDate": "%s"
                    }
                    """
                        .formatted(carId, borrowerId, start, end)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.error").value("borrower is a debtor"));
  }

  @Test
  void putBooking_withActiveBooking_returns200WithUpdatedBooking() throws Exception {
    var booking =
        Booking.reconstitute(
            bookingId,
            carId,
            borrowerId,
            BookingPeriod.reconstitute(start.minusDays(2), end),
            BookingStatus.RETURNED);
    when(returnCarUseCase.handle(any())).thenReturn(booking);

    mockMvc
        .perform(put("/bookings/{id}", bookingId))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(bookingId.toString()))
        .andExpect(jsonPath("$.status").value("RETURNED"));
  }

  @Test
  void putBooking_withNonActiveBooking_returns400() throws Exception {
    when(returnCarUseCase.handle(any()))
        .thenThrow(new IllegalArgumentException("Cannot return a booking with status: PENDING"));

    mockMvc
        .perform(put("/bookings/{id}", bookingId))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.error").value("Cannot return a booking with status: PENDING"));
  }

  @Test
  void putBooking_withUnknownBooking_returns404() throws Exception {
    when(returnCarUseCase.handle(any())).thenThrow(new BookingNotFoundException(bookingId));

    mockMvc.perform(put("/bookings/{id}", bookingId)).andExpect(status().isNotFound());
  }
}
