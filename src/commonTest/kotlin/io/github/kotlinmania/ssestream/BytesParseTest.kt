// port-lint: tests test_bytes_parse.rs
package io.github.kotlinmania.ssestream

import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class BytesParseTest {
    private val testStreamSse =
        """

retry: 1000
event:userconnect
data: {"username": "bobby", "time": "02:33:48"}

data: Here's a system message of some kind that will get used
data: to accomplish some task.

event: usermessage
data: {"username": "bobby", "time": "02:34:11", "text": "Hi everyone."}

: this is a comment

data: Here's a system message of some kind that will get used
data: to accomplish some task.
id: aaaa-bbb-ccc

""".encodeToByteArray()

    @Test
    fun testBytesParse() =
        runTest {
            val byteFlow = listOf(testStreamSse).asFlow()
            val sseStream = SseStream.new(byteFlow)
            val events = sseStream.asFlow().toList()

            assertEquals(4, events.size)

            val e0 = events[0].getOrThrow()
            assertEquals("userconnect", e0.event)
            assertEquals("{\"username\": \"bobby\", \"time\": \"02:33:48\"}", e0.data)
            assertEquals(1000uL, e0.retry)

            val e1 = events[1].getOrThrow()
            assertTrue(e1.isMessage())
            assertEquals("Here's a system message of some kind that will get used\nto accomplish some task.", e1.data)

            val e2 = events[2].getOrThrow()
            assertEquals("usermessage", e2.event)
            assertEquals("{\"username\": \"bobby\", \"time\": \"02:34:11\", \"text\": \"Hi everyone.\"}", e2.data)

            val e3 = events[3].getOrThrow()
            assertEquals("aaaa-bbb-ccc", e3.id)
            assertEquals("Here's a system message of some kind that will get used\nto accomplish some task.", e3.data)
        }
}
