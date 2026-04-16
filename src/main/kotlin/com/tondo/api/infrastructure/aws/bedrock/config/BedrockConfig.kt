package com.tondo.api.infrastructure.aws.bedrock.config

import com.tondo.api.infrastructure.aws.AwsProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.bedrockruntime.BedrockRuntimeClient

@Configuration
@Profile("prod")
@EnableConfigurationProperties(AwsProperties::class)
class BedrockConfig(private val props: AwsProperties) {

    @Bean
    fun bedrockRuntimeClient(): BedrockRuntimeClient =
        BedrockRuntimeClient.builder()
            .region(Region.of(props.region.static))
            .credentialsProvider(credentialsProvider())
            .build()

    @Bean("imageBedrockClient")
    fun imageBedrockClient(): BedrockRuntimeClient =
        BedrockRuntimeClient.builder()
            .region(Region.of(props.bedrock.imageRegion))
            .credentialsProvider(credentialsProvider())
            .build()

    private fun credentialsProvider() = StaticCredentialsProvider.create(
        AwsBasicCredentials.create(props.credentials.accessKey, props.credentials.secretKey)
    )
}
