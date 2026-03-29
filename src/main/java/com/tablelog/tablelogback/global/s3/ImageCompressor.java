package com.tablelog.tablelogback.global.s3;

import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * S3 업로드 전 이미지 리사이즈 및 압축 처리
 * - 최대 1280px 이하로 리사이즈 (비율 유지)
 * - JPEG 품질 0.85로 압축
 * - PNG는 JPEG로 변환하여 압축 (투명도 불필요한 경우)
 */
@Slf4j
@Component
public class ImageCompressor {

    private static final int MAX_WIDTH = 1280;
    private static final int MAX_HEIGHT = 1280;
    private static final double QUALITY = 0.85;

    /**
     * 이미지 bytes를 압축하여 반환
     *
     * @param originalBytes 원본 이미지 바이트
     * @param contentType   이미지 Content-Type (image/jpeg, image/png)
     * @return 압축된 이미지 바이트 (압축 실패 시 원본 반환)
     */
    public byte[] compress(byte[] originalBytes, String contentType) {
        if (originalBytes == null || originalBytes.length == 0) return originalBytes;
        if (contentType == null || !contentType.startsWith("image/")) return originalBytes;

        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

            Thumbnails.of(new ByteArrayInputStream(originalBytes))
                    .size(MAX_WIDTH, MAX_HEIGHT)
                    .keepAspectRatio(true)
                    .outputFormat("JPEG")
                    .outputQuality(QUALITY)
                    .toOutputStream(outputStream);

            byte[] compressed = outputStream.toByteArray();

            log.debug("[ImageCompressor] 압축 완료 - 원본: {}KB → 압축: {}KB",
                    originalBytes.length / 1024, compressed.length / 1024);

            return compressed;

        } catch (IOException e) {
            log.warn("[ImageCompressor] 이미지 압축 실패, 원본 사용 - error: {}", e.getMessage());
            return originalBytes;
        }
    }
}
