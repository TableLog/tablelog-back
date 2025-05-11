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
import java.util.Objects;
import java.util.List;
import java.util.ArrayList;

@Component
@RequiredArgsConstructor
public class S3Provider {

    private final S3Client s3Client;
    public static final String SEPARATOR = "/";
    @Value("${spring.cloud.aws.s3.bucket}")
    private String bucket;
    public final String url = "https://tablelog.s3.ap-northeast-2.amazonaws.com/";

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
        if (multipartFile.isEmpty()) {
            System.out.println("📦 파일이 비었나요? : " + multipartFile.isEmpty());
            return "";
        }
        System.out.println("📄 파일 이름: " + multipartFile.getOriginalFilename());
        System.out.println("📦 업로드된 파일 Content-Type: " + multipartFile.getContentType());

        if (Objects.equals(multipartFile.getContentType(), "image/png")
                || Objects.equals(multipartFile.getContentType(), "image/jpeg")) {

            String fileType = switch (multipartFile.getContentType()) {
                case "image/png" -> ".png";
                case "image/jpeg" -> ".jpg";
                default -> throw new IllegalStateException(
                        "Unexpected value: " + multipartFile.getContentType());
            };

            return UUID.randomUUID() + fileType;

        } else {
            throw new IllegalArgumentException("잘못된 파일 형식입니다: " + multipartFile.getContentType());
        }
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
        if (multipartFile == null || multipartFile.isEmpty()) {
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
    /*TODO - 다중 이미지 처리 로직 구현
    1. 리스트 타입으로 들어온다
    2. 리스트의 길이를 구해서 처리한다
    */
    public List<String> updateImages(List<MultipartFile> multipartFiles, String folderName) throws IOException {
        if (multipartFiles == null || multipartFiles.isEmpty()) {
            return new ArrayList<>();
        }
        List<String> imageUrls = new ArrayList<>();
        String encodedFolderName = URLEncoder.encode(folderName, StandardCharsets.UTF_8);
        for (MultipartFile file : multipartFiles) {
            if (!file.isEmpty()) {
                String newFileName = originalFileName(file);
                String key = folderName + SEPARATOR + newFileName;
                saveFile(file, key);
                imageUrls.add(url + encodedFolderName + SEPARATOR + newFileName);
            }
        }
        return imageUrls;
    }

}
