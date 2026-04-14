package com.tondo.api.controller

import com.tondo.api.dto.ArtworkStreamEvent
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter

@RestController
@RequestMapping("/api/v1/tasks")
class TaskController {

    @GetMapping("/{taskId}/stream", produces = [MediaType.TEXT_EVENT_STREAM_VALUE])
    fun streamProgress(@PathVariable taskId: String) : SseEmitter {
        val emitter = SseEmitter(60_000L) // SSE 연결 타임아웃 : 60초

        // 진행도 Mocking
        Thread {
            try {
                val stages = listOf(
                    ArtworkStreamEvent(25, "PROCESSING", "Frequency Analyzing..."),
                    ArtworkStreamEvent(50, "PROCESSING", "Color Analyzing..."),
                    ArtworkStreamEvent(75, "PROCESSING", "Shaping Your Voice..."),
                    ArtworkStreamEvent(100, "COMPLETED", "Voila!")
                )

                for(stage in stages) {
                    emitter.send(
                        SseEmitter.event()
                            .name("progress")
                            .data(stage)
                    )
                    Thread.sleep(2000) // 각 단계 사이에 2초 대기 (실제 작업 진행 상황에 따라 조정 필요)
                }
                emitter.complete()
            }
            catch (e: Exception) {
                emitter.completeWithError(e)
            }
        }.start()

        return emitter
    }
}