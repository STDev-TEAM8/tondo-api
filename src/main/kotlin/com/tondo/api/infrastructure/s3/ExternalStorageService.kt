package com.tondo.api.infrastructure.s3

/**
 * S3 Storage Service 인터페이스 : Local/Prod 환경에 따라 Mock/S3 Storage Service 구현체를 주입받아 사용할 수 있도록 정의
 */
interface ExternalStorageService {
    fun upload(imageBytes: ByteArray, fileName: String): String
}