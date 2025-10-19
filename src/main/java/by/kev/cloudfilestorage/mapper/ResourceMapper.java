package by.kev.cloudfilestorage.mapper;

import by.kev.cloudfilestorage.Util.PathUtils;
import by.kev.cloudfilestorage.dto.ResourceResponseDTO;
import by.kev.cloudfilestorage.dto.ResourceType;
import io.minio.messages.Item;
import org.springframework.stereotype.Component;

@Component
public class ResourceMapper {

    public ResourceResponseDTO toResourceResponseDTO(String path, Long fileSize) {
        String pathWithoutName = PathUtils.getPathWithoutRoot(PathUtils.getResourcePathWithoutName(path));
        String name = PathUtils.getResourceName(path);
        Long size = PathUtils.isFolderPath(path) ? null : fileSize;
        ResourceType type = PathUtils.getResourceType(path);

        return new ResourceResponseDTO(pathWithoutName, name, size, type);
    }

    public ResourceResponseDTO toResourceResponseDTO(Item object) {
        String objectName = object.objectName();

        String path = PathUtils.getPathWithoutRoot(PathUtils.getResourcePathWithoutName(objectName));
        String name = PathUtils.getResourceName(objectName);
        Long size = PathUtils.isFolderPath(objectName) ? null : object.size();
        ResourceType type = PathUtils.getResourceType(objectName);

        return new ResourceResponseDTO(path, name, size, type);
    }
}
