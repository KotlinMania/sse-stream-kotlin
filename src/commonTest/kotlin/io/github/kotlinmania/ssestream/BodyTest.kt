package io.github.kotlinmania.ssestream

import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.Duration.Companion.seconds

class BodyTest {
    @Test
    fun testEncodeBodyFlow() =
        runTest {
            val sseSequence =
                listOf(
                    Sse().event("1").data("....."),
                    Sse().event("2").data("....."),
                    Sse().event("3").data("....."),
                    Sse().event("4").data("....."),
                )
            val stream = sseSequence.asFlow()
            val body = SseBody.new(stream)
            val sseStream = SseStream.new(body)

            val results = sseStream.asFlow().toList()
            assertEquals(4, results.size)
            for (i in sseSequence.indices) {
                val sse = results[i].getOrThrow()
                assertEquals(sseSequence[i], sse)
            }
        }

    @Test
    fun testKeepAliveConfiguration() {
        val ka = KeepAlive.default()
        assertEquals(15.seconds, ka.maxInterval)
        assertEquals(":\n\n", ka.event.decodeToString())

        val custom =
            KeepAlive
                .new()
                .interval(5.seconds)
                .comment("ping")

        assertEquals(5.seconds, custom.maxInterval)
        assertEquals(": ping\n\n", custom.event.decodeToString())

        val eventKa = KeepAlive.new().event(Sse().event("ping").data("heartbeat"))
        assertEquals("event: ping\ndata: heartbeat\n\n", eventKa.event.decodeToString())
    }

    @Test
    fun testNeverTimer() {
        val timer = NeverTimer.fromDuration(10.seconds)
        assertEquals(null, timer.poll())
        timer.reset(5.seconds)

        val stream = KeepAliveStream.new(KeepAlive.new())
        val eventBytes = stream.pollEvent()
        assertEquals(":\n\n", eventBytes?.decodeToString())
    }

    @Test
    fun testExtensionToSseBody() =
        runTest {
            val sse = Sse().event("status").data("ok")
            val body = flowOf(sse).toSseBody()
            val list = SseStream.new(body).asFlow().toList()
            assertEquals(1, list.size)
            assertEquals("status", list[0].getOrThrow().event)
            assertEquals("ok", list[0].getOrThrow().data)
        }
}
