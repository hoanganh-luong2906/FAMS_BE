package com.example.FAMS.dto.requests;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdatePasswordRequest {
    private String requesterEmail;
    private String newPassword;
    private boolean isLoggedIn;
    private boolean isAuthorized;
}
