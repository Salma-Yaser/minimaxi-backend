// =====================================================================
// UpdateAvatarRequest.java
// =====================================================================
package com.minimaxi.backend.dto.request;

public class UpdateAvatarRequest {
    private String avatar; // base64 image string

    public String getAvatar() { return avatar; }
    public void setAvatar(String avatar) { this.avatar = avatar; }
}