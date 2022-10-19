package com.example.springbootmongodb.service;

import com.example.springbootmongodb.collection.Photo;
import com.example.springbootmongodb.repository.PhotoRepository;
import org.bson.BsonBinarySubType;
import org.bson.types.Binary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class PhotoServiceImpl implements PhotoService{

    @Override
    public String addPhoto(String originalFilename, MultipartFile image) throws IOException {
        Photo photo = new Photo();
        photo.setTitle(originalFilename);
        photo.setPhoto(new Binary(BsonBinarySubType.BINARY,image.getBytes()));

        return photoRepository.save(photo).getId();
    }

    @Override
    public Photo getPhoto(String id) {
        return photoRepository.findById(id).get();
    }

    @Autowired
    private PhotoRepository photoRepository;

    //@Override
    //public String addPhoto(String originalFilename, MultipartFile image) {

        //Photo photo
                //= new Photo();
        // photo.setTitle(originalFilename)
        //return originalFilename;
    //}
}
