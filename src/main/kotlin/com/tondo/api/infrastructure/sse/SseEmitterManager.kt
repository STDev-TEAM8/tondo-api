package com.tondo.api.infrastructure.sse

import com.tondo.api.domain.ArtworkCreationEvent
import com.tondo.api.domain.ArtworkCreationStage
import com.tondo.api.dto.ArtworkStreamEvent
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter
import java.util.concurrent.ConcurrentHashMap

@Component
class SseEmitterManager {

    private val emitters = ConcurrentHashMap<String, SseEmitter>()

    fun connect(taskId: String): SseEmitter {
        val emitter = SseEmitter(60_000L * 5) // 타임아웃 5분 연장
        emitters[taskId] = emitter

        emitter.onCompletion { emitters.remove(taskId) }
        emitter.onTimeout { emitters.remove(taskId) }
        emitter.onError { emitters.remove(taskId) }

        // 초기 접속 이벤트
        sendToClient(taskId, ArtworkStreamEvent(ArtworkCreationStage.INIT.progress, "CONNECTED", ArtworkCreationStage.INIT.description))
        return emitter
    }

    /**
     * ApplicationEventPublisher로 퍼블리시된 이벤트를 소비합니다.
     */
    @EventListener
    fun onStageChanged(event: ArtworkCreationEvent) {
        val taskIdStr = event.taskId.toString()

        val status = if (event.isSuccess) {
            if (event.stage == ArtworkCreationStage.COMPLETED) "COMPLETED" else "PROCESSING"
        } else {
            "FAILED"
        }

        val message = event.errorMessage ?: event.stage.description

        val streamEvent = ArtworkStreamEvent(
            progress = event.stage.progress,
            status = status,
            message = message
        )
        sendToClient(taskIdStr, streamEvent)

        if (event.stage == ArtworkCreationStage.COMPLETED || !event.isSuccess) {
            complete(taskIdStr)
        }
    }

    private fun sendToClient(taskId: String, event: ArtworkStreamEvent) {
        val emitter = emitters[taskId] ?: return
        try {
            emitter.send(
                SseEmitter.event()
                    .name("progress")
                    .data(event)
            )
        } catch (e: Exception) {
            emitters.remove(taskId)
        }
    }

    private fun complete(taskId: String) {
        emitters[taskId]?.complete()
        emitters.remove(taskId)
    }
}
