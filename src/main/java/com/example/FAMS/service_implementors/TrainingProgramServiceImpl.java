package com.example.FAMS.service_implementors;

import com.example.FAMS.dto.responses.*;
import com.example.FAMS.enums.Role;
import com.example.FAMS.models.Class;
import com.example.FAMS.models.TrainingProgram;
import com.example.FAMS.models.TrainingProgramSyllabus;
import com.example.FAMS.models.User;
import com.example.FAMS.models.composite_key.SyllabusTrainingProgramCompositeKey;
import com.example.FAMS.repositories.SyllabusDAO;
import com.example.FAMS.repositories.TrainingProgramDAO;
import com.example.FAMS.repositories.TrainingProgramSyllabusDAO;
import com.example.FAMS.repositories.UserDAO;
import com.example.FAMS.services.JWTService;
import com.example.FAMS.services.TrainingProgramService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Log4j2
@RequiredArgsConstructor
public class TrainingProgramServiceImpl implements TrainingProgramService {
    private final JWTService jwtService;
    private final SyllabusDAO syllabusDAO;
    private final TrainingProgramSyllabusDAO trainingProgramSyllabusDAO;
    private final DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
    private static final String[] HEADER = {"name", "startDate", "duration", "userID", "status", "trainingProgramCode"};
    private final Logger logger = LoggerFactory.getLogger(TrainingProgramServiceImpl.class);
    private final TrainingProgramDAO trainingProgramDAO;
    private final UserDAO userDAO;

