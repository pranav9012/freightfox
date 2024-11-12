package com.freight.s3bucket.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import com.freight.s3bucket.service.S3Service;


@RestController
public class S3Controller {
    @Autowired
    private S3Service s3Service;
    @PostMapping("/upload")
    public ResponseEntity<Map<String, Object>> uploadFile(@RequestParam MultipartFile file, @RequestParam String username) {
        Map<String, Object> res = new HashMap<>();
        try {
            s3Service.uploadFile(username, file);
            res.put("message", "File uploaded successfully");
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            res.put("message", "File upload failed");
            return ResponseEntity.internalServerError().body(res);
        }
    }

    @GetMapping("/download")
    public ResponseEntity<StreamingResponseBody> downloadFile(@RequestParam String filename, @RequestParam String username) {
        try {
            StreamingResponseBody resource = s3Service.downloadFile(username, filename);
            if(resource != null){
                return ResponseEntity.ok()
                                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                                    .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // @GetMapping("/buckets")
    // public ResponseEntity<List<String>> listBuckets() {
    //     try{
    //         List<String> res = s3Service.listBuckets();
    //         return ResponseEntity.ok(res);
    //     } catch (Exception e){
    //         return ResponseEntity.status(500).body(List.of("error" + e.getMessage()));
    //     }
    // }

    // @GetMapping("/search")
    // public ResponseEntity<Map<String, Object>> searchFile(@RequestParam String username, @RequestParam String filename) {
    //     Map<String, Object> res = new HashMap<>();
    //     try{
    //         List<String> users = s3Service.SearchUser(username, filename);
    //         res.put("users", users);
    //         return ResponseEntity.ok(res);
    //     } catch (Exception e){
    //         res.put("message", "Error searching for files");
    //         return ResponseEntity.status(500).body(res);
    //     }

    // }
    
    
}
