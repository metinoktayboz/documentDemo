package com.documentDemo.documentDemo.controller;

import com.documentDemo.documentDemo.models.UploadedFile;
import com.documentDemo.documentDemo.sevices.FileStoreService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping("/api/docs")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class FileController {

    ObjectMapper mapper = new ObjectMapper();
    ObjectWriter objectWriter = mapper.writer().withDefaultPrettyPrinter();
    int id=0;

    @Autowired
    private FileStoreService fileStorageService;


    @ApiOperation("Upload File")
    @ApiResponse(code = 200, message = "ok", response = UploadedFile.class)
    @PostMapping(value= "/uf", produces = "application/json;charset=UTF-8")
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file,
                                        @RequestHeader(name="Token") String token) throws Exception {
        if(!fileStorageService.verifyToken(token)){
            return new ResponseEntity<>(objectWriter.writeValueAsString("Invalid Token"),HttpStatus.BAD_REQUEST);
        }
        double fileSize = (double) file.getSize()/(1024*1024);

        if(!fileStorageService.verifySize(fileSize)){
            return new ResponseEntity<>(objectWriter.writeValueAsString("File size should be <= 5 MB"),HttpStatus.BAD_REQUEST);
        }

        String fileName = fileStorageService.storeFile(file);
        if (!fileStorageService.verifyFileType(fileName)){
            return new ResponseEntity<>(objectWriter.writeValueAsString("File type is invalid"),HttpStatus.BAD_REQUEST);
        }

        String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/downloadFile/")
                .path(fileName)
                .toUriString();
        UploadedFile uploadedFile = new UploadedFile(fileName, fileDownloadUri,
                fileStorageService.getExtension(fileName), fileSize,id);


        fileStorageService.saveFileProps(uploadedFile,id);
        fileStorageService.saveFile(file,id++);


        return new ResponseEntity<>(uploadedFile,HttpStatus.OK);
    }

    @ApiOperation("Get All Files")
    @ApiResponse(code = 200, message = "ok", response = UploadedFile.class)
    @GetMapping(value= "/ga", produces = "application/json;charset=UTF-8")
    public ResponseEntity<?> getAll(@RequestHeader(name="Token") String token) throws Exception {
        if(!fileStorageService.verifyToken(token)){
            return new ResponseEntity<>(objectWriter.writeValueAsString("Invalid Token"),HttpStatus.BAD_REQUEST);
        }


        return new ResponseEntity<>(fileStorageService.getAll(id),HttpStatus.OK);
    }

    @ApiOperation("Delete File")
    @ApiResponse(code = 200, message = "ok", response = UploadedFile.class)
    @DeleteMapping(value= "/df/{idd}", produces = "application/json;charset=UTF-8")
    public ResponseEntity<?> deleteFile(@RequestHeader(name="Token") String token,
                                        @PathVariable("idd") final int idd) throws Exception {
        if(!fileStorageService.verifyToken(token)){
            return new ResponseEntity<>(objectWriter.writeValueAsString("Invalid Token"),HttpStatus.BAD_REQUEST);
        }
        fileStorageService.deleteFile(idd);

        return new ResponseEntity<>(fileStorageService.getAll(id),HttpStatus.OK);
    }

    @ApiOperation("Download File")
    @ApiResponse(code = 200, message = "ok", response = UploadedFile.class)
    @GetMapping(value= "/gf/{idd}", produces = "application/json;charset=UTF-8")
    public ResponseEntity<?> getFile(@RequestHeader(name="Token") String token,
                                        @PathVariable("idd") final int idd) throws Exception {
        if(!fileStorageService.verifyToken(token)){
            return new ResponseEntity<>(objectWriter.writeValueAsString("Invalid Token"),HttpStatus.BAD_REQUEST);
        }

        UploadedFile uploadedFile =fileStorageService.getFileProps(idd);
        if(uploadedFile!=null){
            byte[] decodedBytes = fileStorageService.getFile(idd).getBytes();
            HttpHeaders header = new HttpHeaders();
            header.setContentType(MediaType.parseMediaType ("application/" + uploadedFile.getFileType()));



            header.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename="+uploadedFile.getFileName());
            header.add("Cache-Control", "no-cache, no-store, must-revalidate");
            header.add("Pragma", "no-cache");
            header.add("Expires", "0");


            Resource resource = new ByteArrayResource(decodedBytes);


            return ResponseEntity.ok()
                    .headers(header)
                    .contentType(MediaType.parseMediaType("application/"+uploadedFile.getFileType()))
                    .body(resource);
        }
        fileStorageService.deleteFile(idd);

        return new ResponseEntity<>(fileStorageService.getAll(id),HttpStatus.OK);
    }
}
