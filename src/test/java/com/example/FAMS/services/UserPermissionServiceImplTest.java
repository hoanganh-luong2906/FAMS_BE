package com.example.FAMS.services;

import static com.example.FAMS.enums.Permission.*;
import static com.example.FAMS.enums.Permission.SYLLABUS_IMPORT;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.example.FAMS.dto.responses.GetUserPermissionsResponse;
import com.example.FAMS.enums.Role;
import com.example.FAMS.models.User;
import com.example.FAMS.models.UserPermission;
import com.example.FAMS.repositories.UserDAO;
import com.example.FAMS.repositories.UserPermissionDAO;
import com.example.FAMS.service_implementors.UserPermissionServiceImpl;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class UserPermissionServiceImplTest {
  @Mock private UserDAO userDAO;

  @Mock private UserPermissionDAO userPermissionDAO;

  @InjectMocks private UserPermissionServiceImpl userPermissionService;

  @Test
  void User_GrantPermission_returnResponseObject() {
    int userId = 123;
    UserPermission userPermission =
        UserPermission.builder()
            .role(Role.USER)
            .syllabus(List.of())
            .trainingProgram(List.of())
            .userClass(List.of())
            .userManagement(List.of(USER_VIEW))
            .learningMaterial(List.of())
            .build();

    when(userPermissionDAO.findUserPermissionByRole(any())).thenReturn(Optional.of(userPermission));

    // Create a mock User object
    User mockUser = new User();
    mockUser.setUserId(userId);
    mockUser.setRole(userPermissionDAO.findUserPermissionByRole(Role.USER).orElse(null));

    when(userDAO.findById(Mockito.anyInt())).thenReturn(Optional.of(mockUser));
    when(userDAO.save(Mockito.any())).thenReturn(new User());

    var user = userDAO.findById(1);
    var savedUser = userDAO.save(new User());

    Assertions.assertThat(savedUser).isNotNull();
    Assertions.assertThat(user).isNotNull();
  }

  @Test
  public void testGetUserPermission() {
    // Create mock UserPermission objects for testing
    UserPermission userPermission1 =
        UserPermission.builder()
            .role(Role.SUPER_ADMIN)
            .syllabus(
                List.of(
                    SYLLABUS_CREATE,
                        SYLLABUS_VIEW,
                        SYLLABUS_MODIFY,
                    SYLLABUS_DELETE,
                    SYLLABUS_IMPORT))
            .trainingProgram(
                List.of(
                    TRAINING_CREATE,
                        TRAINING_VIEW,
                        TRAINING_MODIFY,
                    TRAINING_DELETE,
                    TRAINING_IMPORT))
            .userClass(List.of(CLASS_CREATE, CLASS_VIEW, CLASS_MODIFY, CLASS_DELETE, CLASS_IMPORT))
            .userManagement(List.of(USER_CREATE, USER_VIEW, USER_MODIFY, USER_DELETE, USER_IMPORT))
            .learningMaterial(List.of())
            .users(Collections.emptySet())
            .build();

    UserPermission userPermission2 =
        UserPermission.builder()
            .role(Role.TRAINER)
            .syllabus(
                List.of(
                    SYLLABUS_CREATE,
                        SYLLABUS_VIEW,
                        SYLLABUS_MODIFY,
                    SYLLABUS_DELETE,
                    SYLLABUS_IMPORT))
            .trainingProgram(List.of(TRAINING_VIEW))
            .userClass(List.of(CLASS_VIEW))
            .userManagement(List.of())
            .learningMaterial(List.of())
            .users(Collections.emptySet())
            .build();

    UserPermission userPermission3 =
        UserPermission.builder()
            .role(Role.USER)
            .syllabus(List.of())
            .trainingProgram(List.of())
            .userClass(List.of())
            .userManagement(List.of())
            .learningMaterial(List.of())
            .users(Collections.emptySet())
            .build();

    UserPermission userPermission4 =
        UserPermission.builder()
            .role(Role.CLASS_ADMIN)
            .syllabus(
                List.of(
                    SYLLABUS_CREATE,
                        SYLLABUS_VIEW,
                        SYLLABUS_MODIFY,
                    SYLLABUS_DELETE,
                    SYLLABUS_IMPORT))
            .trainingProgram(
                List.of(
                    TRAINING_CREATE,
                        TRAINING_VIEW,
                        TRAINING_MODIFY,
                    TRAINING_DELETE,
                    TRAINING_IMPORT))
            .userClass(List.of(CLASS_CREATE, CLASS_VIEW, CLASS_MODIFY, CLASS_DELETE, CLASS_IMPORT))
            .userManagement(List.of(USER_CREATE, USER_VIEW, USER_MODIFY, USER_DELETE, USER_IMPORT))
            .learningMaterial(List.of())
            .users(Collections.emptySet())
            .build();

    List<UserPermission> mockUserPermissions =
        List.of(userPermission1, userPermission2, userPermission3, userPermission4);

    // Mock the behavior of userPermissionDAO.findAll() to return the mock data
    when(userPermissionDAO.findAll()).thenReturn(mockUserPermissions);

    // Call the method under test
    List<GetUserPermissionsResponse> responseList = userPermissionService.getUserPermission();

    // Verify the behavior of the method
    assertEquals(4, responseList.size()); // Check the size of the response list

    // Verify the transformation of permissions
    GetUserPermissionsResponse response1 = responseList.get(0);
    assertEquals(Role.SUPER_ADMIN.name(), response1.getRoleName());
    assertEquals(
        "FULL_ACCESS", response1.getUserclass());
    assertEquals(
        "FULL_ACCESS",
        response1.getSyllabus());
    assertEquals(
        "FULL_ACCESS",
        response1.getTraining());
    assertEquals("ACCESS_DENIED", response1.getLearningMaterial()); // Check when permissions are empty
    assertEquals(
        "FULL_ACCESS", response1.getUserManagement());

    GetUserPermissionsResponse response2 = responseList.get(1);
    assertEquals("TRAINER", response2.getRoleName());
    assertEquals(
        "READ", response2.getUserclass());
    assertEquals(
        "FULL_ACCESS",
        response2.getSyllabus());
    assertEquals(
        "READ",
        response2.getTraining());
    assertEquals("ACCESS_DENIED", response2.getLearningMaterial()); // Check when permissions are empty
    assertEquals(
        "ACCESS_DENIED", response2.getUserManagement());

    // Verify that userPermissionDAO.findAll() was called exactly once
    verify(userPermissionDAO, times(1)).findAll();
  }
}
