package com.example.FAMS.services;

import com.amazonaws.HttpMethod;
import org.springframework.scheduling.annotation.Async;

public interface FileService {


    String generateUrl(String fileName, HttpMethod httpMethod);

    @Async
    String findByName(String fileName);

    @Async
    String save(String extension);

}
