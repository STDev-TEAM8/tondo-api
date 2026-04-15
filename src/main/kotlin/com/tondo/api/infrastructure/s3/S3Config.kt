package com.tondo.api.infrastructure.s3

import com.tondo.api.infrastructure.AwsProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.s3.S3Client

@Configuration
@Profile("prod")
@EnableConfigurationProperties(AwsProperties::class)
class S3Config (
    private val awsProperties: AwsProperties
)
{
    @Bean
    fun s3Client(): S3Client {
        val credential: AwsBasicCredentials = AwsBasicCredentials.builder()
            .accessKeyId(awsProperties.credentials.accessKey)
            .secretAccessKey(awsProperties.credentials.secretKey)
            .build()

        return S3Client.builder()
            .region(Region.of(awsProperties.region.static))
            .credentialsProvider(StaticCredentialsProvider.create(credential))
            .build()
    }
}