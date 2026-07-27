package com.example.cs.booking.infrastructure;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.cs.booking.application.AvailableCarsUseCase;
import com.example.cs.booking.domain.Car;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(CarsController.class)
class CarsControllerTest {

  @Autowired MockMvc mockMvc;

  @MockitoBean AvailableCarsUseCase availableCarsUseCase;

  @Test
  void getCars_returnsAvailableCars() throws Exception {
    var id = UUID.randomUUID();
    when(availableCarsUseCase.handle()).thenReturn(List.of(Car.reconstitute(id, "SEDAN")));

    mockMvc
        .perform(get("/cars"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].id").value(id.toString()))
        .andExpect(jsonPath("$[0].type").value("SEDAN"));
  }

  @Test
  void getCars_withNoCarsAvailable_returnsEmptyList() throws Exception {
    when(availableCarsUseCase.handle()).thenReturn(List.of());

    mockMvc.perform(get("/cars")).andExpect(status().isOk()).andExpect(jsonPath("$").isEmpty());
  }
}
