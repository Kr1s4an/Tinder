package com.volasoftware.tinder.service;

import com.volasoftware.tinder.repository.UserRepository;
import com.volasoftware.tinder.utility.FirebaseStorageUploader;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
@AllArgsConstructor
public class UserProfilePictureServiceImpl implements UserProfilePictureService {

    private final FirebaseStorageUploader storageUploader;
    private final UserRepository userRepository;

    @Override
    public String uploadProfilePicture(MultipartFile file, Long userId) {
        try {
            String fileName = "profile_picture_" + userId + "_" + System.currentTimeMillis() + ".jpg";

            String imageUrl = storageUploader.uploadImage(file.getInputStream(), fileName);

            userRepository.findById(userId).ifPresent(user -> {
                user.setProfilePictureUrl(imageUrl);
                userRepository.save(user);
            });

            return imageUrl;
        } catch (IOException e) {
            throw new RuntimeException("Failed to upload profile picture", e);
        }
    }
}
