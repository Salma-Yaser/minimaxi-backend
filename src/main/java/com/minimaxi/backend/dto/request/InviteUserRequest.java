// =====================================================================
// InviteUserRequest.java
// =====================================================================
package com.minimaxi.backend.dto.request;

public class InviteUserRequest {
    private String name;
    private String email;
    private String role;
    private Long organizationId;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public Long getOrganizationId() { return organizationId; }
    public void setOrganizationId(Long organizationId) { this.organizationId = organizationId; }
}