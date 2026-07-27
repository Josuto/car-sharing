package com.example.cs.registry.infrastructure;

import com.example.cs.registry.application.RegisterCarHandler;
import com.example.cs.registry.application.RegisterCarUseCase;
import com.example.cs.registry.domain.CarEventPublisher;
import com.example.cs.registry.domain.CarRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class CarRegistryConfig {

  @Bean
  RegisterCarUseCase registerCarUseCase(CarRepository repository, CarEventPublisher publisher) {
    return new RegisterCarHandler(repository, publisher);
  }
}
