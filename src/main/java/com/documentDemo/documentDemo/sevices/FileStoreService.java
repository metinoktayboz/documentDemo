package com.documentDemo.documentDemo.sevices;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.documentDemo.documentDemo.config.FileStorageProps;
import com.documentDemo.documentDemo.models.AllowedTypes;
import com.documentDemo.documentDemo.models.UploadedFile;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;

@Service
public class FileStoreService {
    private static final byte[] secret = Base64.getDecoder().decode("AZBflz7mFtFuY2dRJOdXLhFddeezyNCY");

    Algorithm algorithm = Algorithm.HMAC256(secret);
    private final Path fileStorageLocation;
    HashMap<String,UploadedFile> hashMap = new HashMap<>();
    HashMap<String,MultipartFile> hashMapFile = new HashMap<>();
    AllowedTypes allowedTypes = new AllowedTypes(new ArrayList<>());


    @Autowired
    public FileStoreService(FileStorageProps.FileStorageProperties fileStorageProperties) {
        this.fileStorageLocation = Paths.get(fileStorageProperties.getUploadDir())
                .toAbsolutePath().normalize();

        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (Exception ex) {
            //throw new FileStorageException("Could not create the directory where the uploaded files will be stored.", ex);
        }
    }

    public String storeFile(MultipartFile file) throws Exception {
        // Normalize file name
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());

        try {
            // Check if the file's name contains invalid characters
            if(fileName.contains("..")) {
                //throw new FileStorageException("Sorry! Filename contains invalid path sequence " + fileName);
            }

            // Copy file to the target location (Replacing existing file with the same name)
            Path targetLocation = this.fileStorageLocation.resolve(fileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            return fileName;
        } catch (IOException ex) {
            throw new Exception(ex);
        }
    }
    public void saveFile(MultipartFile file, int id){
        hashMapFile.put(id+"#2",file);
    }
    public void saveFileProps(UploadedFile uploadedFile, int id){
        hashMap.put(id+"#1",uploadedFile);
    }

    public List<UploadedFile> getAll(int id){
        List<UploadedFile> uploadedFiles = new ArrayList<>();
        for(int i=0; i<=id;i++){
            if(hashMap.get(i+"#1")!=null)
            uploadedFiles.add(hashMap.get(i+"#1"));
        }
        return uploadedFiles;
    }

    public void deleteFile(int id){
        hashMap.remove(id+"#1");
    }

    public boolean verifyToken(String token) {
        try {
            JWTVerifier verifier = JWT.require(algorithm)
                    .build();
            DecodedJWT jwt = verifier.verify(token);
            return true;

        } catch (JWTVerificationException exception){

            return false;
        }
    }


    public boolean verifySize(double fileSize){
        return !(fileSize > 5);
    }

    public boolean verifyFileType(String fileName){
        String ext1 = getExtension(fileName);
        return (allowedTypes.getAllowedTypes().contains(ext1));
    }

    public String getExtension(String fileName){
        return FilenameUtils.getExtension(fileName);
    }

    public UploadedFile getFileProps(int id) {
        if (hashMap.get(id+"#1")!=null)
       return hashMap.get(id+"#1");
        else return null;
    }
    public MultipartFile getFile(int id) {
        if (hashMapFile.get(id+"#2")!=null)
            return hashMapFile.get(id+"#2");
        else return null;
    }
}
