package com.volasoftware.tinder.service;

import org.springframework.web.multipart.MultipartFile;

public interface UserProfilePictureService {
    String uploadProfilePicture(MultipartFile file, Long userId);
}
