package by.kev.cloudfilestorage.config.properties;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@ConfigurationProperties(prefix = "minio")
public class MinioProperties {

    private final String bucket;
    private final String url;
    private final String accessKey;
    private final String secretKey;

    public MinioProperties(String bucket, String url, String accessKey, String secretKey) {
        this.bucket = bucket;
        this.url = url;
        this.accessKey = accessKey;
        this.secretKey = secretKey;
    }
}
