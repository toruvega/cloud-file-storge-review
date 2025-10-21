package by.kev.cloudfilestorage.Util;

import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.io.InputStream;

@Component
public class DownloadResponseBuilder {

    public ResponseEntity<InputStreamResource> build(String path, InputStream resource) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDisposition(ContentDisposition.attachment()
                .filename(PathUtils.getResourceName(path))
                .build());

        return ResponseEntity.ok()
                .headers(headers)
                .body(new InputStreamResource(resource));
    }
}

