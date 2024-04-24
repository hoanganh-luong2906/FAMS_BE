package com.example.FAMS.services;

import static com.example.FAMS.enums.Permission.USER_VIEW;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.example.FAMS.dto.requests.CreateRequest;
import com.example.FAMS.dto.requests.LoginRequest;
import com.example.FAMS.dto.responses.CreateResponse;
import com.example.FAMS.dto.responses.LoginResponse;
import com.example.FAMS.dtos.response.UserDTOImp;
import com.example.FAMS.enums.Role;
import com.example.FAMS.models.EmailDetails;
import com.example.FAMS.models.User;
import com.example.FAMS.models.UserPermission;
import com.example.FAMS.repositories.TokenDAO;
import com.example.FAMS.repositories.UserDAO;
import com.example.FAMS.repositories.UserPermissionDAO;
import com.example.FAMS.service_implementors.AuthenticationServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import java.util.*;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@ExtendWith(MockitoExtension.class)
public class AuthenticationServiceImplTest {
  private Logger logger = LoggerFactory.getLogger(AuthenticationServiceImpl.class);
  @Mock private UserDAO userDAO;
  @Mock private AuthenticationManager authenticationManager;
  @Mock
  private UserPermissionDAO userPermissionDAO;
  @Mock private JWTService jwtService;
  @Mock private TokenDAO tokenDAO;
  @Mock private PasswordEncoder passwordEncoder;
  @Mock private EmailService emailService;
  @InjectMocks private AuthenticationServiceImpl authenticationService;


  @Test
  void User_Login_returnLoginResponse() {
    UserPermission userPermission = UserPermission.builder()
            .role(Role.USER)
            .syllabus(List.of())
            .trainingProgram(List.of())
            .userClass(List.of())
            .userManagement(List.of(USER_VIEW))
            .learningMaterial(List.of())
            .build();

    int userId = 123;
    Role role = Role.USER;
    // Create a mock User object
    User mockUser = new User();
    mockUser.setUserId(userId);
    mockUser.setRole(userPermission);

    LoginRequest loginRequest =
        LoginRequest.builder().email("admin@gmail.com").password("1").build();
    User user = User.builder().email("admin@gmail.com").build();
    List<GrantedAuthority> authorities = new ArrayList<>();
    authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
    Authentication customAuthentication =
        new UsernamePasswordAuthenticationToken(user, null, authorities);

    when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
        .thenReturn(customAuthentication);
    when(userDAO.findByEmail(Mockito.any())).thenReturn(Optional.of(mockUser));
    when(jwtService.generateToken(user)).thenReturn("mockedToken");

    var token = jwtService.generateToken(user);
    var person = userDAO.findByEmail(loginRequest.getEmail()).orElseThrow();

    LoginResponse response = authenticationService.login(loginRequest);

    Assertions.assertThat(response).isNotNull();
    Assertions.assertThat(response.getStatus()).isEqualTo("Successful");
    // Mock the behavior of jwtService.generateToken

  }

  @Test
  void User_CreateUser_returnCreateResponse() {
    when(passwordEncoder.encode(anyString())).thenReturn("1fsdfadfqrwgtwerert234");

    HttpServletRequest httpServletRequestMock = mock(HttpServletRequest.class);
    when(httpServletRequestMock.getHeader("Authorization")).thenReturn("Bearer yourToken");
    ServletRequestAttributes customServletRequestAttributes = new ServletRequestAttributes(httpServletRequestMock);
    RequestContextHolder.setRequestAttributes(customServletRequestAttributes);



    UserPermission userPermission = UserPermission.builder()
            .role(Role.USER)
            .syllabus(List.of())
            .trainingProgram(List.of())
            .userClass(List.of())
            .userManagement(List.of(USER_VIEW))
            .learningMaterial(List.of())
            .build();

    when(userPermissionDAO.findUserPermissionByRole(any())).thenReturn(Optional.of(userPermission));
//    when(authenticationService.randomStringGenerator(anyString())).thenReturn("123fwwretwertewrtefqwef4");

    // Create a mock User object
    int userId = 123;
    UserDTOImp mockUser = new UserDTOImp();
    mockUser.setUserId(userId);
    mockUser.setRole(userPermissionDAO.findUserPermissionByRole(Role.SUPER_ADMIN).orElse(null));
    when(userDAO.findUserByEmail(Mockito.any())).thenReturn(Optional.of(mockUser));
    when(userDAO.findByEmail(Mockito.any())).thenReturn(Optional.empty());

//    String initialPassword = authenticationService.randomStringGenerator("Hefqwewretwertwretfqwefqwello");
    CreateRequest request =
            CreateRequest.builder()
                    .name("Albert Einstein")
                    .email("admin@gmail.com")
                    .phone("0972156450")
                    .dob(new Date())
                    .gender("Male")
                    .role(Role.USER)
                    .status(true)
                    .createdBy("RankillerDY")
                    .modifiedBy("Hoang Anh")
                    .build();
    User user =
            User.builder()
                    .name(request.getName())
//                    .password(passwordEncoder.encode(initialPassword))
                    .email(request.getEmail())
                    .phone(request.getPhone())
                    .dob(request.getDob())
                    .gender(request.getGender())
                    .role(mockUser.getRole())
                    .status(request.isStatus())
                    .createdBy(request.getCreatedBy())
                    .modifiedBy(request.getModifiedBy())
                    .build();
    when(userDAO.save(any())).thenReturn(user);
    when(emailService.sendMail(any())).thenReturn("Mail sent successfully");

    var existedUser = userDAO.findByEmail(user.getEmail()).orElse(null);
    logger.info("Existed user " + existedUser);

    var savedUser = userDAO.save(user);
    emailService.sendMail(EmailDetails.builder()
            .subject("Account Password")
            .build());

    CreateResponse response = authenticationService.createUser(request);
    logger.info("Response value: " + response.toString());
    Assertions.assertThat(response).isNotNull();
    Assertions.assertThat(response.getStatus()).isEqualTo("Successful");

  }

