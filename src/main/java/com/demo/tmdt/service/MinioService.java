package com.demo.tmdt.service;

import com.demo.tmdt.common.config.MinioProperties;
import com.demo.tmdt.common.exception.AppException;
import com.demo.tmdt.common.exception.ErrorCode;
import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.SetBucketPolicyArgs;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MinioService {

    private final MinioClient minioClient;
    private final MinioProperties minioProperties;

    public String uploadImage(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return null;
        }

        try {
            ensureBucketExists();

            String objectName = buildObjectName(file.getOriginalFilename());
            String contentType = StringUtils.hasText(file.getContentType())
                    ? file.getContentType()
                    : "application/octet-stream";

            minioClient.putObject(PutObjectArgs.builder()
                    .bucket(minioProperties.getBucketName())
                    .object(objectName)
                    .stream(file.getInputStream(), file.getSize(), -1)
                    .contentType(contentType)
                    .build());

            return buildPublicUrl(objectName);
        } catch (Exception exception) {
            throw new AppException(ErrorCode.MINIO_UPLOAD_FAILED);
        }
    }

    private void ensureBucketExists() throws Exception {
        boolean exists = minioClient.bucketExists(BucketExistsArgs.builder()
                .bucket(minioProperties.getBucketName())
                .build());

        if (!exists) {
            minioClient.makeBucket(MakeBucketArgs.builder()
                    .bucket(minioProperties.getBucketName())
                    .build());
        }

        minioClient.setBucketPolicy(SetBucketPolicyArgs.builder()
                .bucket(minioProperties.getBucketName())
                .config(publicReadPolicy())
                .build());
    }

    private String buildObjectName(String originalFilename) {
        String extension = StringUtils.getFilenameExtension(originalFilename);
        if (!StringUtils.hasText(extension)) {
            return UUID.randomUUID().toString();
        }
        return UUID.randomUUID() + "." + extension;
    }

    private String buildPublicUrl(String objectName) {
        return minioProperties.getPublicUrl().replaceAll("/+$", "")
                + "/"
                + minioProperties.getBucketName()
                + "/"
                + objectName;
    }

    private String publicReadPolicy() {
        return """
                {
                  "Version": "2012-10-17",
                  "Statement": [
                    {
                      "Effect": "Allow",
                      "Principal": {"AWS": ["*"]},
                      "Action": ["s3:GetObject"],
                      "Resource": ["arn:aws:s3:::%s/*"]
                    }
                  ]
                }
                """.formatted(minioProperties.getBucketName());
    }
}
