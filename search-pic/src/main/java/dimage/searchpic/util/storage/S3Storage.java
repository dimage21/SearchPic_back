package dimage.searchpic.util.storage;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import dimage.searchpic.exception.ErrorInfo;
import dimage.searchpic.exception.storage.FileStorageException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Slf4j
@Profile("prod")
@Component
public class S3Storage implements FileStorage {
    private AmazonS3 s3Client;

    @Value("${cloud.aws.credentials.access-key}")
    private String accessKey;

    @Value("${cloud.aws.credentials.secret-key}")
    private String secretKey;

    @Value("${cloud.aws.region.static}")
    private String region;

    @Value("${cloud.aws.s3.bucket}")
    public String bucket;

    @PostConstruct
    public void setClient(){
        BasicAWSCredentials awsCredentials = new BasicAWSCredentials(accessKey, secretKey);
        s3Client = AmazonS3ClientBuilder.standard()
                    .withRegion(region)
                    .withCredentials(new AWSStaticCredentialsProvider(awsCredentials))
                    .build();
    }

    @Override
    public String storeFile(MultipartFile file, Long userId)  {
        String storeFileName = createStoreFileName(Objects.requireNonNull(file.getOriginalFilename()),userId);
        ObjectMetadata metaData = new ObjectMetadata();
        metaData.setContentLength(file.getSize());
        try {
            s3Client.putObject(
                    new PutObjectRequest(bucket, storeFileName, file.getInputStream(), metaData)
                     .withCannedAcl(CannedAccessControlList.PublicRead)
            );
            return s3Client.getUrl(bucket, storeFileName).toString();
        } catch(IOException e){
            throw new FileStorageException(ErrorInfo.FILE_UPLOAD_ERROR);
        }
    }

    private String createStoreFileName(String originalFilename,Long userId) {
        int pos = originalFilename.lastIndexOf(".");
        StringBuilder sb = new StringBuilder();
        String extension = originalFilename.substring(pos + 1);
        List<String> possibleExtensions = Arrays.asList("jpeg","png","jpg","gif");
        if (!possibleExtensions.contains(extension)) {
            extension = "png"; // 확장자가 없으면 default: png 파일
        }
        String uuid = UUID.randomUUID().toString();
        sb.append(userId).append("/").append(uuid).append(".").append(extension);
        return sb.toString();
    }
}
