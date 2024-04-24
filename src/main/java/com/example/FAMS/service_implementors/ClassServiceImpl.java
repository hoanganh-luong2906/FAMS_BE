package com.example.FAMS.service_implementors;

import com.example.FAMS.dto.requests.Calendar.UpdateCalendarRequest;
import com.example.FAMS.dto.requests.ClassRequest.CreateClassDTO;
import com.example.FAMS.dto.requests.ClassRequest.UpdateClass3Request;
import com.example.FAMS.dto.requests.ClassRequest.UpdateClassDTO;
import com.example.FAMS.dto.responses.*;
import com.example.FAMS.dto.responses.Class.*;
import com.example.FAMS.models.Class;
import com.example.FAMS.models.*;
import com.example.FAMS.models.composite_key.ClassUserCompositeKey;
import com.example.FAMS.models.composite_key.SyllabusTrainingProgramCompositeKey;
import com.example.FAMS.repositories.*;
import com.example.FAMS.services.ClassService;
import com.google.common.base.Strings;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@Log4j2
public class ClassServiceImpl implements ClassService {

    @Autowired
    ClassDAO classDAO;

    @Autowired
    UserDAO userDAO;

    @Autowired
    TrainingProgramDAO trainingProgramDAO;

    @Autowired
    SyllabusDAO syllabusDAO;

    @Autowired
    TrainingProgramSyllabusDAO trainingProgramSyllabusDAO;

//    @Autowired
//    FsuDAO fsuDAO;

    @Autowired
    ClassLearningDayDAO classLearningDayDAO;

    @Autowired
    ClassUserDAO classUserDAO;

//    @Autowired
//    LocationDAO locationDAO;

//    @Autowired
//    ClassLocationDAO classLocationDAO;

    @Autowired
    UserClassSyllabusDAO userClassSyllabusDAO;
    List<SearchFilterResponse> filterResponses;

    List<CalendarDayResponse> dayCalendars;
    List<CalendarWeekResponse> weekCalendars;

    @Override
    public List<ClassDetailResponse> getClasses() {
        List<Class> classes = classDAO.findTop1000ByOrderByCreatedDateDesc();
//        List<GetClassesResponse> res = new ArrayList<>();
        List<ClassDetailResponse> res = new ArrayList<>();
        List<String> location = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");

//        log.info(classLearningDayDAO.findByClassId_ClassIdAndLocationId_LocationId("lmao", 2L));

        for (int i = 0; i < classes.size(); i++) {
//            for (int j = 0; j < classes.get(i).getLocations().stream().toList().size(); j++) {
//                location.add(classes.get(i).getLocations().stream().toList().get(j).getLocation());
//            }
            if (!classes.get(i).isDeactivated()) {
//                GetClassesResponse c = GetClassesResponse.builder()
//                        .classCode(classes.get(i).getClassId().split("_")[0])
//                        .className(classes.get(i).getClassName())
//                        .fsu(classes.get(i).getFsu())
//                        .location(capitalizeLocation(classes.get(i).getLocation()))
//                        .createdOn(sdf.format(new Date(classes.get(i).getCreatedDate().getTime())))
//                        .createdBy(classes.get(i).getCreatedBy())
//                        .duration(classes.get(i).getDuration())
//                        .status(classes.get(i).getStatus())
//                        .isDeactivated(Boolean.toString(classes.get(i).isDeactivated()))
//                        .build();

                ClassDetailResponse detail = getFullClassDetail(classes.get(i).getClassId());
//                res.add(c);
                res.add(detail);
            }
        }
        return res;
    }

    @Override
    public GetClassesByPage paging(int amount, int pageNumber) {
        try {
            List<Class> classList = classDAO.findTop1000ByOrderByCreatedDateDesc();
            List<ClassDetailResponse> classDetailList = new ArrayList<>();
            int totalNumberOfPages = classList.size() == amount ? 1 : classList.size() % amount == 0 ? classList.size() / amount : (classList.size() / amount) + 1;
            log.info(classList.size() / amount);
            if (pageNumber < 0 || pageNumber > totalNumberOfPages) {
                return GetClassesByPage.builder()
                        .message("found 0 result.")
                        .totalNumberOfPages(totalNumberOfPages)
                        .status(1)
                        .pageNumber(pageNumber)
                        .classList(classDetailList)
                        .build();
            }

            if (amount > classList.size()) {
                for (int i = 0; i < classList.size(); i++) {
                    if (!classList.get(i).isDeactivated()) {
                        ClassDetailResponse detail = getFullClassDetail(classList.get(i).getClassId());
                        classDetailList.add(detail);
                    }
                }

                return GetClassesByPage.builder()
                        .message("found " + classDetailList.size() + " result.")
                        .totalNumberOfPages(1)
                        .status(0)
                        .pageNumber(1)
                        .classList(classDetailList)
                        .build();

            }
            int maxContent = pageNumber * amount;
            int pageTo = Math.min(maxContent, classList.size());
            int pageFrom = pageNumber * amount > classList.size() ? maxContent - amount : pageTo - amount;
            log.info(maxContent);
            log.info("class size: " + classList.size());
            log.info(pageNumber * amount > classList.size());
            log.info(pageTo - ((pageNumber * amount) - classList.size()));
            log.info("page from: " + pageFrom);
            log.info("page to: " + pageTo);
            List<Class> classSubList = classList.subList(pageFrom, pageTo);

            for (int i = 0; i < classSubList.size(); i++) {
                if (!classSubList.get(i).isDeactivated()) {
                    ClassDetailResponse detail = getFullClassDetail(classSubList.get(i).getClassId());
                    classDetailList.add(detail);
                }
            }
            return GetClassesByPage.builder()
                    .message("found " + classDetailList.size() + " result.")
                    .totalNumberOfPages(totalNumberOfPages)
                    .status(0)
                    .pageNumber(pageNumber)
                    .classList(classDetailList)
                    .build();

        } catch (Exception err) {
            err.printStackTrace();
            return GetClassesByPage.builder()
                    .message("found 0 result.")
                    .totalNumberOfPages(0)
                    .status(-1)
                    .pageNumber(pageNumber)
                    .classList(null)
                    .build();
        }
    }

