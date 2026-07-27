package com.example.cs.registry;

import com.example.cs.registry.domain.CarEventPublisher;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest
class CarRegistryApplicationTests {

  @MockitoBean CarEventPublisher carEventPublisher;

  @Test
  void contextLoads() {}
}
