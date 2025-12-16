package by.kev.cloudfilestorage.service;

import by.kev.cloudfilestorage.Util.PathUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MinioServiceFactory {

    private final FolderService folderService;
    private final FileService fileService;

    public MinioService getServiceForPath(String path) {
        return PathUtils.isFolderPath(path) ? folderService : fileService;
    }
}

