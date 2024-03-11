package com.volasoftware.tinder.utility;

import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.InputStream;

@Service
@AllArgsConstructor
public class FirebaseStorageUploader {

    private final Storage storage;

    public String uploadImage(InputStream inputStream, String fileName) {

        String bucketName = "tinder-d7708.appspot.com";
        String blobPath = "profile_pictures/" + fileName;

        BlobId blobId = BlobId.of(bucketName, blobPath);
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId).build();

        Blob blob = storage.create(blobInfo, inputStream);

        return blob.getMediaLink();
    }
}


