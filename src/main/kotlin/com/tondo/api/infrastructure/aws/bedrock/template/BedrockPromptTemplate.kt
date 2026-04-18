package com.tondo.api.infrastructure.aws.bedrock.template

/**
 * Bedrock(AI) 호출 시 사용되는 프롬프트 문자열을 생성하는 팩토리 객체.
 * 도메인 파라미터를 받아 AI에 최적화된 프롬프트 포맷을 반환합니다.
 */
object BedrockPromptTemplate {

    /**
     * 도슨트 리포트 프롬프트 생성
     */
    fun createDocentPrompt(
        averageHz: Double,
        averageTimbre: Double,
        averageVolume: Double,
        voiceColor: String = "light blue" // 필요시 동적으로 주입할 수 있도록 기본값 파라미터화
    ): String {
        return """
            당신은 미디어 아트 페스티벌의 수석 도슨트입니다. 아래의 관람객 음성 추출 데이터와 이 파동을 시각화하기 위해 AI에 적용된 아트워크 스타일과 실제 적용된 이미지를 바탕으로 도슨트 해설을 작성해 주세요.

            [관람객 음성 분석 데이터]
            - 평균 음고 (형태 결정): ${averageHz}Hz
            - 음색 중심 (굴곡 결정): ${averageTimbre}
            - 목소리 크기 (에너지): ${averageVolume}dB

            [시각화 적용 스타일 및 색상]
            - 메인 색상 코드: ${voiceColor} (이 색상을 기반으로 어두움과 밝음이 배치됨)
            - 렌더링 기법: 캔버스 질감이 살아있는 정교한 아크릴화
            - 세부 묘사: 대담하고 대칭적인 구조, 미세한 점들을 선으로 재해석한 이분법적 점묘 기법
            
            [템플릿]
            Markdown 형태가 아닌 순수 TEXT로 작성

            이 데이터를 바탕으로 관람객의 목소리가 어떻게 이 매혹적인 아크릴 작품으로 탄생했는지 도슨트 해설을 작성해주세요. 인삿말과 서론을 제외하고, 세부 묘사와 표현 기법에 대한 설명도 제외해줘.
        """.trimIndent()
    }

    /**
     * 이미지 생성용 프롬프트 생성
     */
//    fun createImageGenerationPrompt(
//        hexColor: String = "#97b6e1"
//    ): String {
//        // Stability Core 모델은 영문 지시어에 최적화되어 있습니다.
//        return """
//            [Subject & Structure]
//            Precisely preserve all detailed geometric structures and Chladni pattern shapes from the provided reference.
//
//            [Style & Color]
//            A sophisticated acrylic painting with visible canvas texture and fine brushstrokes.
//            Apply the color $hexColor: use deep tones for darker areas and vibrant, bright tones for the pattern's highlights.
//            The composition should be bold, dynamic, and structurally aligned with the geometric pattern.
//
//            [Detail & Texture]
//            Visually captivating smartphone wallpaper style.
//            Reinterpret fine dots as rhythmic acrylic pointillism.
//            Convert fine points into flowing, continuous lines with a binary contrast of light and shadow.
//            Ensure a perfect harmony between symmetrical structure and rich, thick paint texture.
//        """.trimIndent()
//    }
//    fun createImageGenerationPrompt(voiceColor: String = "#97b6e1"): String {
//        return """
//          Sophisticated acrylic painting on textured canvas,
//          preserving the exact geometric Chladni pattern structure,
//          primary color $voiceColor with deep dark tones in shadow regions and vibrant bright tones in highlights,
//          colors structurally distributed along the geometric forms,
//          bold and dynamic composition,
//          fine acrylic brushstroke texture,
//          reinterpreted pointillism with flowing continuous lines instead of dots,
//          binary contrast of light and shadow,
//          symmetrical structure with rich thick paint texture,
//          smartphone wallpaper, 8k resolution, masterpiece
//      """.trimIndent()
//    }
//


    fun createImageGenerationPrompt(voiceColor: String = "light blue"): String {
        return """
        $voiceColor monochromatic painting, strict $voiceColor color scheme only,
        
        redraw the Chladni figure pattern from the reference image,
        preserve the exact geometric sand ripple lines and nodal curves,
        symmetric geometric wave interference pattern,
        fine granular lines forming geometric shapes,
        
        luxurious acrylic oil painting on textured canvas,
        thick impasto brushstrokes, palette knife texture,
        visible canvas weave, rich layered pigment, tactile surface,
        
        dark $voiceColor background, bright $voiceColor pattern lines,
        deep $voiceColor in shadows, bright $voiceColor in highlights,
        every brushstroke and pigment in shades of $voiceColor only,
        NO other dominant colors except $voiceColor variations,
        
        museum-quality fine art, gallery piece,
        dramatic chiaroscuro lighting on paint texture,
        ultra detailed brush fibers, 8k, smartphone wallpaper composition
    """.trimIndent()
    }

    fun createNegativePrompt(): String {
        return """
        sand particles, dots, pointillism, halftone, dotted pattern,
        photorealistic, photograph, 3d render, CGI, smooth plastic surface,
        flat illustration, vector art, digital art,
        dark background, black background, low contrast,
        geometric shapes, mandala, kaleidoscope, radial symmetry,
        text, watermark, signature, blurry, low quality
    """.trimIndent()
    }
}
