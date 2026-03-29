package com.tablelog.tablelogback.global.s3;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class AsyncImageUploadService {

    private final S3Provider s3Provider;

    /**
     * 레시피 이미지 비동기 업로드
     * - CompletableFuture 반환으로 호출부에서 타임아웃 대기 가능
     */
    @Async("s3UploadExecutor")
    public CompletableFuture<Void> uploadRecipeImages(
            byte[] mainImageBytes,
            String mainImageContentType,
            String mainImageKey,
            List<byte[]> processImageBytesList,
            List<String> processImageContentTypes,
            List<String> processImageKeys
    ) {
        try {
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
            log.error("[AsyncImageUploadService] 레시피 이미지 업로드 실패 - error: {}", e.getMessage(), e);
            return CompletableFuture.failedFuture(e);
        }
        return CompletableFuture.completedFuture(null);
    }

    /**
     * 게시판 이미지 비동기 업로드
     * - CompletableFuture 반환으로 호출부에서 타임아웃 대기 가능
     */
    @Async("s3UploadExecutor")
    public CompletableFuture<Void> uploadBoardImages(
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
            return CompletableFuture.failedFuture(e);
        }
        return CompletableFuture.completedFuture(null);
    }
}
