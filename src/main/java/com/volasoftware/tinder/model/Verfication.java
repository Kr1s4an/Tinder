package com.volasoftware.tinder.model;

import jakarta.persistence.*;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

import static jakarta.persistence.TemporalType.TIMESTAMP;

@Entity
@Table(name = "verification_tokens")
public class Verfication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "USER_ID")
    private Long userId;
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "TOKEN")
    private UUID token;

    @Temporal(TIMESTAMP)
    @Column(name = "EXPIRATION_DATE")
    private LocalDateTime expirationDate;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public UUID getToken() {
        return token;
    }

    public void setToken(UUID token) {
        this.token = token;
    }

    public LocalDateTime getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(LocalDateTime expirationDate) {
        this.expirationDate = expirationDate;
    }
}
