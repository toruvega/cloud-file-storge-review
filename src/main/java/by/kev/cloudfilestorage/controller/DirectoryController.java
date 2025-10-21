package by.kev.cloudfilestorage.controller;

import by.kev.cloudfilestorage.controller.api.DirectoryControllerApi;
import by.kev.cloudfilestorage.dto.ResourceResponseDTO;
import by.kev.cloudfilestorage.security.UserDetailsImpl;
import by.kev.cloudfilestorage.service.StorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/directory")
public class DirectoryController implements DirectoryControllerApi {

    private final StorageService storageService;

    @Override
    @GetMapping
    public List<ResourceResponseDTO> getDirectoryContent(@RequestParam(name = "path") String path,
                                                         @AuthenticationPrincipal UserDetailsImpl userDetails) {

        return storageService.getDirectoryContent(path, userDetails.getUser().getId());
    }

    @Override
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResourceResponseDTO createDirectory(@RequestParam(name = "path") String path,
                                               @AuthenticationPrincipal UserDetailsImpl userDetails) {

        return storageService.createFolder(path, userDetails.getUser().getId());
    }
}
