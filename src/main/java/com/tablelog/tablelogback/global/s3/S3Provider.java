package com.tablelog.tablelogback.global.s3;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class S3Provider {

    private final S3Client s3Client;
    public static final String SEPARATOR = "/";

    @Value("${spring.cloud.aws.s3.bucket}")
    private String bucket;

    // S3 퍼블릭 URL prefix
    public final String url = "https://tablelog.s3.ap-northeast-2.amazonaws.com/";

    /**
     * 파일을 S3에 저장 (key는 호출부에서 전달한 값 그대로 사용)
     */
    public String saveFile(MultipartFile multipartFile, String imageName) throws IOException {
        if (multipartFile == null || multipartFile.isEmpty()) return null;
        if (imageName == null || imageName.isBlank()) return null;

        PutObjectRequest putRequest = PutObjectRequest.builder()
                .bucket(bucket)
                .key(imageName)
                .contentType(multipartFile.getContentType())
                .build();

        s3Client.putObject(
                putRequest,
                RequestBody.fromInputStream(multipartFile.getInputStream(), multipartFile.getSize())
        );

        return getImagePath(imageName);
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

    /**
     * S3 "폴더" 생성 (더미 객체 업로드)
     */
    public void createFolder(String folderName) {
        String key = folderName.endsWith(SEPARATOR) ? folderName : folderName + SEPARATOR;

        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .build();

        s3Client.putObject(request, RequestBody.empty());
    }

    /**
     * S3에서 파일 삭제
     */
    public void delete(String imageNameOrUrl) {
        if (imageNameOrUrl == null) return;

        String key = extractKeyFromUrl(imageNameOrUrl);
        if (key == null || key.isBlank()) return;

        DeleteObjectRequest deleteRequest = DeleteObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .build();
        s3Client.deleteObject(deleteRequest);
    }

    /**
     * 이미지 업데이트 (S3에 저장)
     * @param imageName 기존 이미지 URL (삭제용)
     * @param folderName 폴더명
     * @param multipartFile 새 이미지 파일
     * @return 업데이트된 이미지 URL
     */
    public String updateImage(String imageName, String folderName, MultipartFile multipartFile)
            throws IOException {
        if (imageName == null && (multipartFile == null || multipartFile.isEmpty())) return null;

        if (multipartFile == null || multipartFile.isEmpty()) {
            // 삭제
            if (imageName != null) delete(imageName);
            return null;
        }

        // 기존 이미지 삭제 후 새 이미지 업로드
        if (imageName != null) delete(imageName);

        String newFileName = originalFileName(multipartFile);
        String key = folderName + SEPARATOR + newFileName;
        return saveFile(multipartFile, key);
    }

    /**
     * 이미지 경로를 URL로 변환
     */
    public String getImagePath(String objectKey) {
        if (objectKey == null || objectKey.isBlank()) return null;
        if (objectKey.startsWith("http://") || objectKey.startsWith("https://")) return objectKey;
        return url + objectKey;
    }

    /**
     * URL에서 S3 key 추출
     */
    private String extractKeyFromUrl(String imageUrl) {
        if (imageUrl == null) return "";

        // 로컬 URL이면 S3 삭제 대상이 아님
        if (imageUrl.startsWith("/uploads/")) {
            return "";
        }

        if (imageUrl.startsWith(url)) {
            String encodedKey = imageUrl.substring(url.length());
            return URLDecoder.decode(encodedKey, StandardCharsets.UTF_8);
        }

        return imageUrl;
    }

    /**
     * 다중 이미지 업로드 (S3에 저장)
     * @param multipartFiles 업로드할 파일 목록
     * @param folderName 폴더명
     * @return 업로드된 이미지 URL 목록
     */
    public List<String> updateImages(List<MultipartFile> multipartFiles, String folderName) throws IOException {
        if (multipartFiles == null || multipartFiles.isEmpty()) {
            return new ArrayList<>();
        }
        List<String> imageUrls = new ArrayList<>();
        for (MultipartFile file : multipartFiles) {
            if (!file.isEmpty()) {
                String newFileName = originalFileName(file);
                String key = folderName + SEPARATOR + newFileName;
                System.out.println(key);
                imageUrls.add(saveFile(file, key));
            }
        }
        return imageUrls;
    }

}
