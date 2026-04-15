package com.tondo.api.infrastructure.qr

import org.junit.jupiter.api.Test


class QrGeneratorTest (
){
    @Test
    fun `generatedQrCode_shouldNotBeEmpty`() {
        // Given
        val command = QrGenerationCommand(
            content = "https://www.example.com",
            width = 200,
            height = 200
        )

        // When
        val qrGenerator = QrGenerator()
        val qrCodeBytes = qrGenerator.generate(command)

        // Then
        assert(qrCodeBytes.isNotEmpty()) { "Generated QR code should not be empty" }
    }
}