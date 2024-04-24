package com.example.FAMS;

import com.example.FAMS.dto.requests.LoginRequest;
import com.example.FAMS.enums.Role;
import com.example.FAMS.models.*;
import com.example.FAMS.models.composite_key.SyllabusTrainingProgramCompositeKey;
import com.example.FAMS.repositories.*;
import com.example.FAMS.services.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.example.FAMS.enums.Permission.*;

@SpringBootApplication
@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/")
public class FamsApplication {

    private final UserDAO userDAO;
    private final UserPermissionDAO userPermissionDAO;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationService authenticationService;
    private final StandardOutputDAO standardOutputDAO;
    private final SyllabusDAO syllabusDAO;
    private final ClassDAO classDAO;
    private final TrainingProgramDAO trainingProgramDAO;
    private final ClassUserDAO classUserDAO;
    private final TrainingProgramSyllabusDAO trainingProgramSyllabusDAO;
    //    private final FsuDAO fsuDAO;
//    private final LocationDAO locationDAO;

    public static void main(String[] args) {
        SpringApplication.run(FamsApplication.class, args);
    }

    @Bean
    public CommandLineRunner initData() {
        return new CommandLineRunner() {
            @Override
            public void run(String... args) throws Exception {
                if (standardOutputDAO.findAll().size() == 0) {
                    List<StandardOutput> standardOutputList = new ArrayList<>();
                    String[] objectiveCode = {"h4sd", "h6sd", "k6sd", "hk416", "mp5k", "mac10", "m4a1"};
                    for (int i = 0; i < objectiveCode.length; i++) {
                        StandardOutput standardOutput =
                                StandardOutput.builder()
                                        .outputCode(objectiveCode[i].toUpperCase())
                                        .outputName(objectiveCode[i])
                                        .description("some bs description.")
                                        .build();

                        standardOutputList.add(standardOutput);
                    }
                    standardOutputDAO.saveAll(standardOutputList);
                }
                if (userPermissionDAO.findAll().size() == 0) {
                    List<UserPermission> permissionList = new ArrayList<>();
                    permissionList.add(UserPermission.builder()
                            .role(Role.SUPER_ADMIN)
                            .syllabus(
                                    List.of(
                                            SYLLABUS_CREATE, SYLLABUS_VIEW, SYLLABUS_MODIFY, SYLLABUS_DELETE, SYLLABUS_IMPORT))
                            .trainingProgram(
                                    List.of(
                                            TRAINING_CREATE, TRAINING_VIEW, TRAINING_MODIFY, TRAINING_DELETE, TRAINING_IMPORT))
                            .userClass(
                                    List.of(CLASS_CREATE, CLASS_VIEW, CLASS_MODIFY, CLASS_DELETE, CLASS_IMPORT))
                            .userManagement(
                                    List.of(USER_CREATE, USER_VIEW, USER_MODIFY, USER_DELETE, USER_IMPORT))
                            .learningMaterial(List.of())
                            .build());
                    permissionList.add(UserPermission.builder()
                            .role(Role.TRAINER)
                            .syllabus(List.of(SYLLABUS_CREATE, SYLLABUS_VIEW, SYLLABUS_MODIFY, SYLLABUS_DELETE, SYLLABUS_IMPORT))
                            .trainingProgram(List.of(TRAINING_VIEW))
                            .userClass(List.of(CLASS_VIEW))
                            .userManagement(List.of())
                            .learningMaterial(List.of())
                            .build());
                    permissionList.add(UserPermission.builder()
                            .role(Role.USER)
                            .syllabus(List.of())
                            .trainingProgram(List.of())
                            .userClass(List.of())
                            .userManagement(List.of())
                            .learningMaterial(List.of())
                            .build());
                    permissionList.add(UserPermission.builder()
                            .role(Role.CLASS_ADMIN)
                            .syllabus(
                                    List.of(
                                            SYLLABUS_CREATE, SYLLABUS_VIEW, SYLLABUS_MODIFY, SYLLABUS_DELETE, SYLLABUS_IMPORT))
                            .trainingProgram(
                                    List.of(
                                            TRAINING_CREATE, TRAINING_VIEW, TRAINING_MODIFY, TRAINING_DELETE, TRAINING_IMPORT))
                            .userClass(
                                    List.of(CLASS_CREATE, CLASS_VIEW, CLASS_MODIFY, CLASS_DELETE, CLASS_IMPORT))
                            .userManagement(
                                    List.of(USER_CREATE, USER_VIEW, USER_MODIFY, USER_DELETE, USER_IMPORT))
                            .learningMaterial(List.of())
                            .build());
                    userPermissionDAO.saveAll(permissionList);
                }

//                if (fsuDAO.findAll().isEmpty()) {
//                    List<Fsu> fsuList = new ArrayList<>();
//
//                    Fsu fsu = Fsu.builder()
//                            .fsuId("FHCM")
//                            .fsuName("FPT HO CHI MINH")
//                            .build();
//                    Fsu fsu1 = Fsu.builder()
//                            .fsuId("FHN")
//                            .fsuName("FPT HA NOI")
//                            .build();
//                    Fsu fsu2 = Fsu.builder()
//                            .fsuId("FDN")
//                            .fsuName("FPT DA NANG")
//                            .build();
//                    Fsu fsu3 = Fsu.builder()
//                            .fsuId("FVT")
//                            .fsuName("FPT VUNG TAU")
//                            .build();
//
//                    fsuList.add(fsu);
//                    fsuList.add(fsu1);
//                    fsuList.add(fsu2);
//                    fsuList.add(fsu3);
//
//                    fsuDAO.saveAll(fsuList);
//                }
//                if (locationDAO.findAll().isEmpty()) {
//                    List<Location> locationList = new ArrayList<>();
//
//                    Location location = Location.builder()
//                            .location("FTOWN LMAO 1")
//                            .fsuId(fsuDAO.findById("FHCM").get())
//                            .build();
//
//                    Location location1 = Location.builder()
//                            .location("FTOWN LMAO 2")
//                            .fsuId(fsuDAO.findById("FHCM").get())
//                            .build();
//
//                    Location location2 = Location.builder()
//                            .location("123, lmao street hehe")
//                            .fsuId(fsuDAO.findById("FHN").get())
//                            .build();
//
//                    Location location3 = Location.builder()
//                            .location("123, lmao 2 street hihi")
//                            .fsuId(fsuDAO.findById("FHN").get())
//                            .build();
//
//                    Location location4 = Location.builder()
//                            .location("234, cj house")
//                            .fsuId(fsuDAO.findById("FDN").get())
//                            .build();
//
//                    Location location5 = Location.builder()
//                            .location("234, smoke house")
//                            .fsuId(fsuDAO.findById("FDN").get())
//                            .build();
//
//
//                    locationDAO.saveAll(locationList);
//                }
                if (userDAO.findAll().isEmpty()) {
                    List<User> userList = new ArrayList<>();
                    userList.add(User.builder()
                            .email("admin@gmail.com")
                            .password(passwordEncoder.encode("1"))
                            .name("Admin")
                            .phone("0977545450")
                            .dob(new Date())
                            .gender("male")
                            .role(userPermissionDAO.findUserPermissionByRole(Role.SUPER_ADMIN).orElse(null))
                            .status(true)
                            .createdBy("Hoang Anh")
                            .createdDate(new Date())
                            .modifiedBy("Hoang Anh")
                            .modifiedDate(new Date())
                            .build());
                    userList.add(User.builder()
                            .email("classadmin@gmail.com")
                            .password(passwordEncoder.encode("1"))
                            .name("Class Admin")
                            .phone("0977545451")
                            .dob(new Date())
                            .gender("male")
                            .role(userPermissionDAO.findUserPermissionByRole(Role.CLASS_ADMIN).orElse(null))
                            .status(true)
                            .createdBy("Hoang Anh")
                            .createdDate(new Date())
                            .modifiedBy("Hoang Anh")
                            .modifiedDate(new Date())
                            .build());
                    userList.add(User.builder()
                            .email("classadmin1@gmail.com")
                            .password(passwordEncoder.encode("1"))
                            .name("Class Admin1")
                            .phone("0977545451")
                            .dob(new Date())
                            .gender("male")
                            .role(userPermissionDAO.findUserPermissionByRole(Role.CLASS_ADMIN).orElse(null))
                            .status(true)
                            .createdBy("Hoang Anh")
                            .createdDate(new Date())
                            .modifiedBy("Hoang Anh")
                            .modifiedDate(new Date())
                            .build());
                    userList.add(User.builder()
                            .email("classadmin2@gmail.com")
                            .password(passwordEncoder.encode("1"))
                            .name("Class Admin2")
                            .phone("0977545451")
                            .dob(new Date())
                            .gender("male")
                            .role(userPermissionDAO.findUserPermissionByRole(Role.CLASS_ADMIN).orElse(null))
                            .status(true)
                            .createdBy("Hoang Anh")
                            .createdDate(new Date())
                            .modifiedBy("Hoang Anh")
                            .modifiedDate(new Date())
                            .build());
                    userList.add(User.builder()
                            .email("classadmin3@gmail.com")
                            .password(passwordEncoder.encode("1"))
                            .name("Class Admin3")
                            .phone("0977545451")
                            .dob(new Date())
                            .gender("male")
                            .role(userPermissionDAO.findUserPermissionByRole(Role.CLASS_ADMIN).orElse(null))
                            .status(true)
                            .createdBy("Hoang Anh")
                            .createdDate(new Date())
                            .modifiedBy("Hoang Anh")
                            .modifiedDate(new Date())
                            .build());
                    userList.add(User.builder()
                            .email("trainer@gmail.com")
                            .password(passwordEncoder.encode("1"))
                            .name("Trainer")
                            .phone("0977545452")
                            .dob(new Date())
                            .gender("male")
                            .role(userPermissionDAO.findUserPermissionByRole(Role.TRAINER).orElse(null))
                            .status(true)
                            .createdBy("Hoang Anh")
                            .createdDate(new Date())
                            .modifiedBy("Hoang Anh")
                            .modifiedDate(new Date())
                            .build());
                    userList.add(User.builder()
                            .email("trainer1@gmail.com")
                            .password(passwordEncoder.encode("1"))
                            .name("Trainer")
                            .phone("0977545452")
                            .dob(new Date())
                            .gender("male")
                            .role(userPermissionDAO.findUserPermissionByRole(Role.TRAINER).orElse(null))
                            .status(true)
                            .createdBy("Hoang Anh")
                            .createdDate(new Date())
                            .modifiedBy("Hoang Anh")
                            .modifiedDate(new Date())
                            .build());
                    userList.add(User.builder()
                            .email("trainer2@gmail.com")
                            .password(passwordEncoder.encode("1"))
                            .name("Trainer")
                            .phone("0977545452")
                            .dob(new Date())
                            .gender("male")
                            .role(userPermissionDAO.findUserPermissionByRole(Role.TRAINER).orElse(null))
                            .status(true)
                            .createdBy("Hoang Anh")
                            .createdDate(new Date())
                            .modifiedBy("Hoang Anh")
                            .modifiedDate(new Date())
                            .build());
                    userList.add(User.builder()
                            .email("user@gmail.com")
                            .password(passwordEncoder.encode("1"))
                            .name("User")
                            .phone("0977545453")
                            .dob(new Date())
                            .gender("male")
                            .role(userPermissionDAO.findUserPermissionByRole(Role.USER).orElse(null))
                            .status(true)
                            .createdBy("Hoang Anh")
                            .createdDate(new Date())
                            .modifiedBy("Hoang Anh")
                            .modifiedDate(new Date())
                            .build());
                    for (int i = 0; i < 5; i++) {
                        userList.add(User.builder()
                                .email("review" + i + "@gmail.com")
                                .password(passwordEncoder.encode("1"))
                                .name("el rex " + i)
                                .phone("0977545453")
                                .dob(new Date())
                                .gender("male")
                                .role(userPermissionDAO.findUserPermissionByRole(Role.CLASS_ADMIN).orElse(null))
                                .status(true)
                                .createdBy("Xi JinPing")
                                .createdDate(new Date())
                                .modifiedBy("Hoang Anh")
                                .modifiedDate(new Date())
                                .build());
                    }

                    for (int i = 0; i < 5; i++) {
                        userList.add(User.builder()
                                .email("approve" + i + "@gmail.com")
                                .password(passwordEncoder.encode("1"))
                                .name("el mencho " + i)
                                .phone("0977545453")
                                .dob(new Date())
                                .gender("male")
                                .role(userPermissionDAO.findUserPermissionByRole(Role.CLASS_ADMIN).orElse(null))
                                .status(true)
                                .createdBy("Xi JinPing")
                                .createdDate(new Date())
                                .modifiedBy("Hoang Anh")
                                .modifiedDate(new Date())
                                .build());
                    }

                    userList.add(User.builder()
                            .email("fhcmcontacter1@gmail.com")
                            .password(passwordEncoder.encode("1"))
                            .name("User")
                            .phone("0977545453")
                            .dob(new Date())
                            .gender("male")
                            .role(userPermissionDAO.findUserPermissionByRole(Role.USER).orElse(null))
                            .status(true)
                            .createdBy("Hoang Anh")
                            .createdDate(new Date())
                            .modifiedBy("Hoang Anh")
                            .modifiedDate(new Date())
//                            .fsu(fsuDAO.findById("FHCM").get())
                            .build());
                    userList.add(User.builder()
                            .email("fhcmcontacter2@gmail.com")
                            .password(passwordEncoder.encode("1"))
                            .name("User")
                            .phone("0977545453")
                            .dob(new Date())
                            .gender("male")
                            .role(userPermissionDAO.findUserPermissionByRole(Role.USER).orElse(null))
                            .status(true)
                            .createdBy("Hoang Anh")
                            .createdDate(new Date())
                            .modifiedBy("Hoang Anh")
                            .modifiedDate(new Date())
//                            .fsu(fsuDAO.findById("FHCM").get())
                            .build());
                    userList.add(User.builder()
                            .email("fhncontacter1@gmail.com")
                            .password(passwordEncoder.encode("1"))
                            .name("User")
                            .phone("0977545453")
                            .dob(new Date())
                            .gender("male")
                            .role(userPermissionDAO.findUserPermissionByRole(Role.USER).orElse(null))
                            .status(true)
                            .createdBy("Hoang Anh")
                            .createdDate(new Date())
                            .modifiedBy("Hoang Anh")
                            .modifiedDate(new Date())
//                            .fsu(fsuDAO.findById("FHN").get())
                            .build());
                    userList.add(User.builder()
                            .email("fhncontacter2@gmail.com")
                            .password(passwordEncoder.encode("1"))
                            .name("User")
                            .phone("0977545453")
                            .dob(new Date())
                            .gender("male")
                            .role(userPermissionDAO.findUserPermissionByRole(Role.USER).orElse(null))
                            .status(true)
                            .createdBy("Hoang Anh")
                            .createdDate(new Date())
                            .modifiedBy("Hoang Anh")
                            .modifiedDate(new Date())
                            .build());

                    for (int i = 0; i < 5; i++) {
                        userList.add(User.builder()
                                .email("trainee" + i + "@gmail.com")
                                .password(passwordEncoder.encode("1"))
                                .name("Trainee" + i)
                                .phone("0977545453")
                                .dob(new Date())
                                .gender("male")
                                .role(userPermissionDAO.findUserPermissionByRole(Role.USER).orElse(null))
                                .status(true)
                                .createdBy("Hoang Anh")
                                .createdDate(new Date())
                                .modifiedBy("Hoang Anh")
                                .modifiedDate(new Date())
                                .build());
                    }
                    userDAO.saveAll(userList);
                    System.out.println("SUPER_ADMIN Token: " + authenticationService.login(
                            LoginRequest.builder()
                                    .email(userList.get(0).getEmail())
                                    .password("1")
                                    .build()).getToken());
                    System.out.println("CLASS_ADMIN Token: " + authenticationService.login(
                            LoginRequest.builder()
                                    .email(userList.get(1).getEmail())
                                    .password("1")
                                    .build()).getToken());
                    System.out.println("TRAINER Token: " + authenticationService.login(
                            LoginRequest.builder()
                                    .email(userList.get(2).getEmail())
                                    .password("1")
                                    .build()).getToken());
                    System.out.println("USER Token: " + authenticationService.login(
                            LoginRequest.builder()
                                    .email(userList.get(3).getEmail())
                                    .password("1")
                                    .build()).getToken());
                }
                if (syllabusDAO.findAll().isEmpty()) {
                    List<Syllabus> syllabusList = new ArrayList<>();

                    for (int i = 1; i <= 4; i++) {
                        Syllabus s = Syllabus.builder()
                                .topicCode("TOPIC00" + i)
                                .topicName("lmao " + i)
                                .version("1.6")
                                .publishStatus(i % 2 == 0 ? "active" : "inactive")
                                .technicalGroup("mao ze dong picture at home.")
                                .priority("low")
                                .duration(4)
                                .courseObjective("glory to ccp.")
                                .trainingAudience(10)
                                .createdBy(userDAO.findByEmail("admin@gmail.com").get())
                                .createdDate(new Date())
                                .modifiedDate(new Date())
                                .build();
                        syllabusList.add(s);
                    }
                    syllabusDAO.saveAll(syllabusList);

                }

                if (trainingProgramDAO.findAll().isEmpty()) {
                    List<TrainingProgram> trainingPrograms = new ArrayList<>();

                    TrainingProgram trainingProgram = TrainingProgram.builder()
                            .name("tp_1")
                            .userID(userDAO.findById(1).get())
                            .startDate(new Date("2001/9/11"))
                            .duration(999)
                            .status("active")
                            .createdBy("admin@gmail.com")
                            .createdDate(new Date())
                            .build();
                    TrainingProgram trainingProgram1 = TrainingProgram.builder()
                            .name("tp_2")
                            .userID(userDAO.findById(2).get())
                            .startDate(new Date("2001/9/11"))
                            .duration(999)
                            .status("inactive")
                            .createdBy("dio")
                            .createdDate(new Date())
                            .build();
                    TrainingProgram trainingProgram2 = TrainingProgram.builder()
                            .name("tp_3")
                            .userID(userDAO.findById(1).get())
                            .startDate(new Date("2001/9/11"))
                            .duration(999)
                            .status("drafting")
                            .createdBy("jojo")
                            .createdDate(new Date())
                            .build();

                    trainingPrograms.add(trainingProgram);
                    trainingPrograms.add(trainingProgram1);
                    trainingPrograms.add(trainingProgram2);

                    trainingProgramDAO.saveAll(trainingPrograms);
                }
                if (trainingProgramSyllabusDAO.findAll().isEmpty()) {
                    List<TrainingProgramSyllabus> trainingProgramSyllabusList = new ArrayList<>();

                    TrainingProgramSyllabus trainingProgramSyllabus = TrainingProgramSyllabus.builder()
                            .id(SyllabusTrainingProgramCompositeKey.builder()
                                    .trainingProgramCode(1)
                                    .topicCode("TOPIC001")
                                    .build())
                            .trainingProgramCode(trainingProgramDAO.findById(1).get())
                            .topicCode(syllabusDAO.findById("TOPIC001").get())
                            .build();
                    TrainingProgramSyllabus trainingProgramSyllabus1 = TrainingProgramSyllabus.builder()
                            .id(SyllabusTrainingProgramCompositeKey.builder()
                                    .trainingProgramCode(1)
                                    .topicCode("TOPIC003")
                                    .build())
                            .trainingProgramCode(trainingProgramDAO.findById(1).get())
                            .topicCode(syllabusDAO.findById("TOPIC003").get())
                            .build();
                    TrainingProgramSyllabus trainingProgramSyllabus2 = TrainingProgramSyllabus.builder()
                            .id(SyllabusTrainingProgramCompositeKey.builder()
                                    .trainingProgramCode(2)
                                    .topicCode("TOPIC002")
                                    .build())
                            .trainingProgramCode(trainingProgramDAO.findById(2).get())
                            .topicCode(syllabusDAO.findById("TOPIC002").get())
                            .build();
                    TrainingProgramSyllabus trainingProgramSyllabus3 = TrainingProgramSyllabus.builder()
                            .id(SyllabusTrainingProgramCompositeKey.builder()
                                    .trainingProgramCode(2)
                                    .topicCode("TOPIC004")
                                    .build())
                            .trainingProgramCode(trainingProgramDAO.findById(2).get())
                            .topicCode(syllabusDAO.findById("TOPIC004").get())
                            .build();

                    trainingProgramSyllabusList.add(trainingProgramSyllabus);
                    trainingProgramSyllabusList.add(trainingProgramSyllabus1);
                    trainingProgramSyllabusList.add(trainingProgramSyllabus2);
                    trainingProgramSyllabusList.add(trainingProgramSyllabus3);

                    trainingProgramSyllabusDAO.saveAll(trainingProgramSyllabusList);
                }

            }
        };
    }

    @GetMapping("")
    public String greeting() {
        return "Hello from FAMS Application made from GROUP 1 WITH LOVE";
    }
}
