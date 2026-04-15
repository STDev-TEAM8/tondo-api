package com.tondo.api.infrastructure.qr

import com.google.zxing.BarcodeFormat
import com.google.zxing.client.j2se.MatrixToImageWriter
import com.google.zxing.qrcode.QRCodeWriter
import org.springframework.stereotype.Component
import java.io.ByteArrayOutputStream

@Component
class QrGenerator {
    fun generate(qrGenerationCommand: QrGenerationCommand) : ByteArray {
        // 1. QR 코드 스펙으로 텍스트 인코딩
        val qrCodeWriter = QRCodeWriter()
        val bitMatrix = qrCodeWriter.encode(
            qrGenerationCommand.content, BarcodeFormat.QR_CODE, qrGenerationCommand.width, qrGenerationCommand.height
        )

        // use 블록의 결과값이 통째로 반환되도록 return을 앞으로 뺍니다.
        return ByteArrayOutputStream().use { outputStream ->
            MatrixToImageWriter.writeToStream(bitMatrix, "PNG", outputStream)
            outputStream.toByteArray() // 마지막 줄의 결과가 use 블록의 반환값이 됨
        }
    }
}