    @Override
    public ResponseEntity<ResponseObject> createTrainingProgram(
            TrainingProgramDTO trainingProgramDTO) {
        TrainingProgram trainingProgram = new TrainingProgram();
        Date date = new Date();
        String token =
                ((ServletRequestAttributes)
                        Objects.requireNonNull(RequestContextHolder.getRequestAttributes()))
                        .getRequest()
                        .getHeader("Authorization")
                        .substring(7);
        String userEmail = jwtService.extractUserEmail(token);
        var requester = userDAO.findUserByEmail(userEmail).orElse(null);
        var person = userDAO.findByEmail(trainingProgramDTO.getTrainerGmail()).orElse(null);
        if (trainingProgramDAO
                .getTrainingProgramByName(trainingProgramDTO.getTrainingProgramName())
                .orElse(null)
                == null) {
            trainingProgram.setName(trainingProgramDTO.getTrainingProgramName());
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseObject("Failed", "The Training Program name already been used", null));
        }
        if (trainingProgramDTO.getDuration() > 0) {
            trainingProgram.setDuration(trainingProgramDTO.getDuration());
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseObject("Failed", "The duration cannot be negative", null));
        }
        if (person != null && person.getRole().getRole() == Role.TRAINER) {
            trainingProgram.setUserID(person);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseObject("Failed", "The person is not a trainer or not found", null));
        }
        if (requester != null) {
            trainingProgram.setStatus("active");
            trainingProgram.setStartDate(date);
            trainingProgram.setCreatedBy(requester.getName());
            trainingProgram.setCreatedDate(date);
            trainingProgram.setModifiedBy(requester.getName());
            trainingProgram.setModifiedDate(date);
            var result = trainingProgramDAO.save(trainingProgram);

            createTrainingSyllabus(result, trainingProgramDTO.getTopicCode()); // problem
            return ResponseEntity.ok(new ResponseObject("Successful", "Added training program", result));
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseObject("Failed", "Cannot found the user", null));
        }
    }

    public void createTrainingSyllabus(TrainingProgram trainingProgram, String[] syllabus) {
        try {
            for (String element : syllabus) {
                var Item = syllabusDAO.findById(element).orElse(null);
                if (Item != null) {
                    TrainingProgramSyllabus trainingProgramSyllabus =
                            TrainingProgramSyllabus.builder()
                                    .id(
                                            SyllabusTrainingProgramCompositeKey.builder()
                                                    .topicCode(Item.getTopicCode())
                                                    .trainingProgramCode(trainingProgram.getTrainingProgramCode())
                                                    .build())
                                    .topicCode(Item)
                                    .trainingProgramCode(trainingProgram)
                                    .deleted(false)
                                    .build();
                    trainingProgramSyllabusDAO.save(trainingProgramSyllabus);
                }
            }
        } catch (Exception e) {
            logger.info("Error occur in create TrainingSyllabus func " + e);
        }
    }

    @Override
    public ResponseEntity<ResponseObject> getAllActive() {
        List<TrainingProgram> userList;
        List<TrainingProgramDTOV2> responseList = new ArrayList<>();
        try {
            userList = trainingProgramDAO.findTrainingProgramsByStatus("active");
            for (TrainingProgram element : userList) {
                TrainingProgramDTOV2 trainingProgramDTOV2 =
                        TrainingProgramDTOV2.builder()
                                .trainingProgramSyllabus(element.getTrainingProgramSyllabus())
                                .classes(
                                        element.getClasses().stream()
                                                .map(
                                                        classEntity ->
                                                                Class.builder()
                                                                        .classId(classEntity.getClassId())
                                                                        .approve(classEntity.getApprove())
                                                                        .attendeeActual(classEntity.getAttendeeActual())
                                                                        .attendeePlanned(classEntity.getAttendeePlanned())
                                                                        .attendee(classEntity.getAttendee())
                                                                        .attendeeAccepted(classEntity.getAttendeeAccepted())
                                                                        .fsu(classEntity.getFsu())
                                                                        .endDate(classEntity.getEndDate())
                                                                        .className(classEntity.getClassName())
                                                                        .location(classEntity.getLocation())
                                                                        .review(classEntity.getReview())
                                                                        .timeTo(classEntity.getTimeTo())
                                                                        .timeFrom(classEntity.getTimeFrom())
                                                                        .modifiedDate(classEntity.getModifiedDate())
                                                                        .startDate(classEntity.getStartDate())
                                                                        .status(classEntity.getStatus())
                                                                        .modifiedBy(classEntity.getModifiedBy())
                                                                        .duration(classEntity.getDuration())
                                                                        .createdBy(classEntity.getCreatedBy())
                                                                        .createdDate(classEntity.getCreatedDate())
                                                                        .deactivated(classEntity.isDeactivated())
                                                                        .build())
                                                .collect(Collectors.toSet()))
                                .userID(element.getUserID())
                                .trainingProgramCode(element.getTrainingProgramCode())
                                .name(element.getName())
                                .createdDate(element.getCreatedDate())
                                .createdBy(element.getCreatedBy())
                                .status(element.getStatus())
                                .startDate(element.getStartDate())
                                .modifiedBy(element.getModifiedBy())
                                .modifiedDate(element.getModifiedDate())
                                .duration(element.getDuration())
                                .build();
                responseList.add(trainingProgramDTOV2);
            }
            return ResponseEntity.ok(new ResponseObject("Successful", "Found user", responseList));
        } catch (Exception e) {
            userList = Collections.emptyList();
            return ResponseEntity.ok(new ResponseObject("Failed", "Not found user", userList));
        }
    }

    @Override
    public ResponseEntity<ResponseObject> getAll() {
        List<TrainingProgram> userList;
        List<TrainingProgramDTOV2> responseList = new ArrayList<>();
        try {
            userList = trainingProgramDAO.findTrainingProgramsBy();
            for (TrainingProgram element : userList) {
                TrainingProgramDTOV2 trainingProgramDTOV2 =
                        TrainingProgramDTOV2.builder()
                                .trainingProgramSyllabus(element.getTrainingProgramSyllabus())
                                .classes(
                                        element.getClasses().stream()
                                                .map(
                                                        classEntity ->
                                                                Class.builder()
                                                                        .classId(classEntity.getClassId())
                                                                        .approve(classEntity.getApprove())
                                                                        .attendeeActual(classEntity.getAttendeeActual())
                                                                        .attendeePlanned(classEntity.getAttendeePlanned())
                                                                        .attendee(classEntity.getAttendee())
                                                                        .attendeeAccepted(classEntity.getAttendeeAccepted())
                                                                        .fsu(classEntity.getFsu())
                                                                        .endDate(classEntity.getEndDate())
                                                                        .className(classEntity.getClassName())
                                                                        .location(classEntity.getLocation())
                                                                        .review(classEntity.getReview())
                                                                        .timeTo(classEntity.getTimeTo())
                                                                        .timeFrom(classEntity.getTimeFrom())
                                                                        .modifiedDate(classEntity.getModifiedDate())
                                                                        .startDate(classEntity.getStartDate())
                                                                        .status(classEntity.getStatus())
                                                                        .modifiedBy(classEntity.getModifiedBy())
                                                                        .duration(classEntity.getDuration())
                                                                        .createdBy(classEntity.getCreatedBy())
                                                                        .createdDate(classEntity.getCreatedDate())
                                                                        .deactivated(classEntity.isDeactivated())
                                                                        .build())
                                                .collect(Collectors.toSet()))
                                .userID(element.getUserID())
                                .trainingProgramCode(element.getTrainingProgramCode())
                                .name(element.getName())
                                .createdDate(element.getCreatedDate())
                                .createdBy(element.getCreatedBy())
                                .status(element.getStatus())
                                .startDate(element.getStartDate())
                                .modifiedBy(element.getModifiedBy())
                                .modifiedDate(element.getModifiedDate())
                                .duration(element.getDuration())
                                .build();
                responseList.add(trainingProgramDTOV2);
            }
            return ResponseEntity.ok(new ResponseObject("Successful", "Found user", responseList));
        } catch (Exception e) {
            userList = Collections.emptyList();
            return ResponseEntity.ok(new ResponseObject("Failed", "Not found user", userList));
        }
    }

    @Override
    public ResponseEntity<ResponseTrainingProgram> updateTrainingProgram(
            int trainingProgramCode, String choice, TrainingProgramDTO2 trainingProgramDTO) {
        TrainingProgram trainingProgramExisted =
                trainingProgramDAO.findById(trainingProgramCode).orElse(null);


        if (trainingProgramExisted == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseTrainingProgram("Failed", "Training program not found", 0, null));
        }

        if (!trainingProgramDTO.getTrainingProgramName().equals(trainingProgramExisted.getName())) {
            if (trainingProgramDAO
                    .getTrainingProgramByName(trainingProgramDTO.getTrainingProgramName())
                    .isPresent()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(
                                new ResponseTrainingProgram(
                                        "Failed", "The Training Program name is already in use", 0, null));
            }
        }

        if (trainingProgramDTO.getDuration() <= 0) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(
                            new ResponseTrainingProgram(
                                    "Failed", "The duration cannot be negative", 0, null));
        }
        if (!"Active".equalsIgnoreCase(trainingProgramExisted.getStatus())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseTrainingProgram("Failed", "The status must be Active", 0, null));
        }
        User trainer =
                userDAO
                        .findByEmail(trainingProgramDTO.getTrainerGmail())
                        .filter(user -> user.getRole().getRole() == Role.TRAINER)
                        .orElse(null);
        if (trainer == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(
                            new ResponseTrainingProgram(
                                    "Failed", "The person is not a trainer or not found", 0, null));
        }

        String token =
                ((ServletRequestAttributes)
                        Objects.requireNonNull(RequestContextHolder.getRequestAttributes()))
                        .getRequest()
                        .getHeader("Authorization")
                        .substring(7);
        String userEmail = jwtService.extractUserEmail(token);
        var requester = userDAO.findUserByEmail(userEmail).orElse(null);

        if (requester == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseTrainingProgram("Failed", "Cannot find the user", 0, null));
        }
        trainingProgramExisted.setName(trainingProgramDTO.getTrainingProgramName());
        trainingProgramExisted.setDuration(trainingProgramDTO.getDuration());
        trainingProgramExisted.setUserID(trainer);
        trainingProgramExisted.setStartDate(new Date());
        trainingProgramExisted.setModifiedBy(requester.getName());
        trainingProgramExisted.setModifiedDate(new Date());

        TrainingProgram updatedProgram = trainingProgramDAO.save(trainingProgramExisted);
        if (choice.equalsIgnoreCase("Save")) {
            updateTrainingSyllabus(trainingProgramExisted, trainingProgramDTO.getTopicCode());
        } else if (choice.equalsIgnoreCase("Cancel")) {
            deleteTrainingSyllabus(trainingProgramExisted, trainingProgramDTO.getTopicCode());

        }


        return ResponseEntity.ok(
                new ResponseTrainingProgram(
                        "Successful",
                        "Updated training program",
                        0,
                        updatedProgram));
    }


    private TrainingProgramSyllabus updateTrainingSyllabus(TrainingProgram trainingProgram, String[] topicCodes) {
        TrainingProgramSyllabus resultEntry = null;
        for (String code : topicCodes) {
            var syllabus = syllabusDAO.findById(code).orElse(null);

            if (syllabus != null) {
                var existingEntry = trainingProgramSyllabusDAO.findByIdTopicCodeAndIdTrainingProgramCode(
                        syllabus.getTopicCode(), trainingProgram.getTrainingProgramCode()
                );
                if (existingEntry != null) {
                    existingEntry.setDeleted(false);
                    resultEntry = trainingProgramSyllabusDAO.save(existingEntry);
                } else {
                    TrainingProgramSyllabus newEntry = TrainingProgramSyllabus.builder()
                            .id(SyllabusTrainingProgramCompositeKey.builder()
                                    .topicCode(syllabus.getTopicCode())
                                    .trainingProgramCode(trainingProgram.getTrainingProgramCode())
                                    .build())
                            .topicCode(syllabus)
                            .trainingProgramCode(trainingProgram)
                            .deleted(false)
                            .build();
                    resultEntry = trainingProgramSyllabusDAO.save(newEntry);
                }
            }
        }
        return resultEntry;
    }

    private void deleteTrainingSyllabus(TrainingProgram trainingProgram, String[] topicCodes) {
        for (String code : topicCodes) {
            var syllabus = syllabusDAO.findById(code).orElse(null);
            if (syllabus != null) {
                var syllabusEntry = trainingProgramSyllabusDAO.findByIdTopicCodeAndIdTrainingProgramCode(
                        syllabus.getTopicCode(), trainingProgram.getTrainingProgramCode()
                );
                if (syllabusEntry != null) {
                    syllabusEntry.setDeleted(true);
                    trainingProgramSyllabusDAO.delete(syllabusEntry);

                }
            }
        }
    }


    public TrainingProgram duplicateTrainingProgramName(TrainingProgram originalTraining) {
//        TrainingProgram originalTraining = trainingProgramDAO.findTrainingProgramByNameAndAndTrainingProgramCode().orElse(null);
        int version = trainingProgramDAO.countByNameLike(originalTraining.getName() + "%");
        System.out.println(version);
        String originalName = originalTraining.getName();
        int newId = createNewId(trainingProgramDAO);
        String trainingProgramName = originalName + "_" + version;
        TrainingProgram newTrainingProgram = TrainingProgram.builder()
                .name(trainingProgramName)
                .duration(originalTraining.getDuration())
                .userID(originalTraining.getUserID())
                .startDate(originalTraining.getStartDate())
                .createdBy(originalTraining.getCreatedBy())
                .createdDate(originalTraining.getCreatedDate())
                .modifiedDate(originalTraining.getModifiedDate())
                .modifiedBy(originalTraining.getModifiedBy())
                .status(originalTraining.getStatus())
                .trainingProgramCode(newId)
                .build();
        return trainingProgramDAO.save(newTrainingProgram);
    }

    @Override
    public ResponseEntity<ResponseObject> processDataFromCSV(
            MultipartFile file, String choice, String separator, String scan, Authentication authentication) throws Exception {
        //already check not null file in controller
        if (!isCSVFile(file)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ResponseObject.builder()
                            .status("Import fail")
                            .message("File is not csv")
                            .payload(null)
                            .build());
        }
        int count = 0, skip = 0;
        BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()));
        SimpleDateFormat[] dateFormats = {
                new SimpleDateFormat("dd/MM/yyyy"),
                new SimpleDateFormat("dd-MM-yyyy")
        };
        String headerLine = reader.readLine();
        if (headerLine == null || !isCSVHeaderValid(headerLine, separator)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ResponseObject.builder()
                            .status("Fail")
                            .message("CSV file header does not match expected headers")
                            .payload(null)
                            .build());
        }
        reader = new BufferedReader(new InputStreamReader(file.getInputStream()));
        String line;
        boolean hasData = false;
        boolean firstLine = true;
        while ((line = reader.readLine()) != null) {
            if (firstLine) {
                firstLine = false;
                continue;
            }
            String[] data = line.split("[" + separator + "]");
            if (data.length == 6) {
                hasData = true;
                try {
                    if (scan.equalsIgnoreCase("Name")) {
                        String trainingProgramName = data[0];
                        Integer importedId = Integer.parseInt(data[5]);
                        var duplicateUsingID = trainingProgramDAO.findById(importedId).orElse(null);
                        var duplicateName = trainingProgramDAO.findTrainingProgramByNameAndTrainingProgramCode(trainingProgramName, importedId).orElse(null);
                        if (choice.equalsIgnoreCase("Replace")) {
                            if (duplicateUsingID != null) {
                                log.info(count);
                                duplicateUsingID.setName(data[0]);
                                duplicateUsingID.setStartDate(parseDate(data[1]));
                                duplicateUsingID.setDuration(Integer.parseInt(data[2]));
                                duplicateUsingID.setUserID(userDAO.findById(Integer.parseInt(data[3])).get());
                                duplicateUsingID.setStatus(data[4].equalsIgnoreCase("1") ? "active" : "inactive");
                                duplicateUsingID.setCreatedBy(userDAO.findById(Integer.parseInt(data[3])).get().getEmail());
                                duplicateUsingID.setCreatedDate(parseDate(formatter.format(new Date())));
                                duplicateUsingID.setModifiedBy(null);
                                duplicateUsingID.setModifiedDate(null);
                                trainingProgramDAO.save(duplicateUsingID);
                                count++;
                            }
                        } else if (choice.equalsIgnoreCase("Skip")) {
                            if (duplicateName == null) {
                                trainingProgramDAO.save(createTrainingProgramFromCSVData(data, dateFormats, authentication));
                                count++;
                            } else skip++;
                        } else if (choice.equalsIgnoreCase("Allow")) {
                            if (duplicateName != null) {
                                duplicateTrainingProgramName(duplicateName);
                                count++;
                            }
                        }
                    } else if (scan.equalsIgnoreCase("ID")) {
                        Integer code = Integer.parseInt(data[5]);
                        var duplicateCode = trainingProgramDAO.findById(code).orElse(null);
                        if (choice.equalsIgnoreCase("Replace")) {
                            if (duplicateCode != null) {
                                log.info(count);
                                duplicateCode.setName(data[0]);
                                duplicateCode.setStartDate(parseDate(data[1]));
                                duplicateCode.setDuration(Integer.parseInt(data[2]));
                                duplicateCode.setUserID(userDAO.findById(Integer.parseInt(data[3])).get());
                                duplicateCode.setStatus(data[4].equalsIgnoreCase("1") ? "active" : "inactive");
                                duplicateCode.setCreatedBy(userDAO.findById(Integer.parseInt(data[3])).get().getEmail());
                                duplicateCode.setCreatedDate(parseDate(formatter.format(new Date())));
                                duplicateCode.setModifiedBy(null);
                                duplicateCode.setModifiedDate(null);
                                trainingProgramDAO.save(duplicateCode);
                                count++;
                            }
                        } else if (choice.equalsIgnoreCase("Skip")) {
                            if (duplicateCode == null) {
                                trainingProgramDAO.save(createTrainingProgramFromCSVData(data, dateFormats, authentication));
                                count++;
                            } else skip++;
                        } else if (choice.equalsIgnoreCase("Allow")) {
                            if (duplicateCode != null) {
                                TrainingProgram newProgram = TrainingProgram.builder()
                                        .name(data[0])
                                        .startDate(parseDate(data[1]))
                                        .duration(Integer.parseInt(data[2]))
                                        .userID(getCreator(authentication))
                                        .status(data[4].equalsIgnoreCase("1") ? "active" : "inactive")
                                        .createdBy(getCreator(authentication).getName())
                                        .createdDate(parseDate(formatter.format(new Date())))
                                        .modifiedBy(null)
                                        .modifiedDate(null)
                                        .build();
                                trainingProgramDAO.save(newProgram);
                                count++;
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body(ResponseObject.builder()
                                    .status("Fail")
                                    .message("Column data is missing or invalid")
                                    .payload(null)
                                    .build());
                }

            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ResponseObject.builder()
                                .status("Fail")
                                .message("CSV file's data is missing")
                                .payload(null)
                                .build());
            }
        }
        if (!hasData) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ResponseObject.builder()
                            .status("Fail")
                            .message("CSV file has no data")
                            .payload(null)
                            .build());
        }
        if (count > 0 || skip > 0) {
            return ResponseEntity.status(HttpStatus.OK)
                    .body(
                            ResponseObject.builder()
                                    .status("Success")
                                    .message(skip > 0 ? skip + " row(s) be skipped" : "Import successfully")
                                    .payload(null)
                                    .build());
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(
                        ResponseObject.builder().
                                status("Fail").
                                message("Import failed").
                                payload(null).
                                build());
    }

    private Date parseDate(String dateString) throws ParseException {
        return formatter.parse(dateString);
    }


    private boolean isCSVHeaderValid(String headerLine, String separator) {
        String[] headers = headerLine.split("[" + separator + "]");

        if (headers.length != HEADER.length) {
            return false;
        }
        for (int i = 0; i < headers.length; i++) {
            if (!headers[i].equalsIgnoreCase(HEADER[i].trim())) {
                return false;
            }
        }
        return true;
    }

    private int createNewId(TrainingProgramDAO trainingProgramDAO) {
        Integer maxId = trainingProgramDAO.findMaxTrainingProgramCode();
        if (maxId == null) {
            return 1;
        } else { // Ngược lại, tạo một ID mới không trùng lặp
            return maxId + 1;
        }
    }

    private java.sql.Date parseDate(String dateString, SimpleDateFormat[] dateFormats)
            throws ParseException {
        for (SimpleDateFormat dateFormat : dateFormats) {
            try {
                Date parsedDate = dateFormat.parse(dateString);
                return new java.sql.Date(parsedDate.getTime());
            } catch (ParseException e) {
                log.info(dateFormat.toPattern() + " not equal " + dateString);
            }
        }
        throw new ParseException("Không thể chuyển đổi ngày", 0);
    }

    private boolean isCSVFile(MultipartFile file) {
        String filename = file.getOriginalFilename();
        return filename != null && filename.endsWith(".csv");
    }

    private TrainingProgram createTrainingProgramFromCSVData(
            String[] data, SimpleDateFormat[] dateFormats, Authentication authentication)
            throws ParseException {
        int importedId = Integer.parseInt(data[6]);
        TrainingProgram existingTrainingProgram = trainingProgramDAO.findById(importedId).orElse(null);

        if (existingTrainingProgram != null) {
            existingTrainingProgram.setName(data[0]);
            existingTrainingProgram.setUserID(getCreator(authentication));
            existingTrainingProgram.setStartDate(parseDate(data[1], dateFormats));
            existingTrainingProgram.setDuration(Integer.parseInt(data[2]));
            existingTrainingProgram.setStatus(data[3].equalsIgnoreCase("1") ? "active" : "inactive");
            existingTrainingProgram.setCreatedBy(getCreator(authentication).getName());
            existingTrainingProgram.setCreatedDate(parseDate(data[4], dateFormats));
            existingTrainingProgram.setModifiedBy(getCreator(authentication).getName());
            existingTrainingProgram.setModifiedDate(parseDate(data[5], dateFormats));
            existingTrainingProgram.setTrainingProgramCode(importedId);
            return existingTrainingProgram; // Return the updated existing record
        } else {
            // Create a new TrainingProgram if the ID does not exist in the database
            return TrainingProgram.builder()
                    .name(data[0])
                    .userID(getCreator(authentication))
                    .startDate(parseDate(data[1], dateFormats))
                    .duration(Integer.parseInt(data[2]))
                    .status(data[3].equalsIgnoreCase("1") ? "active" : "inactive")
                    .createdBy(getCreator(authentication).getName())
                    .createdDate(parseDate(data[4], dateFormats))
                    .modifiedBy(getCreator(authentication).getName())
                    .modifiedDate(parseDate(data[5], dateFormats))
                    .trainingProgramCode(importedId)
                    .build();
        }
    }

    @Override
    public TrainingProgram searchTrainingProgram(String keyword) {
        List<TrainingProgram> trainingProgramList = trainingProgramDAO.findAll();
        TrainingProgram trainingProgramByName = getNameIfExisted(keyword, trainingProgramList);
        TrainingProgram trainingProgramByCode = getCodeIfExisted(keyword, trainingProgramList);
        if (trainingProgramByName == null) {
            return trainingProgramByCode;
        }
        return trainingProgramByName;
    }

    @Override
    public ResponseEntity<ResponseObjectVersion2> getTrainingProgramByCode(int code) {
        try {
            Integer totalSubjectDays = trainingProgramDAO.totalSubjectDays(code);
            Integer totalTrainingProgramDates = trainingProgramDAO.totalTrainingProgramDates(code);

            List<TrainingProgramDetails> object = trainingProgramDAO.getTrainingProgramDetails(code);
            return ResponseEntity.ok(
                    new ResponseObjectVersion2(
                            "Successful", "Found user", totalSubjectDays, totalTrainingProgramDates, object));
        } catch (Exception e) {
            var object = Collections.emptyList();
            return ResponseEntity.ok(
                    new ResponseObjectVersion2("Failed", "Not found user", 0, 0, object));
        }
    }

    private TrainingProgram getNameIfExisted(String name, List<TrainingProgram> trainingProgramList) {
        for (TrainingProgram trainingProgram : trainingProgramList) {
            if (trainingProgram.getName().equalsIgnoreCase(name)
                    && trainingProgram.getStatus().equalsIgnoreCase("active")) return trainingProgram;
        }
        return null;
    }

    private TrainingProgram getCodeIfExisted(String code, List<TrainingProgram> trainingProgramList) {
        for (TrainingProgram trainingProgram : trainingProgramList) {
            if (Integer.toString(trainingProgram.getTrainingProgramCode()).equalsIgnoreCase(code)
                    && trainingProgram.getStatus().equalsIgnoreCase("active")) return trainingProgram;
        }
        return null;
    }

    @Override
    public ResponseEntity<ResponseObject> changeTrainingProgramStatus(
            int trainingProgramCode, String value) {
        if (checkExisted(trainingProgramCode, value)) {
            switch (value) {
                case "Activate":
                    activateProgram(trainingProgramCode);
                    break;
                case "De-activate":
                    deactivateProgram(trainingProgramCode);
                    break;
            }
            return ResponseEntity.status(HttpStatus.OK)
                    .body(
                            new ResponseObject(
                                    value + " training program successfully",
                                    "Training program with code "
                                            + trainingProgramCode
                                            + " is now "
                                            + value.toLowerCase(),
                                    trainingProgramDAO.findById(trainingProgramCode).orElse(null)));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(
                        new ResponseObject(
                                value + " training program failed",
                                "Training program with code "
                                        + trainingProgramCode
                                        + " is not found or already be "
                                        + value.toLowerCase()
                                        + "d",
                                "No data"));
    }

    private void activateProgram(int trainingProgramCode) {
        TrainingProgram trainingProgram = trainingProgramDAO.findById(trainingProgramCode).orElse(null);
        if (trainingProgram != null) {
            trainingProgram.setStatus("active");
            trainingProgramDAO.save(trainingProgram);
        }
    }

    private void deactivateProgram(int trainingProgramCode) {
        TrainingProgram trainingProgram = trainingProgramDAO.findById(trainingProgramCode).orElse(null);
        if (trainingProgram != null) {
            trainingProgram.setStatus("inactive");
            trainingProgramDAO.save(trainingProgram);
        }
    }

    private boolean checkExisted(int trainingProgramCode, String value) {
        TrainingProgram trainingProgram = trainingProgramDAO.findById(trainingProgramCode).orElse(null);
        String result = value.equalsIgnoreCase("Activate") ? "active" : "inactive";
        return trainingProgram != null && !trainingProgram.getStatus().equalsIgnoreCase(result);
    }

    public User getCreator(Authentication authentication) {
        Object principal = authentication.getPrincipal();
        if (principal instanceof User) {
            return (User) principal;
        }
        return null;
    }
}
