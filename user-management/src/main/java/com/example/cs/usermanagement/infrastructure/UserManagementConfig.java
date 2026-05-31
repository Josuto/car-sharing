package com.example.cs.usermanagement.infrastructure;

import com.example.cs.usermanagement.application.CreateUserHandler;
import com.example.cs.usermanagement.application.CreateUserUseCase;
import com.example.cs.usermanagement.application.DeleteUserHandler;
import com.example.cs.usermanagement.application.DeleteUserUseCase;
import com.example.cs.usermanagement.application.UpdateUserHandler;
import com.example.cs.usermanagement.application.UpdateUserUseCase;
import com.example.cs.usermanagement.domain.UserEventPublisher;
import com.example.cs.usermanagement.domain.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class UserManagementConfig {

  @Bean
  CreateUserUseCase createUserUseCase(UserRepository repository, UserEventPublisher publisher) {
    return new CreateUserHandler(repository, publisher);
  }

  @Bean
  UpdateUserUseCase updateUserUseCase(UserRepository repository, UserEventPublisher publisher) {
    return new UpdateUserHandler(repository, publisher);
  }

  @Bean
  DeleteUserUseCase deleteUserUseCase(UserRepository repository) {
    return new DeleteUserHandler(repository);
  }
}
