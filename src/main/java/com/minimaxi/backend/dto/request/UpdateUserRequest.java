// =====================================================================
// UpdateUserRequest.java
// =====================================================================
package com.minimaxi.backend.dto.request;

public class UpdateUserRequest {
    private String name;
    private String phone;
    private String role;
    private String status;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
 