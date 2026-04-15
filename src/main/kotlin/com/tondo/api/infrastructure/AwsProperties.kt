package com.tondo.api.infrastructure

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "cloud.aws")
data class AwsProperties(
    val region: RegionProperties,
    val credentials: CredentialProperties,
    val s3: S3Properties,
    val bedrock: BedrockProperties
) {
    data class RegionProperties(val static: String)
    data class CredentialProperties(val accessKey: String, val secretKey: String)
    data class S3Properties(val bucket: String)
    data class BedrockProperties(val modelId: String, val imageModelId: String)
}