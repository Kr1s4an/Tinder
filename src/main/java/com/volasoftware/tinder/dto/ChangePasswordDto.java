package com.volasoftware.tinder.dto;

import com.volasoftware.tinder.annotation.Password;
import jakarta.validation.constraints.NotBlank;

public class ChangePasswordDto {
    @NotBlank(message = "Password is mandatory")
    @Password
    private String newPassword;
    private String confirmNewPassword;

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public String getConfirmNewPassword() {
        return confirmNewPassword;
    }

    public void setConfirmNewPassword(String confirmNewPassword) {
        this.confirmNewPassword = confirmNewPassword;
    }
}
