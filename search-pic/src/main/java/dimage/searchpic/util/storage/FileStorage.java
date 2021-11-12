package dimage.searchpic.util.storage;

import org.springframework.web.multipart.MultipartFile;

public interface FileStorage {
    String storeFile(MultipartFile file, Long userId);
}
