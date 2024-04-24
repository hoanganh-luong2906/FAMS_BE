package com.example.FAMS.service_implementors;

import com.example.FAMS.dto.requests.UpdatePermissionRequest;
import com.example.FAMS.dto.responses.GetUserPermissionsResponse;
import com.example.FAMS.dto.responses.ResponseObject;
import com.example.FAMS.enums.Permission;
import com.example.FAMS.enums.Role;
import com.example.FAMS.models.UserPermission;
import com.example.FAMS.repositories.UserDAO;
import com.example.FAMS.repositories.UserPermissionDAO;
import com.example.FAMS.services.UserPermissionService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserPermissionServiceImpl implements UserPermissionService {

    private final Logger logger = LoggerFactory.getLogger(UserPermissionServiceImpl.class);
    private final UserPermissionDAO userPermissionDAO;
    private final UserDAO userDAO;

    @Override
    public List<GetUserPermissionsResponse> getUserPermission() {
        var listUserDTO = userPermissionDAO.findAll(); // fake
        List<GetUserPermissionsResponse> responseList = new ArrayList<>();
        for (UserPermission userPermission : listUserDTO) {
            String[] permissionArr = {"FULL_ACCESS", "ACCESS_DENIED"};
            GetUserPermissionsResponse responseObj = new GetUserPermissionsResponse();
            Class<?> objClass = userPermission.getClass();
            Field[] fields = objClass.getDeclaredFields();
            int countField = 0;
            for (Field field : fields) {
                field.setAccessible(true);
                try {
                    Object fieldValue = field.get(userPermission);
                    if (countField != 0) {
                        String permissionString;
                        if (countField != 1) {
                            permissionString =
                                    fieldValue.toString().substring(1, fieldValue.toString().length() - 1);
                        } else {
                            permissionString = fieldValue.toString();
                        }
                        switch (countField) {
                            case 1:
                                responseObj.setRoleName(fieldValue.toString());
                                break;
                            case 2:
                                if (!permissionString.isEmpty()) {
                                    if (permissionString.split(",").length == 5) {
                                        responseObj.setSyllabus(permissionArr[0]);
                                    } else if (permissionString.split(",").length == 1) {
                                        responseObj.setSyllabus(permissionString.split("_")[1]);
                                    } else {
                                        responseObj.setSyllabus(permissionString);
                                    }
                                } else {
                                    responseObj.setSyllabus(permissionArr[1]);
                                }
                                break;
                            case 3:
                                if (!permissionString.isEmpty()) {
                                    if (permissionString.split(",").length == 5) {
                                        responseObj.setTraining(permissionArr[0]);
                                    } else if (permissionString.split(",").length == 1) {
                                        responseObj.setTraining(permissionString.split("_")[1]);
                                    } else {
                                        responseObj.setTraining(permissionString);
                                    }
                                } else {
                                    responseObj.setTraining(permissionArr[1]);
                                }
                                break;
                            case 4:
                                if (!permissionString.isEmpty()) {
                                    if (permissionString.split(",").length == 5) {
                                        responseObj.setUserclass(permissionArr[0]);
                                    } else if (permissionString.split(",").length == 1) {
                                        responseObj.setUserclass(permissionString.split("_")[1]);
                                    } else {
                                        responseObj.setUserclass(permissionString);
                                    }
                                } else {
                                    responseObj.setUserclass(permissionArr[1]);
                                }
                                break;
                            case 5:
                                if (!permissionString.isEmpty()) {
                                    if (permissionString.split(",").length == 5) {
                                        responseObj.setLearningMaterial(permissionArr[0]);
                                    } else if (permissionString.split(",").length == 1) {
                                        responseObj.setLearningMaterial(permissionString.split("_")[1]);
                                    } else {
                                        responseObj.setLearningMaterial(permissionString);
                                    }
                                } else {
                                    responseObj.setLearningMaterial(permissionArr[1]);
                                }
                                break;
                            case 6:
                                if (!permissionString.isEmpty()) {
                                    if (permissionString.split(",").length == 5) {
                                        responseObj.setUserManagement(permissionArr[0]);
                                    } else if (permissionString.split(",").length == 1) {
                                        responseObj.setUserManagement(permissionString.split("_")[1]);
                                    } else {
                                        responseObj.setUserManagement(permissionString);
                                    }
                                } else {
                                    responseObj.setUserManagement(permissionArr[1]);
                                }
                                break;
                            default:
                                break;
                        }
                    }
                    countField++;
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
            responseList.add(responseObj);
        }
        return responseList;
    }

    @Override
    public ResponseObject updatePermission(List<UpdatePermissionRequest> updateRequest) {
        var permissionList = userPermissionDAO.findAll();
        final String[] prefixArr = {"ROLE", "SYLLABUS", "TRAINING", "CLASS", "MATERIAL", "USER"};
        String[] permissionArr = {"FULL_ACCESS", "ACCESS_DENIED"};
        for (UpdatePermissionRequest updatePermission : updateRequest) {
            for (UserPermission permission : permissionList) {
                if (permission.getRole().name().equals(updatePermission.getRoleName())) {
                    int nthChild = 0;
                    Class<?> objClass = updatePermission.getClass();
                    Field[] fields = objClass.getDeclaredFields();
                    for (Field field : fields) {
                        field.setAccessible(true);
                        List<Permission> tmpUserPermission = new ArrayList<>();
                        if (nthChild > 0 && nthChild < fields.length) {
                            try {
                                Object value = field.get(updatePermission);
                                if (value.equals(permissionArr[0])) {
                                    tmpUserPermission =
                                            List.of(
                                                    Permission.valueOf(prefixArr[nthChild] + "_DELETE"),
                                                    Permission.valueOf(prefixArr[nthChild] + "_CREATE"),
                                                    Permission.valueOf(prefixArr[nthChild] + "_MODIFY"),
                                                    Permission.valueOf(prefixArr[nthChild] + "_VIEW"),
                                                    Permission.valueOf(prefixArr[nthChild] + "_IMPORT"));
                                } else if (value.equals(permissionArr[1])) {
                                } else {
                                    tmpUserPermission = List.of(
                                            Permission.valueOf(prefixArr[nthChild] + "_" + value)
                                    );
                                }
                                switch (nthChild) {
                                    case 1:
                                        permission.setSyllabus(tmpUserPermission);
                                        break;
                                    case 2:
                                        permission.setTrainingProgram(tmpUserPermission);
                                        break;
                                    case 3:
                                        permission.setUserClass(tmpUserPermission);
                                        break;
                                    case 4:
                                        permission.setLearningMaterial(tmpUserPermission);
                                        break;
                                    case 5:
                                        permission.setUserManagement(tmpUserPermission);
                                        break;
                                    default:
                                        break;
                                }
                                nthChild++;
                            } catch (IllegalAccessException e) {
                                throw new RuntimeException(e);
                            }
                        } else {
                            nthChild++;
                        }
                    }
                }
            }
        }
        var savedPermissionList = userPermissionDAO.saveAll(permissionList);
        return ResponseObject.builder()
                .status("Successful")
                .message("Permission updated")
                .payload(
//                savedPermissionList
                        convertToResponseList(savedPermissionList)
                )
                .build();
    }

    private List<GetUserPermissionsResponse> convertToResponseList(List<UserPermission> userPermissionList) {
        List<GetUserPermissionsResponse> responseList = new ArrayList<>();
        for (UserPermission userPermission : userPermissionList) {
            String[] permissionArr = {"FULL_ACCESS", "ACCESS_DENIED"};
            GetUserPermissionsResponse responseObj = new GetUserPermissionsResponse();
            Class<?> objClass = userPermission.getClass();
            Field[] fields = objClass.getDeclaredFields();
            int countField = 0;
            for (Field field : fields) {
                field.setAccessible(true);
                try {
                    Object fieldValue = field.get(userPermission);
                    if (countField != 0) {
                        String permissionString;
                        if (countField != 1) {
                            permissionString =
                                    fieldValue.toString().substring(1, fieldValue.toString().length() - 1);
                        } else {
                            permissionString = fieldValue.toString();
                        }
                        switch (countField) {
                            case 1:
                                responseObj.setRoleName(fieldValue.toString());
                                break;
                            case 2:
                                if (!permissionString.isEmpty()) {
                                    if (permissionString.split(",").length == 5) {
                                        responseObj.setSyllabus(permissionArr[0]);
                                    } else if (permissionString.split(",").length == 1) {
                                        responseObj.setSyllabus(permissionString.split("_")[1]);
                                    } else {
                                        responseObj.setSyllabus(permissionString);
                                    }
                                } else {
                                    responseObj.setSyllabus(permissionArr[1]);
                                }
                                break;
                            case 3:
                                if (!permissionString.isEmpty()) {
                                    if (permissionString.split(",").length == 5) {
                                        responseObj.setTraining(permissionArr[0]);
                                    } else if (permissionString.split(",").length == 1) {
                                        responseObj.setTraining(permissionString.split("_")[1]);
                                    } else {
                                        responseObj.setTraining(permissionString);
                                    }
                                } else {
                                    responseObj.setTraining(permissionArr[1]);
                                }
                                break;
                            case 4:
                                if (!permissionString.isEmpty()) {
                                    if (permissionString.split(",").length == 5) {
                                        responseObj.setUserclass(permissionArr[0]);
                                    } else if (permissionString.split(",").length == 1) {
                                        responseObj.setUserclass(permissionString.split("_")[1]);
                                    } else {
                                        responseObj.setUserclass(permissionString);
                                    }
                                } else {
                                    responseObj.setUserclass(permissionArr[1]);
                                }
                                break;
                            case 5:
                                if (!permissionString.isEmpty()) {
                                    if (permissionString.split(",").length == 5) {
                                        responseObj.setLearningMaterial(permissionArr[0]);
                                    } else if (permissionString.split(",").length == 1) {
                                        responseObj.setLearningMaterial(permissionString.split("_")[1]);
                                    } else {
                                        responseObj.setLearningMaterial(permissionString);
                                    }
                                } else {
                                    responseObj.setLearningMaterial(permissionArr[1]);
                                }
                                break;
                            case 6:
                                if (!permissionString.isEmpty()) {
                                    if (permissionString.split(",").length == 5) {
                                        responseObj.setUserManagement(permissionArr[0]);
                                    } else if (permissionString.split(",").length == 1) {
                                        responseObj.setUserManagement(permissionString.split("_")[1]);
                                    } else {
                                        responseObj.setUserManagement(permissionString);
                                    }
                                } else {
                                    responseObj.setUserManagement(permissionArr[1]);
                                }
                                break;
                            default:
                                break;
                        }
                    }
                    countField++;
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
            responseList.add(responseObj);
        }
        return responseList;
    }

    @Override
    public ResponseEntity<ResponseObject> grantPermission(int userID, Role role) {
        var person = userDAO.findById(userID).orElse(null);
        var permission = userPermissionDAO.findUserPermissionByRole(role).orElse(null);
        if (person == null) {
            return null;
        }
        person.setRole(permission);

        logger.info("Grant permission for a user");
        return ResponseEntity.ok(new ResponseObject("Successful", "Change user permission", userDAO.save(person)));
    }

}
