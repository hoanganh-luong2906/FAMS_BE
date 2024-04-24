package com.example.FAMS.controllers;

import com.example.FAMS.dto.requests.CreateRequest;
import com.example.FAMS.dto.requests.LoginRequest;
import com.example.FAMS.dto.responses.CreateResponse;
import com.example.FAMS.dto.responses.LoginResponse;
import com.example.FAMS.dto.responses.ResponseObject;
import com.example.FAMS.services.AuthenticationService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/authentication")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> loginRequest(@RequestBody LoginRequest loginRequest) {
        return ResponseEntity.ok(authenticationService.login(loginRequest));
    }

    @PostMapping("/create-user")
    @PreAuthorize("hasAuthority('user:create')")
    public ResponseEntity<CreateResponse> createUserRequest(@RequestBody CreateRequest createRequest) {
        try {
            var response = authenticationService.createUser(createRequest);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(CreateResponse.builder()
                    .status(e.getMessage())
                    .build());
        }

    }

    @PostMapping("/refresh")
    public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
        authenticationService.refresh(request, response);
    }

    @GetMapping("/login/get-user")
    public ResponseEntity<ResponseObject> getLoggedInUser(HttpServletRequest request) {
        return ResponseEntity.ok(authenticationService.getLoggedInUser(request));
    }
}
