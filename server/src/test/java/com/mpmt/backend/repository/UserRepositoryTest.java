package com.mpmt.backend.repository;

import com.mpmt.backend.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("Devrait sauvegarder et retrouver un utilisateur par username")
    void shouldSaveAndFindUserByUsername() {
        User user = new User();
        user.setUsername("johndoe");
        user.setEmail("john@example.com");
        user.setPassword("secure123");
        userRepository.save(user);

        Optional<User> found = userRepository.findByUsername("johndoe");
        assertThat(found).isPresent();
        assertThat(found.get().getEmail()).isEqualTo("john@example.com");
    }

    @Test
    @DisplayName("Devrait vérifier l'existence d'un email")
    void shouldCheckEmailExists() {
        User user = new User();
        user.setUsername("janedoe");
        user.setEmail("jane@example.com");
        user.setPassword("secure123");
        userRepository.save(user);

        assertThat(userRepository.existsByEmail("jane@example.com")).isTrue();
        assertThat(userRepository.existsByEmail("unknown@example.com")).isFalse();
    }
}
