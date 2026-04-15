package com.tondo.api.infrastructure.aws.s3

import com.tondo.api.infrastructure.aws.AwsProperties
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Service
import software.amazon.awssdk.core.sync.RequestBody
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.PutObjectRequest
import software.amazon.awssdk.services.s3.model.S3Exception

@Service
@Profile("prod")
class S3StorageService (
    private val s3Client: S3Client,
    private val awsProperties: AwsProperties
) : ExternalStorageService {

    override fun upload(imageBytes: ByteArray, fileName: String): String {
        val bucketName = awsProperties.s3.bucket
        val putObjectRequest = PutObjectRequest.builder()
            .bucket(bucketName)
            .key(fileName)
            .contentType("image/png")
            .build()

        // 업로드된 파일의 URL 반환
        try {
            s3Client.putObject(putObjectRequest, RequestBody.fromBytes(imageBytes))
            return "https://$bucketName.s3.${awsProperties.region.static}.amazonaws.com/$fileName"
        } catch (e: S3Exception) {
            throw RuntimeException("Failed to upload file to S3: ${e.awsErrorDetails().errorMessage()}" + " | Details : S3 업로드 실패: bucket=$bucketName, key=$fileName")
        }
    }
}