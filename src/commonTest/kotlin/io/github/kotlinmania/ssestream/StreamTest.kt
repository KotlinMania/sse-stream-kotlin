// port-lint: tests stream.rs
package io.github.kotlinmania.ssestream

import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue

class StreamTest {
    private val testStreamSse =
        """
retry: 1000
event: userconnect
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
    fun testFlowStreaming() =
        runTest {
            val sseStream = SseStream.fromByteStream(flowOf(testStreamSse))
            val list = sseStream.asFlow().toList()
            assertEquals(4, list.size)
            assertEquals("userconnect", list[0].getOrThrow().event)
        }

    @Test
    fun testCrlfAndCrLineEndings() =
        runTest {
            val text = "event: ev\r\ndata: dt\r\n\r\nevent: ev2\ndata: dt2\n\n"
            val sseStream = SseStream.fromByteStream(flowOf(text.encodeToByteArray()))
            val results = sseStream.asFlow().toList()
            assertEquals(2, results.size)
            assertEquals("ev", results[0].getOrThrow().event)
            assertEquals("dt", results[0].getOrThrow().data)
            assertEquals("ev2", results[1].getOrThrow().event)
            assertEquals("dt2", results[1].getOrThrow().data)
        }

    @Test
    fun testErrorsAndDescription() =
        runTest {
            val sseStream1 = SseStream.fromByteStream(flowOf("no_colon_here\n\n".encodeToByteArray()))
            val invalidLine = sseStream1.asFlow().toList()
            assertEquals(1, invalidLine.size)
            assertTrue(invalidLine[0].isFailure)
            assertIs<Error.InvalidLine>(invalidLine[0].exceptionOrNull())
            assertEquals("invalid line", (invalidLine[0].exceptionOrNull() as Error).description())
            assertEquals("invalid line", (invalidLine[0].exceptionOrNull() as Error).fmt())

            val dupEvent = SseStream.fromByteStream(flowOf("event: one\nevent: two\n\n".encodeToByteArray())).asFlow().toList()
            assertIs<Error.DuplicatedEventLine>(dupEvent[0].exceptionOrNull())
            assertEquals("duplicated event line", (dupEvent[0].exceptionOrNull() as Error).description())

            val dupId = SseStream.fromByteStream(flowOf("id: 1\nid: 2\n\n".encodeToByteArray())).asFlow().toList()
            assertIs<Error.DuplicatedIdLine>(dupId[0].exceptionOrNull())
            assertEquals("duplicated id line", (dupId[0].exceptionOrNull() as Error).description())

            val dupRetry = SseStream.fromByteStream(flowOf("retry: 100\nretry: 200\n\n".encodeToByteArray())).asFlow().toList()
            assertIs<Error.DuplicatedRetry>(dupRetry[0].exceptionOrNull())
            assertEquals("duplicated retry line", (dupRetry[0].exceptionOrNull() as Error).description())

            val invalidRetry = SseStream.fromByteStream(flowOf("retry: not_a_number\n\n".encodeToByteArray())).asFlow().toList()
            assertIs<Error.IntParse>(invalidRetry[0].exceptionOrNull())
            assertEquals("int parse error", (invalidRetry[0].exceptionOrNull() as Error).description())
        }
}
