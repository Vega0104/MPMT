package com.mpmt.backend.service;

import com.mpmt.backend.entity.User;
import com.mpmt.backend.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class UserServiceTest {
    @Test
    void shouldCreateAndFindUser() {
        UserRepository mockRepo = Mockito.mock(UserRepository.class);
        UserService service = new UserService(mockRepo);

        User user = new User();
        user.setUsername("bob");
        user.setEmail("bob@mail.com");
        user.setPassword("test");

        Mockito.when(mockRepo.save(user)).thenReturn(user);
        Mockito.when(mockRepo.findByUsername("bob")).thenReturn(Optional.of(user));

        service.createUser(user);
        Optional<User> found = service.getUserByUsername("bob");
        assertThat(found).isPresent();
        assertThat(found.get().getEmail()).isEqualTo("bob@mail.com");
    }
}
