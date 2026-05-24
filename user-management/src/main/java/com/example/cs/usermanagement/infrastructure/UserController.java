package com.example.cs.usermanagement.infrastructure;

import com.example.cs.usermanagement.application.CreateUserCommand;
import com.example.cs.usermanagement.application.CreateUserUseCase;
import com.example.cs.usermanagement.application.DeleteUserUseCase;
import com.example.cs.usermanagement.domain.User;
import com.example.cs.usermanagement.domain.UserNotFoundException;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
class UserController {

  private final CreateUserUseCase createUserUseCase;
  private final DeleteUserUseCase deleteUserUseCase;

  @PostMapping
  ResponseEntity<UserResponse> create(@RequestBody CreateUserRequest request) {
    var user =
        createUserUseCase.handle(
            new CreateUserCommand(request.username(), request.name(), request.surname()));
    return ResponseEntity.status(HttpStatus.CREATED).body(UserResponse.from(user));
  }

  @DeleteMapping("/{id}")
  ResponseEntity<Void> delete(@PathVariable UUID id) {
    deleteUserUseCase.handle(id);
    return ResponseEntity.noContent().build();
  }

  @ExceptionHandler(IllegalArgumentException.class)
  ResponseEntity<Void> handleIllegalArgument() {
    return ResponseEntity.badRequest().build();
  }

  @ExceptionHandler(UserNotFoundException.class)
  ResponseEntity<Void> handleUserNotFound() {
    return ResponseEntity.notFound().build();
  }

  record CreateUserRequest(String username, String name, String surname) {}

  record UserResponse(String id, String username, String name, String surname) {
    static UserResponse from(User user) {
      return new UserResponse(user.id().toString(), user.username(), user.name(), user.surname());
    }
  }
}
