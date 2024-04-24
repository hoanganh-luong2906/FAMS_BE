package com.example.FAMS.dto.responses;

import com.example.FAMS.dto.UserDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {
    private String status;
    private String role;
    private String token;
    private UserDTO userInfo;
}
