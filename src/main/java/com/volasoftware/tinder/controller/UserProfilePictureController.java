package com.volasoftware.tinder.controller;

import com.volasoftware.tinder.service.UserProfilePictureService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class UserProfilePictureController {
    private final UserProfilePictureService profilePictureService;

    @RequestMapping(path = "/profile-picture",
            method = RequestMethod.POST,
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> uploadProfilePicture(@RequestParam("file") MultipartFile file,
                                                       @RequestParam("userId") Long userId) {
        String fileName = profilePictureService.uploadProfilePicture(file, userId);
        return ResponseEntity.ok().body(fileName);
    }
}
