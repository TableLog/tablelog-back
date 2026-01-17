package com.tablelog.tablelogback.global.s3;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
// S3 관련 import 주석 처리
// import software.amazon.awssdk.core.sync.RequestBody;
// import software.amazon.awssdk.services.s3.S3Client;
// import software.amazon.awssdk.services.s3.model.*;

import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;
import java.util.Objects;
import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class S3Provider {

    // S3 관련 필드 주석 처리
    // private final S3Client s3Client;
    public static final String SEPARATOR = "/";
    private static final String PARENT_FOLDER = "tablelog-images";
    private static final String ROOT_FOLDER = "tablelog";
    private static final String FULL_ROOT_PATH = PARENT_FOLDER + SEPARATOR + ROOT_FOLDER;
    // @Value("${spring.cloud.aws.s3.bucket}")
    // private String bucket;
    
    // 로컬 파일 시스템 경로 및 URL
    private static final String UPLOAD_DIR = PARENT_FOLDER + File.separator + ROOT_FOLDER;
    public final String url = "/uploads/tablelog/";

    /**
     * 파일을 로컬 파일 시스템에 저장 (tablelog-images/tablelog 폴더 내부에 저장)
     * @param multipartFile 업로드할 파일
     * @param imageName 저장할 파일 경로 (tablelog-images/tablelog/ prefix가 자동으로 추가됨)
     * @return 저장된 파일의 URL
     */
    public String saveFile(MultipartFile multipartFile, String imageName) throws IOException {
        if (multipartFile.isEmpty()) return null;

        // tablelog-images/tablelog 폴더가 없으면 생성
        ensureTablelogFolderExists();

        // 경로 정규화 (SEPARATOR를 File.separator로 변환)
        String normalizedPath = normalizePath(imageName);
        
        // 최종 저장 경로 생성
        Path uploadPath = getUploadPath();
        Path filePath = uploadPath.resolve(normalizedPath);
        
        // 상위 디렉토리 생성
        Files.createDirectories(filePath.getParent());
        
        // 파일 저장
        Files.copy(multipartFile.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
        
        // URL 반환 (SEPARATOR를 /로 변환)
        String urlPath = normalizedPath.replace(File.separator, SEPARATOR);
        return url + urlPath;
    }

    /**
     * 이전 S3 saveFile 메서드 (주석 처리)
     */
    /*
    public String saveFile(MultipartFile multipartFile, String imageName) throws IOException {
        if (multipartFile.isEmpty()) return null;

        // tablelog-back/tablelog 폴더가 없으면 생성
        ensureTablelogFolderExists();

        // tablelog-back/tablelog/ prefix가 없으면 추가
        String finalKey;
        if (imageName.startsWith(FULL_ROOT_PATH + SEPARATOR)) {
            finalKey = imageName;
        } else if (imageName.startsWith(ROOT_FOLDER + SEPARATOR)) {
            // tablelog/로 시작하는 경우 tablelog-back/tablelog/로 변경
            finalKey = PARENT_FOLDER + SEPARATOR + imageName;
        } else if (imageName.startsWith(PARENT_FOLDER + SEPARATOR)) {
            // tablelog-back/로 시작하는 경우 tablelog-back/tablelog/로 변경
            finalKey = FULL_ROOT_PATH + SEPARATOR + imageName.substring(PARENT_FOLDER.length() + 1);
        } else {
            // 그 외의 경우 tablelog-back/tablelog/ prefix 추가
            finalKey = FULL_ROOT_PATH + SEPARATOR + imageName;
        }

        PutObjectRequest putRequest = PutObjectRequest.builder()
                .bucket(bucket)
                .key(finalKey)
                .contentType(multipartFile.getContentType())
                .build();

        s3Client.putObject(putRequest, RequestBody.fromInputStream(multipartFile.getInputStream(), multipartFile.getSize()));

        return url + finalKey;
    }
    */

    /**
     * 이전 saveFile 메서드 (주석 처리)
     */
    /*
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
    */

    /**
     * tablelog-images/tablelog 폴더가 존재하는지 확인하고 없으면 생성 (로컬 파일 시스템)
     */
    private void ensureTablelogFolderExists() {
        try {
            Path uploadPath = getUploadPath();
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
        } catch (Exception e) {
            throw new RuntimeException("폴더 생성 실패: " + UPLOAD_DIR, e);
        }
    }

    /**
     * 업로드 경로 가져오기 (서버 실행 위치 기준)
     */
    private Path getUploadPath() {
        // 현재 작업 디렉토리 (서버 실행 위치)
        String currentDir = System.getProperty("user.dir");
        return Paths.get(currentDir, UPLOAD_DIR);
    }

    /**
     * 경로 정규화 (tablelog-images/tablelog/ prefix 추가)
     */
    private String normalizePath(String imageName) {
        String normalized;
        if (imageName.startsWith(FULL_ROOT_PATH + SEPARATOR) || imageName.startsWith(FULL_ROOT_PATH + File.separator)) {
            normalized = imageName.substring(FULL_ROOT_PATH.length() + 1);
        } else if (imageName.startsWith(ROOT_FOLDER + SEPARATOR) || imageName.startsWith(ROOT_FOLDER + File.separator)) {
            normalized = imageName.substring(ROOT_FOLDER.length() + 1);
        } else if (imageName.startsWith(PARENT_FOLDER + SEPARATOR) || imageName.startsWith(PARENT_FOLDER + File.separator)) {
            String subPath = imageName.substring(PARENT_FOLDER.length() + 1);
            if (subPath.startsWith(ROOT_FOLDER + SEPARATOR) || subPath.startsWith(ROOT_FOLDER + File.separator)) {
                normalized = subPath.substring(ROOT_FOLDER.length() + 1);
            } else {
                normalized = ROOT_FOLDER + File.separator + subPath;
            }
        } else {
            normalized = imageName;
        }
        
        // SEPARATOR를 File.separator로 변환
        return normalized.replace(SEPARATOR, File.separator);
    }

    /**
     * 이전 S3 ensureTablelogFolderExists 메서드 (주석 처리)
     */
    /*
    private void ensureTablelogFolderExists() {
        try {
            // 먼저 tablelog-back 폴더 확인 및 생성
            String parentFolderKey = PARENT_FOLDER + SEPARATOR;
            ListObjectsV2Request parentListRequest = ListObjectsV2Request.builder()
                    .bucket(bucket)
                    .prefix(parentFolderKey)
                    .maxKeys(1)
                    .build();
            
            ListObjectsV2Response parentResponse = s3Client.listObjectsV2(parentListRequest);
            if (parentResponse.keyCount() == 0) {
                createFolder(PARENT_FOLDER);
            }
            
            // tablelog-back/tablelog 폴더 확인 및 생성
            String folderKey = FULL_ROOT_PATH + SEPARATOR;
            ListObjectsV2Request listRequest = ListObjectsV2Request.builder()
                    .bucket(bucket)
                    .prefix(folderKey)
                    .maxKeys(1)
                    .build();
            
            ListObjectsV2Response response = s3Client.listObjectsV2(listRequest);
            
            // 폴더가 없으면 생성 (객체가 0개이면 폴더가 없음)
            if (response.keyCount() == 0) {
                createFolder(FULL_ROOT_PATH);
            }
        } catch (Exception e) {
            // 에러 발생 시에도 폴더 생성 시도
            try {
                createFolder(PARENT_FOLDER);
                createFolder(FULL_ROOT_PATH);
            } catch (Exception ex) {
                // 폴더 생성 실패는 무시 (이미 존재할 수 있음)
            }
        }
    }
    */

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
     * 폴더 생성 (로컬 파일 시스템)
     */
    public void createFolder(String folderName) {
        try {
            Path folderPath = getUploadPath().resolve(folderName.replace(SEPARATOR, File.separator));
            Files.createDirectories(folderPath);
        } catch (IOException e) {
            throw new RuntimeException("폴더 생성 실패: " + folderName, e);
        }
    }

    /**
     * 이전 S3 createFolder 메서드 (주석 처리)
     */
    /*
    public void createFolder(String folderName) {
        String key = folderName.endsWith(SEPARATOR) ? folderName : folderName + SEPARATOR;

        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .build();

        s3Client.putObject(request, RequestBody.empty());
    }
    */

    /**
     * 로컬 파일 시스템에서 파일 삭제 (tablelog-images/tablelog 폴더 내부 파일 삭제)
     * @param imageName 삭제할 파일 경로 또는 URL
     */
    public void delete(String imageName) {
        if (imageName == null) return;
        
        try {
            // URL에서 경로 추출
            String filePath = extractKeyFromUrl(imageName);
            String normalizedPath = normalizePath(filePath);
            
            Path uploadPath = getUploadPath();
            Path fileToDelete = uploadPath.resolve(normalizedPath);
            
            if (Files.exists(fileToDelete)) {
                Files.delete(fileToDelete);
            }
        } catch (IOException e) {
            throw new RuntimeException("파일 삭제 실패: " + imageName, e);
        }
    }

    /**
     * 이전 S3 delete 메서드 (주석 처리)
     */
    /*
    public void delete(String imageName) {
        if (imageName == null) return;
        
        // tablelog-back/tablelog/ prefix 처리
        String finalKey;
        if (imageName.startsWith(FULL_ROOT_PATH + SEPARATOR)) {
            finalKey = imageName;
        } else if (imageName.startsWith(ROOT_FOLDER + SEPARATOR)) {
            // tablelog/로 시작하는 경우 tablelog-back/tablelog/로 변경
            finalKey = PARENT_FOLDER + SEPARATOR + imageName;
        } else if (imageName.startsWith(PARENT_FOLDER + SEPARATOR)) {
            // tablelog-back/로 시작하는 경우 tablelog-back/tablelog/로 변경
            finalKey = FULL_ROOT_PATH + SEPARATOR + imageName.substring(PARENT_FOLDER.length() + 1);
        } else {
            // 그 외의 경우 tablelog-back/tablelog/ prefix 추가
            finalKey = FULL_ROOT_PATH + SEPARATOR + imageName;
        }
            
        DeleteObjectRequest deleteRequest = DeleteObjectRequest.builder()
                .bucket(bucket)
                .key(finalKey)
                .build();
        s3Client.deleteObject(deleteRequest);
    }
    */

    /**
     * 이전 delete 메서드 (주석 처리)
     */
    /*
    public void delete(String imageName) {
        if (imageName == null) return;
        DeleteObjectRequest deleteRequest = DeleteObjectRequest.builder()
                .bucket(bucket)
                .key(imageName)
                .build();
        s3Client.deleteObject(deleteRequest);
    }
    */
    /**
     * 이미지 업데이트 (로컬 파일 시스템에 저장)
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
            if (imageName != null) {
                String key = extractKeyFromUrl(imageName);
                delete(key);
            }
            return null;
        } else if (imageName == null) {
            // 신규 업로드
            String newFileName = originalFileName(multipartFile);
            String key = folderName + SEPARATOR + newFileName;
            return saveFile(multipartFile, key);
        } else {
            // 기존 이미지 삭제 후 새 이미지 업로드
            String oldKey = extractKeyFromUrl(imageName);
            delete(oldKey);

            String newFileName = originalFileName(multipartFile);
            String key = folderName + SEPARATOR + newFileName;
            return saveFile(multipartFile, key);
        }
    }

    /**
     * 이전 updateImage 메서드 (주석 처리)
     */
    /*
    public String updateImage(String imageName, String folderName, MultipartFile multipartFile)
            throws IOException {
        if (imageName == null && (multipartFile == null || multipartFile.isEmpty())) return null;
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
    */
    /**
     * 이미지 경로를 URL로 변환
     */
    public String getImagePath(String objectKey) {
        String normalized = normalizePath(objectKey);
        String urlPath = normalized.replace(File.separator, SEPARATOR);
        return url + urlPath;
    }

    /**
     * URL에서 파일 경로 추출
     */
    private String extractKeyFromUrl(String imageUrl) {
        if (imageUrl == null) return "";
        
        // URL에서 경로 부분만 추출
        if (imageUrl.startsWith(url)) {
            return imageUrl.substring(url.length());
        }
        
        // 기존 S3 URL 처리
        String s3Url = "https://tablelog.s3.ap-northeast-2.amazonaws.com/";
        if (imageUrl.startsWith(s3Url)) {
            return imageUrl.substring(s3Url.length());
        }
        
        // 이미 경로인 경우 그대로 반환
        return imageUrl;
    }
    /*TODO - 다중 이미지 처리 로직 구현
    1. 리스트 타입으로 들어온다
    2. 리스트의 길이를 구해서 처리한다
    */
    /**
     * 다중 이미지 업로드 (tablelog 폴더 내부에 저장)
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
                String savedUrl = saveFile(file, key);
                imageUrls.add(savedUrl);
            }
        }
        return imageUrls;
    }

    /**
     * 이전 updateImages 메서드 (주석 처리)
     */
    /*
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
                System.out.println(key);
                saveFile(file, key);
                imageUrls.add(url + encodedFolderName + SEPARATOR + newFileName);
            }
        }
        return imageUrls;
    }
    */

}
