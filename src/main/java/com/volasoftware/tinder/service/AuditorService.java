package com.volasoftware.tinder.service;

import org.springframework.data.domain.AuditorAware;

import java.util.Optional;

public class AuditorService implements AuditorAware<String> {
    @Override
    public Optional<String> getCurrentAuditor() {
        return Optional.ofNullable("Kindson").filter(s -> !s.isEmpty());
    }
}
