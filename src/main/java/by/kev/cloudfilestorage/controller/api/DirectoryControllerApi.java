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
import jakarta.validation.constraints.Pattern;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Tag(name = "Directory Controller", description = "Directory API")
@ApiResponses(value = {
        @ApiResponse(responseCode = "400", description = "Invalid path format",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "401", description = "User is unauthorized",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "500", description = "Unknown error",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDTO.class)))
})
public interface DirectoryControllerApi {

    @Operation(summary = "Get information about the contents of a folder")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully received directory information",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResourceResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Directory doesn't exist",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    @GetMapping
    List<ResourceResponseDTO> getDirectoryContent(
            @Parameter(example = "docs/folder1/")
            @Pattern(regexp = ValidationConstants.PATH_REGEX, message = ValidationConstants.PATH_INVALID)
            String path,
            UserDetailsImpl userDetails
    );

    @Operation(summary = "Create an empty folder")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Directory successfully created",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResourceResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Parent directory doesn't exist",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "409", description = "Directory already exists",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    @PostMapping
    ResourceResponseDTO createDirectory(
            @Parameter(example = "docs/folder1/")
            @NotBlank(message = ValidationConstants.PATH_NOT_EMPTY)
            @Pattern(regexp = ValidationConstants.PATH_REGEX, message = ValidationConstants.PATH_INVALID)
            String path,
            UserDetailsImpl userDetails
    );
}
