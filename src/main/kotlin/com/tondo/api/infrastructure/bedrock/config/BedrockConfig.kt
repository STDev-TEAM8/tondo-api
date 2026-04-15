package com.tondo.api.infrastructure.bedrock.config

import com.tondo.api.infrastructure.AwsProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.bedrockruntime.BedrockRuntimeClient

@Configuration
@EnableConfigurationProperties(AwsProperties::class)
class BedrockConfig(private val props: AwsProperties) {

    @Bean
    fun bedrockRuntimeClient(): BedrockRuntimeClient =
        BedrockRuntimeClient.builder()
            .region(Region.of(props.region))
            .credentialsProvider(
                StaticCredentialsProvider.create(
                    AwsBasicCredentials.create(props.accessKey, props.secretKey)
                )
            )
            .build()
}