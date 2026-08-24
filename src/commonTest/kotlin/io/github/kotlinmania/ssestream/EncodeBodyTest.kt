// port-lint: tests tests/test_encode_body.rs
package io.github.kotlinmania.ssestream

import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class EncodeBodyTest {
    @Test
    fun testEncodeBody() =
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

            val received = sseStream.asFlow().toList()
            assertEquals(sseSequence.size, received.size)
            for (i in sseSequence.indices) {
                assertEquals(sseSequence[i], received[i].getOrThrow())
            }
        }
}
