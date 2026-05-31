package com.example.cs.usermanagement.infrastructure;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.cs.usermanagement.application.CreateUserUseCase;
import com.example.cs.usermanagement.application.DeleteUserUseCase;
import com.example.cs.usermanagement.application.UpdateUserUseCase;
import com.example.cs.usermanagement.domain.User;
import com.example.cs.usermanagement.domain.UserNotFoundException;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(UserController.class)
class UserControllerTest {

  @Autowired MockMvc mockMvc;

  @MockitoBean CreateUserUseCase createUserUseCase;

  @MockitoBean UpdateUserUseCase updateUserUseCase;

  @MockitoBean DeleteUserUseCase deleteUserUseCase;

  @Test
  void createUser_withValidBody_returns201WithUser() throws Exception {
    var id = UUID.randomUUID();
    when(createUserUseCase.handle(any()))
        .thenReturn(User.reconstitute(id, "johndoe", "John", "Doe", "ES1234567890", false, false));

    mockMvc
        .perform(
            post("/users")
                .contentType(APPLICATION_JSON)
                .content(
                    """
{"username":"johndoe","name":"John","surname":"Doe","bankAccount":"ES1234567890"}
"""))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").value(id.toString()))
        .andExpect(jsonPath("$.username").value("johndoe"))
        .andExpect(jsonPath("$.bankAccount").value("ES1234567890"));
  }

  @Test
  void createUser_withBlankUsername_returns400() throws Exception {
    when(createUserUseCase.handle(any()))
        .thenThrow(new IllegalArgumentException("Username must not be blank"));

    mockMvc
        .perform(
            post("/users")
                .contentType(APPLICATION_JSON)
                .content(
                    """
                    {"username":"","name":"John","surname":"Doe","bankAccount":"ES1234567890"}
                    """))
        .andExpect(status().isBadRequest());
  }

  @Test
  void updateUser_withNewBankAccount_returns200WithUpdatedUser() throws Exception {
    var id = UUID.randomUUID();
    when(updateUserUseCase.handle(any()))
        .thenReturn(User.reconstitute(id, "johndoe", "John", "Doe", "ES0987654321", false, false));

    mockMvc
        .perform(
            put("/users/{id}", id)
                .contentType(APPLICATION_JSON)
                .content(
                    """
                    {"name":"John","surname":"Doe","bankAccount":"ES0987654321"}
                    """))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.bankAccount").value("ES0987654321"));
  }

  @Test
  void updateUser_unknownId_returns404() throws Exception {
    var id = UUID.randomUUID();
    when(updateUserUseCase.handle(any())).thenThrow(new UserNotFoundException(id));

    mockMvc
        .perform(
            put("/users/{id}", id)
                .contentType(APPLICATION_JSON)
                .content(
                    """
                    {"name":"John","surname":"Doe","bankAccount":"ES0987654321"}
                    """))
        .andExpect(status().isNotFound());
  }

  @Test
  void deleteUser_existingId_returns204() throws Exception {
    mockMvc.perform(delete("/users/{id}", UUID.randomUUID())).andExpect(status().isNoContent());
  }

  @Test
  void deleteUser_unknownId_returns404() throws Exception {
    var id = UUID.randomUUID();
    doThrow(new UserNotFoundException(id)).when(deleteUserUseCase).handle(id);

    mockMvc.perform(delete("/users/{id}", id)).andExpect(status().isNotFound());
  }
}
