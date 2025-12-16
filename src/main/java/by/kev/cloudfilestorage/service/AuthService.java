package by.kev.cloudfilestorage.service;

import by.kev.cloudfilestorage.dto.UserRequestDTO;
import by.kev.cloudfilestorage.dto.UserResponseDTO;
import by.kev.cloudfilestorage.exception.AlreadyExistException;
import by.kev.cloudfilestorage.mapper.UserMapper;
import by.kev.cloudfilestorage.model.User;
import by.kev.cloudfilestorage.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final AuthenticationManager authManager;
    private final FolderService folderService;

    private static final String SPRING_SECURITY_CONTEXT = "SPRING_SECURITY_CONTEXT";


    @Transactional
    public UserResponseDTO register(UserRequestDTO userRequestDTO, HttpSession session) {
        User user = userMapper.toUser(userRequestDTO);

        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            log.warn("User with username [{}] already exists.", user.getUsername());
            throw new AlreadyExistException("User with username " + user.getUsername() + " already exists.");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        User savedUser = userRepository.save(user);
        folderService.createRootDirectory(savedUser.getId());

        authenticateAndSetSession(userRequestDTO.username(), userRequestDTO.password(), session);

        return userMapper.toUserResponseDTO(user);
    }

    public UserResponseDTO login(UserRequestDTO userRequestDTO, HttpSession session) {
        authenticateAndSetSession(userRequestDTO.username(), userRequestDTO.password(), session);

        return userMapper.toUserResponseDTO(userRequestDTO);
    }

    private void authenticateAndSetSession(String username, String rawPassword, HttpSession session) {
        Authentication authentication = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, rawPassword)
        );

        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);

        SecurityContextHolder.setContext(context);
        session.setAttribute(SPRING_SECURITY_CONTEXT, context);
    }
}
