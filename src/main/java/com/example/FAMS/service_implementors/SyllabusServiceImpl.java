package com.example.FAMS.service_implementors;

import com.amazonaws.HttpMethod;
import com.example.FAMS.dto.requests.SyllbusRequest.CreateSyllabusOutlineRequest;
import com.example.FAMS.dto.responses.Class.UserDTO;
import com.example.FAMS.dto.responses.ResponseObject;
import com.example.FAMS.dto.responses.Syllabus.*;
import com.example.FAMS.dto.responses.UpdateSyllabusResponse;
import com.example.FAMS.models.*;
import com.example.FAMS.models.composite_key.SyllabusStandardOutputCompositeKey;
import com.example.FAMS.models.composite_key.SyllabusTrainingUnitCompositeKey;
import com.example.FAMS.models.composite_key.SyllabusTrainingUnitTrainingContentCompositeKey;
import com.example.FAMS.repositories.*;
import com.example.FAMS.services.FileService;
import com.example.FAMS.services.SyllabusService;
import com.google.common.base.Strings;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Nullable;
import java.io.*;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@Log4j2
public class SyllabusServiceImpl implements SyllabusService {

    @Autowired
    SyllabusDAO syllabusDAO;

    @Autowired
    TrainingUnitDAO trainingUnitDAO;

    @Autowired
    UserDAO userDAO;

    @Autowired
    StandardOutputDAO standardOutputDAO;

    @Autowired
    TrainingContentDAO trainingContentDAO;

//    @Autowired
//    LearningObjectiveDAO learningObjectiveDAO;

    @Autowired
    SyllabusObjectiveDAO syllabusObjectiveDAO;

    @Autowired
    UserClassSyllabusDAO userSyllabusDAO;

    @Autowired
    FileService fileService;

    String line = "";

    @Autowired
    TrainingMaterialDAO trainingMaterialDAO;

    @Value("${cloud.aws.credentials.access-key}")
    static String accessKey;

    @Value("${cloud.aws.credentials.secret-key}")
    static String secretKey;

    @Override
    public List<SyllabusResponse> getSyllabuses(@Nullable String type) {
        log.info(isPresignedUrlExpired("https://fams-datas.s3.ap-southeast-1.amazonaws.com/textAyyLmao.txt_TOPIC00111?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Date=20231121T030414Z&X-Amz-SignedHeaders=host&X-Amz-Expires=86400&X-Amz-Credential=AKIAZYNTKMTKQKDQI6PP%2F20231121%2Fap-southeast-1%2Fs3%2Faws4_request&X-Amz-Signature=47058d5c6267bbaf8936849e87c539f0efdbffb1c11f81f4b3d3b07b081cc240"));
//        log.info("convert to utc: " + convertToUTCPlus7("20231116T144445Z"));
//        log.info("millisecond today: " + new Date().getTime());
//        log.info("millisecond in url expiration: " + convertToMilliseconds(convertToUTCPlus7("20231116T144445Z")));
//        log.info("millisecond in url expiration + 86400 * 1000: " + (convertToMilliseconds(convertToUTCPlus7("20231116T144445Z")) + 86400 * 1000));
//        log.info((convertToMilliseconds(convertToUTCPlus7("20231116T144445Z")) + 86400 * 1000) > new Date().getTime());
        List<Syllabus> syllabusList = syllabusDAO.findTop1000ByOrderByCreatedDateDesc();
        List<SyllabusResponse> syllabuses = new ArrayList<>();
        List<String> objectiveList = null;
        List<SyllabusObjective> syllabusObjectiveList = null;

        for (int i = 0; i < syllabusList.size(); i++) {
            objectiveList = new ArrayList<>();
            syllabusObjectiveList = syllabusList.get(i).getSyllabusObjectives().stream().toList();
            for (int j = 0; j < syllabusObjectiveList.size(); j++) {
                objectiveList.add(syllabusObjectiveList.get(j).getOutputCode().getOutputCode());
            }
//            GetAllSyllabusResponse res = GetAllSyllabusResponse.builder()
//                    .syllabusName(syllabusList.get(i).getTopicName())
//                    .syllabusCode(syllabusList.get(i).getTopicCode())
//                    .createdOn(convertToMMDDYYYY(syllabusList.get(i).getCreatedDate().toString().split(" ")[0]))
//                    .createdBy(syllabusList.get(i).getCreatedBy())
//                    .duration(Integer.toString(syllabusList.get(i).getNumberOfDay()))
//                    .status(syllabusList.get(i).getPublishStatus())
//                    .syllabusObjectiveList(objectiveList)
//                    .build();
            SyllabusResponse res = getDetailOfSyllabus(syllabusList.get(i).getTopicCode());

            syllabuses.add(res);
        }

        return syllabuses;
    }

