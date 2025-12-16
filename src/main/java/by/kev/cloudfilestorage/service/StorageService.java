package by.kev.cloudfilestorage.service;

import by.kev.cloudfilestorage.Util.PathUtils;
import by.kev.cloudfilestorage.dto.ResourceResponseDTO;
import by.kev.cloudfilestorage.exception.AlreadyExistException;
import by.kev.cloudfilestorage.exception.ResourceNotFoundException;
import by.kev.cloudfilestorage.mapper.ResourceMapper;
import io.minio.ObjectWriteResponse;
import io.minio.StatObjectResponse;
import io.minio.messages.Item;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Slf4j
public class StorageService {

    private final MinioServiceFactory minioServiceFactory;
    private final FolderService folderService;
    private final ResourceMapper mapper;
    private final ThreadPoolTaskExecutor fileUploadExecutor;

    public void delete(String path, Long userId) {
        MinioService service = minioServiceFactory.getServiceForPath(path);
        String fullPath = PathUtils.getPathWithRoot(path, userId);

        if (!service.doesObjectExist(fullPath)) {
            log.warn("User [{}] tried to delete non-existing resource [{}]", userId, path);
            throw new ResourceNotFoundException("Resource not found: " + path);
        }

        service.delete(fullPath);
        log.info("Resource [{}] deleted by user [{}]", path, userId);
    }

    public ResourceResponseDTO createFolder(String path, Long userId) {
        String fullPath = PathUtils.getPathWithRoot(path, userId);
        ObjectWriteResponse response = folderService.createEmptyDirectory(fullPath);

        log.info("Folder [{}] created by user [{}]", path, userId);
        return mapper.toResourceResponseDTO(response.object(), null);
    }

    public List<ResourceResponseDTO> uploadFiles(MultipartFile[] files, String path, Long userId) {
        String parentPath = PathUtils.getPathWithRoot(path, userId);

        List<CompletableFuture<ResourceResponseDTO>> futures = Arrays.stream(files)
                .map(file -> CompletableFuture.supplyAsync(() -> {
                    String fullPath = parentPath + file.getOriginalFilename();
                    MinioService service = minioServiceFactory.getServiceForPath(fullPath);

                    if (service.doesObjectExist(fullPath)) {
                        log.warn("User [{}] tried to upload an existing file [{}]", userId, file.getOriginalFilename());
                        throw new AlreadyExistException("Resource already exists");
                    }

                    ObjectWriteResponse response = service.uploadFile(fullPath, file);
                    log.info("File [{}] ({} bytes) uploaded by user [{}]", file.getOriginalFilename(), file.getSize(), userId);

                    return mapper.toResourceResponseDTO(response.object(), file.getSize());
                }, fileUploadExecutor))
                .toList();

        return futures.stream()
                .map(CompletableFuture::join)
                .toList();
    }

    public ResourceResponseDTO moveResource(String oldPath, String newPath, Long userId) {
        MinioService service = minioServiceFactory.getServiceForPath(oldPath);
        String fullOldPath = PathUtils.getPathWithRoot(oldPath, userId);
        String fullNewPath = PathUtils.getPathWithRoot(newPath, userId);

        if (!service.doesObjectExist(fullOldPath)) {
            log.warn("User [{}] tried to move non-existing resource [{}]", userId, oldPath);
            throw new ResourceNotFoundException("Resource not found: " + oldPath);
        }

        if (service.doesObjectExist(fullNewPath)) {
            log.warn("User [{}] tried to overwrite existing resource [{}]", userId, newPath);
            throw new AlreadyExistException("Resource already exists: " + newPath);
        }

        StatObjectResponse resourceMetadata = service.getResourceMetadata(fullOldPath);
        service.move(fullOldPath, fullNewPath);

        log.info("Resource [{}] moved to [{}] by user [{}]", oldPath, newPath, userId);
        return mapper.toResourceResponseDTO(fullNewPath, resourceMetadata.size());
    }

    public InputStream download(String path, Long userId) {
        MinioService service = minioServiceFactory.getServiceForPath(path);
        String fullPath = PathUtils.getPathWithRoot(path, userId);

        if (!service.doesObjectExist(fullPath)) {
            log.warn("User [{}] tried to download non-existing resource [{}]", userId, path);
            throw new ResourceNotFoundException("Resource not found: " + path);
        }

        return service.getResourceStream(fullPath);
    }

    public ResourceResponseDTO getResourceMetadata(String path, Long userId) {
        MinioService service = minioServiceFactory.getServiceForPath(path);
        String fullPath = PathUtils.getPathWithRoot(path, userId);

        if (!service.doesObjectExist(fullPath)) {
            log.warn("User [{}] requested metadata for non-existing resource [{}]", userId, path);
            throw new ResourceNotFoundException("Resource not found: " + path);
        }
        StatObjectResponse resourceMetadata = service.getResourceMetadata(fullPath);
        log.info("Metadata retrieved for resource [{}] by user [{}]", path, userId);
        return mapper.toResourceResponseDTO(path, resourceMetadata.size());
    }

    public List<ResourceResponseDTO> getDirectoryContent(String path, Long userId) {
        String fullPath = PathUtils.getPathWithRoot(path, userId);

        if (!folderService.doesObjectExist(fullPath)) {
            log.warn("User [{}] requested content of non-existing folder [{}]", userId, path);
            throw new ResourceNotFoundException("Folder doesn't exist");
        }

        List<Item> items = folderService.getDirectoryObjects(fullPath, false);

        return items.stream()
                .filter(item -> !item.objectName().equals(fullPath))
                .map(mapper::toResourceResponseDTO)
                .toList();
    }

    public List<ResourceResponseDTO> searchResources(String query, Long userId) {
        String rootPath = PathUtils.getPathWithRoot("", userId);
        List<Item> items = folderService.getDirectoryObjects(rootPath, true);
        List<ResourceResponseDTO> result = new ArrayList<>();

        for (Item item : items) {
            String itemName = PathUtils.getResourceName(item.objectName());

            if (itemName.contains(query) && !item.objectName().equals(rootPath))
                result.add(mapper.toResourceResponseDTO(item));
        }

        if (result.isEmpty()) {
            log.warn("User [{}] searched for [{}], no results found", userId, query);
            throw new ResourceNotFoundException("Resource not found");
        }

        log.info("User [{}] searched for [{}], {} result(s) found", userId, query, result.size());
        return result;
    }
}
