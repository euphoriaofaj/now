package com.morago.backend.service.file;

import com.morago.backend.dto.FileResponse;
import com.morago.backend.entity.File;
import com.morago.backend.entity.enumFiles.FileCategory;
import com.morago.backend.entity.enumFiles.FileVisibility;
import com.morago.backend.exception.file.AvatarNotFoundException;
import com.morago.backend.repository.FileRepository;
import com.morago.backend.repository.UserRepository;
import com.morago.backend.service.storage.StorageService;
import com.morago.backend.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Set;
import java.util.UUID;


@Service
@RequiredArgsConstructor
public class FileServiceImpl implements FileService {

    private final StorageService storage;
    private final FileRepository fileRepo;
    private final UserRepository userRepo;
    private final UserService userService;

    @Override
    @Transactional
    public FileResponse uploadAvatar(Long userId, MultipartFile mf) {
        validate(mf, 8 * 1024 * 1024, Set.of("image/png","image/jpeg","image/webp"));
        String ext = ext(mf.getOriginalFilename());
        String key = "avatars/" + userId + "." + ext;

        StorageService.StoredObject obj = store(mf, key, true);

        File file = fileRepo.findByUserIdAndCategory(userId, FileCategory.AVATAR)
                .orElse(File.builder()
                        .user(userRepo.findById(userId).orElseThrow())
                        .category(FileCategory.AVATAR)
                        .visibility(FileVisibility.PUBLIC)
                        .build());

        file.setOriginalTitle(mf.getOriginalFilename());
        file.setPath(key);

        file = fileRepo.save(file);
        return new FileResponse(file.getId(), file.getOriginalTitle(), obj.publicUrl(), file.getCategory().name());
    }

    @Override
    @Transactional
    @PreAuthorize("hasAnyRole('USER','TRANSLATOR','ADMIN')")
    public FileResponse uploadMyAvatar(MultipartFile mf) {
        var me = userService.getCurrentUser();
        return uploadAvatar(me.getId(), mf);
    }

    @Override
    @Transactional
    @PreAuthorize("hasAnyRole('USER','TRANSLATOR','ADMIN')")
    public void deleteMyAvatar() {
        var me = userService.getCurrentUser();
        deleteAvatarInternal(me.getId());
    }

    @Override
    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteAvatar(Long userId) {
        deleteAvatarInternal(userId);
    }

    private void deleteAvatarInternal(Long userId) {
        var avatar = fileRepo.findByUserIdAndCategory(userId, FileCategory.AVATAR)
                .orElseThrow(() -> new AvatarNotFoundException(userId));

        storage.delete(avatar.getPath());
        fileRepo.delete(avatar);
    }

    @Override
    @Transactional
    public FileResponse uploadThemeIcon(Long themeId, MultipartFile mf) {
        validate(mf, 2 * 1024 * 1024, Set.of("image/svg+xml","image/png","image/webp"));
        String ext = ext(mf.getOriginalFilename());
        String key = "icons/" + themeId + "." + ext;

        StorageService.StoredObject obj = store(mf, key, true);

        File file = File.builder()
                .originalTitle(mf.getOriginalFilename())
                .path(key)
                .category(FileCategory.ICON)
                .visibility(FileVisibility.PUBLIC)
                .build();

        file = fileRepo.save(file);
        return new FileResponse(file.getId(), file.getOriginalTitle(), obj.publicUrl(), file.getCategory().name());
    }

    @Override
    @Transactional
    public FileResponse uploadTranslatorDoc(Long translatorId, MultipartFile mf) {
        validate(mf, 15 * 1024 * 1024, null);
        String ext = ext(mf.getOriginalFilename());
        String key = "docs/" + translatorId + "/" + UUID.randomUUID() + "." + ext;

        StorageService.StoredObject obj = store(mf, key, false);

        File file = File.builder()
                .originalTitle(mf.getOriginalFilename())
                .path(key)
                .category(FileCategory.DOC)
                .visibility(FileVisibility.PRIVATE)
                .user(userRepo.findById(translatorId).orElseThrow())
                .build();

        file = fileRepo.save(file);
        return new FileResponse(file.getId(), file.getOriginalTitle(), null, file.getCategory().name());
    }

    @Override
    @Transactional
    @PreAuthorize("hasRole('TRANSLATOR') or hasRole('ADMIN')")
    public FileResponse uploadMyTranslatorDoc(MultipartFile mf) {
        var me = userService.getCurrentUser();
        return uploadTranslatorDoc(me.getId(), mf);
    }
    // --- helpers ---

    private StorageService.StoredObject store(MultipartFile mf, String key, boolean isPublic) {
        try {
            return storage.store(
                    mf.getInputStream(),
                    mf.getSize(),
                    mf.getOriginalFilename(),
                    mf.getContentType(),
                    key,
                    isPublic
            );
        } catch (IOException e) {
            throw new RuntimeException("Upload failed", e);
        }
    }

    private void validate(MultipartFile mf, long maxBytes, Set<String> whitelist) {
        if (mf == null || mf.isEmpty()) throw new IllegalArgumentException("Empty file");
        if (mf.getSize() > maxBytes) throw new IllegalArgumentException("File too large");
        if (whitelist != null && mf.getContentType() != null && !whitelist.contains(mf.getContentType())) {
            throw new IllegalArgumentException("Unsupported content type");
        }
    }

    private String ext(String name) {
        if (name == null) return "bin";
        int i = name.lastIndexOf('.');
        return (i > 0 && i < name.length() - 1) ? name.substring(i + 1) : "bin";
    }
}