  @Test
  void User_CreateUser_returnFailCreateResponse() {
    when(passwordEncoder.encode(anyString())).thenReturn("1fsdfadfqrwgtwerert234");

    HttpServletRequest httpServletRequestMock = mock(HttpServletRequest.class);
    when(httpServletRequestMock.getHeader("Authorization")).thenReturn("Bearer yourToken");
    ServletRequestAttributes customServletRequestAttributes = new ServletRequestAttributes(httpServletRequestMock);
    RequestContextHolder.setRequestAttributes(customServletRequestAttributes);



    UserPermission userPermission = UserPermission.builder()
            .role(Role.USER)
            .syllabus(List.of())
            .trainingProgram(List.of())
            .userClass(List.of())
            .userManagement(List.of(USER_VIEW))
            .learningMaterial(List.of())
            .build();

    when(userPermissionDAO.findUserPermissionByRole(any())).thenReturn(Optional.of(userPermission));
//    when(authenticationService.randomStringGenerator(anyString())).thenReturn("123fwwretwertewrtefqwef4");

    // Create a mock User object
    int userId = 123;
    UserDTOImp mockUser = new UserDTOImp();
    mockUser.setUserId(userId);
    mockUser.setRole(userPermissionDAO.findUserPermissionByRole(Role.SUPER_ADMIN).orElse(null));
    when(userDAO.findUserByEmail(Mockito.any())).thenReturn(Optional.of(mockUser));


//    String initialPassword = authenticationService.randomStringGenerator("Hefqwewretwertwretfqwefqwello");
    CreateRequest request =
            CreateRequest.builder()
                    .name("Albert Einstein")
                    .email("admin@gmail.com")
                    .phone("0972156450")
                    .dob(new Date())
                    .gender("Male")
                    .role(Role.USER)
                    .status(true)
                    .createdBy("RankillerDY")
                    .modifiedBy("Hoang Anh")
                    .build();
    User user =
            User.builder()
                    .name(request.getName())
//                    .password(passwordEncoder.encode(initialPassword))
                    .email(request.getEmail())
                    .phone(request.getPhone())
                    .dob(request.getDob())
                    .gender(request.getGender())
                    .role(mockUser.getRole())
                    .status(request.isStatus())
                    .createdBy(request.getCreatedBy())
                    .modifiedBy(request.getModifiedBy())
                    .build();
    when(userDAO.findByEmail(Mockito.any())).thenReturn(Optional.of(user));
    when(userDAO.save(any())).thenReturn(user);
    when(emailService.sendMail(any())).thenReturn("Mail sent successfully");

    var existedUser = userDAO.findByEmail(user.getEmail()).orElse(null);
    logger.info("Existed user " + existedUser);

    var savedUser = userDAO.save(user);
    emailService.sendMail(EmailDetails.builder()
            .subject("Account Password")
            .build());

    CreateResponse response = authenticationService.createUser(request);
    logger.info("Response value: " + response.toString());
    Assertions.assertThat(response).isNotNull();
    Assertions.assertThat(response.getStatus()).isEqualTo("Fail");
  }


}
