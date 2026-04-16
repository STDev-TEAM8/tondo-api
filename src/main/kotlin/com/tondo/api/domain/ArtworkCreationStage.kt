package com.tondo.api.domain

enum class ArtworkCreationStage(val progress: Int, val description: String) {
    INIT(0, "Starting generation..."),
    DOCENT_GENERATED(25, "Docent script generated."),
    IMAGE_GENERATED(50, "Artwork image generated."),
    STORAGE_UPLOADED(75, "Resources uploaded to storage."),
    COMPLETED(100, "Voila! Artwork creation complete."),
    FAILED(-1, "Generation failed.")
}
