package com.example.cs.usermanagement;

import com.example.cs.usermanagement.domain.UserEventPublisher;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest
class UserManagementApplicationTests {

  @MockitoBean UserEventPublisher userEventPublisher;

  @Test
  void contextLoads() {}
}
