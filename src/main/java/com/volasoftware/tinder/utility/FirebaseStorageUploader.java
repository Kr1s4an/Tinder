package com.volasoftware.tinder.utility;

import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.InputStream;

@Service
@RequiredArgsConstructor
public class FirebaseStorageUploader {

    private final Storage storage;

    @Value("${bucketName}")
    private String bucketName;

    public String uploadImage(InputStream inputStream, String fileName) {

        String blobPath = "profile_pictures/" + fileName;

        BlobId blobId = BlobId.of(bucketName, blobPath);
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId).build();

        Blob blob = storage.create(blobInfo, inputStream);

        return blob.getMediaLink();
    }
}
