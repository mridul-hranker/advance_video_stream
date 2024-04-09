data class PreviewFrames(
    val urls: List<String>,
    val frameWidth: Int,
    val frameHeight: Int,
    val totalCount: Int,
    val durationPerFrame: Long,
    val framesPerPageX: Int,
    val framesPerPageY: Int
)
