package com.morago.backend.service.file;

import com.morago.backend.dto.FileResponse;
import org.springframework.web.multipart.MultipartFile;

public interface FileService {
    FileResponse uploadAvatar(Long userId, MultipartFile file);
    FileResponse uploadMyAvatar(MultipartFile file);

    void deleteMyAvatar();
    void deleteAvatar(Long userId);

    FileResponse uploadThemeIcon(Long themeId, MultipartFile file);

    FileResponse uploadTranslatorDoc(Long translatorId, MultipartFile file);
    FileResponse uploadMyTranslatorDoc(MultipartFile file);

}