    @Override
    public ResponseEntity<ResponseObject> getFilter() {
        try {
            filterResponses = classDAO.searchByFilter();
            return ResponseEntity.ok(new ResponseObject("Successful", "List of classroom", filterResponses));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseObject("Failed", "Couldn't found the list", e.getMessage()));
        }
    }

    @Override
    public CreateClassResponse createClass(CreateClassDTO request, Authentication authentication) {
        try {
            boolean existingClass = classDAO.findById(request.getClassCode()).isPresent();
            if (existingClass) {
                return CreateClassResponse.builder()
                        .message("class with id: " + request.getClassCode() + " already exist.")
                        .createdClass(null)
                        .status(1)
                        .build();
            }
            log.info(request);
            Class classInfo = null;
            List<ClassUser> classUserList = new ArrayList<>();
            List<UserClassSyllabus> userSyllabusList = new ArrayList<>();
            List<ClassLearningDay> classLearningDayList = new ArrayList<>();
//            List<ClassLocation> classLocationList = new ArrayList<>();
//            Fsu fsu = fsuDAO.findById(request.getFsu().toUpperCase()).get();
//            Location l = null;
            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
            Date today = new Date();
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
            LocalDate startDate = LocalDate.parse(request.getStartDate(), dateFormatter);
            LocalDate endDate = LocalDate.parse(request.getEndDate(), dateFormatter);
            User user = null;
//            String review = userDAO.findByEmail(request.getReview()).get().getName();
//            String approve = userDAO.findByEmail(request.getApprove()).get().getName();
            String timeFromStr = request.getClassTimeFrom().split(":").length == 3 ? request.getClassTimeFrom() : request.getClassTimeFrom() + ":00";
            String timeToStr = request.getClassTimeTo().split(":").length == 3 ? request.getClassTimeTo() : request.getClassTimeTo() + ":00";

            if (!isValidDate(startDate, request.getStartDate())) {
                return CreateClassResponse.builder()
                        .message("invalid start date.")
                        .createdClass(null)
                        .status(2)
                        .build();
            }

            if (!isValidDate(endDate, request.getEndDate())) {
                return CreateClassResponse.builder()
                        .message("invalid end date.")
                        .createdClass(null)
                        .status(2)
                        .build();
            }

            if (!sdf.parse(request.getStartDate()).after(today)) {
                return CreateClassResponse.builder()
                        .message("start date for this class should be after today")
                        .createdClass(null)
                        .status(2)
                        .build();
            }

            if (!sdf.parse(request.getEndDate()).after(sdf.parse(request.getStartDate()))) {
                return CreateClassResponse.builder()
                        .message("end date for this class should be after start date")
                        .createdClass(null)
                        .status(2)
                        .build();
            }

            if (!isNumber(request.getAttendeePlanned()) || !isNumber(request.getAttendeeAccepted()) || !isNumber(request.getAttendeeActual())) {
                return CreateClassResponse.builder()
                        .message("attendee planned, accepted, actual can't be a words or negative or floating point number")
                        .createdClass(null)
                        .status(2)
                        .build();
            }

            classInfo = Class.builder()
                    .className(request.getNameClass())
                    .classId(request.getClassCode())
                    .duration(request.getTotalTimeLearning())
                    .startDate(sdf.parse(request.getStartDate()))
                    .endDate(sdf.parse(request.getEndDate()))
                    .createdBy(request.getCreated())
                    .review(request.getReview())
                    .approve(request.getApprove())
                    .attendeeActual(Integer.parseInt(request.getAttendeeActual()))
                    .attendee(request.getAttendee())
                    .attendeePlanned(Integer.parseInt(request.getAttendeePlanned()))
                    .attendeeAccepted(Integer.parseInt(request.getAttendeeAccepted()))
                    .timeFrom(Time.valueOf(timeFromStr))
                    .timeTo(Time.valueOf(timeToStr))
                    .fsu(request.getFsu())
                    .location(capitalizeLocation(request.getLocation()))
                    .status(request.getStatus())
                    .trainingProgram(trainingProgramDAO.findById(Integer.parseInt(request.getTrainingProgram())).get())
                    .createdDate(new java.util.Date())
                    .build();

            classDAO.save(classInfo);

//                Location location = locationDAO.findById(Long.parseLong(request.getLocation().get(i))).get();
//                ClassLocation cl = ClassLocation.builder()
//                        .id(ClassLocationCompositeKey.builder()
//                                .classCode(classInfo.getClassId())
//                                .locationId(location.getLocationId())
//                                .build())
//                        .locationCode(location)
//                        .classCode(classInfo)
//                        .build();

//                l = Location.builder()
//                        .location(request.getLocation())
//                        .classId(classInfo)
//                        .build();

//                classLocationList.add(cl);
//            l = locationDAO.save(l);

            for (int j = 0; j < request.getListDay().size(); j++) {
                Date date = sdf.parse(request.getListDay().get(j));
                LocalDate enrollDate = LocalDate.parse(request.getListDay().get(j), dateFormatter);
                if (!date.after(today) || !isValidDate(enrollDate, request.getListDay().get(j))) {
                    return CreateClassResponse.builder()
                            .message("one of class study date is invalid or should be after today")
                            .createdClass(null)
                            .status(2)
                            .build();
                }
                String[] getDate = request.getListDay().get(j).split("/");
                ClassLearningDay learningDay = ClassLearningDay.builder()
                        .classId(classInfo)
                        .date(Integer.parseInt(getDate[1]))
                        .month(Integer.parseInt(getDate[0]))
                        .year(Integer.parseInt(getDate[2]))
                        .enrollDate(date)
//                            .locationId(l)
                        .timeFrom(classInfo.getTimeFrom())
                        .timeTo(classInfo.getTimeTo())
                        .build();

                classLearningDayList.add(learningDay);
            }


            for (int i = 0; i < request.getAttendeeList().size(); i++) {
                user = userDAO.findByEmail(request.getAttendeeList().get(i)).orElse(null);
                if (user == null || !user.getRole().getRole().name().equalsIgnoreCase("USER")) {
                    return CreateClassResponse.builder()
                            .message("one of the class attendee of this class may not be a class attendee or not exist, please fix it or else.")
                            .createdClass(null)
                            .status(2)
                            .build();
                }
                ClassUser classUser = ClassUser.builder()
                        .id(ClassUserCompositeKey.builder()
                                .userId(user.getUserId())
                                .classId(classInfo.getClassId())
                                .build())
                        .userID(user)
                        .classId(classInfo)
                        .userType(user.getRole().getRole().name())
                        .build();

                classUserList.add(classUser);
            }
            for (int i = 0; i < request.getAdmin().size(); i++) {
                user = userDAO.findByEmail(request.getAdmin().get(i)).orElse(null);
                if (user == null || !user.getRole().getRole().name().equalsIgnoreCase("CLASS_ADMIN")) {
                    return CreateClassResponse.builder()
                            .message("one of the class admin of this class may not be a class admin or not exist, please fix it or else.")
                            .createdClass(null)
                            .status(2)
                            .build();
                }
                ClassUser classAdmin = ClassUser.builder()
                        .id(ClassUserCompositeKey.builder()
                                .userId(user.getUserId())
                                .classId(classInfo.getClassId())
                                .build())
                        .userID(user)
                        .classId(classInfo)
                        .userType(user.getRole().getRole().name())
//                        .location(l.getLocation())
                        .build();

                classUserList.add(classAdmin);
            }

            for (int i = 0; i < request.getTrainer().size(); i++) {
                user = userDAO.findByEmail(request.getTrainer().get(i).getGmail()).orElse(null);
                if (user == null || !user.getRole().getRole().name().equalsIgnoreCase("TRAINER")) {
                    return CreateClassResponse.builder()
                            .message("one of the trainer of this class may not be a trainer or not exist, please fix it or else.")
                            .createdClass(null)
                            .status(2)
                            .build();
                }
                for (int j = 0; j < request.getTrainer().get(i).getClassCode().size(); j++) {
                    Syllabus s = syllabusDAO.findById(request.getTrainer().get(i).getClassCode().get(j)).get();

                    UserClassSyllabus userClassSyllabus = UserClassSyllabus.builder()
//                            .id(UserClassSyllabusCompositeKey.builder()
//                                    .classCode(classInfo.getClassId())
//                                    .userId(user.getUserId())
//                                    .topicCode(s.getTopicCode())
//                                    .build())
                            .classCode(classInfo)
                            .topicCode(s)
                            .userId(user)
//                            .location(l.getLocation())
                            .userType(user.getRole().getRole().name())
                            .build();

                    userSyllabusList.add(userClassSyllabus);
                }
            }


            classLearningDayDAO.saveAll(classLearningDayList);
            classUserDAO.saveAll(classUserList);
            userClassSyllabusDAO.saveAll(userSyllabusList);
//            classLocationDAO.saveAll(classLocationList);

            String timeFrom = classInfo.getTimeFrom().toString();
            timeFrom = timeFrom.substring(0, timeFrom.lastIndexOf(":"));
            String timeTo = classInfo.getTimeTo().toString();
            timeTo = timeTo.substring(0, timeTo.lastIndexOf(":"));

            CreateClassDTO createClassDTO = CreateClassDTO.builder()
                    .classCode(classInfo.getClassId())
                    .nameClass(classInfo.getClassName())
                    .totalTimeLearning(classInfo.getDuration())
                    .classTimeTo(timeTo)
                    .classTimeFrom(timeFrom)
                    .location(classInfo.getLocation())
                    .status(classInfo.getStatus())
                    .fsu(classInfo.getFsu())
                    .startDate(request.getStartDate())
                    .endDate(request.getEndDate())
                    .created(request.getCreated())
                    .review(request.getReview())
                    .approve(request.getApprove())
                    .attendee(request.getAttendee())
                    .attendeePlanned(request.getAttendeePlanned())
                    .attendeeAccepted(request.getAttendeeAccepted())
                    .attendeeActual(request.getAttendeeActual())
                    .trainingProgram(request.getTrainingProgram())
                    .attendeeList(request.getAttendeeList())
                    .listDay(request.getListDay())
                    .trainer(request.getTrainer())
                    .admin(request.getAdmin())

                    .build();

            return CreateClassResponse.builder()
                    .message("create class successfully.")
                    .createdClass(createClassDTO)
                    .status(0)
                    .build();

        } catch (Exception err) {
            err.printStackTrace();
            return CreateClassResponse.builder()
                    .message("fail to create class.")
                    .createdClass(null)
                    .status(-1)
                    .build();
        }

    }

    @Override
    public ResponseEntity<DeactivateClassResponse> deactivateClass(String classCode) {
        Class c = classDAO.findById(classCode).isPresent() ? classDAO.findById(classCode).get() : null;
        if (c != null) {
            c.setDeactivated(true);
            classDAO.save(c);
            return ResponseEntity.status(200).body(new DeactivateClassResponse("successfully deactivate class with id " + classCode + " (⌐■_■)👍"));
        }
        return ResponseEntity.status(400).body(new DeactivateClassResponse("fail to deactivate class with id " + classCode + " (⌐■⌒■)👎"));
    }

    @Override
    public ResponseEntity<ClassDetailResponse> getClassDetail(String classCode) throws InterruptedException {
        Class c = classDAO.findById(classCode).isPresent() ? classDAO.findById(classCode).get() : null;
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
        Map<Integer, List<String>> trainerSyllabusMap = new HashMap<>();
        if (c != null) {
            try {
                log.info(c.getTrainingProgram().getTrainingProgramSyllabus().size());
//                TrainingProgram tp = trainingProgramDAO.findById(c.getTrainingProgram().getTrainingProgramCode()).get();
//
////                List<String> locationList = new ArrayList<>();
////                List<Location> classLocations = c.getLocations().stream().toList();
//
//                List<String> listDay = new ArrayList<>();
//                List<ClassLearningDay> classLearningDays = c.getClassLearningDays().stream().toList();
//
//                List<TrainerDTO> trainerList = new ArrayList<>();
//                List<UserClassSyllabus> userClassSyllabuses = c.getUserClassSyllabus().stream().toList();
//
//                List<UserDTO> attendeeList = new ArrayList<>();
//                List<UserDTO> adminList = new ArrayList<>();
//                List<ClassUser> classUsers = c.getClassUsers().stream().toList();
//
//                List<SyllabusDTO> syllabusList = new ArrayList<>();
//                List<TrainingProgramSyllabus> trainingProgramSyllabuses = tp.getTrainingProgramSyllabus().stream().toList();
//
//                List<User> trainers = new ArrayList<>();
//
////                for (int i = 0; i < classLocations.size(); i++) {
////                    locationList.add(classLocations.get(i).getLocation());
////                }
//                for (int i = 0; i < classLearningDays.size(); i++) {
//                    listDay.add(convertToMMDDYYYY(classLearningDays.get(i).getEnrollDate().toString().split(" ")[0]));
//                }
//                for (int i = 0; i < userClassSyllabuses.size(); i++) {
//                    List<String> syllabusCodeList;
//                    if (trainerSyllabusMap.containsKey(userClassSyllabuses.get(i).getUserId().getUserId())) {
//                        syllabusCodeList = trainerSyllabusMap.get(userClassSyllabuses.get(i).getUserId().getUserId());
//                    } else {
//                        syllabusCodeList = new ArrayList<>();
//                        trainers.add(userClassSyllabuses.get(i).getUserId());
//                    }
//                    syllabusCodeList.add(userClassSyllabuses.get(i).getTopicCode().getTopicCode());
//                    trainerSyllabusMap.put(userClassSyllabuses.get(i).getUserId().getUserId(), syllabusCodeList);
//                }
//                for (int i = 0; i < trainers.size(); i++) {
//                    TrainerDTO trainer = TrainerDTO.builder()
//                            .userId(trainers.get(i).getUserId())
//                            .userName(trainers.get(i).getName())
//                            .userEmail(trainers.get(i).getEmail())
//                            .syllabusList(trainerSyllabusMap.get(trainers.get(i).getUserId()))
//                            .build();
//                    trainerList.add(trainer);
//                }
//                log.info(trainerSyllabusMap);
//                for (int i = 0; i < classUsers.size(); i++) {
//                    if (classUsers.get(i).getUserType().equalsIgnoreCase("user")) {
//                        UserDTO trainee = UserDTO.builder()
//                                .userId(classUsers.get(i).getUserID().getUserId())
//                                .userName(classUsers.get(i).getUserID().getName())
//                                .userEmail(classUsers.get(i).getUserID().getEmail())
//                                .build();
//                        attendeeList.add(trainee);
//                    } else {
//                        UserDTO admin = UserDTO.builder()
//                                .userId(classUsers.get(i).getUserID().getUserId())
//                                .userName(classUsers.get(i).getUserID().getName())
//                                .userEmail(classUsers.get(i).getUserID().getEmail())
//                                .build();
//                        adminList.add(admin);
//                    }
//                }
//                for (int i = 0; i < trainingProgramSyllabuses.size(); i++) {
//                    SyllabusDTO syllabus = SyllabusDTO.builder()
//                            .numberOfDay(trainingProgramSyllabuses.get(i).getTopicCode().getNumberOfDay())
//                            .version(trainingProgramSyllabuses.get(i).getTopicCode().getVersion())
//                            .createdDate(convertToMMDDYYYY(trainingProgramSyllabuses.get(i).getTopicCode().getCreatedDate().toString().split(" ")[0]))
//                            .createdBy(trainingProgramSyllabuses.get(i).getTopicCode().getCreatedBy().getName())
//                            .topicName(trainingProgramSyllabuses.get(i).getTopicCode().getTopicName())
//                            .publishStatus(trainingProgramSyllabuses.get(i).getTopicCode().getPublishStatus())
//                            .topicCode(trainingProgramSyllabuses.get(i).getTopicCode().getTopicCode())
//                            .build();
//
//                    syllabusList.add(syllabus);
//                }
//
//                log.info(c.getTimeFrom().toString());
//                log.info(c.getTimeTo().toString());
//
//                String timeFrom = c.getTimeFrom().toString();
//                timeFrom = timeFrom.substring(0, timeFrom.lastIndexOf(":"));
//                String timeTo = c.getTimeTo().toString();
//                timeTo = timeTo.substring(0, timeTo.lastIndexOf(":"));
//
//
//                ClassDetailResponse res = ClassDetailResponse.builder()
//                        .classCode(classCode.split("_")[0])
//                        .nameClass(c.getClassName())
//                        .created(c.getCreatedBy())
//                        .createdDate(convertToMMDDYYYY(c.getCreatedDate().toString().split(" ")[0]))
//                        .deactivated(c.isDeactivated())
//                        .totalTimeLearning(c.getDuration())
//                        .endDate(convertToMMDDYYYY(c.getEndDate().toString().split(" ")[0]))
//                        .modifiedBy(c.getModifiedBy())
//                        .modifiedDate(c.getModifiedDate() != null ? convertToMMDDYYYY(c.getModifiedDate().toString().split(" ")[0]) : "")
//                        .startDate(convertToMMDDYYYY(c.getStartDate().toString().split(" ")[0]))
//                        .status(c.getStatus())
//                        .classTimeFrom(timeFrom)
//                        .classTimeTo(timeTo)
//                        .approve(c.getApprove())
//                        .review(c.getReview())
//                        .fsu(c.getFsu())
//                        .attendee(c.getAttendee())
//                        .attendeeAccepted(Integer.toString(c.getAttendeeAccepted()))
//                        .attendeePlanned(Integer.toString(c.getAttendeePlanned()))
//                        .attendeeActual(Integer.toString(c.getAttendeeActual()))
//                        .trainingProgram(TrainingProgramDTO.builder()
//                                .trainingProgramCode(c.getTrainingProgram().getTrainingProgramCode())
//                                .trainingProgramName(c.getTrainingProgram().getName())
//                                .modifyBy(c.getTrainingProgram().getModifiedBy())
//                                .modifyDate(c.getModifiedDate() != null ? convertToMMDDYYYY(c.getTrainingProgram().getModifiedDate().toString().split(" ")[0]) : "")
//                                .duration(tp.getDuration())
//                                .status(c.getTrainingProgram().getStatus())
//                                .build())
//                        .listDay(listDay)
////                        .location(locationList)
//                        .location(capitalizeLocation(c.getLocation()))
//                        .trainer(trainerList)
//                        .attendeeList(attendeeList)
//                        .syllabusList(syllabusList)
//                        .admin(adminList)
//                        .message("found class with id " + classCode)
//                        .build();
                ClassDetailResponse res = getFullClassDetail(classCode);
                return ResponseEntity.status(200).body(res);
            } catch (Exception err) {
                err.printStackTrace();
                return ResponseEntity.status(500).body(null);
            }

        }
        return ResponseEntity.status(400).body(new ClassDetailResponse("class with id " + classCode + " not found"));
    }

    @Override
    public UpdateClassResponse updateClass(UpdateClassDTO request, String classCode) {
        Class existingClass = classDAO.findById(classCode).get();
        try {
            if (existingClass != null) {
//                if(!existingClass.getClassId().equalsIgnoreCase(request.getClassCode())){
//                    classCode = request.getClassCode();
//                    existingClass.setClassId(classCode);
//                    if (classDAO.findById(request.getClassCode()).isPresent()) {
//                        return UpdateClassResponse.builder()
//                                .status(1)
//                                .updatedClass(null)
//                                .message("class with id: " + request.getClassCode() + " already exist in this location.")
//                                .build();
//                    }

//                }
                User user = null;
                SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
                String timeFromStr = request.getClassTimeFrom().split(":").length == 3 ? request.getClassTimeFrom() : request.getClassTimeFrom() + ":00";
                String timeToStr = request.getClassTimeTo().split(":").length == 3 ? request.getClassTimeTo() : request.getClassTimeTo() + ":00";
                Date today = new Date();
                DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
                LocalDate startDate = LocalDate.parse(request.getStartDate(), dateFormatter);
                LocalDate endDate = LocalDate.parse(request.getEndDate(), dateFormatter);
                List<ClassUser> classUserList = new ArrayList<>();
                List<UserClassSyllabus> userSyllabusList = new ArrayList<>();
                List<ClassLearningDay> classLearningDayList = new ArrayList<>();

                if (!isValidDate(startDate, request.getStartDate())) {
                    return UpdateClassResponse.builder()
                            .message("invalid start date.")
                            .updatedClass(null)
                            .status(2)
                            .build();
                }

                if (!isValidDate(endDate, request.getEndDate())) {
                    return UpdateClassResponse.builder()
                            .message("invalid end date.")
                            .updatedClass(null)
                            .status(2)
                            .build();
                }

                if (sdf.parse(request.getStartDate()).before(existingClass.getStartDate())) {
                    return UpdateClassResponse.builder()
                            .message("start date for this class should be after today")
                            .updatedClass(null)
                            .status(2)
                            .build();
                }

                if (!sdf.parse(request.getEndDate()).after(sdf.parse(request.getStartDate()))) {
                    return UpdateClassResponse.builder()
                            .message("end date for this class should be after start date")
                            .updatedClass(null)
                            .status(2)
                            .build();
                }

                if (!isNumber(request.getAttendeePlanned()) || !isNumber(request.getAttendeeAccepted()) || !isNumber(request.getAttendeeActual())) {
                    return UpdateClassResponse.builder()
                            .message("attendee planned, accepted, actual can't be a words or negative or floating point number")
                            .updatedClass(null)
                            .status(2)
                            .build();
                }

                existingClass.setClassName(request.getNameClass());
                existingClass.setDuration(request.getTotalTimeLearning());
                existingClass.setStartDate(sdf.parse(request.getStartDate()));
                existingClass.setEndDate(sdf.parse(request.getEndDate()));
                existingClass.setAttendeeActual(Integer.parseInt(request.getAttendeeActual()));
                existingClass.setAttendee(request.getAttendee());
                existingClass.setAttendeePlanned(Integer.parseInt(request.getAttendeePlanned()));
                existingClass.setAttendeeAccepted(Integer.parseInt(request.getAttendeeAccepted()));
                existingClass.setTimeFrom(Time.valueOf(timeFromStr));
                existingClass.setTimeTo(Time.valueOf(timeToStr));
                existingClass.setFsu(request.getFsu());
                existingClass.setLocation(request.getLocation());
                existingClass.setStatus(request.getStatus());
                existingClass.setModifiedDate(new Date());

//                if (!existingClass.getTrainingProgram().getName().equalsIgnoreCase(request.getTrainingProgram())) {
//                    existingClass.setTrainingProgram(trainingProgramDAO.findByName(request.getTrainingProgram()));
//                    log.info("change training program");
//                    log.info(ucs.size());
//                } else {
//                    log.info("not changing training program");
//                    List<UserClassSyllabus> ucs = userClassSyllabusDAO.findByClassCode_ClassId(request.getClassCode());
//                    log.info(ucs.size());
//                    userClassSyllabusDAO.deleteAll(ucs);
//
//                }

                Class updatedClass = classDAO.save(existingClass);

                log.info("1");
                List<ClassLearningDay> cldl = classLearningDayDAO.findByClassId_ClassId(classCode);
                List<ClassUser> cu = classUserDAO.findByClassId_ClassId(classCode);
                List<UserClassSyllabus> ucs = userClassSyllabusDAO.findByClassCode_ClassId(classCode);
//                classLearningDayDAO.deleteAllInBatch(cldl);
                classLearningDayDAO.deleteAll(cldl);
//                classLearningDayDAO.flush();
//                classUserDAO.deleteAllInBatch(cu);
                classUserDAO.deleteAll(cu);
//                classUserDAO.flush();
                userClassSyllabusDAO.deleteAllInBatch(ucs);
                userClassSyllabusDAO.deleteAll(ucs);
                userClassSyllabusDAO.flush();
                log.info("2");

                for (int i = 0; i < request.getListDay().size(); i++) {
                    Date date = sdf.parse(request.getListDay().get(i));
                    LocalDate enrollDate = LocalDate.parse(request.getListDay().get(i), dateFormatter);
                    if (!date.after(today) || !isValidDate(enrollDate, request.getListDay().get(i))) {
                        return UpdateClassResponse.builder()
                                .message("one of class study date is invalid or  should be after today")
                                .updatedClass(null)
                                .status(2)
                                .build();
                    }
                    String[] getDate = request.getListDay().get(i).split("/");
                    ClassLearningDay learningDay = ClassLearningDay.builder()
                            .classId(existingClass)
                            .date(Integer.parseInt(getDate[1]))
                            .month(Integer.parseInt(getDate[0]))
                            .year(Integer.parseInt(getDate[2]))
                            .enrollDate(date)
//                            .locationId(l)
                            .timeFrom(existingClass.getTimeFrom())
                            .timeTo(existingClass.getTimeTo())
                            .build();
                    classLearningDayList.add(learningDay);
                }

                for (int i = 0; i < request.getAttendeeList().size(); i++) {
                    user = userDAO.findByEmail(request.getAttendeeList().get(i)).orElse(null);
                    if (user == null || !user.getRole().getRole().name().equalsIgnoreCase("USER")) {
                        return UpdateClassResponse.builder()
                                .message("one of the class attendee of this class may not be a class attendee or not exist, please fix it or else.")
                                .updatedClass(null)
                                .status(2)
                                .build();
                    }
                    ClassUser classUser = ClassUser.builder()
                            .id(ClassUserCompositeKey.builder()
                                    .userId(user.getUserId())
                                    .classId(updatedClass.getClassId())
                                    .build())
                            .userID(user)
                            .classId(updatedClass)
                            .userType(user.getRole().getRole().name())
//                        .location(l.getLocation())
                            .build();

                    classUserList.add(classUser);
                }
                for (int i = 0; i < request.getAdmin().size(); i++) {
                    user = userDAO.findByEmail(request.getAdmin().get(i)).orElse(null);
                    if (user == null || !user.getRole().getRole().name().equalsIgnoreCase("CLASS_ADMIN")) {
                        return UpdateClassResponse.builder()
                                .message("one of the class admin of this class may not be a class admin or not exist, please fix it or else.")
                                .updatedClass(null)
                                .status(2)
                                .build();
                    }

                    ClassUser classAdmin = ClassUser.builder()
                            .id(ClassUserCompositeKey.builder()
                                    .userId(user.getUserId())
                                    .classId(updatedClass.getClassId())
                                    .build())
                            .userID(user)
                            .classId(updatedClass)
                            .userType(user.getRole().getRole().name())
//                        .location(l.getLocation())
                            .build();

                    classUserList.add(classAdmin);
                }

                for (int i = 0; i < request.getTrainer().size(); i++) {
                    user = userDAO.findByEmail(request.getTrainer().get(i).getGmail()).orElse(null);
                    if (user == null || !user.getRole().getRole().name().equalsIgnoreCase("TRAINER")) {
                        return UpdateClassResponse.builder()
                                .message("one of the trainer of this class may not be a trainer or not exist, please fix it or else.")
                                .updatedClass(null)
                                .status(2)
                                .build();
                    }
                    for (int j = 0; j < request.getTrainer().get(i).getClassCode().size(); j++) {
                        Syllabus s = syllabusDAO.findById(request.getTrainer().get(i).getClassCode().get(j)).get();

                        UserClassSyllabus userClassSyllabus = UserClassSyllabus.builder()
//                                .id(UserClassSyllabusCompositeKey.builder()
//                                        .classCode(existingClass.getClassId())
//                                        .userId(user.getUserId())
//                                        .topicCode(s.getTopicCode())
//                                        .build())
                                .classCode(existingClass)
                                .topicCode(s)
                                .userId(user)
//                            .location(l.getLocation())
                                .userType(user.getRole().getRole().name())
                                .build();

                        userSyllabusList.add(userClassSyllabus);
                    }
                }

                User moder = userDAO.findByEmail(request.getModerEmail()).get();
                existingClass.setModifiedBy(moder.getEmail());

                classLearningDayDAO.saveAll(classLearningDayList);
                classUserDAO.saveAll(classUserList);
                userClassSyllabusDAO.saveAll(userSyllabusList);
                existingClass.setTrainingProgram(trainingProgramDAO.findById(Integer.parseInt(request.getTrainingProgram())).get());
                classDAO.save(existingClass);
                log.info("3");
                String timeFrom = updatedClass.getTimeFrom().toString();
                timeFrom = timeFrom.substring(0, timeFrom.lastIndexOf(":"));
                String timeTo = updatedClass.getTimeTo().toString();
                timeTo = timeTo.substring(0, timeTo.lastIndexOf(":"));

                List<ClassUser> userList = classUserDAO.findByClassId_ClassIdAndUserType(existingClass.getClassId(), "user");
                List<String> u = new ArrayList<>();
                List<ClassUser> adminList = classUserDAO.findByClassId_ClassIdAndUserType(existingClass.getClassId(), "class_admin");
                List<String> a = new ArrayList<>();

                for (int i = 0; i < userList.size(); i++) {
                    u.add(userList.get(i).getUserID().getEmail());
                }

                for (int i = 0; i < adminList.size(); i++) {
                    a.add(adminList.get(i).getUserID().getEmail());
                }

                UpdatedClassDTO uc = UpdatedClassDTO.builder()
                        .classCode(updatedClass.getClassId().split("_")[0])
                        .className(updatedClass.getClassName())
                        .attendee(updatedClass.getAttendee())
                        .classTimeFrom(timeFrom)
                        .classTimeTo(timeTo)
                        .attendeeAccepted(Integer.toString(updatedClass.getAttendeeAccepted()))
                        .attendeeActual(Integer.toString(updatedClass.getAttendeeActual()))
                        .attendeePlanned(Integer.toString(updatedClass.getAttendeePlanned()))
                        .totalLearningTime(updatedClass.getDuration())
                        .listDay(request.getListDay())
                        .attendeeList(u)
                        .adminList(a)
                        .location(updatedClass.getLocation())
                        .status(updatedClass.getStatus())
                        .fsu(updatedClass.getFsu())
                        .startDate(request.getStartDate())
                        .endDate(request.getEndDate())
                        .build();
                return UpdateClassResponse.builder()
                        .status(0)
                        .updatedClass(uc)
                        .message("update class with id: " + existingClass.getClassId() + " successfully")
                        .build();
            }

        } catch (Exception err) {
            err.printStackTrace();
            return UpdateClassResponse.builder()
                    .status(-1)
                    .updatedClass(null)
                    .message("server error")
                    .build();
        }
        return UpdateClassResponse.builder()
                .status(1)
                .updatedClass(null)
                .message("class with id: " + classCode + " doesn't exist")
                .build();
    }


    @Override
    public Class getClassById(String classId) {
        Optional<Class> optionalClass = classDAO.findById(classId);
        return optionalClass.orElse(null);
    }

    @Override
    public List<Class> CalendarSort() {
        return classDAO.getCalendarSort();
    }


    @Override
    public Page<Class> getAllPagenation(Pageable pageable) {

        return classDAO.findAll(pageable);
    }

    @Override
    public ResponseEntity<ResponseObject> getDayCalendar(java.util.Date currentDate) {
        try {
            dayCalendars = classDAO.getCalendarByDay(currentDate);
            return ResponseEntity.ok(new ResponseObject("Successful", "List of classroom", dayCalendars));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseObject("Failed", "Couldn't found the list", e.getMessage()));
        }
    }

    @Override
    public ResponseEntity<ResponseObject> getWeekCalendar(java.util.Date startDate, java.util.Date endDate) {
        try {
            weekCalendars = classDAO.getCalendarByWeek(startDate, endDate);
            return ResponseEntity.ok(new ResponseObject("Successful", "List of classroom", weekCalendars));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseObject("Failed", "Couldn't found the list", e.getMessage()));
        }
    }

    @Override
    public UpdateCalendarResponse updateClassLearningDay(UpdateCalendarRequest request) throws ParseException {
//        String id = request.getId();
        int id = Integer.parseInt(request.getId());
        String classid = request.getClassid();
        String enrollDate = request.getEnrollDate();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date eDate = dateFormat.parse(enrollDate);
        Time timeFrom = request.getTimeFrom();
        Time timeTo = request.getTimeTo();
        String value = request.getValue();

        if (timeFrom.after(timeTo)) {
            return UpdateCalendarResponse.builder()
                    .status("Invalid time range: timeFrom must be before timeTo")
                    .updateClassLearningDay(null)
                    .build();
        } else {
            int startHour = timeFrom.getHours();
            int endHour = timeTo.getHours();

            if ((startHour >= 8 && startHour < 12) && (endHour > 8 && endHour <= 12)) {
            } else if ((startHour >= 13 && startHour < 17) && (endHour > 13 && endHour <= 17)) {
            } else if ((startHour >= 18 && startHour < 22) && (endHour > 18 && endHour <= 22)) {
            } else {
                return UpdateCalendarResponse.builder()
                        .status("Invalid time range: Start time and end time are not in 1 shift")
                        .updateClassLearningDay(null)
                        .build();
            }
        }

        // ClassLearningDay classLearningDay = classLearningDayDAO.findByClassIdAndEnrollDate(classDAO.findById(request.getId()).orElse(null), eDate);
        ClassLearningDay classLearningDay = classLearningDayDAO.findByIdAndAndEnrollDate(id, eDate);

        if (classLearningDay != null) {
            if ("Only".equals(value)) {
                classLearningDay.setTimeFrom(timeFrom);
                classLearningDay.setTimeTo(timeTo);
                classLearningDay = classLearningDayDAO.save(classLearningDay);
            } else if ("All".equals(value)) {
                // List<ClassLearningDay> classLearningDays = classLearningDayDAO.findByClassId_ClassId(id);
                List<ClassLearningDay> classLearningDays = classLearningDayDAO.findByClassId_ClassId(classid);
                for (ClassLearningDay day : classLearningDays) {
                    day.setTimeFrom(timeFrom);
                    day.setTimeTo(timeTo);
                }
                classLearningDayDAO.saveAll(classLearningDays);
            }

            if (classLearningDay != null) {
                return UpdateCalendarResponse.builder()
                        .status("Update Calendar successful")
                        .updateClassLearningDay(classLearningDay)
                        .build();
            } else {
                return UpdateCalendarResponse.builder()
                        .status("Update Calendar failed")
                        .updateClassLearningDay(classLearningDay)
                        .build();
            }
        } else {
            return UpdateCalendarResponse.builder()
                    .status("Calendar not found")
                    .updateClassLearningDay(null)
                    .build();
        }
    }

    @Override
    public UpdateClass3Response updateClass3(UpdateClass3Request updateClass3Request) {

        boolean status = updateClass3Request.isDeleted();
        String topicCode = updateClass3Request.getTopicCode();
        int trainingProgramCode = updateClass3Request.getTrainingProgramCode();

        var syllabus = syllabusDAO.findById(topicCode).orElse(null);
        var trainingProgram = trainingProgramDAO.findById(trainingProgramCode).orElse(null);

        TrainingProgramSyllabus trainingProgramSyllabus = trainingProgramSyllabusDAO.findByIdTopicCodeAndIdTrainingProgramCode(topicCode, trainingProgramCode);
//        TrainingProgramSyllabus topicCode1 = trainingProgramSyllabusDAO.findByIdTopicCode(topicCode);
//        TrainingProgramSyllabus trainingProgram1= trainingProgramSyllabusDAO.findByIdTrainingProgramCode(trainingProgramCode);


        if (trainingProgramSyllabus != null) {

//            trainingProgramSyllabus.setDeleted(status);
            trainingProgramSyllabus = trainingProgramSyllabusDAO.save(trainingProgramSyllabus);

            if (trainingProgramSyllabus != null) {
                return UpdateClass3Response.builder()
                        .status("Update TrainingProgramSyllabus successful")
                        .updatedClass3(trainingProgramSyllabus)
                        .build();
            } else {
                return UpdateClass3Response.builder()
                        .status("Update TrainingProgramSyllabus failed")
                        .updatedClass3(null)
                        .build();
            }

        } else {
            TrainingProgramSyllabus trainingProgramSyllabus2 =
                    TrainingProgramSyllabus.builder()
                            .id(
                                    SyllabusTrainingProgramCompositeKey.builder()
                                            .topicCode(topicCode)
                                            .trainingProgramCode(trainingProgramCode)
                                            .build())
                            .topicCode(syllabus)
                            .trainingProgramCode(trainingProgram)
//                            .deleted(true)
                            .build();
            trainingProgramSyllabus2 = trainingProgramSyllabusDAO.save(trainingProgramSyllabus2);

            if (trainingProgramSyllabus2 != null) {
                return UpdateClass3Response.builder()
                        .status("Update TrainingProgramSyllabus successful")
                        .updatedClass3(trainingProgramSyllabus2)
                        .build();

            } else {
                return UpdateClass3Response.builder()
                        .status("Update TrainingProgramSyllabus failed")
                        .updatedClass3(null)
                        .build();
            }


        }


    }

    @Override
    public ResponseEntity<ResponseObject> deleteAllTrainingProgramSyllabus() {
        try {
            trainingProgramSyllabusDAO.deleteAll(); // Assuming your DAO has a deleteAll method
            return new ResponseEntity<>(new ResponseObject("Success", "All TrainingProgramSyllabus have been deleted successfully.", null), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(new ResponseObject("Error", "Failed to delete TrainingProgramSyllabus.", null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    public List<Class> searchClass(String createdDate, String searchValue, String orderBy) {
        List<Class> classList = classDAO.findAll();

        if (!Strings.isNullOrEmpty(createdDate)) {
            classList =
                    classList.stream()
                            .filter(
                                    c ->
                                            new SimpleDateFormat("yyyy-MM-dd")
                                                    .format(c.getCreatedDate())
                                                    .equals(createdDate))
                            .collect(Collectors.toList());
        }

        if (!Strings.isNullOrEmpty(searchValue)) {
            classList =
                    classList.stream()
                            .filter(
                                    c ->
                                            c.getClassName().toLowerCase().contains(searchValue.trim().toLowerCase())
                                                    || c.getClassId()
                                                    .toLowerCase()
                                                    .contains(searchValue.trim().toLowerCase()))
                            .collect(Collectors.toList());
        }

        if (!Strings.isNullOrEmpty(orderBy)) {
            if (orderBy.equals("asc")) {
                classList.sort(Comparator.comparing(Class::getClassName));
            } else if (orderBy.equals("desc")) {
                classList.sort(Comparator.comparing(Class::getClassName).reversed());
            }
        }

        return classList;
    }


//    public List<SyllabusDTO> getAllSyllabusInTrainingProgram(
//            List<TrainingProgramSyllabus> trainingProgramSyllabusList
//    ) throws InterruptedException {
//        List<SyllabusDTO> syllabusList = new ArrayList<>();
//        int numThreads = calculateNumThreads(trainingProgramSyllabusList.size());
//
//        ExecutorService executorService = Executors.newFixedThreadPool(numThreads);
//
//        int batchSize = (int) Math.ceil((double) trainingProgramSyllabusList.size() / numThreads);
//        int fromIndex = 0;
//
//        for (int i = 0; i < numThreads; i++) {
//            int toIndex = Math.min(fromIndex + batchSize, trainingProgramSyllabusList.size());
//
//            List<TrainingProgramSyllabus> tps = trainingProgramSyllabusList.subList(fromIndex, toIndex);
//
//            executorService.submit(() -> {
//                for (int j = 0; j < tps.size(); j++) {
//                    Syllabus syllabus = tps.get(j).getTopicCode();
//                    SyllabusDTO syllabusDTO = SyllabusDTO.builder()
//                            .topicCode(syllabus.getTopicCode())
//                            .numberOfDay(syllabus.getNumberOfDay())
//                            .version(syllabus.getVersion())
//                            .publishStatus(syllabus.getPublishStatus())
//                            .topicName(syllabus.getTopicName())
//                            .createdBy(syllabus.getCreatedBy().getName())
//                            .createdDate(syllabus.getCreatedDate())
//                            .build();
//                    syllabusList.add(syllabusDTO);
//                }
//            });
//
//            fromIndex = toIndex;
//
//        }
//
//        executorService.shutdown();
//        executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.SECONDS);
//
//        return syllabusList;
//    }

    private int calculateNumThreads(int numRecords) {
        final int maxThreads = 10;
        return Math.min(maxThreads, (int) Math.ceil((double) numRecords / 10));
    }

    public User getCreator(Authentication authentication) {
        Object creator = authentication.getPrincipal();
        if (creator instanceof User) {
            return (User) creator;
        }
        return null;
    }

    public String convertToMMDDYYYY(String dateStr) {
        DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate date = LocalDate.parse(dateStr, inputFormatter);

        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
        String formattedDate = date.format(outputFormatter);
        return formattedDate;
    }

    private String capitalizeLocation(String locationStr) {
        String[] strArr = locationStr.split(" ");
        String result = "";
        for (int i = 0; i < strArr.length; i++) {
            strArr[i] = strArr[i].substring(0, 1).toUpperCase() + strArr[i].substring(1);
            result += strArr[i] + " ";
        }

        return result.trim();
    }

    private boolean isValidDate(LocalDate date, String originalDateString) {
        try {
            // Parse the original string to extract the day of the month
            int originalDay = Integer.parseInt(originalDateString.substring(3, 5));
            log.info(date);
            log.info(originalDateString);
            log.info(originalDay);
            log.info(date.getDayOfMonth() == originalDay);
            // Check if the parsed day of the month matches the original input
            return date.getDayOfMonth() == originalDay;
        } catch (NumberFormatException | DateTimeException e) {
            // Invalid date, e.g., Feb 31 or invalid day format
            return false;
        }
    }

    private ClassDetailResponse getFullClassDetail(String classCode) {
        Class c = classDAO.findById(classCode).isPresent() ? classDAO.findById(classCode).get() : null;
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
        Map<Integer, List<String>> trainerSyllabusMap = new HashMap<>();
        log.info(c.getTrainingProgram().getTrainingProgramSyllabus().size());
        TrainingProgram tp = trainingProgramDAO.findById(c.getTrainingProgram().getTrainingProgramCode()).get();

//                List<String> locationList = new ArrayList<>();
//                List<Location> classLocations = c.getLocations().stream().toList();

        List<String> listDay = new ArrayList<>();
        List<ClassLearningDay> classLearningDays = c.getClassLearningDays().stream().toList();

        List<TrainerDTO> trainerList = new ArrayList<>();
        List<UserClassSyllabus> userClassSyllabuses = c.getUserClassSyllabus().stream().toList();

        List<UserDTO> attendeeList = new ArrayList<>();
        List<UserDTO> adminList = new ArrayList<>();
        List<ClassUser> classUsers = c.getClassUsers().stream().toList();

        List<SyllabusDTO> syllabusList = new ArrayList<>();
        List<TrainingProgramSyllabus> trainingProgramSyllabuses = tp.getTrainingProgramSyllabus().stream().toList();

        List<User> trainers = new ArrayList<>();

//                for (int i = 0; i < classLocations.size(); i++) {
//                    locationList.add(classLocations.get(i).getLocation());
//                }
        for (int i = 0; i < classLearningDays.size(); i++) {
            listDay.add(convertToMMDDYYYY(classLearningDays.get(i).getEnrollDate().toString().split(" ")[0]));
        }
        for (int i = 0; i < userClassSyllabuses.size(); i++) {
            List<String> syllabusCodeList;
            if (trainerSyllabusMap.containsKey(userClassSyllabuses.get(i).getUserId().getUserId())) {
                syllabusCodeList = trainerSyllabusMap.get(userClassSyllabuses.get(i).getUserId().getUserId());
            } else {
                syllabusCodeList = new ArrayList<>();
                trainers.add(userClassSyllabuses.get(i).getUserId());
            }
            syllabusCodeList.add(userClassSyllabuses.get(i).getTopicCode().getTopicCode());
            trainerSyllabusMap.put(userClassSyllabuses.get(i).getUserId().getUserId(), syllabusCodeList);
        }
        for (int i = 0; i < trainers.size(); i++) {
            TrainerDTO trainer = TrainerDTO.builder()
                    .userId(trainers.get(i).getUserId())
                    .userName(trainers.get(i).getName())
                    .userEmail(trainers.get(i).getEmail())
                    .syllabusList(trainerSyllabusMap.get(trainers.get(i).getUserId()))
                    .build();
            trainerList.add(trainer);
        }
        log.info(trainerSyllabusMap);
        for (int i = 0; i < classUsers.size(); i++) {
            if (classUsers.get(i).getUserType().equalsIgnoreCase("user")) {
                UserDTO trainee = UserDTO.builder()
                        .userId(classUsers.get(i).getUserID().getUserId())
                        .userName(classUsers.get(i).getUserID().getName())
                        .userEmail(classUsers.get(i).getUserID().getEmail())
                        .build();
                attendeeList.add(trainee);
            } else {
                UserDTO admin = UserDTO.builder()
                        .userId(classUsers.get(i).getUserID().getUserId())
                        .userName(classUsers.get(i).getUserID().getName())
                        .userEmail(classUsers.get(i).getUserID().getEmail())
                        .build();
                adminList.add(admin);
            }
        }
        for (int i = 0; i < trainingProgramSyllabuses.size(); i++) {
            SyllabusDTO syllabus = SyllabusDTO.builder()
                    .numberOfDay(trainingProgramSyllabuses.get(i).getTopicCode().getDuration())
                    .version(trainingProgramSyllabuses.get(i).getTopicCode().getVersion())
                    .createdDate(convertToMMDDYYYY(trainingProgramSyllabuses.get(i).getTopicCode().getCreatedDate().toString().split(" ")[0]))
                    .createdBy(trainingProgramSyllabuses.get(i).getTopicCode().getCreatedBy().getName())
                    .topicName(trainingProgramSyllabuses.get(i).getTopicCode().getTopicName())
                    .publishStatus(trainingProgramSyllabuses.get(i).getTopicCode().getPublishStatus())
                    .topicCode(trainingProgramSyllabuses.get(i).getTopicCode().getTopicCode())
                    .build();

            syllabusList.add(syllabus);
        }

        log.info(c.getTimeFrom().toString());
        log.info(c.getTimeTo().toString());

        String timeFrom = c.getTimeFrom().toString();
        timeFrom = timeFrom.substring(0, timeFrom.lastIndexOf(":"));
        String timeTo = c.getTimeTo().toString();
        timeTo = timeTo.substring(0, timeTo.lastIndexOf(":"));

        User creator = userDAO.findByEmail(c.getCreatedBy()).get();
        User reviewer = userDAO.findByEmail(c.getReview()).get();
        User approver = userDAO.findByEmail(c.getApprove()).get();
        UserDTO modify = null;
        User moder = userDAO.findByEmail(c.getModifiedBy()).isPresent() ? userDAO.findByEmail(c.getModifiedBy()).get() : null;
        if (moder != null) {
            modify = UserDTO.builder()
                    .userId(moder.getUserId())
                    .userEmail(moder.getEmail())
                    .userName(moder.getName())
                    .build();
        }
        UserDTO created = UserDTO.builder()
                .userEmail(creator.getEmail())
                .userName(creator.getName())
                .userId(creator.getUserId())
                .build();

        UserDTO review = UserDTO.builder()
                .userId(reviewer.getUserId())
                .userEmail(reviewer.getEmail())
                .userName(reviewer.getName())
                .build();

        UserDTO approve = UserDTO.builder()
                .userName(approver.getName())
                .userEmail(approver.getEmail())
                .userId(approver.getUserId())
                .build();


        return ClassDetailResponse.builder()
                .oldClassCode(classCode)
                .classCode(classCode.split("_")[0])
                .nameClass(c.getClassName())
                .created(created)
                .createdDate(convertToMMDDYYYY(c.getCreatedDate().toString().split(" ")[0]))
                .deactivated(c.isDeactivated())
                .totalTimeLearning(c.getDuration())
                .endDate(convertToMMDDYYYY(c.getEndDate().toString().split(" ")[0]))
                .modifiedBy(modify)
                .modifiedDate(c.getModifiedDate() != null ? convertToMMDDYYYY(c.getModifiedDate().toString().split(" ")[0]) : "")
                .startDate(convertToMMDDYYYY(c.getStartDate().toString().split(" ")[0]))
                .status(c.getStatus())
                .classTimeFrom(timeFrom)
                .classTimeTo(timeTo)
                .approve(approve)
                .review(review)
                .fsu(c.getFsu())
                .attendee(c.getAttendee())
                .attendeeAccepted(Integer.toString(c.getAttendeeAccepted()))
                .attendeePlanned(Integer.toString(c.getAttendeePlanned()))
                .attendeeActual(Integer.toString(c.getAttendeeActual()))
//                .trainingProgram(TrainingProgramDTO.builder()
//                        .trainingProgramCode(c.getTrainingProgram().getTrainingProgramCode())
//                        .trainingProgramName(c.getTrainingProgram().getName())
//                        .modifyBy(c.getTrainingProgram().getModifiedBy())
//                        .modifyDate(c.getModifiedDate() != null ? convertToMMDDYYYY(c.getTrainingProgram().getModifiedDate().toString().split(" ")[0]) : "")
//                        .duration(tp.getDuration())
//                        .status(c.getTrainingProgram().getStatus())
//                        .build())
                .trainingProgram(c.getTrainingProgram().getName())
                .listDay(listDay)
//                        .location(locationList)
                .location(capitalizeLocation(c.getLocation()))
                .trainer(trainerList)
                .attendeeList(attendeeList)
                .syllabusList(syllabusList)
                .admin(adminList)
                .message("found class with id " + classCode)
                .build();

    }

    private boolean isNumber(String str) {
        // Use a regular expression to check if the string is a number or a floating-point number
        return Pattern.matches("\\d+", str);
    }

}
