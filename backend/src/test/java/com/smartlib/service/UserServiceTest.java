package com.smartlib.service;

import com.smartlib.entity.User;
import com.smartlib.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository repo;

    @InjectMocks
    private UserService service;

    @Test
    void loginReturnsUserForCorrectPassword() {
        User user = new User();
        user.setEmail("member@email.com");
        user.setPassword("pass123");

        when(repo.findByEmail("member@email.com")).thenReturn(Optional.of(user));

        User loggedIn = service.login("member@email.com", "pass123");

        assertEquals("member@email.com", loggedIn.getEmail());
    }

    @Test
    void loginReturnsNullForWrongPassword() {
        User user = new User();
        user.setEmail("member@email.com");
        user.setPassword("pass123");

        when(repo.findByEmail("member@email.com")).thenReturn(Optional.of(user));

        User loggedIn = service.login("member@email.com", "wrong");

        assertNull(loggedIn);
    }
}
