package by.kev.cloudfilestorage.controller.api;

import by.kev.cloudfilestorage.dto.ErrorResponseDTO;
import by.kev.cloudfilestorage.dto.ResourceResponseDTO;
import by.kev.cloudfilestorage.security.UserDetailsImpl;
import by.kev.cloudfilestorage.validation.ValidationConstants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Tag(name = "Resource Controller", description = "Resource API")
@ApiResponses(value = {
        @ApiResponse(responseCode = "400", description = "Invalid path format",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "401", description = "User is unauthorized",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "500", description = "Unknown error",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDTO.class)))
})
public interface ResourceControllerApi {
    @Operation(summary = "Get information about a resource")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully received resource information",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResourceResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Resource not found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    @GetMapping
    ResourceResponseDTO getResourceInfo(@Parameter(example = "docs/text.txt")
                                        @NotBlank(message = ValidationConstants.PATH_NOT_EMPTY)
                                        @Pattern(regexp = ValidationConstants.PATH_REGEX,
                                                message = ValidationConstants.PATH_INVALID)
                                        String path,
                                        UserDetailsImpl userDetails
    );

    @Operation(summary = "Download a resource")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "file successfully downloaded",
                    content = @Content(mediaType = "application/octet-stream")),
            @ApiResponse(responseCode = "404", description = "Resource not found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    @GetMapping("/download")
    ResponseEntity<InputStreamResource> downloadResource(@Parameter(example = "docs/text.txt")
                                                         @NotBlank(message = ValidationConstants.PATH_NOT_EMPTY)
                                                         @Pattern(regexp = ValidationConstants.PATH_REGEX,
                                                                 message = ValidationConstants.PATH_INVALID)
                                                         String path,
                                                         UserDetailsImpl userDetails
    );

    @Operation(summary = "Upload a resource")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Resource successfully uploaded",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResourceResponseDTO.class))),
            @ApiResponse(responseCode = "409", description = "Resource already exists",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    List<ResourceResponseDTO> uploadResource(@NotEmpty(message = ValidationConstants.FILES_NOT_EMPTY)
                                             MultipartFile[] files,
                                             @Parameter(example = "folder/")
                                             @Pattern(regexp = ValidationConstants.PATH_REGEX,
                                                     message = ValidationConstants.PATH_INVALID)
                                             String path,
                                             UserDetailsImpl userDetails
    );

    @Operation(summary = "Delete a resource")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Resource successfully deleted"),
            @ApiResponse(responseCode = "404", description = "Resource not found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    @DeleteMapping
    void deleteResource(@Parameter(example = "docs/text.txt")
                        @NotBlank(message = ValidationConstants.PATH_NOT_EMPTY)
                        @Pattern(regexp = ValidationConstants.PATH_REGEX,
                                message = ValidationConstants.PATH_INVALID)
                        String path,
                        UserDetailsImpl userDetails
    );

    @Operation(summary = "Rename/move a resource")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Resource successfully moved",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResourceResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Resource not found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "409", description = "Resource already exists",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    @GetMapping("/move")
    ResourceResponseDTO moveResource(@Parameter(example = "docs/folder1/text.txt")
                                     @NotBlank(message = ValidationConstants.FROM_PATH_NOT_EMPTY)
                                     @Pattern(regexp = ValidationConstants.PATH_REGEX,
                                             message = ValidationConstants.PATH_INVALID)
                                     String pathFrom,
                                     @Parameter(example = "docs/folder2/text.txt")
                                     @NotBlank(message = ValidationConstants.TO_PATH_NOT_EMPTY)
                                     @Pattern(regexp = ValidationConstants.PATH_REGEX,
                                             message = ValidationConstants.PATH_INVALID)
                                     String pathTo,
                                     UserDetailsImpl userDetails
    );

    @Operation(summary = "Search")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Search results",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResourceResponseDTO.class)))
    })
    @GetMapping("/search")
    List<ResourceResponseDTO> searchResource(@Parameter(example = "text")
                                             @NotBlank(message = ValidationConstants.QUERY_NOT_EMPTY)
                                             @Pattern(regexp = ValidationConstants.PATH_REGEX,
                                                     message = ValidationConstants.PATH_INVALID)
                                             String query,
                                             UserDetailsImpl userDetails
    );
}
