package dimage.searchpic.util.storage;

import dimage.searchpic.exception.ErrorInfo;
import dimage.searchpic.exception.storage.FileStorageException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Slf4j
@Service
public class FileStorageLocal implements FileStorage {
    @Value("${file.upload-dir}")
    private String baseDir;

    public String getFullPath(Path path,String filename){
        return path + "/" + filename;
    }

    @Override
    public String storeFile(MultipartFile file,Long userId) {
        try {
            String storeFileName = createStoreFileName(Objects.requireNonNull(file.getOriginalFilename()));
            Path path = Paths.get(baseDir+userId);
            try {
                Files.createDirectories(path); // userId 로 폴더 생성
            } catch (IOException e) {
                log.info("폴더를 생성하는데 실패했습니다.");
            }
            // 유저 폴더에 해당 파일 저장
            String imagePath = getFullPath(path,storeFileName);
            file.transferTo(new File(imagePath));
            return  imagePath;
        } catch (IOException e) {
            throw new FileStorageException(ErrorInfo.FILE_UPLOAD_ERROR);
        }
    }

    private String createStoreFileName(String originalFilename) {
        int pos = originalFilename.lastIndexOf(".");
        String extension = originalFilename.substring(pos + 1);
        List<String> possibleExtensions = Arrays.asList("jpeg","png","jpg","gif");
        if (!possibleExtensions.contains(extension)) {
            extension = "png"; // 확장자가 없으면 default: png 파일
        }
        String uuid = UUID.randomUUID().toString();
        return uuid + "." + extension;
    }
}
