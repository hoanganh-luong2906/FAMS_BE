package com.example.FAMS.service_implementors;

import com.example.FAMS.controllers.UserController;
import com.example.FAMS.dto.requests.UpdatePasswordRequest;
import com.example.FAMS.dto.requests.UpdateRequest;
import com.example.FAMS.dto.responses.ListUserResponse;
import com.example.FAMS.dto.responses.ResponseObject;
import com.example.FAMS.dto.responses.UpdateResponse;
import com.example.FAMS.dto.responses.UserWithRoleDTO;
import com.example.FAMS.enums.Role;
import com.example.FAMS.models.EmailDetails;
import com.example.FAMS.models.User;
import com.example.FAMS.repositories.UserDAO;
import com.example.FAMS.repositories.UserPermissionDAO;
import com.example.FAMS.services.EmailService;
import com.example.FAMS.services.JWTService;
import com.example.FAMS.services.UserService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.example.FAMS.utils.StringHandler.randomStringGenerator;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    String patterns
            = "^(\\+\\d{1,3}( )?)?((\\(\\d{3}\\))|\\d{3})[- .]?\\d{3}[- .]?\\d{4}$"
            + "|^(\\+\\d{1,3}( )?)?(\\d{3}[ ]?){2}\\d{3}$"
            + "|^(\\+\\d{1,3}( )?)?(\\d{3}[ ]?)(\\d{2}[ ]?){2}\\d{2}$";
    Pattern pattern = Pattern.compile(patterns);
    private final Logger logger = LoggerFactory.getLogger(UserController.class);
    private final UserDAO userDAO;
    private final UserPermissionDAO userPermissionDAO;
    private final JWTService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private List<ListUserResponse> userList;

    @Override
    public ResponseEntity<ResponseObject> getAll() {
        try {
            userList = userDAO.getAllUsersWithRole();
            logger.info("Return list of user");
            return ResponseEntity.ok(new ResponseObject("Successful", "Found user", userList));
        } catch (Exception e) {
            e.printStackTrace();
            userList = Collections.emptyList();
            return ResponseEntity.ok(new ResponseObject("Failed", "Not found user", userList));
        }
    }

    @Override
    public ResponseEntity<ResponseObject> pagination(int pageNo) {
        int totalPage = getNumberOfUsers();

        Pageable paging = PageRequest.of(pageNo, 8);
        Page<ListUserResponse> pagedResult = userDAO.findAllUsersBy(paging);

        if (pagedResult.hasContent()) {
            return ResponseEntity.ok(new ResponseObject("Successful", "null", totalPage, pagedResult.getContent()));
        } else {
            return ResponseEntity.ok(new ResponseObject("Failed", "null", totalPage, pagedResult.getContent()));
        }
    }

    @Override
    public UpdateResponse updateUser(String userEmail, UpdateRequest updateRequest) {
        Matcher matcher = pattern.matcher(updateRequest.getPhone());
        if (!matcher.matches()) {
            throw new RuntimeException(("Invalid phone number"));
        }
        var existedUser = userDAO.findByEmail(userEmail).orElse(null);
        var permission = userPermissionDAO.findUserPermissionByRole(updateRequest.getRole())
                .orElse(null);
        if (existedUser != null) {
            existedUser.setRole(permission);
            existedUser.setName(updateRequest.getName());
            existedUser.setPhone(updateRequest.getPhone());
            existedUser.setDob(updateRequest.getDob());
            existedUser.setGender(updateRequest.getGender());
            existedUser.setModifiedBy(updateRequest.getModifiedBy());
            existedUser.setModifiedDate(new Date());

            // Save the updated user
            User updatedUser = userDAO.save(existedUser);

            return UpdateResponse.builder()
                    .status("Update successful")
                    .updatedUser(UserWithRoleDTO.builder()
                            .name(updatedUser.getName())
                            .role(updatedUser.getRole().getRole())
                            .email(updatedUser.getEmail())
                            .phone(updatedUser.getPhone())
                            .dob(updatedUser.getDob())
                            .gender(updatedUser.getGender())
                            .status(updatedUser.isStatus())
                            .createdBy(updatedUser.getCreatedBy())
                            .createdDate(updatedUser.getCreatedDate())
                            .modifiedBy(updatedUser.getModifiedBy())
                            .modifiedDate(updatedUser.getModifiedDate())
                            .build())
                    .build();
        } else {
            // Return an UpdateResponse indicating user not found
            return UpdateResponse.builder()
                    .status("User not found")
                    .updatedUser(null)
                    .build();
        }
    }

    @Override
    public ResponseObject deleteUser(String mail) {
        String token = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes()))
                .getRequest().getHeader("Authorization").substring(7);
        String performUserEmail = jwtService.extractUserEmail(token);
        User performUser = userDAO.findByEmail(performUserEmail).orElse(null);

        //set default ResponseObject
        ResponseObject responseObject = ResponseObject
                .builder()
                .status("Failed")
                .message("Delete user failed")
                .payload("Performed user " + performUserEmail + " not found")
                .build();

        if (performUser != null) {
            switch (performUser.getRole().getRole()) {
                case SUPER_ADMIN:
                    responseObject = disableUser(performUserEmail, mail, null);
                    break;
                case CLASS_ADMIN:
                    responseObject = disableUser(performUserEmail, mail, Role.USER);
                    break;
            }
        }
        return responseObject;
    }


    @Override
    public String authorizeAccount(String emailAddress) {
        var existingUser = userDAO.findUserByEmail(emailAddress).orElse(null);
        if (existingUser == null) {
            throw new RuntimeException("User not found");
        } else {
            String verificationCode = randomStringGenerator(8);
            emailService.sendMail(EmailDetails.builder()
                    .subject("Verification Code")
                    .msgBody(verificationCode)
                    .recipient(existingUser.getEmail())
                    .build());
            return verificationCode;
        }
    }

    @Override
    public ResponseEntity<ResponseObject> changeStatus(int id, String type) {
        if (checkExistedAndValid(id, type)) {
            switch (type) {
                case "active":
                    return ResponseEntity
                            .ok()
                            .body(activateUser(id));

                case "inactive":
                    return ResponseEntity
                            .ok()
                            .body(inactivateUser(id));
            }
        }
        String msg = type.toUpperCase().charAt(0) + type.substring(1) + " user with id " + id + " fail";
        return ResponseEntity
                .badRequest()
                .body(new ResponseObject("Fail", msg, null));
    }

    private boolean checkExistedAndValid(int id, String type) {
        User u = userDAO.findById(id).orElse(null);
        String token = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes()))
                .getRequest().getHeader("Authorization").substring(7);
        String userEmail = jwtService.extractUserEmail(token);
        User currentUser = userDAO.findByEmail(userEmail).orElse(null);

        boolean status = type.equalsIgnoreCase("active");
        return currentUser != null && id != currentUser.getUserId()
                && u != null && u.isStatus() != status;
    }

    private ResponseObject activateUser(int id) {
        userDAO.changeStatus(id, true);
        String msg = "Active user with id " + id + " successfully";
        return new ResponseObject("Success", msg, userDAO.findById(id).orElse(null));
    }

    private ResponseObject inactivateUser(int id) {
        userDAO.changeStatus(id, false);
        String msg = "Inactive user with id " + id + " successfully";
        return new ResponseObject("Success", msg, userDAO.findById(id).orElse(null));
    }

    @Override
    public ResponseObject updatePassword(UpdatePasswordRequest updateRequest) {
        if (updateRequest.isAuthorized() || updateRequest.isLoggedIn()) {
            var existedUser = userDAO.findByEmail(updateRequest.getRequesterEmail()).orElse(null);
            if (existedUser == null) {
                throw new RuntimeException("User not found");
            } else {
                existedUser.setPassword(passwordEncoder.encode(updateRequest.getNewPassword()));
                User savedUser = userDAO.save(existedUser);
                return ResponseObject.builder()
                        .status("Successful")
                        .message("Update successfully")
                        .payload(UpdatePasswordRequest.builder()
                                .requesterEmail(savedUser.getEmail())
                                .newPassword(updateRequest.getNewPassword())
                                .build()
                        )
                        .build();
            }
        } else {
            throw new RuntimeException("Unauthorized User");
        }
    }

    @Override
    public ResponseEntity<ResponseObject> getAllTrainersByRole() {
        try {
            var list = userDAO.findUsersByRoleAndStatus(userPermissionDAO.findById(2).orElse(null), true);
            logger.info("Return list of user");
            return ResponseEntity.ok(new ResponseObject("Successful", "Found user", list));
        } catch (Exception e) {
            var list = Collections.emptyList();
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseObject("Failed", "Not found user", list));
        }
    }

    @Override
    public ResponseEntity<ResponseObject> getAllTraineeByRole() {
        try {
            var list = userDAO.findUsersByRole(userPermissionDAO.findById(3).orElse(null));
            logger.info("Return list of user");
            return ResponseEntity.ok(new ResponseObject("Successful", "Found user", list));
        } catch (Exception e) {
            var list = Collections.emptyList();
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseObject("Failed", "Not found user", list));
        }
    }

    @Override
    public ResponseEntity<ResponseObject> getAllAdminsByRole() {
        try {
            var list = userDAO.findUsersByRole(userPermissionDAO.findById(4).orElse(null));
            logger.info("Return list of user");
            return ResponseEntity.ok(new ResponseObject("Successful", "Found user", list));
        } catch (Exception e) {
            var list = Collections.emptyList();
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseObject("Failed", "Not found user", list));
        }
    }

    @Override
    public ResponseEntity<ResponseObject> getAllAdminAndSuperAdminByRole() {
        try {
            userList = userDAO.getAllUsersWithRoleAdmin_SuperAdmin();
            logger.info("Return list of Admin_SuperAdmin");
            return ResponseEntity.ok(new ResponseObject("Successful", "Found user", userList));
        } catch (Exception e) {
            userList = Collections.emptyList();
            return ResponseEntity.ok(new ResponseObject("Failed", "Not found user", userList));
        }
    }

    private ResponseObject disableUser(String performUserEmail, String deletedUserEmail, Role role) {
        User deletedUser = userDAO.findByEmail(deletedUserEmail).orElse(null);

        if (deletedUser != null) {
            if (!performUserEmail.equalsIgnoreCase(deletedUserEmail)) {
                if (role == null) {
                    deletedUser.setStatus(false);
                    userDAO.save(deletedUser);
                    return ResponseObject
                            .builder()
                            .status("Successful")
                            .message("Delete user successfully")
                            .payload("")
                            .build();
                } else {
                    if (deletedUser.getRole().getRole().equals(role)) {
                        deletedUser.setStatus(false);
                        userDAO.save(deletedUser);
                        return ResponseObject
                                .builder()
                                .status("Successful")
                                .message("Delete user successfully")
                                .payload("")
                                .build();
                    } else {
                        return ResponseObject
                                .builder()
                                .status("Failed")
                                .message("Delete user failed")
                                .payload("You don't have enough permission to delete this user")
                                .build();
                    }
                }
            }
            return ResponseObject
                    .builder()
                    .status("Failed")
                    .message("Delete user failed")
                    .payload("Can't delete yourself")
                    .build();
        }
        return ResponseObject
                .builder()
                .status("Failed")
                .message("Delete user failed")
                .payload("User " + deletedUserEmail + " not found")
                .build();
    }

    public int getNumberOfUsers() {
        return userDAO.countAllByStatusIs(true);
    }
}
