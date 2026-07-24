package com.venus.meetspace.service.impl;

import com.venus.meetspace.common.enums.ResultCode;
import com.venus.meetspace.common.exception.BusinessException;
import com.venus.meetspace.model.dto.LoginCmd;
import com.venus.meetspace.model.dto.RegisterCmd;
import com.venus.meetspace.model.entity.User;
import com.venus.meetspace.repository.UserMapper;
import com.venus.meetspace.security.CustomUserDetails;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private UserMapper userMapper;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private SecurityContextRepository securityContextRepository;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private HttpSession session;

    @Mock
    private Authentication authentication;

    @Mock
    private SecurityContext securityContext;

    private AuthServiceImpl authService;

    private MockedStatic<SecurityContextHolder> securityContextHolderMock;

    private static final Long USER_ID = 1L;
    private static final String USERNAME = "testuser";
    private static final String PASSWORD = "password123";
    private static final String ENCODED_PASSWORD = "$2a$10$encoded";

    @BeforeEach
    void setUp() {
        authService = new AuthServiceImpl();
        ReflectionTestUtils.setField(authService, "userMapper", userMapper);
        ReflectionTestUtils.setField(authService, "authenticationManager", authenticationManager);
        ReflectionTestUtils.setField(authService, "passwordEncoder", passwordEncoder);
        ReflectionTestUtils.setField(authService, "securityContextRepository", securityContextRepository);
        securityContextHolderMock = mockStatic(SecurityContextHolder.class);
    }

    @AfterEach
    void tearDown() {
        securityContextHolderMock.close();
    }

    // ==================== login ====================

    @Test
    void login_shouldReturnUserDetails_whenCredentialsValid() {
        LoginCmd cmd = new LoginCmd();
        cmd.setUsername(USERNAME);
        cmd.setPassword(PASSWORD);

        User user = new User();
        user.setId(USER_ID);
        user.setUsername(USERNAME);
        user.setPassword(ENCODED_PASSWORD);
        user.setNickname("Test");

        CustomUserDetails userDetails = new CustomUserDetails(user);

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        securityContextHolderMock.when(SecurityContextHolder::createEmptyContext).thenReturn(securityContext);
        securityContextHolderMock.when(() -> SecurityContextHolder.setContext(securityContext)).then(inv -> null);

        CustomUserDetails result = authService.login(cmd, request, response);

        assertNotNull(result);
        assertEquals(USER_ID, result.getId());
        assertEquals(USERNAME, result.getUsername());

        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(securityContext).setAuthentication(authentication);
        verify(securityContextRepository).saveContext(securityContext, request, response);
    }

    // ==================== register ====================

    @Test
    void register_shouldSucceed_whenUsernameNotExists() {
        RegisterCmd cmd = new RegisterCmd();
        cmd.setUsername(USERNAME);
        cmd.setPassword(PASSWORD);
        cmd.setNickname("Test");

        when(userMapper.findByUsername(USERNAME)).thenReturn(null);
        when(passwordEncoder.encode(PASSWORD)).thenReturn(ENCODED_PASSWORD);
        when(userMapper.insert(any(User.class))).thenReturn(1);

        assertDoesNotThrow(() -> authService.register(cmd));

        verify(userMapper).insert(any(User.class));
    }

    @Test
    void register_shouldThrowException_whenUsernameExists() {
        RegisterCmd cmd = new RegisterCmd();
        cmd.setUsername(USERNAME);
        cmd.setPassword(PASSWORD);
        cmd.setNickname("Test");

        User existingUser = new User();
        existingUser.setId(USER_ID);
        existingUser.setUsername(USERNAME);

        when(userMapper.findByUsername(USERNAME)).thenReturn(existingUser);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> authService.register(cmd));

        assertEquals(ResultCode.CONFLICT, ex.getCode());
        assertTrue(ex.getMessage().contains("Username already exists"));
        verify(userMapper, never()).insert(any(User.class));
    }

    // ==================== logout ====================

    @Test
    void logout_shouldSucceed_whenUserAuthenticated() {
        securityContextHolderMock.when(SecurityContextHolder::getContext).thenReturn(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(request.getSession()).thenReturn(session);

        assertDoesNotThrow(() -> authService.logout(request, response));

        securityContextHolderMock.verify(SecurityContextHolder::clearContext);
        verify(session).invalidate();
    }

    @Test
    void logout_shouldThrowException_whenNotAuthenticated() {
        securityContextHolderMock.when(SecurityContextHolder::getContext).thenReturn(securityContext);
        when(securityContext.getAuthentication()).thenReturn(null);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> authService.logout(request, response));

        assertEquals(ResultCode.UNAUTHORIZED, ex.getCode());
        assertTrue(ex.getMessage().contains("No user is currently authenticated"));
    }
}
