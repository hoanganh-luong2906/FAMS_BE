package com.example.FAMS.repositories;

import com.example.FAMS.dto.UserDTO;
import com.example.FAMS.dto.responses.ListUserResponse;
import com.example.FAMS.models.User;
import com.example.FAMS.models.UserPermission;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserDAO extends JpaRepository<User, Integer> {

    @Query(
            """
                            SELECT Us FROM User Us WHERE Us.email = :email AND Us.status = true
                    """)
    Optional<User> findByEmail(String email);


    List<User> findUsersByRole(UserPermission role);

    List<User> findUsersByRoleAndStatus(UserPermission role, boolean status);

    Optional<UserDTO> findUserByEmail(String email);

    List<ListUserResponse> findAllByRole(Enum role);

    Integer countAllByStatusIs(boolean status);

    @Query(
            value =
                    "SELECT a.user_id as 'userId', a.name, a.email, a.phone, a.dob, a.gender, a.created_by as 'createdBy', a.modified_by as 'modifiedBy', a.is_active as 'status', b.role\n"
                            + "FROM users a INNER JOIN user_permission b on a.role = b.permission_id WHERE a.is_active = 1 AND b.role = 'CLASS_ADMIN' OR b.role = 'SUPER_ADMIN';",
            nativeQuery = true)
    List<ListUserResponse> getAllUsersWithRoleAdmin_SuperAdmin();

    @Query(
            value =
                    "SELECT a.user_id as 'userId', a.name, a.email, a.phone, a.dob, a.gender, a.created_by as 'createdBy', a.modified_by as 'modifiedBy', a.is_active as 'status', b.role\n"
                            + "FROM users a INNER JOIN user_permission b on a.role = b.permission_id WHERE a.is_active = 1",
            nativeQuery = true)
    Page<ListUserResponse> findAllUsersBy(Pageable pageable);

    <T> List<T> findBy(Class<T> classType);

    @Query(
            value =
                    "SELECT u.user_id as 'userId', u.name, u.email, u.phone, u.dob, u.gender, u.created_by as 'createdBy', u.modified_by as 'modifiedBy', u.is_active as 'status', p.role\n"
                            + "FROM users as u INNER JOIN user_permission as p on u.role = p.permission_id",
            nativeQuery = true)
    List<ListUserResponse> getAllUsersWithRole();

    @Modifying
    @Transactional
    @Query(value = "UPDATE User u SET u.status = :newStatus WHERE u.userId = :id AND u.status <> :newStatus")
    void changeStatus(@Param("id") int id, @Param("newStatus") boolean newStatus);
}
