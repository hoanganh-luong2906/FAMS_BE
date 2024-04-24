package com.example.FAMS.controllers;

import com.example.FAMS.dto.requests.Calendar.UpdateCalendarRequest;
import com.example.FAMS.dto.requests.ClassRequest.CreateClassDTO;
import com.example.FAMS.dto.requests.ClassRequest.UpdateClass3Request;
import com.example.FAMS.dto.requests.ClassRequest.UpdateClassDTO;
import com.example.FAMS.dto.responses.Class.*;
import com.example.FAMS.dto.responses.ResponseObject;
import com.example.FAMS.dto.responses.UpdateCalendarResponse;
import com.example.FAMS.models.Class;
import com.example.FAMS.repositories.UserClassSyllabusDAO;
import com.example.FAMS.services.ClassService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/class")
@PreAuthorize("hasRole('CLASS_ADMIN') or hasRole('SUPER_ADMIN') or hasRole('TRAINER')")
@Log4j2
public class ClassController {

    @Autowired
    ClassService classService;

    @Autowired
    UserClassSyllabusDAO userClassSyllabusDAO;

//    @GetMapping
//    @PreAuthorize("hasAuthority('class:read')")
//    public ResponseEntity<List<GetClassesResponse>> getClasses() {
//        return ResponseEntity.status(HttpStatus.OK).body(classService.getClasses());
//    }

    @GetMapping
    @PreAuthorize("hasAuthority('class:read')")
    public ResponseEntity<List<ClassDetailResponse>> getClasses() {
        return ResponseEntity.status(HttpStatus.OK).body(classService.getClasses());
    }

    @GetMapping("/page")
    @PreAuthorize("hasAuthority('class:read')")
    public ResponseEntity<GetClassesByPage> getClasses(@RequestParam int amount, @RequestParam int pageNumber) {
        GetClassesByPage classList = classService.paging(amount, pageNumber);

        if (classList.getStatus() > 0) {
            return ResponseEntity.status(400).body(classList);
        } else if (classList.getStatus() < 0) {
            return ResponseEntity.status(500).body(classList);
        }
        return ResponseEntity.status(HttpStatus.OK).body(classList);
    }

//    @GetMapping("/detail")
//    @PreAuthorize("hasAuthority('class:read')")
//    public ResponseEntity<List<Class>> getDetailClasses() {
//        return ResponseEntity.ok(classService.getDetailClasses());
//    }

    @PostMapping("/create")
    @PreAuthorize("hasAuthority('class:create')")
    public ResponseEntity<CreateClassResponse> createClass(
            @RequestBody CreateClassDTO createClassDTO,
            Authentication authentication) {
        // Xử lý tạo lớp học dựa trên thông tin từ request
        CreateClassResponse result = classService.createClass(createClassDTO, authentication);
        if (result.getStatus() == 0) {
            return ResponseEntity.status(200).body(result);
        } else if (result.getStatus() > 0) {
            return ResponseEntity.status(418).body(result);
        }
        return ResponseEntity.status(500).body(result);
    }

    @PutMapping("/update/{id}")
    @PreAuthorize("hasAuthority('class:update')")
    public ResponseEntity<UpdateClassResponse> updateClass(@RequestBody UpdateClassDTO updateClassRequest, @PathVariable("id") String classCode) {
        UpdateClassResponse updatedClass = classService.updateClass(updateClassRequest, classCode);
        if (updatedClass.getStatus() == 0) {
            return ResponseEntity.ok(updatedClass);
        } else if (updatedClass.getStatus() > 0) {
            return ResponseEntity.status(400).body(updatedClass);
        } else {
            return ResponseEntity.status(500).body(updatedClass);
        }
    }

    @GetMapping("/search/{classId}")
    public ResponseEntity<?> getClassById(@PathVariable String classId) {
        Class classInfo = classService.getClassById(classId);
        if (classInfo != null) {
            return ResponseEntity.ok(classInfo);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/filterClass")
    public ResponseEntity<ResponseObject> getFilterClass() {
        return classService.getFilter();
    }

    @PutMapping("/deactivate/{id}")
    public ResponseEntity<DeactivateClassResponse> deactivateClass(@PathVariable("id") String classCode) {
        return classService.deactivateClass(classCode);
    }

    @GetMapping("/detail/{id}")
    @PreAuthorize("hasAuthority('class:read')")
    public ResponseEntity<ClassDetailResponse> getClassDetail(@PathVariable("id") String classCode) throws InterruptedException {
        return classService.getClassDetail(classCode);
    }

    @GetMapping("/listClassPagination")
    public ResponseEntity<?> getAllPagination(Pageable pageable) {
        return ResponseEntity.ok(classService.getAllPagenation(pageable));

    }

    @GetMapping("/sortCalendar")
    @PreAuthorize("hasAuthority('class:read')")
    public ResponseEntity<?> sortCalendar() {
        return ResponseEntity.ok(classService.CalendarSort());

    }

    @GetMapping("/view-calendar/day")
    public ResponseEntity<ResponseObject> getDayCalendar(@RequestParam(name = "currentDate") String currentDate) throws ParseException {
        Date current = new SimpleDateFormat("yyyy-MM-dd").parse(currentDate);
        return classService.getDayCalendar(current);
    }

    @GetMapping("/view-calendar/week")
    public ResponseEntity<ResponseObject> getWeekCalendar(
            @RequestParam(name = "startDate") String startDate,
            @RequestParam(name = "endDate") String endDate
    ) throws ParseException {
        Date start = new SimpleDateFormat("yyyy-MM-dd").parse(startDate);
        Date end = new SimpleDateFormat("yyyy-MM-dd").parse(endDate);
        return classService.getWeekCalendar(start, end);
    }

    @PutMapping("/update-calendar")
    @PreAuthorize("hasAuthority('class:update')")
    public UpdateCalendarResponse updateClassLearningDay(@RequestBody UpdateCalendarRequest request) throws ParseException {
        return classService.updateClassLearningDay(request);
    }

    @PostMapping("/updateClass3")
    public UpdateClass3Response updateClass3(@RequestBody UpdateClass3Request updateClass3Request) {
        return classService.updateClass3(updateClass3Request);
    }

    @DeleteMapping("/deleteAll")
    public ResponseEntity<ResponseObject> deleteAllTrainingProgramSyllabus() {
        return classService.deleteAllTrainingProgramSyllabus();
    }
}
