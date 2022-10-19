package com.example.springbootmongodb.service;

import com.example.springbootmongodb.collection.Photo;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface PhotoService {
     default String addPhoto(String originalFilename, MultipartFile image) throws IOException {
         return originalFilename;
     }

    Photo getPhoto(String id);
}
