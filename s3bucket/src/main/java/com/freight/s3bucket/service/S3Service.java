package com.freight.s3bucket.service;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.ListObjectsV2Request;
import com.amazonaws.services.s3.model.ListObjectsV2Result;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;

@Service
public class S3Service {
    @Value("${cloud.aws.bucket.name}")
    private String bucketName;
    @Autowired
    private AmazonS3 s3Client;

    public void uploadFile(String username, MultipartFile file) throws Exception {
        File localFile = multipartToFile(file);
        try{
            String filePath = username + "/" + file.getOriginalFilename();
            s3Client.putObject(new PutObjectRequest(bucketName, filePath, localFile));
        } catch (Exception e) {
            throw new Exception("Error uploading file to S3", e);
        }finally {
            localFile.delete();
        }
    }

    public StreamingResponseBody downloadFile(String username, String filename) throws Exception {
        String filepath = username + "/" + filename;
        try{
            if(SearchUser(username, filename)){
                S3Object s3Object = s3Client.getObject(bucketName, filepath);
                if(s3Object == null){
                    throw new Exception("File not found");
                }
                S3ObjectInputStream inputStream = s3Object.getObjectContent();
                StreamingResponseBody response = outputStream -> {
                    byte[] bytes = new byte[1024];
                    int read;
                    while((read = inputStream.read(bytes)) != -1){
                        outputStream.write(bytes, 0, read);
                    }
                    inputStream.close();
                };
                return response;
            } else {
                return null;
            }
        } catch (Exception e){
            throw new Exception("Error while downloading file");
        }
    }

    private File multipartToFile(MultipartFile multipart) throws Exception {
        File file = new File(multipart.getOriginalFilename());
        try{
            FileOutputStream os = new FileOutputStream(file);
            os.write(multipart.getBytes());
            os.close();
        } catch (Exception e) {
            throw new Exception("Error converting multipart to file", e);
        }
        return file;
    }

    private boolean SearchUser(String username, String filename) throws Exception {
        try{
            String prefix = username + "/" + filename;
            ListObjectsV2Request request = new ListObjectsV2Request()
                                                .withBucketName(bucketName)
                                                .withPrefix(prefix);
            ListObjectsV2Result result = s3Client.listObjectsV2(request);
            if(result.getKeyCount() != 0)   return true;
            return false;
        } catch (Exception e) {
            throw new Exception("Error searching user's files");
        }
    }
    
    public List<String> listBuckets() {
        List<Bucket> buckets = s3Client.listBuckets();
        List<String> bucketsName = buckets.stream().map(Bucket -> Bucket.getName()).toList();
        return bucketsName;
    }
}