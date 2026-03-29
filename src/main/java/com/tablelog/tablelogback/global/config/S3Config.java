package com.tablelog.tablelogback.global.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.http.urlconnection.UrlConnectionHttpClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

import java.time.Duration;

@Configuration
public class S3Config {

    @Bean
    public S3Client s3Client(
            @Value("${spring.cloud.aws.credentials.access-key}") String accessKey,
            @Value("${spring.cloud.aws.credentials.secret-key}") String secretKey,
            @Value("${spring.cloud.aws.region.static}") String region
    ) {
        AwsBasicCredentials awsCreds = AwsBasicCredentials.create(accessKey, secretKey);

        return S3Client.builder()
                .region(Region.of(region))
                .credentialsProvider(StaticCredentialsProvider.create(awsCreds))
                .httpClient(
                        UrlConnectionHttpClient.builder()
                                .connectionTimeout(Duration.ofSeconds(3))   // TCP 연결 타임아웃: 3초
                                .socketTimeout(Duration.ofSeconds(10))      // 소켓 읽기 타임아웃: 10초
                                .build()
                )
                .build();
    }
}