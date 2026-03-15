package com.tablelog.tablelogback.global.s3;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AsyncImageUploadService {

    private final S3Provider s3Provider;

    /**
     * 레시피 이미지 비동기 업로드
     * - DB 저장 후 호출하여 클라이언트 응답을 즉시 반환
     */
    @Async("s3UploadExecutor")
    public void uploadRecipeImages(
            String folderName,
            byte[] mainImageBytes,
            String mainImageContentType,
            String mainImageKey,
            List<byte[]> processImageBytesList,
            List<String> processImageContentTypes,
            List<String> processImageKeys
    ) {
        try {
            s3Provider.createFolder(folderName);
            if (mainImageBytes != null && mainImageBytes.length > 0) {
                s3Provider.saveBytes(mainImageBytes, mainImageContentType, mainImageKey);
            }
            for (int i = 0; i < processImageBytesList.size(); i++) {
                s3Provider.saveBytes(
                        processImageBytesList.get(i),
                        processImageContentTypes.get(i),
                        processImageKeys.get(i)
                );
            }
        } catch (Exception e) {
            log.error("[AsyncImageUploadService] 레시피 이미지 업로드 실패 - folder: {}, error: {}",
                    folderName, e.getMessage(), e);
        }
    }

    /**
     * 게시판 이미지 비동기 업로드
     */
    @Async("s3UploadExecutor")
    public void uploadBoardImages(
            List<byte[]> imageBytesList,
            List<String> contentTypes,
            List<String> keys
    ) {
        try {
            for (int i = 0; i < imageBytesList.size(); i++) {
                s3Provider.saveBytes(imageBytesList.get(i), contentTypes.get(i), keys.get(i));
            }
        } catch (Exception e) {
            log.error("[AsyncImageUploadService] 게시판 이미지 업로드 실패 - error: {}", e.getMessage(), e);
        }
    }
}
