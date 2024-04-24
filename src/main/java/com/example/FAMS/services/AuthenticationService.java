package com.example.FAMS.services;

import com.example.FAMS.dto.requests.CreateRequest;
import com.example.FAMS.dto.requests.LoginRequest;
import com.example.FAMS.dto.responses.CreateResponse;
import com.example.FAMS.dto.responses.LoginResponse;
import com.example.FAMS.dto.responses.ResponseObject;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public interface AuthenticationService {
    LoginResponse login(LoginRequest loginRequest);

    CreateResponse createUser(CreateRequest createRequest) throws RuntimeException;

    void refresh(HttpServletRequest request, HttpServletResponse response) throws IOException;

    ResponseObject getLoggedInUser(HttpServletRequest request);
}
