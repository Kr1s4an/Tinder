package com.volasoftware.tinder.service;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Service
@RequiredArgsConstructor
public class EmailContentServiceImpl implements EmailContentService {

    private final ResourceLoader resourceLoader;

    public String createContent(String replacement, String templateClassPath) throws IOException {
        Resource emailResource = resourceLoader.getResource(templateClassPath);
        File emailFile = emailResource.getFile();
        Path path = Path.of(emailFile.getPath());
        String emailContent = Files.readString(path);

        return emailContent.replace("{{#target#}}", replacement);
    }
}
