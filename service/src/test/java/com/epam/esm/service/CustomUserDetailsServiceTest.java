package com.epam.esm.service;

import com.epam.esm.model.User;
import com.epam.esm.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

class CustomUserDetailsServiceTest {
    private final UserRepository repository = mock(UserRepository.class);
    private final CustomUserDetailsService service = new CustomUserDetailsService(repository);
    private final User user = new User(1L, "username", "password");

    @Test
    public void loadUserByUsername_shouldReturnUserDetails() {
        when(repository.findByUsername(user.getUsername())).thenReturn(Optional.of(user));

        UserDetails userDetails = service.loadUserByUsername(user.getUsername());
        assertEquals(user.getUsername(), userDetails.getUsername());
        assertEquals(user.getPassword(), userDetails.getPassword());
        assertTrue(userDetails.getAuthorities().isEmpty());

        verify(repository).findByUsername(user.getUsername());
    }
}