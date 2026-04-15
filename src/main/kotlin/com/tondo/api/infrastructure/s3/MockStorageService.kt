package com.tondo.api.infrastructure.s3

import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Service

@Service
@Profile("local")
class MockStorageService : ExternalStorageService{

    override fun upload(imageBytes: ByteArray, fileName: String) : String {
        return "https://i.ibb.co/21zPYrT7/Gemini-Generated-Image-qhl6v4qhl6v4qhl6.png" // Mock URL 반환 (실제 S3 업로드 대신 고정된 URL 반환)
    }
}