package com.tablelog.tablelogback.global.s3;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class S3Provider {

    private final S3Client s3Client;
    public static final String SEPARATOR = "/";
    @Value("${spring.cloud.aws.s3.bucket}")
    private String bucket;
    public final String url = "https://onceclick.s3.ap-northeast-2.amazonaws.com/";

    public String saveFile(MultipartFile multipartFile, String imageName) throws IOException {
        if (multipartFile.isEmpty()) return null;

        PutObjectRequest putRequest = PutObjectRequest.builder()
                .bucket(bucket)
                .key(imageName)
                .contentType(multipartFile.getContentType())
                .build();

        s3Client.putObject(putRequest, RequestBody.fromInputStream(multipartFile.getInputStream(), multipartFile.getSize()));

        return url + imageName;
    }

    public String originalFileName(MultipartFile multipartFile) {
        if (multipartFile.isEmpty()) return "";

        String fileType = switch (multipartFile.getContentType()) {
            case "image/png" -> ".png";
            case "image/jpeg" -> ".jpg";
            default -> throw new IllegalArgumentException("잘못된 파일 형식입니다");
        };
        return UUID.randomUUID() + fileType;
    }

    public void createFolder(String folderName) {
        String key = folderName.endsWith(SEPARATOR) ? folderName : folderName + SEPARATOR;

        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .build();

        s3Client.putObject(request, RequestBody.empty());
    }

    public void delete(String imageName) {
        if (imageName == null) return;

        DeleteObjectRequest deleteRequest = DeleteObjectRequest.builder()
                .bucket(bucket)
                .key(imageName)
                .build();

        s3Client.deleteObject(deleteRequest);
    }
    public String updateImage(String imageName, String folderName, MultipartFile multipartFile)
            throws IOException {
        if (imageName == null && multipartFile.isEmpty()) return null;
        String encodedFolderName = URLEncoder.encode(folderName, StandardCharsets.UTF_8);
        if (multipartFile.isEmpty()) {
            // 삭제
            String key = extractKeyFromUrl(imageName);
            delete(key);
            return null;
        } else if (imageName == null) {
            // 신규 업로드
            String newFileName = originalFileName(multipartFile);
            String key = folderName + SEPARATOR + newFileName;

            saveFile(multipartFile, key);
            return url + encodedFolderName + SEPARATOR + newFileName;
        } else {
            // 기존 이미지 삭제 후 새 이미지 업로드
            String oldKey = extractKeyFromUrl(imageName);
            delete(oldKey);

            String newFileName = originalFileName(multipartFile);
            String key = folderName + SEPARATOR + newFileName;

            saveFile(multipartFile, key);
            return url + encodedFolderName + SEPARATOR + newFileName;
        }
    }
    public String getImagePath(String objectKey) {
        return url + objectKey;
    }

    private String extractKeyFromUrl(String imageUrl) {
        return imageUrl.replace(url, "");
    }
}