    @Override
    public ResponseEntity<ResponseObject> getAllActiveSyllabus() {
        try {
            var list = syllabusDAO.findAllByPublishStatus("active");
            return ResponseEntity.ok(new ResponseObject("Successful", "Found active syllabus", list));
        } catch (Exception e) {
            var list = Collections.emptyList();
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseObject("Failed", "Not found user", list));
        }
    }

    @Override
    public GetSyllabusByPage paging(int amount, int pageNumber) {
        try {
            List<Syllabus> syllabusList = syllabusDAO.findTop1000ByOrderByCreatedDateDesc();
            System.out.println(syllabusList);
            List<SyllabusResponse> syllabuses = new ArrayList<>();
            List<String> objectiveList = null;
            List<SyllabusObjective> syllabusObjectiveList = null;
            int totalNumberOfPages = syllabusList.size() == amount ? 1 : syllabusList.size() % amount == 0 ? syllabusList.size() / amount : (syllabusList.size() / amount) + 1;
            log.info(syllabusList.size() / amount);
            if (pageNumber < 0 || pageNumber > totalNumberOfPages) {
                return GetSyllabusByPage.builder()
                        .message("found 0 result.")
                        .totalNumberOfPages(totalNumberOfPages)
                        .status(1)
                        .pageNumber(pageNumber)
                        .syllabusList(syllabuses)
                        .build();
            }

            if (amount > syllabusList.size()) {
                for (int i = 0; i < syllabusList.size(); i++) {
                    objectiveList = new ArrayList<>();
                    syllabusObjectiveList = syllabusList.get(i).getSyllabusObjectives().stream().toList();
                    for (int j = 0; j < syllabusObjectiveList.size(); j++) {
                        objectiveList.add(syllabusObjectiveList.get(j).getOutputCode().getOutputCode());
                    }
//                    GetAllSyllabusResponse res = GetAllSyllabusResponse.builder()
//                            .syllabusName(syllabusList.get(i).getTopicName())
//                            .syllabusCode(syllabusList.get(i).getTopicCode())
//                            .createdOn(convertToMMDDYYYY(syllabusList.get(i).getCreatedDate().toString().split(" ")[0]))
//                            .createdBy(syllabusList.get(i).getCreatedBy())
//                            .duration(Integer.toString(syllabusList.get(i).getNumberOfDay()))
//                            .status(syllabusList.get(i).getPublishStatus())
//                            .syllabusObjectiveList(objectiveList)
//                            .build();
                    SyllabusResponse res = getDetailOfSyllabus(syllabusList.get(i).getTopicCode());

                    syllabuses.add(res);
                }

                return GetSyllabusByPage.builder()
                        .message("found " + syllabuses.size() + " result.")
                        .totalNumberOfPages(1)
                        .status(0)
                        .pageNumber(1)
                        .syllabusList(syllabuses)
                        .build();

            }
            int maxContent = pageNumber * amount;
            int pageTo = Math.min(maxContent, syllabusList.size());
            int pageFrom = pageNumber * amount > syllabusList.size() ? maxContent - amount : pageTo - amount;
            log.info(maxContent);
            log.info("syllabus size: " + syllabusList.size());
            log.info(pageNumber * amount > syllabusList.size());
            log.info(pageTo - ((pageNumber * amount) - syllabusList.size()));
            log.info("page from: " + pageFrom);
            log.info("page to: " + pageTo);
            List<Syllabus> syllabusSubList = syllabusList.subList(pageFrom, pageTo);

            log.info("syllabus sub list size: " + syllabusSubList.size());
            log.info("syllabus sub list: " + syllabusSubList);

            for (int i = 0; i < syllabusSubList.size(); i++) {
//                GetAllSyllabusResponse res = GetAllSyllabusResponse.builder()
//                        .syllabusName(syllabusSubList.get(i).getTopicName())
//                        .syllabusCode(syllabusSubList.get(i).getTopicCode())
//                        .createdOn(convertToMMDDYYYY(syllabusList.get(i).getCreatedDate().toString().split(" ")[0]))
//                        .createdBy(syllabusList.get(i).getCreatedBy())
//                        .duration(Integer.toString(syllabusList.get(i).getNumberOfDay()))
//                        .status(syllabusSubList.get(i).getPublishStatus())
//                        .syllabusObjectiveList(objectiveList)
//                        .build();

                log.info(syllabusSubList.get(i).getTopicCode());
                SyllabusResponse res = getDetailOfSyllabus(syllabusSubList.get(i).getTopicCode());

                syllabuses.add(res);
            }
            return GetSyllabusByPage.builder()
                    .message("found " + syllabuses.size() + " result.")
                    .totalNumberOfPages(totalNumberOfPages)
                    .status(0)
                    .pageNumber(pageNumber)
                    .syllabusList(syllabuses)
                    .build();

        } catch (Exception err) {
            err.printStackTrace();
            return GetSyllabusByPage.builder()
                    .message("found 0 result.")
                    .totalNumberOfPages(0)
                    .status(-1)
                    .pageNumber(pageNumber)
                    .syllabusList(null)
                    .build();
        }
    }

    @Override
    public Syllabus getDetailSyllabus(String topicCode) {
        Syllabus syllabus = syllabusDAO.findById(topicCode).get();

        syllabus.getTu().stream().toList();
        for (int i = 0; i < syllabus.getTu().stream().toList().size(); i++) {
            syllabus.getTu().stream().toList().get(i).getTrainingContents();
            log.info(syllabus);
        }

//        syllabus.getTrainingMaterials().stream().toList().get(0).getMaterial();


        return syllabus;
    }

    @Override
    public CreateSyllabusResponse createSyllabus(CreateSyllabusOutlineRequest request) {

        if (syllabusDAO.findById(request.getTopicCode()).isPresent()) {
            return CreateSyllabusResponse.builder()
                    .status(1)
                    .message("Create syllabus successfully.")
                    .url(null)
                    .build();
        }
        int unitBatchSize = 10;
        int contentBatchSize = 10;
        String putPresignedUrl = "";
        String getPresignedUrl = "";
        List<TrainingUnit> unitList = new ArrayList<>();
        List<TrainingContent> contentList = new ArrayList<>();
        List<TrainingContent> savedContentList;
        List<SyllabusObjective> syllabusObjectiveList = new ArrayList<>();
        List<TrainingMaterial> trainingMaterialList = new ArrayList<>();
        LinkedHashMap<Integer, String> learningObjectiveMap = new LinkedHashMap<>();
        List<PresignedUrlResponse> presignedUrlResponseList = new ArrayList<>();
        Map<String, String> syllabusObjectiveMap = new HashMap<>();
//        List<LearningObjective> learningObjectiveList = new ArrayList<>();

        User creator = userDAO.findByEmail(request.getCreatorEmail()).get();
        try {
            if (Integer.parseInt(request.getTrainingAudience()) < 0 || request.getAssignmentLab() < 0 || request.getConceptLecture() < 0 ||
                    request.getGuideReview() < 0 || request.getTestQuiz() < 0 || request.getExam() < 0 ||
                    request.getQuiz() < 0 || request.getAssignment() < 0 || request.getFin() < 0 ||
                    request.getFinalTheory() < 0 || request.getFinalPractice() < 0 || request.getGpa() < 0
            ) {
                return CreateSyllabusResponse.builder()
                        .status(1)
                        .message("Some of the value can't be negative.")
                        .url(null)
                        .build();
            }

            if (!isNumberOrFloatingPoint(request.getVersion())) {
                return CreateSyllabusResponse.builder()
                        .status(1)
                        .message("Version must be number and can't be negative.")
                        .url(null)
                        .build();
            }
            Syllabus syllabus = Syllabus.builder()
                    .topicCode(request.getTopicCode())
                    .topicName(request.getTopicName())
                    .trainingAudience(Integer.parseInt(request.getTrainingAudience()))
                    .courseObjective(request.getCourseObjective())
                    .technicalGroup(request.getTechnicalRequirement())
                    .publishStatus(request.getPublishStatus())
                    .priority(request.getPriority())
                    .version(request.getVersion())
                    .createdBy(creator)
                    .createdDate(new Date())
                    .trainingPrinciples(request.getTrainingPrinciple())
                    .modifiedDate(new Date())
                    .modifiedBy(creator.getEmail())
                    .assignmentLab(request.getAssignmentLab() + " %")
                    .conceptLecture(request.getConceptLecture() + " %")
                    .guideReview(request.getGuideReview() + " %")
                    .testQuiz(request.getTestQuiz() + " %")
                    .exam(request.getExam() + " %")
                    .quiz(request.getQuiz() + " %")
                    .assignment(request.getAssignment() + " %")
                    .final_(request.getFin() + " %")
                    .finalTheory(request.getFinalTheory() + " %")
                    .finalPractice(request.getFinalPractice() + " %")
                    .gpa(request.getGpa() + " %")
                    .build();

            syllabusDAO.save(syllabus);

            StandardOutput standardOutput = standardOutputDAO.findById("H4SD").get();

            for (int i = 0; i < request.getSyllabus().size(); i++) {
                for (int j = 0; j < request.getSyllabus().get(i).getUnits().size(); j++) {
                    TrainingUnit trainingUnit = TrainingUnit.builder()
                            .unitName(request.getSyllabus().get(i).getUnits().get(j).getUnitName())
                            .dayNumber(request.getSyllabus().get(i).getDayNumber())
                            .id(SyllabusTrainingUnitCompositeKey.builder()
                                    .uCode(request.getSyllabus().get(i).getUnits().get(j).getUnitCode())
                                    .tCode(request.getTopicCode())
                                    .build())
                            .syllabus(syllabus)
                            .build();

                    unitList.add(trainingUnit);
                    for (int z = 0; z < request.getSyllabus().get(i).getUnits().get(j).getContents().size(); z++) {
                        StandardOutput outputCode = standardOutputDAO.findById(request.getSyllabus().get(i).getUnits().get(j).getContents().get(z).getStandardOutput()).get();


                        TrainingContent trainingContent = TrainingContent.builder()
                                .id(SyllabusTrainingUnitTrainingContentCompositeKey.builder()
                                        .contentCode(request.getSyllabus().get(i).getUnits().get(j).getContents().get(z).getContentId())
                                        .id(SyllabusTrainingUnitCompositeKey.builder()
                                                .tCode(request.getTopicCode())
                                                .uCode(trainingUnit.getId().getUCode())
                                                .build())
                                        .build())
                                .unitCode(trainingUnit)
                                .deliveryType(request.getSyllabus().get(i).getUnits().get(j).getContents().get(z).getDeliveryType())
                                .note(request.getSyllabus().get(i).getUnits().get(j).getContents().get(z).getNote())
                                .contentName(request.getSyllabus().get(i).getUnits().get(j).getContents().get(z).getContent())
                                .trainingFormat(request.getSyllabus().get(i).getUnits().get(j).getContents().get(z).isOnline())
                                .duration(request.getSyllabus().get(i).getUnits().get(j).getContents().get(z).getDuration())
                                .outputCode(outputCode)
                                .build();

                        learningObjectiveMap.put(contentList.size(), request.getSyllabus().get(i).getUnits().get(j).getContents().get(z).getStandardOutput());
                        contentList.add(trainingContent);

                        List<String> putPresignedUrlList = new ArrayList<>();
                        List<String> getPresginedUrlList = new ArrayList<>();
                        for (int x = 0; x < request.getSyllabus().get(i).getUnits().get(j).getContents().get(z).getTrainingMaterials().size(); x++) {

                            putPresignedUrl = fileService.generateUrl(request.getSyllabus().get(i).getUnits().get(j).getContents().get(z).getTrainingMaterials().get(x) + "_"
                                    + syllabus.getTopicCode() + request.getSyllabus().get(i).getUnits().get(j).getUnitCode() + trainingContent.getId().getContentCode(), HttpMethod.PUT);
                            getPresignedUrl = fileService.generateUrl(request.getSyllabus().get(i).getUnits().get(j).getContents().get(z).getTrainingMaterials().get(x) + "_"
                                    + syllabus.getTopicCode() + request.getSyllabus().get(i).getUnits().get(j).getUnitCode() + trainingContent.getId().getContentCode(), HttpMethod.GET);

                            TrainingMaterial trainingMaterial = TrainingMaterial.builder()
                                    .material(request.getSyllabus().get(i).getUnits().get(j).getContents().get(z).getTrainingMaterials().get(x))
                                    .source(getPresignedUrl)
                                    .trainingContent(trainingContent)
//                                    .syllabus(syllabus)
                                    .build();

                            trainingMaterialList.add(trainingMaterial);
                            getPresginedUrlList.add(getPresignedUrl);
                            putPresignedUrlList.add(putPresignedUrl);
                        }
                        PresignedUrlResponse res = PresignedUrlResponse.builder()
                                .getPresignedUrl(getPresginedUrlList)
                                .putPresignedUrl(putPresignedUrlList)
                                .id("c" + request.getSyllabus().get(i).getUnits().get(j).getUnitCode() + trainingContent.getId().getContentCode())
                                .build();

                        log.info(res);

                        presignedUrlResponseList.add(res);
                    }


                    if (contentList.size() >= contentBatchSize && unitList.size() >= unitBatchSize) {
                        log.info("content list size: " + contentList.size());
                        trainingUnitDAO.saveAll(unitList);
                        savedContentList = trainingContentDAO.saveAll(contentList);
                        trainingMaterialDAO.saveAll(trainingMaterialList);
                        contentList.clear();
                        unitList.clear();
                        trainingMaterialList.clear();
                        for (Map.Entry<Integer, String> entry : learningObjectiveMap.entrySet()) {
                            log.info(entry.getKey());
                            log.info(entry.getValue());
                            if (!standardOutput.getOutputCode().equalsIgnoreCase(entry.getValue())) {
                                standardOutput = standardOutputDAO.findById(entry.getValue().toUpperCase()).get();
                            }

                            if (!syllabusObjectiveMap.containsKey(entry.getValue())) {
                                SyllabusObjective syllabusObjective = SyllabusObjective.builder()
                                        .id(SyllabusStandardOutputCompositeKey.builder()
                                                .outputCode(entry.getValue())
                                                .topicCode(syllabus.getTopicCode())
                                                .build())
                                        .outputCode(standardOutput)
                                        .topicCode(syllabus)
                                        .build();
                                syllabusObjectiveList.add(syllabusObjective);
                                syllabusObjectiveMap.put(entry.getValue(), syllabus.getTopicCode());
                            }
                            log.info(standardOutput.getOutputCode());
//                                LearningObjective learningObjective = LearningObjective.builder()
//                                        .contentCode(savedContentList.get(entry.getKey()))
//                                        .outputCode(standardOutput)
//                                        .description(standardOutput.getDescription())
//                                        .name(standardOutput.getOutputName())
//                                        .type("fpt dogshit")
//                                        .build();
//                                learningObjectiveList.add(learningObjective);
//                            log.info(learningObjectiveList);
                            log.info(savedContentList.get(entry.getKey()));
//                            learningObjectiveDAO.saveAll(learningObjectiveList);
//                            learningObjectiveList.clear();
//                            learningObjectiveList = new ArrayList<>();
                        }
                        learningObjectiveMap.clear();
                    }
                    syllabusObjectiveDAO.saveAll(syllabusObjectiveList);

                }

            }

            log.info("saved all training unit");

            if (!unitList.isEmpty()) {
                trainingUnitDAO.saveAll(unitList);
                unitList.clear();
                log.info(learningObjectiveMap);
                savedContentList = trainingContentDAO.saveAll(contentList);
                trainingMaterialDAO.saveAll(trainingMaterialList);
                contentList.clear();
                trainingMaterialList.clear();
                for (Map.Entry<Integer, String> entry : learningObjectiveMap.entrySet()) {
                    log.info(entry.getKey());
                    log.info(entry.getValue());
                    if (!standardOutput.getOutputCode().equalsIgnoreCase(entry.getValue())) {
                        standardOutput = standardOutputDAO.findById(entry.getValue().toUpperCase()).get();
                    }

                    if (!syllabusObjectiveMap.containsKey(entry.getValue())) {
                        SyllabusObjective syllabusObjective = SyllabusObjective.builder()
                                .id(SyllabusStandardOutputCompositeKey.builder()
                                        .outputCode(entry.getValue())
                                        .topicCode(syllabus.getTopicCode())
                                        .build())
                                .outputCode(standardOutput)
                                .topicCode(syllabus)
                                .build();
                        syllabusObjectiveList.add(syllabusObjective);
                        syllabusObjectiveMap.put(entry.getValue(), syllabus.getTopicCode());
                    }
                    log.info(standardOutput.getOutputCode());
//                        LearningObjective learningObjective = LearningObjective.builder()
//                                .contentCode(savedContentList.get(entry.getKey()))
//                                .outputCode(standardOutput)
//                                .description(standardOutput.getDescription())
//                                .name(standardOutput.getOutputName())
//                                .type("fpt dogshit")
//                                .build();
//                        learningObjectiveList.add(learningObjective);
//                    log.info(learningObjectiveList);
                    log.info(savedContentList.get(entry.getKey()));

                }
//                learningObjectiveDAO.saveAll(learningObjectiveList);
//                learningObjectiveList.clear();
//                learningObjectiveList = new ArrayList<>();
            }

            log.info(syllabusObjectiveMap);
            syllabusObjectiveDAO.saveAll(syllabusObjectiveList);

            syllabus.setDuration(request.getSyllabus().size());
            syllabusDAO.save(syllabus);

//            for (int i = 0; i < request.getTrainingMaterials().size(); i++) {
//                for (int j = 0; j < request.getTrainingMaterials().get(i).getFiles().getFileName().size(); j++) {
//                    String putPresignedUrl = fileService.generateUrl(request.getTrainingMaterials().get(i).getFiles().getFileName().get(j) + "_"
//                            + request.getTopicCode() + Integer.toString(request.getTrainingMaterials().get(i).getFiles().getUnitCode()) + Integer.toString(request.getTrainingMaterials().get(i).getFiles().getContentCode()), HttpMethod.PUT);
//                    String getPresignedUrl = fileService.generateUrl(request.getTrainingMaterials().get(i).getFiles().getFileName().get(j) + "_" + request.getTopicCode() + Integer.toString(request.getTrainingMaterials().get(i).getFiles().getUnitCode()) + Integer.toString(request.getTrainingMaterials().get(i).getFiles().getContentCode()), HttpMethod.GET);
//
//                    syllabus = syllabusDAO.findById(request.getTopicCode()).get();
//
//                    TrainingUnit trainingUnit = trainingUnitDAO.findById(
//                            SyllabusTrainingUnitCompositeKey.builder()
//                                    .tCode(syllabus.getTopicCode())
//                                    .uCode(request.getTrainingMaterials().get(i).getFiles().getUnitCode())
//                                    .build()).get();
//
//                    TrainingContent trainingContent = trainingContentDAO.findByContentIdAndUnitCode_Id(
//                            request.getTrainingMaterials().get(i).getFiles().getContentCode(),
//                            SyllabusTrainingUnitCompositeKey.builder()
//                                    .tCode(syllabus.getTopicCode())
//                                    .uCode(request.getTrainingMaterials().get(i).getFiles().getUnitCode())
//                                    .build()
//                    );
//
//                    TrainingMaterial trainingMaterial = TrainingMaterial.builder()
//                            .material(request.getTrainingMaterials().get(i).getFiles().getFileName().get(j))
//                            .source(getPresignedUrl)
//                            .trainingContent(trainingContent)
//                            .trainingUnit(trainingUnit)
//                            .syllabus(syllabus)
//                            .build();
//
//                    trainingMaterialList.add(trainingMaterial);
//                }
//            }

            return CreateSyllabusResponse.builder()
                    .status(0)
                    .message("Create syllabus successfully.")
                    .url(presignedUrlResponseList)
                    .build();

//        for(Map.Entry<String, String> entry: syllabusObjectiveMap.entrySet()){
//            LearningObjective learningObjective = learningObjectiveDAO.findById(entry.getKey()).get();
//            syllabus.getLearningObjectives().add(learningObjective);
////            learningObjective.getSyllabus().add(syllabus);
//
//            syllabusDAO.save(syllabus);
//            learningObjectiveList.add(learningObjective);
//        }
//        learningObjectiveDAO.saveAll(learningObjectiveList);
        } catch (Exception err) {
            err.printStackTrace();
            return CreateSyllabusResponse.builder()
                    .status(-1)
                    .message("Server error, have to fix some shit again :(.")
                    .url(null)
                    .build();
        }
    }

    @Override
    public UpdateSyllabusResponse updateSyllabus(CreateSyllabusOutlineRequest request) {
        Syllabus syllabus = syllabusDAO.findById(request.getTopicCode()).isEmpty() ? null : syllabusDAO.findById(request.getTopicCode()).get();
        if (syllabus == null) {
            return UpdateSyllabusResponse.builder()
                    .status(1)
                    .message("syllabus with id " + request.getTopicCode() + " don't exist.")
                    .url(null)
                    .build();
        }
        int unitBatchSize = 10;
        int contentBatchSize = 10;
        String putPresignedUrl = "";
        String getPresignedUrl = "";
        List<TrainingUnit> unitList = trainingUnitDAO.findBySyllabus_TopicCode(request.getTopicCode());
        List<TrainingContent> contentList = trainingContentDAO.findByUnitCode_Syllabus_TopicCode(request.getTopicCode());
        List<TrainingContent> savedContentList;
        List<SyllabusObjective> syllabusObjectiveList = syllabusObjectiveDAO.findByTopicCode_TopicCode(request.getTopicCode());
        List<TrainingMaterial> trainingMaterialList = trainingMaterialDAO.findByTrainingContent_UnitCode_Syllabus_TopicCode(request.getTopicCode());
        LinkedHashMap<Integer, String> learningObjectiveMap = new LinkedHashMap<>();
        List<PresignedUrlResponse> presignedUrlResponseList = new ArrayList<>();
        Map<String, String> syllabusObjectiveMap = new HashMap<>();

        User creator = userDAO.findByEmail(request.getCreatorEmail()).get();
        try {

            trainingUnitDAO.deleteAll(unitList);
            trainingContentDAO.deleteAll(contentList);
            syllabusObjectiveDAO.deleteAll(syllabusObjectiveList);
            trainingMaterialDAO.deleteAll(trainingMaterialList);

            unitList.clear();
            contentList.clear();
            syllabusObjectiveList.clear();
            trainingMaterialList.clear();

            if (Integer.parseInt(request.getTrainingAudience()) < 0 || request.getAssignmentLab() < 0 || request.getConceptLecture() < 0 ||
                    request.getGuideReview() < 0 || request.getTestQuiz() < 0 || request.getExam() < 0 ||
                    request.getQuiz() < 0 || request.getAssignment() < 0 || request.getFin() < 0 ||
                    request.getFinalTheory() < 0 || request.getFinalPractice() < 0 || request.getGpa() < 0
            ) {
                return UpdateSyllabusResponse.builder()
                        .status(1)
                        .message("Some of the value can't be negative.")
                        .url(null)
                        .build();
            }

            if (!isNumberOrFloatingPoint(request.getVersion())) {
                return UpdateSyllabusResponse.builder()
                        .status(1)
                        .message("Version must be number and can't be negative.")
                        .url(null)
                        .build();
            }

            syllabus.setTopicName(request.getTopicName());
            syllabus.setTrainingAudience(Integer.parseInt(request.getTrainingAudience()));
            syllabus.setCourseObjective(request.getCourseObjective());
            syllabus.setTechnicalGroup(request.getTechnicalRequirement());
            syllabus.setPublishStatus(request.getPublishStatus());
            syllabus.setPriority(request.getPriority());
            syllabus.setVersion(request.getVersion());
            syllabus.setTrainingPrinciples(request.getTrainingPrinciple());
            syllabus.setModifiedDate(new Date());
            syllabus.setModifiedBy(creator.getEmail());
            syllabus.setAssignmentLab(request.getAssignmentLab() + " %");
            syllabus.setConceptLecture(request.getConceptLecture() + " %");
            syllabus.setGuideReview(request.getGuideReview() + " %");
            syllabus.setTestQuiz(request.getTestQuiz() + " %");
            syllabus.setExam(request.getExam() + " %");
            syllabus.setQuiz(request.getQuiz() + " %");
            syllabus.setAssignment(request.getAssignment() + " %");
            syllabus.setFinal_(request.getFin() + " %");
            syllabus.setFinalTheory(request.getFinalTheory() + " %");
            syllabus.setFinalPractice(request.getFinalPractice() + " %");
            syllabus.setGpa(request.getGpa() + " %");

            syllabusDAO.save(syllabus);

            StandardOutput standardOutput = standardOutputDAO.findById("H4SD").get();

            for (int i = 0; i < request.getSyllabus().size(); i++) {
                for (int j = 0; j < request.getSyllabus().get(i).getUnits().size(); j++) {
                    TrainingUnit trainingUnit = TrainingUnit.builder()
                            .unitName(request.getSyllabus().get(i).getUnits().get(j).getUnitName())
                            .dayNumber(request.getSyllabus().get(i).getDayNumber())
                            .id(SyllabusTrainingUnitCompositeKey.builder()
                                    .uCode(request.getSyllabus().get(i).getUnits().get(j).getUnitCode())
                                    .tCode(request.getTopicCode())
                                    .build())
                            .syllabus(syllabus)
                            .build();

                    unitList.add(trainingUnit);
                    for (int z = 0; z < request.getSyllabus().get(i).getUnits().get(j).getContents().size(); z++) {
                        StandardOutput outputCode = standardOutputDAO.findById(request.getSyllabus().get(i).getUnits().get(j).getContents().get(z).getStandardOutput()).get();


                        TrainingContent trainingContent = TrainingContent.builder()
                                .id(SyllabusTrainingUnitTrainingContentCompositeKey.builder()
                                        .contentCode(request.getSyllabus().get(i).getUnits().get(j).getContents().get(z).getContentId())
                                        .id(SyllabusTrainingUnitCompositeKey.builder()
                                                .tCode(request.getTopicCode())
                                                .uCode(trainingUnit.getId().getUCode())
                                                .build())
                                        .build())
                                .unitCode(trainingUnit)
//                                .contentId(request.getSyllabus().get(i).getUnits().get(j).getContents().get(z).getContentId())
                                .deliveryType(request.getSyllabus().get(i).getUnits().get(j).getContents().get(z).getDeliveryType())
                                .note(request.getSyllabus().get(i).getUnits().get(j).getContents().get(z).getNote())
                                .contentName(request.getSyllabus().get(i).getUnits().get(j).getContents().get(z).getContent())
                                .trainingFormat(request.getSyllabus().get(i).getUnits().get(j).getContents().get(z).isOnline())
                                .duration(request.getSyllabus().get(i).getUnits().get(j).getContents().get(z).getDuration())
                                .outputCode(outputCode)
                                .build();

                        learningObjectiveMap.put(contentList.size(), request.getSyllabus().get(i).getUnits().get(j).getContents().get(z).getStandardOutput());
                        contentList.add(trainingContent);

                        List<String> putPresignedUrlList = new ArrayList<>();
                        List<String> getPresginedUrlList = new ArrayList<>();
                        for (int x = 0; x < request.getSyllabus().get(i).getUnits().get(j).getContents().get(z).getTrainingMaterials().size(); x++) {

                            putPresignedUrl = fileService.generateUrl(request.getSyllabus().get(i).getUnits().get(j).getContents().get(z).getTrainingMaterials().get(x) + "_"
                                    + syllabus.getTopicCode() + request.getSyllabus().get(i).getUnits().get(j).getUnitCode() + trainingContent.getId().getContentCode(), HttpMethod.PUT);
                            getPresignedUrl = fileService.generateUrl(request.getSyllabus().get(i).getUnits().get(j).getContents().get(z).getTrainingMaterials().get(x) + "_"
                                    + syllabus.getTopicCode() + request.getSyllabus().get(i).getUnits().get(j).getUnitCode() + trainingContent.getId().getContentCode(), HttpMethod.GET);

                            TrainingMaterial trainingMaterial = TrainingMaterial.builder()
                                    .material(request.getSyllabus().get(i).getUnits().get(j).getContents().get(z).getTrainingMaterials().get(x))
                                    .source(getPresignedUrl)
                                    .trainingContent(trainingContent)
//                                    .syllabus(syllabus)
                                    .build();

                            trainingMaterialList.add(trainingMaterial);
                            getPresginedUrlList.add(getPresignedUrl);
                            putPresignedUrlList.add(putPresignedUrl);
                        }
                        PresignedUrlResponse res = PresignedUrlResponse.builder()
                                .getPresignedUrl(getPresginedUrlList)
                                .putPresignedUrl(putPresignedUrlList)
                                .id("c" + request.getSyllabus().get(i).getUnits().get(j).getUnitCode() + trainingContent.getId().getContentCode())
                                .build();

                        log.info(res);

                        presignedUrlResponseList.add(res);
                    }


                    if (contentList.size() >= contentBatchSize && unitList.size() >= unitBatchSize) {
                        log.info("content list size: " + contentList.size());
                        trainingUnitDAO.saveAll(unitList);
                        savedContentList = trainingContentDAO.saveAll(contentList);
                        trainingMaterialDAO.saveAll(trainingMaterialList);
                        contentList.clear();
                        unitList.clear();
                        trainingMaterialList.clear();
                        for (Map.Entry<Integer, String> entry : learningObjectiveMap.entrySet()) {
                            log.info(entry.getKey());
                            log.info(entry.getValue());
                            if (!standardOutput.getOutputCode().equalsIgnoreCase(entry.getValue())) {
                                standardOutput = standardOutputDAO.findById(entry.getValue().toUpperCase()).get();
                            }

                            if (!syllabusObjectiveMap.containsKey(entry.getValue())) {
                                SyllabusObjective syllabusObjective = SyllabusObjective.builder()
                                        .id(SyllabusStandardOutputCompositeKey.builder()
                                                .outputCode(entry.getValue())
                                                .topicCode(syllabus.getTopicCode())
                                                .build())
                                        .outputCode(standardOutput)
                                        .topicCode(syllabus)
                                        .build();
                                syllabusObjectiveList.add(syllabusObjective);
                                syllabusObjectiveMap.put(entry.getValue(), syllabus.getTopicCode());
                            }
                            log.info(standardOutput.getOutputCode());
//                                LearningObjective learningObjective = LearningObjective.builder()
//                                        .contentCode(savedContentList.get(entry.getKey()))
//                                        .outputCode(standardOutput)
//                                        .description(standardOutput.getDescription())
//                                        .name(standardOutput.getOutputName())
//                                        .type("fpt dogshit")
//                                        .build();
//                                learningObjectiveList.add(learningObjective);
//                            log.info(learningObjectiveList);
                            log.info(savedContentList.get(entry.getKey()));
//                            learningObjectiveDAO.saveAll(learningObjectiveList);
//                            learningObjectiveList.clear();
//                            learningObjectiveList = new ArrayList<>();
                        }


                        learningObjectiveMap.clear();
                    }

                }

            }

            log.info("saved all training unit");

            if (!unitList.isEmpty()) {
                trainingUnitDAO.saveAll(unitList);
                unitList.clear();
                log.info(learningObjectiveMap);
                savedContentList = trainingContentDAO.saveAll(contentList);
                trainingMaterialDAO.saveAll(trainingMaterialList);
                contentList.clear();
                trainingMaterialList.clear();
                for (Map.Entry<Integer, String> entry : learningObjectiveMap.entrySet()) {
                    log.info(entry.getKey());
                    log.info(entry.getValue());
                    if (!standardOutput.getOutputCode().equalsIgnoreCase(entry.getValue())) {
                        standardOutput = standardOutputDAO.findById(entry.getValue().toUpperCase()).get();
                    }

                    if (!syllabusObjectiveMap.containsKey(entry.getValue())) {
                        SyllabusObjective syllabusObjective = SyllabusObjective.builder()
                                .id(SyllabusStandardOutputCompositeKey.builder()
                                        .outputCode(entry.getValue())
                                        .topicCode(syllabus.getTopicCode())
                                        .build())
                                .outputCode(standardOutput)
                                .topicCode(syllabus)
                                .build();
                        syllabusObjectiveList.add(syllabusObjective);
                        syllabusObjectiveMap.put(entry.getValue(), syllabus.getTopicCode());
                    }
                    log.info(standardOutput.getOutputCode());
//                        LearningObjective learningObjective = LearningObjective.builder()
//                                .contentCode(savedContentList.get(entry.getKey()))
//                                .outputCode(standardOutput)
//                                .description(standardOutput.getDescription())
//                                .name(standardOutput.getOutputName())
//                                .type("fpt dogshit")
//                                .build();
//                        learningObjectiveList.add(learningObjective);
//                    log.info(learningObjectiveList);
                    log.info(savedContentList.get(entry.getKey()));

                }
//                learningObjectiveDAO.saveAll(learningObjectiveList);
//                learningObjectiveList.clear();
//                learningObjectiveList = new ArrayList<>();
            }
            log.info(syllabusObjectiveMap);
            syllabusObjectiveDAO.saveAll(syllabusObjectiveList);

            syllabus.setDuration(request.getSyllabus().size());
            syllabusDAO.save(syllabus);

//            for (int i = 0; i < request.getTrainingMaterials().size(); i++) {
//                for (int j = 0; j < request.getTrainingMaterials().get(i).getFiles().getFileName().size(); j++) {
//                    String putPresignedUrl = fileService.generateUrl(request.getTrainingMaterials().get(i).getFiles().getFileName().get(j) + "_"
//                            + request.getTopicCode() + Integer.toString(request.getTrainingMaterials().get(i).getFiles().getUnitCode()) + Integer.toString(request.getTrainingMaterials().get(i).getFiles().getContentCode()), HttpMethod.PUT);
//                    String getPresignedUrl = fileService.generateUrl(request.getTrainingMaterials().get(i).getFiles().getFileName().get(j) + "_" + request.getTopicCode() + Integer.toString(request.getTrainingMaterials().get(i).getFiles().getUnitCode()) + Integer.toString(request.getTrainingMaterials().get(i).getFiles().getContentCode()), HttpMethod.GET);
//
//                    syllabus = syllabusDAO.findById(request.getTopicCode()).get();
//
//                    TrainingUnit trainingUnit = trainingUnitDAO.findById(
//                            SyllabusTrainingUnitCompositeKey.builder()
//                                    .tCode(syllabus.getTopicCode())
//                                    .uCode(request.getTrainingMaterials().get(i).getFiles().getUnitCode())
//                                    .build()).get();
//
//                    TrainingContent trainingContent = trainingContentDAO.findByContentIdAndUnitCode_Id(
//                            request.getTrainingMaterials().get(i).getFiles().getContentCode(),
//                            SyllabusTrainingUnitCompositeKey.builder()
//                                    .tCode(syllabus.getTopicCode())
//                                    .uCode(request.getTrainingMaterials().get(i).getFiles().getUnitCode())
//                                    .build()
//                    );
//
//                    TrainingMaterial trainingMaterial = TrainingMaterial.builder()
//                            .material(request.getTrainingMaterials().get(i).getFiles().getFileName().get(j))
//                            .source(getPresignedUrl)
//                            .trainingContent(trainingContent)
//                            .trainingUnit(trainingUnit)
//                            .syllabus(syllabus)
//                            .build();
//
//                    trainingMaterialList.add(trainingMaterial);
//                }
//            }

            return UpdateSyllabusResponse.builder()
                    .message("update syllabus with id: " + request.getTopicCode() + " successfully.")
                    .status(0)
                    .url(presignedUrlResponseList)
                    .build();

//        for(Map.Entry<String, String> entry: syllabusObjectiveMap.entrySet()){
//            LearningObjective learningObjective = learningObjectiveDAO.findById(entry.getKey()).get();
//            syllabus.getLearningObjectives().add(learningObjective);
////            learningObjective.getSyllabus().add(syllabus);
//
//            syllabusDAO.save(syllabus);
//            learningObjectiveList.add(learningObjective);
//        }
//        learningObjectiveDAO.saveAll(learningObjectiveList);
        } catch (Exception err) {
            err.printStackTrace();
            return UpdateSyllabusResponse.builder()
                    .status(-1)
                    .message("Server error, have to fix some shit again :(.")
                    .url(null)
                    .build();
        }

    }

    @Override
    public Syllabus getSyllabusById(String topicCode) {
        Optional<Syllabus> optionalSyllabus = syllabusDAO.findById(topicCode);
        return optionalSyllabus.orElse(null);
    }

    @Override
    public DeleteSyllabusResponse deleteSyllabus(String topicCode) {
        Syllabus existedSyllabus = syllabusDAO.findById(topicCode).isPresent() ? syllabusDAO.findById(topicCode).get() : null;

        try {
            if (existedSyllabus != null) {
                existedSyllabus.setDeleted(true);
                existedSyllabus.setPublishStatus("inactive");
                syllabusDAO.save(existedSyllabus);
                return DeleteSyllabusResponse.builder()
                        .status(0)
                        .message("Successfully delete syllabus with id: " + topicCode)
                        .build();
            }
            return DeleteSyllabusResponse.builder()
                    .status(1)
                    .message("Can't find syllabus with id: " + topicCode)
                    .build();
        } catch (Exception err) {
            err.printStackTrace();
            return DeleteSyllabusResponse.builder()
                    .status(-1)
                    .message("Fail to delete syllabus with id: " + topicCode)
                    .build();
        }
    }

    @Override
    public DeleteSyllabusResponse unDeleteSyllabus(String topicCode) {
        Syllabus existedSyllabus = syllabusDAO.findById(topicCode).isPresent() ? syllabusDAO.findById(topicCode).get() : null;

        try {
            if (existedSyllabus != null) {
                existedSyllabus.setDeleted(false);
                existedSyllabus.setPublishStatus("active");
                syllabusDAO.save(existedSyllabus);
                return DeleteSyllabusResponse.builder()
                        .status(0)
                        .message("Successfully un-delete syllabus with id: " + topicCode)
                        .build();
            }
            return DeleteSyllabusResponse.builder()
                    .status(1)
                    .message("Can't find syllabus with id: " + topicCode)
                    .build();
        } catch (Exception err) {
            err.printStackTrace();
            return DeleteSyllabusResponse.builder()
                    .status(-1)
                    .message("Fail to un-delete syllabus with id: " + topicCode)
                    .build();
        }
    }

    @Override
    public List<Syllabus> processDataFromCSV(MultipartFile file, String choice, String seperator, String scan, Authentication authentication) throws IOException {
        if (!isCSVFile(file)) {
            throw new IllegalArgumentException("Uploaded file is not a CSV file.");
        }
        List<Syllabus> syllabusList = new ArrayList<>();
        Optional<Syllabus> optionalSyllabus = Optional.empty();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            String line;

            boolean firstLine = true;
            // Mảng các đối tượng SimpleDateFormat với các định dạng khác nhau
            SimpleDateFormat[] dateFormats = {
                    new SimpleDateFormat("dd/MM/yyyy"),
                    new SimpleDateFormat("dd-MM-yyyy")
            };
            while ((line = reader.readLine()) != null) {
                if (firstLine) {
                    firstLine = false; // Đây là dòng đầu tiên, bỏ qua nó
                    if (line == null || !isCSVHeaderValid(line, seperator)) {
                        throw new IllegalArgumentException("Header not match!.");
                    }
                    continue;
                }
                String[] data = line.split("[" + seperator + "]");
                for (int i = 0; i < 12; i++) {
                    if (data[i].isEmpty()) {
                        throw new IllegalArgumentException("Data is Empty.");
                    }
                }
                if (scan.equals("Code")) {
                    optionalSyllabus = syllabusDAO.findById(data[0]);

                } else if (scan.equals("Name")) {
                    optionalSyllabus = syllabusDAO.findByTopicName(data[6]);
                }

                Syllabus syllabusexits = optionalSyllabus.orElse(null);

                if (choice.equals("Replace")) {
                    if (syllabusexits != null) {
                        syllabusexits.setCreatedBy(getCreator(authentication));
                        // Chuyển đổi từ chuỗi ngày thành Date và chỉ lấy phần ngày
                        syllabusexits.setCreatedDate(parseDate(data[1], dateFormats));
                        syllabusexits.setModifiedBy(getCreator(authentication).getName());
                        syllabusexits.setModifiedDate(parseDate(data[2], dateFormats));
                        syllabusexits.setPriority(data[3]);
                        syllabusexits.setPublishStatus(data[4]);
                        syllabusexits.setTechnicalGroup(data[5]);
                        syllabusexits.setTopicName(data[6]);
                        syllabusexits.setTopicOutline(data[7]);
                        syllabusexits.setTrainingAudience(Integer.parseInt(data[8]));
//                        syllabusexits.setTrainingMaterials(data[9]);
                        syllabusexits.setTrainingPrinciples(data[9]);
                        syllabusexits.setVersion(data[10]);
                        syllabusexits.setCourseObjective(data[11]);
//                        syllabusexits.setUserID(getCreator(authentication));
                        syllabusDAO.save(syllabusexits);
                    } else {
                        if (scan.equals("Name")) {
                            Optional<Syllabus> SyllabusCode = syllabusDAO.findById(data[0]);
                            if (SyllabusCode.isEmpty()) {
                                Syllabus c = new Syllabus();

                                c.setTopicCode(data[0]);
                                c.setCreatedBy(getCreator(authentication));
                                // Chuyển đổi từ chuỗi ngày thành Date và chỉ lấy phần ngày
                                c.setCreatedDate(parseDate(data[1], dateFormats));
                                c.setModifiedBy(getCreator(authentication).getName());
                                c.setModifiedDate(parseDate(data[2], dateFormats));
                                c.setPriority(data[3]);
                                c.setPublishStatus(data[4]);
                                c.setTechnicalGroup(data[5]);
                                c.setTopicName(data[6]);
                                c.setTopicOutline(data[7]);
                                c.setTrainingAudience(Integer.parseInt(data[8]));
//                        c.setTrainingMaterials(data[9]);
                                c.setTrainingPrinciples(data[9]);
                                c.setVersion(data[10]);
                                c.setCourseObjective(data[11]);
//                        c.setUserID(getCreator(authentication));
                                syllabusList.add(c);
                                syllabusDAO.saveAll(syllabusList);
                            }
                        } else {
                            Syllabus c = new Syllabus();

                            c.setTopicCode(data[0]);
                            c.setCreatedBy(getCreator(authentication));
                            // Chuyển đổi từ chuỗi ngày thành Date và chỉ lấy phần ngày
                            c.setCreatedDate(parseDate(data[1], dateFormats));
                            c.setModifiedBy(getCreator(authentication).getName());
                            c.setModifiedDate(parseDate(data[2], dateFormats));
                            c.setPriority(data[3]);
                            c.setPublishStatus(data[4]);
                            c.setTechnicalGroup(data[5]);
                            c.setTopicName(data[6]);
                            c.setTopicOutline(data[7]);
                            c.setTrainingAudience(Integer.parseInt(data[8]));
//                        c.setTrainingMaterials(data[9]);
                            c.setTrainingPrinciples(data[9]);
                            c.setVersion(data[10]);
                            c.setCourseObjective(data[11]);
//                        c.setUserID(getCreator(authentication));
                            syllabusList.add(c);
                            syllabusDAO.saveAll(syllabusList);
                        }
                    }
                } else if (choice.equals("Skip")) {
                    if (syllabusexits != null) {

                    } else {
                        if (scan.equals("Name")) {
                            Optional<Syllabus> SyllabusCode = syllabusDAO.findById(data[0]);
                            if (SyllabusCode.isEmpty()) {
                                Syllabus c = new Syllabus();

                                c.setTopicCode(data[0]);
                                c.setCreatedBy(getCreator(authentication));
                                // Chuyển đổi từ chuỗi ngày thành Date và chỉ lấy phần ngày
                                c.setCreatedDate(parseDate(data[1], dateFormats));
                                c.setModifiedBy(getCreator(authentication).getName());
                                c.setModifiedDate(parseDate(data[2], dateFormats));
                                c.setPriority(data[3]);
                                c.setPublishStatus(data[4]);
                                c.setTechnicalGroup(data[5]);
                                c.setTopicName(data[6]);
                                c.setTopicOutline(data[7]);
                                c.setTrainingAudience(Integer.parseInt(data[8]));
//                        c.setTrainingMaterials(data[9]);
                                c.setTrainingPrinciples(data[9]);
                                c.setVersion(data[10]);
                                c.setCourseObjective(data[11]);
//                        c.setUserID(getCreator(authentication));
                                syllabusList.add(c);
                                syllabusDAO.saveAll(syllabusList);
                            }
                        } else {
                            Syllabus c = new Syllabus();

                            c.setTopicCode(data[0]);
                            c.setCreatedBy(getCreator(authentication));
                            // Chuyển đổi từ chuỗi ngày thành Date và chỉ lấy phần ngày
                            c.setCreatedDate(parseDate(data[1], dateFormats));
                            c.setModifiedBy(getCreator(authentication).getName());
                            c.setModifiedDate(parseDate(data[2], dateFormats));
                            c.setPriority(data[3]);
                            c.setPublishStatus(data[4]);
                            c.setTechnicalGroup(data[5]);
                            c.setTopicName(data[6]);
                            c.setTopicOutline(data[7]);
                            c.setTrainingAudience(Integer.parseInt(data[8]));
//                        c.setTrainingMaterials(data[9]);
                            c.setTrainingPrinciples(data[9]);
                            c.setVersion(data[10]);
                            c.setCourseObjective(data[11]);
//                        c.setUserID(getCreator(authentication));
                            syllabusList.add(c);
                            syllabusDAO.saveAll(syllabusList);
                        }
                    }
                } else if (choice.equals("Allow")) {
                    if (syllabusexits != null) {
                        if (scan.equals("Code")) {
                            duplicateSyllabus(data[0], authentication);
                        } else if (scan.equals("Name")) {
                            Optional<Syllabus> SyllabusCode = syllabusDAO.findById(data[0]);
                            if (SyllabusCode.isEmpty()) {
                                duplicateSyllabusByName(data[0], data[6], authentication);
                            } else {
                                duplicateSyllabusByNameAndCode(data[0], data[6], authentication);
                            }
                        }
                    } else {
                        Optional<Syllabus> SyllabusCode = syllabusDAO.findById(data[0]);
                        if (SyllabusCode.isEmpty()) {
                            Syllabus c = new Syllabus();

                            c.setTopicCode(data[0]);
                            c.setCreatedBy(getCreator(authentication));
                            // Chuyển đổi từ chuỗi ngày thành Date và chỉ lấy phần ngày
                            c.setCreatedDate(parseDate(data[1], dateFormats));
                            c.setModifiedBy(getCreator(authentication).getName());
                            c.setModifiedDate(parseDate(data[2], dateFormats));
                            c.setPriority(data[3]);
                            c.setPublishStatus(data[4]);
                            c.setTechnicalGroup(data[5]);
                            c.setTopicName(data[6]);
                            c.setTopicOutline(data[7]);
                            c.setTrainingAudience(Integer.parseInt(data[8]));
//                        c.setTrainingMaterials(data[9]);
                            c.setTrainingPrinciples(data[9]);
                            c.setVersion(data[10]);
                            c.setCourseObjective(data[11]);
//                        c.setUserID(getCreator(authentication));
                            syllabusList.add(c);
                            syllabusDAO.saveAll(syllabusList);
                        }
                    }
                }
            }
        } catch (IOException |
                 ParseException e) {
            e.printStackTrace();
        }
        return syllabusList;
    }

    private boolean isCSVFile(MultipartFile file) {
        String filename = file.getOriginalFilename();
        return filename != null && filename.endsWith(".csv");
    }

    private boolean isCSVHeaderValid(String headerLine, String seperator) {
        String[] HEADER = {"Topic code", "Created date", "Modified Date", "Priority", "Publish Status", "Technical Group", "Topic_name", "Topic Outline", "Training Audience", "Training Principles", "Version", "Course Objective"};
        String[] headers = headerLine.split("[" + seperator + "]");
//        String[] expectedHeaders = HEADER.split("[" + seperator + ",.;]");
        if (headers.length == 1) {
            throw new IllegalArgumentException("Seperator not match!");
        }
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

    private java.sql.Date parseDate(String dateString, SimpleDateFormat[] dateFormats) throws ParseException {
        for (SimpleDateFormat dateFormat : dateFormats) {
            try {
                Date parsedDate = dateFormat.parse(dateString);
                return new java.sql.Date(parsedDate.getTime());
            } catch (ParseException e) {
                // Nếu không thành công, thử định dạng khác
                log.info(dateFormat.toPattern() + " not equal " + dateString);
            }
        }
        throw new ParseException("Không thể chuyển đổi ngày", 0);
    }

    public void downloadCSV() throws IOException {
        String computerAccountName = System.getProperty("user.name");

        File csvFile = new File("C:/Users/" + computerAccountName + "/Downloads/TemplateSyllabus.csv");

        // Sử dụng FileWriter để ghi vào tệp CSV
        FileWriter fileWriter = new FileWriter(csvFile, false); // Use false to overwrite the file

        // Sử dụng BufferedWriter để ghi dữ liệu vào tệp CSV
        BufferedWriter out = new BufferedWriter(fileWriter);

        // Thêm nội dung vào tệp
        out.write("Topic code,Created date,Modified Date,Priority,Publish Status,Technical Group,Topic_name,Topic Outline,Training Audience,Training Principles,Version,Course Objective");
        out.newLine(); // Xuống dòng
        // Đóng BufferedWriter
        out.close();
    }

    @Override
    public Syllabus duplicateSyllabusByNameAndCode(String topicCode, String topicName, Authentication authentication) {
        int duplicateName = syllabusDAO.countByTopicNameLike(topicName + "%");
        int duplicateCode = syllabusDAO.countByTopicCodeLike(topicCode + "%");
        String topicCodeClone = "";
        Syllabus originalSyllabusName = syllabusDAO.findByTopicName(topicName).get();
        Syllabus originalSyllabusCode = syllabusDAO.findById(topicCode).get();
        if (originalSyllabusCode != null) {
            topicCode = originalSyllabusCode.getTopicCode();
            topicCodeClone = topicCode + "_" + duplicateCode;
        } else {
            topicCodeClone = originalSyllabusCode.getTopicCode();
        }
        topicName = originalSyllabusName.getTopicName();
        String topicNameClone = topicName + "_" + duplicateName;
        Syllabus duplicatedSyllabusByName = Syllabus.builder()
                .topicName(topicNameClone)
                .trainingPrinciples(originalSyllabusName.getTrainingPrinciples())
                .version(originalSyllabusName.getVersion())
                .technicalGroup(originalSyllabusName.getTechnicalGroup())
                .trainingAudience(originalSyllabusName.getTrainingAudience())
                .topicOutline(originalSyllabusName.getTopicOutline())
//                .trainingMaterials(originalSyllabusName.getTrainingMaterials())
                .priority(originalSyllabusName.getPriority())
                .publishStatus(originalSyllabusName.getPublishStatus())
                .createdBy(originalSyllabusName.getCreatedBy())
                .createdDate(new Date())
                .modifiedBy(originalSyllabusName.getModifiedBy())
                .modifiedDate(new Date())
                .courseObjective(originalSyllabusName.getCourseObjective())
                .topicCode(topicCodeClone)
                .build();

        syllabusDAO.save(duplicatedSyllabusByName);

        return duplicatedSyllabusByName;
    }

    @Override
    public Syllabus duplicateSyllabusByName(String topicCode, String topicName, Authentication authentication) {
        int countName = syllabusDAO.countByTopicNameLike(topicName + "%");
        Syllabus originalSyllabus = syllabusDAO.findByTopicName(topicName).get();

        topicName = originalSyllabus.getTopicName();
        String topicNameClone = topicName + "_" + countName;


        Syllabus duplicatedSyllabus = Syllabus.builder()
                .topicName(topicNameClone)
                .trainingPrinciples(originalSyllabus.getTrainingPrinciples())
                .version(originalSyllabus.getVersion())
                .technicalGroup(originalSyllabus.getTechnicalGroup())
                .trainingAudience(originalSyllabus.getTrainingAudience())
                .topicOutline(originalSyllabus.getTopicOutline())
//                .trainingMaterials(originalSyllabus.getTrainingMaterials())
                .priority(originalSyllabus.getPriority())
                .publishStatus(originalSyllabus.getPublishStatus())
                .createdBy(originalSyllabus.getCreatedBy())
                .createdDate(new Date())
                .modifiedBy(originalSyllabus.getModifiedBy())
                .modifiedDate(new Date())
                .courseObjective(originalSyllabus.getCourseObjective())
                .topicCode(topicCode)
                .build();

        syllabusDAO.save(duplicatedSyllabus);

        return duplicatedSyllabus;
    }

    @Override
    public Syllabus duplicateSyllabus(String topicCode, Authentication authentication) {
        int numberOfSameTopicCode = syllabusDAO.countByTopicCodeLike(topicCode + "%");
        Syllabus originalSyllabus = syllabusDAO.findById(topicCode).get();
        List<TrainingUnit> unitList = originalSyllabus.getTu().stream().toList();
        List<SyllabusObjective> syllabusObjectiveList = originalSyllabus.getSyllabusObjectives().stream().toList();
        List<TrainingContent> duplicatedTrainingContentList = new ArrayList<>();
        List<TrainingUnit> duplicatedTrainingUnitList = new ArrayList<>();
        List<SyllabusObjective> duplicatedSyllabusObjective = new ArrayList<>();
        topicCode = originalSyllabus.getTopicCode();
        String topicCodeClone = topicCode + "_" + numberOfSameTopicCode;

        try {

            Syllabus duplicatedSyllabus = Syllabus.builder()
                    .topicCode(topicCodeClone)
                    .topicName(originalSyllabus.getTopicName())
                    .trainingAudience(originalSyllabus.getTrainingAudience())
                    .courseObjective(originalSyllabus.getCourseObjective())
                    .technicalGroup(originalSyllabus.getTechnicalGroup())
                    .publishStatus("draft")
                    .priority(originalSyllabus.getPriority())
                    .version(originalSyllabus.getVersion())
                    .createdBy(originalSyllabus.getCreatedBy())
                    .createdDate(new Date())
                    .trainingPrinciples(originalSyllabus.getTrainingPrinciples())
                    .modifiedDate(new Date())
                    .modifiedBy(originalSyllabus.getModifiedBy())
                    .assignmentLab(originalSyllabus.getAssignmentLab())
                    .conceptLecture(originalSyllabus.getConceptLecture())
                    .guideReview(originalSyllabus.getGuideReview())
                    .testQuiz(originalSyllabus.getTestQuiz())
                    .exam(originalSyllabus.getExam())
                    .quiz(originalSyllabus.getQuiz())
                    .assignment(originalSyllabus.getAssignment())
                    .final_(originalSyllabus.getFinal_())
                    .finalTheory(originalSyllabus.getFinalTheory())
                    .finalPractice(originalSyllabus.getFinalPractice())
                    .gpa(originalSyllabus.getGpa())
                    .duration(originalSyllabus.getDuration())
                    .build();

            syllabusDAO.save(duplicatedSyllabus);

            for (int i = 0; i < unitList.size(); i++) {
                List<TrainingContent> trainingContentList = unitList.get(i).getTrainingContents().stream().toList();
                TrainingUnit trainingUnit = TrainingUnit.builder()
                        .unitName(unitList.get(i).getUnitName())
                        .dayNumber(unitList.get(i).getDayNumber())
                        .id(SyllabusTrainingUnitCompositeKey.builder()
                                .tCode(duplicatedSyllabus.getTopicCode())
                                .uCode(unitList.get(i).getId().getUCode())
                                .build())
                        .syllabus(duplicatedSyllabus)
                        .build();
                duplicatedTrainingUnitList.add(trainingUnit);
                for (int j = 0; j < trainingContentList.size(); j++) {
                    TrainingContent trainingContent = TrainingContent.builder()
                            .id(SyllabusTrainingUnitTrainingContentCompositeKey.builder()
                                    .contentCode(trainingContentList.get(j).getId().getContentCode())
                                    .id(SyllabusTrainingUnitCompositeKey.builder()
                                            .uCode(unitList.get(i).getId().getUCode())
                                            .tCode(duplicatedSyllabus.getTopicCode())
                                            .build())
                                    .build())
                            .unitCode(trainingUnit)
                            .deliveryType(trainingContentList.get(j).getDeliveryType())
                            .note(trainingContentList.get(j).getNote())
                            .contentName(trainingContentList.get(j).getContentName())
                            .trainingFormat(trainingContentList.get(j).isTrainingFormat())
                            .duration(trainingContentList.get(j).getDuration())
                            .outputCode(trainingContentList.get(j).getOutputCode())
                            .build();

                    duplicatedTrainingContentList.add(trainingContent);
                }
            }

            for (int i = 0; i < syllabusObjectiveList.size(); i++) {
                SyllabusObjective syllabusObjective = SyllabusObjective.builder()
                        .id(SyllabusStandardOutputCompositeKey.builder()
                                .topicCode(duplicatedSyllabus.getTopicCode())
                                .outputCode(syllabusObjectiveList.get(i).getOutputCode().getOutputCode())
                                .build())
                        .topicCode(duplicatedSyllabus)
                        .outputCode(syllabusObjectiveList.get(i).getOutputCode())
                        .build();

                duplicatedSyllabusObjective.add(syllabusObjective);
            }

            trainingUnitDAO.saveAll(duplicatedTrainingUnitList);
            trainingContentDAO.saveAll(duplicatedTrainingContentList);
            syllabusObjectiveDAO.saveAll(duplicatedSyllabusObjective);

            return duplicatedSyllabus;

        } catch (Exception err) {
            err.printStackTrace();
            return null;
        }

    }

    public Syllabus saveSyllabus(Syllabus syllabus) {
        return syllabusDAO.save(syllabus);
    }

    public List<Syllabus> searchSyllabus(String createdDate, String searchValue) {
        List<Syllabus> syllabusList = syllabusDAO.findAll();
        if (!Strings.isNullOrEmpty(createdDate)) {
            syllabusList = syllabusList.stream().filter(n -> {
                return new SimpleDateFormat("yyyy-MM-dd").format(n.getCreatedDate()).equals(createdDate);
            }).collect(Collectors.toList());
        }
        if (!Strings.isNullOrEmpty(searchValue)) {
            syllabusList = syllabusList.stream().filter(n -> n.getTopicName().trim().toLowerCase().contains(searchValue.trim().toLowerCase())
                    || n.getTopicCode().trim().toLowerCase().contains(searchValue.trim().toLowerCase())).collect(Collectors.toList());
        }
        return syllabusList;
    }

    public User getCreator(Authentication authentication) {
        Object creator = authentication.getPrincipal();
        if (creator instanceof User) {
            return (User) creator;
        }
        return null;
    }

    public boolean isPresignedUrlExpired(String presignedUrl) {
        try {
            // Parse the presigned URL to extract the expiration timestamp string
            String createDateMillisecond = convertToUTCPlus7(new URL(presignedUrl).getQuery().split("&")[1].split("=")[1]);
            long expirationDurationMillisecond = Long.parseLong(new URL(presignedUrl).getQuery().split("&")[3].split("=")[1]) * 1000;

            long todayMilliSecond = new Date().getTime();

            long expirationDateMillisecond = convertToMilliseconds(createDateMillisecond) + expirationDurationMillisecond;

            return todayMilliSecond > expirationDateMillisecond;
        } catch (Exception e) {
            // Handle exceptions (e.g., URL parsing, AWS SDK exceptions)
            e.printStackTrace();
            return false; // Assume the URL is not expired if an exception occurs
        }
    }

    private String convertToUTCPlus7(String zuluTimeString) {
        try {
            // Define the date format for Zulu time
            SimpleDateFormat zuluFormat = new SimpleDateFormat("yyyyMMdd'T'HHmmss'Z'");
            zuluFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

            // Parse Zulu time string
            Date zuluTime = zuluFormat.parse(zuluTimeString);

            // Define the date format for UTC+7
            SimpleDateFormat utcPlus7Format = new SimpleDateFormat("yyyyMMdd'T'HHmmss");
            utcPlus7Format.setTimeZone(TimeZone.getTimeZone("GMT+7"));

            // Format the date as UTC+7 and return the result
            return utcPlus7Format.format(zuluTime);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    private long convertToMilliseconds(String utcPlus7TimeString) {
        try {
            // Define the date format for UTC+7
            SimpleDateFormat utcPlus7Format = new SimpleDateFormat("yyyyMMdd'T'HHmmss");
            utcPlus7Format.setTimeZone(TimeZone.getTimeZone("GMT+7"));

            // Parse UTC+7 time string
            Date utcPlus7Time = utcPlus7Format.parse(utcPlus7TimeString);

            // Return the time in milliseconds since epoch
            return utcPlus7Time.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
            return -1;
        }
    }

    private SyllabusResponse getDetailOfSyllabus(String topicCode) {
        Syllabus syllabus = syllabusDAO.findById(topicCode).get();
        User creator = syllabus.getCreatedBy();
        List<TrainingUnit> trainingUnitList = syllabus.getTu().stream().toList();
        List<TrainingContent> trainingContentList = new ArrayList<>();
        List<TrainingMaterial> trainingMaterialList = new ArrayList<>();
        List<SyllabusObjective> syllabusObjectiveList = syllabus.getSyllabusObjectives().stream().toList();
        HashSet<Integer> dayList = new HashSet<>();
        List<DayDTO> dayDTOList = new ArrayList<>();
        List<UnitDTO> unitDTOList = new ArrayList<>();
        List<ContentDTO> contentDTOList = new ArrayList<>();
        List<MaterialDTO> trainingMaterialDTOList = new ArrayList<>();

        for (int i = 0; i < trainingUnitList.size(); i++) {
            dayList.add(trainingUnitList.get(i).getDayNumber());
        }

        log.info(dayList);

        for (Integer dayNumber : dayList) {
            for (int i = 0; i < trainingUnitList.size(); i++) {
                if (trainingUnitList.get(i).getDayNumber() == dayNumber) {
                    trainingContentList = trainingUnitList.get(i).getTrainingContents().stream().toList();
                    for (int j = 0; j < trainingContentList.size(); j++) {
                        trainingMaterialList = trainingContentList.get(j).getTrainingMaterials().stream().toList();
                        for (int z = 0; z < trainingMaterialList.size(); z++) {
                            String source = trainingMaterialList.get(z).getSource();
                            if (isPresignedUrlExpired(source)) {
                                source = fileService.generateUrl(trainingMaterialList.get(z).getMaterial() + "_"
                                        + syllabus.getTopicCode() + trainingUnitList.get(i).getId().getUCode() + trainingContentList.get(j).getId().getContentCode(), HttpMethod.GET);
                                trainingMaterialList.get(z).setSource(source);
                                trainingMaterialDAO.save(trainingMaterialList.get(z));
                            }
                            MaterialDTO trainingMaterial = MaterialDTO.builder()
                                    .materialName(trainingMaterialList.get(z).getMaterial())
                                    .materialSource(source)
                                    .build();

                            trainingMaterialDTOList.add(trainingMaterial);
                        }
                        ContentDTO content = ContentDTO.builder()
                                .contentId(Integer.toString(trainingContentList.get(j).getId().getContentCode()))
                                .contentName(trainingContentList.get(j).getContentName())
                                .standardOutput(trainingContentList.get(j).getOutputCode().getOutputCode())
                                .deliveryType(trainingContentList.get(j).getDeliveryType())
                                .duration(Integer.toString(trainingContentList.get(j).getDuration()))
                                .online(trainingContentList.get(j).isTrainingFormat())
                                .trainingMaterial(trainingMaterialDTOList)
                                .build();

                        contentDTOList.add(content);
                        trainingMaterialDTOList = new ArrayList<>();
                    }
                    UnitDTO unit = UnitDTO.builder()
                            .unitId(Integer.toString(trainingUnitList.get(i).getId().getUCode()))
                            .unitName(trainingUnitList.get(i).getUnitName())
                            .contentList(contentDTOList)
                            .build();

                    unitDTOList.add(unit);
                    contentDTOList = new ArrayList<>();
                }
            }
            DayDTO day = DayDTO.builder()
                    .dayNumber(Integer.toString(dayNumber))
                    .unitList(unitDTOList)
                    .build();

            dayDTOList.add(day);
            unitDTOList = new ArrayList<>();
        }
        List<String> outputCodeList = new ArrayList<>();
        for (int i = 0; i < syllabusObjectiveList.size(); i++) {
            outputCodeList.add(syllabusObjectiveList.get(i).getOutputCode().getOutputCode());
        }

        UserDTO modifier = null;

        if (syllabus.getModifiedBy() != null) {
            User moder = userDAO.findByEmail(syllabus.getModifiedBy()).get();
            modifier = UserDTO.builder()
                    .userId(moder.getUserId())
                    .userEmail(moder.getEmail())
                    .userName(moder.getName())
                    .build();
        }

        UserDTO user = UserDTO.builder()
                .userId(creator.getUserId())
                .userName(creator.getName())
                .userEmail(creator.getEmail())
                .build();


        return SyllabusResponse.builder()
                .syllabusStatus(syllabus.getPublishStatus())
                .duration(Integer.toString(syllabus.getDuration()))
                .syllabusName(syllabus.getTopicName())
                .syllabusCode(syllabus.getTopicCode())
                .createdBy(user)
                .createdOn(convertToMMDDYYYY(syllabus.getCreatedDate().toString().split(" ")[0]))
                .level(syllabus.getPriority())
                .attendeeNumber(Integer.toString(syllabus.getTrainingAudience()))
                .technicalRequirement(syllabus.getTechnicalGroup())
                .courseObjective(syllabus.getCourseObjective())
                .trainingPrinciple(syllabus.getTrainingPrinciples())
                .version(syllabus.getVersion())
                .modifiedBy(modifier)
                .modifiedOn(convertToMMDDYYYY(syllabus.getModifiedDate().toString().split(" ")[0]))
                .syllabusObjectiveList(outputCodeList)
                .dayList(dayDTOList)
                .assignmentLab(syllabus.getAssignmentLab())
                .conceptLecture(syllabus.getConceptLecture())
                .guideReview(syllabus.getGuideReview())
                .testQuiz(syllabus.getTestQuiz())
                .exam(syllabus.getExam())
                .quiz(syllabus.getQuiz())
                .assignment(syllabus.getAssignment())
                .final_(syllabus.getFinal_())
                .finalTheory(syllabus.getFinalTheory())
                .finalPractice(syllabus.getFinalPractice())
                .gpa(syllabus.getGpa())
                .deleted(syllabus.isDeleted())
                .build();

    }

    public String convertToMMDDYYYY(String dateStr) {
        DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate date = LocalDate.parse(dateStr, inputFormatter);

        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
        String formattedDate = date.format(outputFormatter);
        return formattedDate;
    }

    public String processString(String input) {
        if (isFloatingPoint(input)) {
            // If the string is a number with a dot
            String[] parts = input.split("\\.");
            return parts[0]; // Get the first part of the split string array
        } else {
            // If the string is a number without a dot
            return input;
        }
    }

    private boolean isNumberOrFloatingPoint(String str) {
        // Use a regular expression to check if the string is a number or a floating-point number
        return Pattern.matches("\\d+(\\.\\d+)?", str);
    }

    private boolean isFloatingPoint(String str) {
        // Use a regular expression to check if the string is a number or a floating-point number
        return Pattern.matches("\\d+\\.\\d+?", str);
    }


}

