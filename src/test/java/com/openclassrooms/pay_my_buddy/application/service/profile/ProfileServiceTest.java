package com.openclassrooms.pay_my_buddy.application.service.profile;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.openclassrooms.pay_my_buddy.domain.model.User;
import com.openclassrooms.pay_my_buddy.domain.repository.UserRepository;
import com.openclassrooms.pay_my_buddy.web.dto.user.ProfileUpdateRequest;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
public class ProfileServiceTest {

    @Mock
    UserRepository userRepository;
    @Mock
    PasswordEncoder passwordEncoder;
    @Mock
    Authentication auth;

    @InjectMocks
    ProfileService profileService;

    private User existingUser;

    @BeforeEach
    public void setUp() {
        existingUser = User.builder()
            .email("test@email.com")
            .username("username")
            .password("encodedPassword")
            .build();

        when(auth.getName()).thenReturn("test@email.com");
        when(userRepository.findByEmail("test@email.com")).thenReturn(Optional.of(existingUser));
    }

    private ProfileUpdateRequest request(boolean changePw, String reqUsername, String reqEmail,
        String currentPw, String newPw, String confirmPw) {
        ProfileUpdateRequest request = new ProfileUpdateRequest();
        request.setUsername(reqUsername);
        if (changePw) {
            request.setCurrentPassword(currentPw);
            request.setNewPassword(newPw);
            request.setConfirmPassword(confirmPw);
        }
        return request;
    }

    @Test
    void updateProfile_changeUsernameOnly_success() {
        //given
        ProfileUpdateRequest changeUsernameRequest = request(false, "new_username",
            null, null, null, null);

        //when
        profileService.updateProfile(auth, changeUsernameRequest);

        //then
        assertThat(existingUser.getUsername()).isEqualTo("new_username");
        assertThat(existingUser.getPassword()).isEqualTo("encodedPassword");

        verify(passwordEncoder, never()).matches(any(), any());
        verify(passwordEncoder, never()).encode(any());
    }

    @Test
    void updateProfile_changePassword_success() {
        //given
        ProfileUpdateRequest pwChangeRequest = request(true, "username", "test@email.com",
            "currentPassword", "newPassword", "newPassword");

        //when
        when(passwordEncoder.matches("currentPassword", "encodedPassword")).thenReturn(true);
        when(passwordEncoder.matches("newPassword", "encodedPassword")).thenReturn(false);
        when(passwordEncoder.encode("newPassword")).thenReturn("encodedNewPassword");

        profileService.updateProfile(auth, pwChangeRequest);

        //then
        assertThat(existingUser.getUsername()).isEqualTo("username");
        assertThat(existingUser.getPassword()).isEqualTo("encodedNewPassword");

        verify(passwordEncoder).matches("currentPassword", "encodedPassword");
        verify(passwordEncoder).matches("newPassword", "encodedPassword");
        verify(passwordEncoder).encode("newPassword");
    }

    @Test
    void changePassword_wrongCurrentPw() {
        //given
        ProfileUpdateRequest pwChangeRequest = request(true, "username", "test@email.com",
            "wrongCurrentPw", "newPassword", "newPassword");

        //when
        when(passwordEncoder.matches("wrongCurrentPw", "encodedPassword")).thenReturn(false);
        IllegalArgumentException ex =
            assertThrows(IllegalArgumentException.class,
                () -> profileService.updateProfile(auth, pwChangeRequest));

        //then
        assertTrue(ex.getMessage().contains("Le mot de passe actuel est incorrect."));
    }

    @Test
    void changePassword_confirmPwMismatch() {
        //given
        ProfileUpdateRequest pwChangeRequest = request(true, "username", "test@email.com",
            "currentPassword", "newPassword", "newPassword2");

        //when
        when(passwordEncoder.matches("currentPassword", "encodedPassword")).thenReturn(true);

        IllegalArgumentException ex =
            assertThrows(IllegalArgumentException.class,
                () -> profileService.updateProfile(auth, pwChangeRequest));

        //then
        assertTrue(ex.getMessage().contains("La confirmation du nouveau mot de passe ne correspond pas."));
    }

    @Test
    void changePassword_newPwLengthLessThan8() {
        //given
        ProfileUpdateRequest pwChangeRequest = request(true, "username", "test@email.com",
            "currentPassword", "short", "short");

        //when
        when(passwordEncoder.matches("currentPassword", "encodedPassword")).thenReturn(true);

        IllegalArgumentException ex =
            assertThrows(IllegalArgumentException.class,
                () -> profileService.updateProfile(auth, pwChangeRequest));

        //then
        assertTrue(ex.getMessage().contains("au moins 8 caractères."));
    }

    @Test
    void changePassword_CannotUseSamePassword() {
        //given
        ProfileUpdateRequest pwChangeRequest = request(true, "username", "test@email.com",
            "currentPassword", "samePasswordWithCurPw", "samePasswordWithCurPw");

        //when
        when(passwordEncoder.matches("currentPassword", "encodedPassword")).thenReturn(true);
        when(passwordEncoder.matches("samePasswordWithCurPw", "encodedPassword")).thenReturn(true);

        IllegalArgumentException ex =
            assertThrows(IllegalArgumentException.class,
                () -> profileService.updateProfile(auth, pwChangeRequest));

        //then
        assertTrue(ex.getMessage().contains("Le nouveau mot de passe est identique à l’ancien."));
        verify(passwordEncoder, never()).encode(any());
    }

}
