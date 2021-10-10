package dimage.searchpic.service.storage;

import org.springframework.web.multipart.MultipartFile;

public interface StorageService {
    String storeFile(MultipartFile file, Long userId);
}
