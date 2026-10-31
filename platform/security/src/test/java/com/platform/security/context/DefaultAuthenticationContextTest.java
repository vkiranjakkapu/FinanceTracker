package com.platform.security.context;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import com.platform.security.adapter.AuthenticationAdapter;
import com.platform.security.model.AuthenticatedUser;

@ExtendWith(MockitoExtension.class)
class DefaultAuthenticationContextTest {

    @Mock
    private AuthenticationAdapter authenticationAdapter;

    @InjectMocks
    private DefaultAuthenticationContext authenticationContext;

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void shouldReturnAuthenticatedUser() {

        AuthenticatedUser user = mock(AuthenticatedUser.class);

        Authentication authentication = mock(Authentication.class);

        SecurityContext securityContext = mock(SecurityContext.class);

        SecurityContextHolder.setContext(securityContext);

        when(securityContext.getAuthentication())
                .thenReturn(authentication);

        when(authentication.isAuthenticated())
                .thenReturn(true);

        when(authenticationAdapter.adapt(authentication))
                .thenReturn(user);

        Optional<AuthenticatedUser> result = authenticationContext.getCurrentUser();

        assertThat(result)
                .isPresent()
                .contains(user);

        verify(authenticationAdapter)
                .adapt(authentication);
    }

    @Test
    void shouldReturnEmptyWhenAuthenticationIsNull() {

        SecurityContext securityContext = mock(SecurityContext.class);

        SecurityContextHolder.setContext(securityContext);

        when(securityContext.getAuthentication())
                .thenReturn(null);

        assertThat(authenticationContext.getCurrentUser())
                .isEmpty();

        verifyNoInteractions(authenticationAdapter);
    }

    @Test
    void shouldReturnEmptyWhenAuthenticationIsNotAuthenticated() {

        Authentication authentication = mock(Authentication.class);

        SecurityContext securityContext = mock(SecurityContext.class);

        SecurityContextHolder.setContext(securityContext);

        when(securityContext.getAuthentication())
                .thenReturn(authentication);

        when(authentication.isAuthenticated())
                .thenReturn(false);

        assertThat(authenticationContext.getCurrentUser())
                .isEmpty();

        verifyNoInteractions(authenticationAdapter);
    }

    @Test
    void shouldReturnEmptyWhenAuthenticationIsAnonymous() {

        SecurityContext securityContext = mock(SecurityContext.class);

        SecurityContextHolder.setContext(securityContext);

        when(securityContext.getAuthentication())
                .thenReturn(new AnonymousAuthenticationToken(
                        "test-key",
                        "anonymousUser",
                        AuthorityUtils.createAuthorityList("ROLE_ANONYMOUS")));

        assertThat(authenticationContext.getCurrentUser())
                .isEmpty();
        assertThat(authenticationContext.isAuthenticated())
                .isFalse();

        verifyNoInteractions(authenticationAdapter);
    }
}