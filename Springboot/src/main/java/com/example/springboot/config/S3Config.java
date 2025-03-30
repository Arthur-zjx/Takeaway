package com.example.springboot.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.CreateBucketRequest;
import software.amazon.awssdk.services.s3.model.CreateBucketResponse;

@Configuration
public class S3Config {

    @Value("${aws.s3.region}")
    private String region;

    @Value("${aws.s3.access-key}")
    private String accessKey;

    @Value("${aws.s3.secret-key}")
    private String secretKey;

    @Value("${aws.s3.bucket}")
    private String bucketName;

    @Bean
    public S3Client s3Client() {
        // 创建 S3 客户端
        S3Client s3Client = S3Client.builder()
                .region(Region.of(region))
                .credentialsProvider(
                        StaticCredentialsProvider.create(
                                AwsBasicCredentials.create(accessKey, secretKey)
                        )
                )
                .build();

        // 检查并创建桶
        createBucketIfNotExists(s3Client);

        return s3Client;
    }

    // 如果桶不存在，则创建它
    private void createBucketIfNotExists(S3Client s3Client) {
        try {
            // 检查桶是否存在
            boolean exists = s3Client.listBuckets().buckets()
                    .stream()
                    .anyMatch(bucket -> bucket.name().equals(bucketName));

            // 如果桶不存在，则创建
            if (!exists) {
                CreateBucketRequest createBucketRequest = CreateBucketRequest.builder()
                        .bucket(bucketName)
                        .build();
                CreateBucketResponse response = s3Client.createBucket(createBucketRequest);
                System.out.println("Bucket created: " + response.location());
            }
        } catch (Exception e) {
            System.out.println("Error checking or creating S3 bucket: " + e.getMessage());
        }
    }
}
