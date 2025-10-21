package by.kev.cloudfilestorage.controller;

import by.kev.cloudfilestorage.Util.DownloadResponseBuilder;
import by.kev.cloudfilestorage.controller.api.ResourceControllerApi;
import by.kev.cloudfilestorage.dto.ResourceResponseDTO;
import by.kev.cloudfilestorage.security.UserDetailsImpl;
import by.kev.cloudfilestorage.service.StorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/resource")
public class ResourceController implements ResourceControllerApi {

    private final StorageService storageService;
    private final DownloadResponseBuilder downloadResponseBuilder;

    @Override
    @GetMapping
    public ResourceResponseDTO getResourceInfo(@RequestParam(name = "path") String path,
                                               @AuthenticationPrincipal UserDetailsImpl userDetails) {

        return storageService.getResourceMetadata(path, userDetails.getUser().getId());
    }

    @Override
    @GetMapping("/download")
    public ResponseEntity<InputStreamResource> downloadResource(@RequestParam(name = "path") String path,
                                                                @AuthenticationPrincipal UserDetailsImpl userDetails) {

        InputStream resource = storageService.download(path, userDetails.getUser().getId());

        return downloadResponseBuilder.build(path, resource);
    }

    @Override
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public List<ResourceResponseDTO> uploadResource(@RequestPart(name = "object") MultipartFile[] files,
                                                    @RequestParam(name = "path") String path,
                                                    @AuthenticationPrincipal UserDetailsImpl userDetails) {

        return storageService.uploadFiles(files, path, userDetails.getUser().getId());
    }

    @Override
    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteResource(@RequestParam(name = "path") String path,
                               @AuthenticationPrincipal UserDetailsImpl userDetails) {

        storageService.delete(path, userDetails.getUser().getId());
    }

    @Override
    @GetMapping("/move")
    public ResourceResponseDTO moveResource(@RequestParam(name = "from") String pathFrom,
                                            @RequestParam(name = "to") String pathTo,
                                            @AuthenticationPrincipal UserDetailsImpl userDetails) {

        return storageService.moveResource(pathFrom, pathTo, userDetails.getUser().getId());
    }

    @Override
    @GetMapping("/search")
    public List<ResourceResponseDTO> searchResource(@RequestParam(name = "query") String query,
                                                    @AuthenticationPrincipal UserDetailsImpl userDetails) {

        return storageService.searchResources(query, userDetails.getUser().getId());
    }
}
