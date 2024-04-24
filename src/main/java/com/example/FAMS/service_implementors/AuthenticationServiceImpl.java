package com.example.FAMS.service_implementors;

import com.example.FAMS.dto.UserDTO;
import com.example.FAMS.dto.requests.CreateRequest;
import com.example.FAMS.dto.requests.LoginRequest;
import com.example.FAMS.dto.responses.*;
import com.example.FAMS.enums.Role;
import com.example.FAMS.enums.TokenType;
import com.example.FAMS.models.EmailDetails;
import com.example.FAMS.models.Token;
import com.example.FAMS.models.User;
import com.example.FAMS.repositories.TokenDAO;
import com.example.FAMS.repositories.UserDAO;
import com.example.FAMS.repositories.UserPermissionDAO;
import com.example.FAMS.services.AuthenticationService;
import com.example.FAMS.services.EmailService;
import com.example.FAMS.services.JWTService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.io.IOException;
import java.util.Date;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.example.FAMS.utils.StringHandler.randomStringGenerator;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

    private final String emailRegex = "^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@"
            + "[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$";
    private final String phoneRegex
            = "^(\\+\\d{1,3}( )?)?((\\(\\d{3}\\))|\\d{3})[- .]?\\d{3}[- .]?\\d{4}$"
            + "|^(\\+\\d{1,3}( )?)?(\\d{3}[ ]?){2}\\d{3}$"
            + "|^(\\+\\d{1,3}( )?)?(\\d{3}[ ]?)(\\d{2}[ ]?){2}\\d{2}$";
    Pattern emailPattern = Pattern.compile(emailRegex);
    Pattern phonePattern = Pattern.compile(phoneRegex);

    private final UserDAO userDAO;
    private final AuthenticationManager authenticationManager;
    private final JWTService jwtService;
    private final TokenDAO tokenDAO;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final UserPermissionDAO userPermissionDAO;

    @Override
    public LoginResponse login(LoginRequest loginRequest) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getEmail(),
                        loginRequest.getPassword()
                )
        );
        var user = userDAO.findByEmail(loginRequest.getEmail())
                .orElseThrow();
        var token = jwtService.generateToken(user);
        revokeAllUserToken(user);
        saveUserToken(user, token);
        return LoginResponse.builder()
                .status("Successful")
                .role(user.getRole().getRole().name())
                .token(token)
                .userInfo(userDAO.findUserByEmail(loginRequest.getEmail()).orElse(null))
                .build();
    }

    @Override
    public CreateResponse createUser(CreateRequest createRequest) throws RuntimeException {
        Matcher matcherEmail = emailPattern.matcher(createRequest.getEmail());
        Matcher matcherPhone = phonePattern.matcher(createRequest.getPhone());
        if (!matcherPhone.matches()) {
            throw new RuntimeException(("Invalid phone number"));
        }
        if (!matcherEmail.matches()) {
            throw new RuntimeException("Invalid email");
        }
        String token = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes()))
                .getRequest().getHeader("Authorization").substring(7);
        String userEmail = jwtService.extractUserEmail(token);
        var requester = userDAO.findUserByEmail(userEmail).orElse(null);
        assert requester != null;
        if (requester.getRole().getRole().equals(Role.CLASS_ADMIN) &&
                createRequest.getRole().equals(Role.SUPER_ADMIN)) {
            throw new RuntimeException("ADMIN can not create SUPER_ADMIN");
        }
        var permission = userPermissionDAO.findUserPermissionByRole(createRequest.getRole()).orElse(null);
        String initialPassword = randomStringGenerator(10);
        User user = User.builder()
                .name(createRequest.getName())
                .email(createRequest.getEmail())
                .password(passwordEncoder.encode(initialPassword))
                .phone(createRequest.getPhone())
                .dob(createRequest.getDob())
                .gender(createRequest.getGender())
                .role(permission)
                .status(true)
                .createdBy(requester.getEmail())
                .createdDate(new Date())
                .modifiedBy(requester.getEmail())
                .modifiedDate(new Date())
                .build();
        var existedUser = userDAO.findByEmail(user.getEmail()).orElse(null);
        if (existedUser == null) {
            var savedUser = userDAO.save(user);
            emailService.sendMail(EmailDetails.builder()
                    .subject("Account Password")
                    .msgBody(initialPassword)
                    .recipient(savedUser.getEmail())
                    .build());
            return CreateResponse.builder()
                    .status("Successful")
                    .createdUser(UserWithRoleDTO.builder()
                            .name(savedUser.getName())
                            .role(savedUser.getRole().getRole())
                            .email(savedUser.getEmail())
                            .phone(savedUser.getPhone())
                            .dob(savedUser.getDob())
                            .gender(savedUser.getGender())
                            .status(savedUser.isStatus())
                            .createdBy(savedUser.getCreatedBy())
                            .createdDate(savedUser.getCreatedDate())
                            .modifiedBy(savedUser.getModifiedBy())
                            .modifiedDate(savedUser.getModifiedDate())
                            .build())
                    .build();
        } else {
            throw new RuntimeException("Existed User!");
        }
    }

    @Override
    public void refresh(HttpServletRequest request, HttpServletResponse response) throws IOException {
        final String authenHeader = request.getHeader("Authorization");
        final String refreshToken;
        final String userEmail;
        if (authenHeader == null || !authenHeader.startsWith("Bearer ")) {
            return;
        }
        refreshToken = authenHeader.substring(7);
        userEmail = jwtService.extractUserEmail(refreshToken);
        if (userEmail != null) {
            var existedUser = userDAO.findByEmail(userEmail).orElseThrow();
            if (jwtService.isTokenValid(refreshToken, existedUser)) {
                var newToken = jwtService.generateToken(existedUser);
                revokeAllUserToken(existedUser);
                saveUserToken(existedUser, newToken);
                var authResponse = AuthenticationResponse.builder()
                        .status("Successful")
                        .token(newToken)
                        .refreshToken(refreshToken)
                        .build();
                new ObjectMapper().writeValue(response.getOutputStream(), authResponse);
            }
        }
    }

    @Override
    public ResponseObject getLoggedInUser(HttpServletRequest request) {
        String authenHeader = request.getHeader("Authorization");
        final String token;
        final String userEmail;
        if (authenHeader == null || !authenHeader.startsWith("Bearer ")) {
            return null;
        }
        token = authenHeader.substring(7);
        userEmail = jwtService.extractUserEmail(token);
        if (userEmail != null) {
            var loggedInUser = userDAO.findByEmail(userEmail).orElseThrow();
            if (jwtService.isTokenValid(token, loggedInUser)) {
                UserDTO userDTO = userDAO.findUserByEmail(userEmail).orElse(null);
                return ResponseObject.builder()
                        .status("Successful")
                        .message(loggedInUser.getRole().getRole().name())
                        .payload(UserWithRoleDTO.builder()
                                .name(userDTO.getName())
                                .role(userDTO.getRole().getRole())
                                .email(userDTO.getEmail())
                                .phone(userDTO.getPhone())
                                .dob(userDTO.getDob())
                                .gender(userDTO.getGender())
                                .status(userDTO.isStatus())
                                .createdBy(userDTO.getCreatedBy())
                                .createdDate(userDTO.getCreatedDate())
                                .modifiedBy(userDTO.getModifiedBy())
                                .modifiedDate(userDTO.getModifiedDate())
                                .build())
                        .build();
            }
        }
        return ResponseObject.builder()
                .status("Fail")
                .message("Invalid request")
                .build();
    }

    public void saveUserToken(User user, String token) {
        var userToken = Token.builder()
                .token(token)
                .user(user)
                .tokenType(TokenType.BEARER)
                .expired(false)
                .revoked(false)
                .build();
        tokenDAO.save(userToken);
    }

    public void revokeAllUserToken(User user) {
        var tokenList = tokenDAO.findAllUserTokenByUserId(user.getUserId());
        tokenList.forEach(token -> {
            token.setRevoked(true);
            token.setExpired(true);
        });
        tokenDAO.saveAll(tokenList);
    }
}
