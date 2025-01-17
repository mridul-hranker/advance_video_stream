package com.example.advance_video_stream.new_pipe_extractor


class BgPoTokenProvider(private val bgHelperUrl: String) {}
    /*: PoTokenProvider {

    private val scheduler: ScheduledExecutorService = Executors.newScheduledThreadPool(1)

    // Queue to hold valid PO tokens
    private val validPoTokens: Queue<PoTokenResult> = ConcurrentLinkedQueue()

    // Function to get the web visitor data
    private suspend fun getWebVisitorData(): String {
        val html = RequestUtils.sendGet("https://www.youtube.com").get()  // Consider using a suspend function for this
        val matcher = Pattern.compile("visitorData\":\"([\\w%-]+)\"").matcher(html)

        if (matcher.find()) {
            return matcher.group(1)
        }
        throw RuntimeException("Failed to get visitor data")
    }

    // Function to get PO token from the pool or create a new one
    private suspend fun getPoTokenPooled(): PoTokenResult? {
        var poToken = validPoTokens.poll()

        if (poToken == null) {
            poToken = createWebClientPoToken()
        }

        // if still null, return null
        if (poToken == null) {
            return null
        }

        // Timer to insert back into queue after 10 + random seconds
        val delay = 10_000 + Random.nextInt(5000)
        val finalPoToken = poToken
        scheduler.schedule({
            validPoTokens.offer(finalPoToken)
        }, delay.toLong(), TimeUnit.MILLISECONDS)

        return poToken
    }

    // Function to create a new PO token for web client
    private suspend fun createWebClientPoToken(): PoTokenResult? {
        val visitorData = getWebVisitorData()

        val poToken = ReqwestUtils.fetch("$bgHelperUrl/generate", "POST", mapper.writeValueAsBytes(
            mapper.createObjectNode().put("visitorData", visitorData)
        ), mapOf("Content-Type" to "application/json"))
            .thenApply { response ->
                try {
                    val responseBody = response.body()
                    mapper.readTree(responseBody)["poToken"]?.asText()
                } catch (e: Exception) {
                    null
                }
            }
            .join()

        return if (poToken != null) {
            PoTokenResult(visitorData, poToken)
        } else {
            null
        }
    }

    // Override method to get PO token for web client
    override fun getWebClientPoToken(): PoTokenResult? {
        return try {
            runBlocking { getPoTokenPooled() }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    // Override method for Android client PO token (not implemented)
    override fun getAndroidClientPoToken(): PoTokenResult? {
        // TODO: Allow setting from config, maybe
        return null
    }
}*/
