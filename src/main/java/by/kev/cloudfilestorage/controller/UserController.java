package by.kev.cloudfilestorage.controller;

import by.kev.cloudfilestorage.dto.UserResponseDTO;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
@Tag(name = "User Controller", description = "User API")
public class UserController {

    @GetMapping("/me")
    public UserResponseDTO getUser(@AuthenticationPrincipal UserDetails userDetails) {

        return new UserResponseDTO(userDetails.getUsername());
    }
}
