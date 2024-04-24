package com.example.FAMS.services;

import com.example.FAMS.dto.requests.UpdateRequest;
import com.example.FAMS.dto.responses.ListUserResponse;
import com.example.FAMS.dto.responses.UpdateResponse;
import com.example.FAMS.dtos.response.ListUserResponseImpl;
import com.example.FAMS.enums.Role;
import com.example.FAMS.models.User;
import com.example.FAMS.models.UserPermission;
import com.example.FAMS.repositories.UserDAO;
import com.example.FAMS.repositories.UserPermissionDAO;
import com.example.FAMS.service_implementors.UserServiceImpl;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static com.example.FAMS.enums.Permission.USER_VIEW;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {
  @Mock private UserDAO userDAO;
  @InjectMocks private UserServiceImpl userService;
  @Mock private UserPermissionDAO userPermissionDAO;

  @Test
  void User_getAllUser_returnResponseObject() {

    List<ListUserResponse> expectedList = new ArrayList<>();
    expectedList.add(
        new ListUserResponseImpl(
            1, "User1", "user1@example.com", "1234567890", new Date(), "Male", Role.USER));
    expectedList.add(
        new ListUserResponseImpl(
            2, "User2", "user2@example.com", "9876543210", new Date(), "Female", Role.USER));

    when(userDAO.save(Mockito.any())).thenReturn(new User());
    when(userDAO.findAllByRole(Role.USER)).thenReturn(expectedList);

    List<ListUserResponse> list = userDAO.findAllByRole(Role.USER);
    var savedUser = userDAO.save(new User());
    Assertions.assertThat(list).isNotNull();
    Assertions.assertThat(list.size()).isGreaterThan(0);
    Assertions.assertThat(savedUser).isNotNull();
  }

  @Test
  public void testUpdateUser() {
    // Tạo dữ liệu giả lập
    UpdateRequest updateRequest = new UpdateRequest();
    updateRequest.setRole(Role.USER);
    updateRequest.setName("Anh Quan");
    updateRequest.setPhone("1234567890");
    updateRequest.setDob(new Date());
    updateRequest.setGender("Male");
    updateRequest.setStatus(true);
    updateRequest.setModifiedBy("admin");

    User existingUser = new User();
    existingUser.setEmail("user@gmail.com");
    UserPermission userPermission = new UserPermission();
    userPermission.setRole(Role.USER);

    when(userDAO.findByEmail("user@gmail.com")).thenReturn(Optional.of(existingUser));
    when(userPermissionDAO.findUserPermissionByRole(Role.USER)).thenReturn(Optional.of(userPermission));
    when(userDAO.save(any())).thenAnswer(invocation -> {
      Object[] args = invocation.getArguments();
      return (User) args[0];
    });

    UpdateResponse updateResponse = userService.updateUser("user@gmail.com", updateRequest);

    // Kiểm tra các lệnh đã được gọi đúng số lần
    verify(userDAO, times(1)).findByEmail("user@gmail.com");
    verify(userPermissionDAO, times(1)).findUserPermissionByRole(Role.USER);
    verify(userDAO, times(1)).save(existingUser);
  }
}
