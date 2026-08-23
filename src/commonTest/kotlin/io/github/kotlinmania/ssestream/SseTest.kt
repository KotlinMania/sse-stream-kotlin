// port-lint: tests src/lib.rs
package io.github.kotlinmania.ssestream

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlin.time.Duration.Companion.seconds

class SseTest {
    @Test
    fun testEventAndMessage() {
        val sseEvent = Sse().event("update").data("hello")
        assertTrue(sseEvent.isEvent())
        assertFalse(sseEvent.isMessage())

        val sseMsg = Sse().data("hello")
        assertFalse(sseMsg.isEvent())
        assertTrue(sseMsg.isMessage())
    }

    @Test
    fun testToByteArray() {
        val sse =
            Sse()
                .event("add")
                .data("sample data")
                .id("123")
                .retry(5000u)

        val bytes = sse.toByteArray()
        val text = bytes.decodeToString()

        assertEquals("event: add\ndata: sample data\nid: 123\nretry: 5000\n\n", text)
    }

    @Test
    fun testRetryDuration() {
        val sse = Sse().retryDuration(3.seconds)
        assertEquals(3000uL, sse.retry)
    }
}
