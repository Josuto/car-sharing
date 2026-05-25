package com.example.cs.registry.infrastructure;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.cs.registry.application.RegisterCarUseCase;
import com.example.cs.registry.domain.Car;
import com.example.cs.registry.domain.DuplicateRegistrationNumberException;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(CarController.class)
class CarControllerTest {

  @Autowired MockMvc mockMvc;

  @MockitoBean RegisterCarUseCase registerCarUseCase;

  @Test
  void registerCar_withValidBody_returns201WithCar() throws Exception {
    var id = UUID.randomUUID();
    var ownerId = UUID.randomUUID();
    when(registerCarUseCase.handle(any()))
        .thenReturn(Car.reconstitute(id, ownerId, "SEDAN", "1234ABC"));

    mockMvc
        .perform(
            post("/cars")
                .contentType(APPLICATION_JSON)
                .content(
                    """
                    {"ownerId":"%s","type":"SEDAN","registrationNumber":"1234ABC"}
                    """
                        .formatted(ownerId)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").value(id.toString()))
        .andExpect(jsonPath("$.registrationNumber").value("1234ABC"));
  }

  @Test
  void registerCar_withInvalidRegistrationNumber_returns400() throws Exception {
    when(registerCarUseCase.handle(any()))
        .thenThrow(new IllegalArgumentException("Invalid registration number"));

    mockMvc
        .perform(
            post("/cars")
                .contentType(APPLICATION_JSON)
                .content(
                    """
                    {"ownerId":"%s","type":"SEDAN","registrationNumber":"INVALID"}
                    """
                        .formatted(UUID.randomUUID())))
        .andExpect(status().isBadRequest());
  }

  @Test
  void registerCar_withDuplicateRegistrationNumber_returns400() throws Exception {
    when(registerCarUseCase.handle(any()))
        .thenThrow(new DuplicateRegistrationNumberException("1234ABC"));

    mockMvc
        .perform(
            post("/cars")
                .contentType(APPLICATION_JSON)
                .content(
                    """
                    {"ownerId":"%s","type":"SEDAN","registrationNumber":"1234ABC"}
                    """
                        .formatted(UUID.randomUUID())))
        .andExpect(status().isBadRequest());
  }
}
