package com.volasoftware.tinder.service;

import java.io.IOException;

public interface EmailContentService {
    String createContent(String replacement, String templateClassPath) throws IOException;
}
