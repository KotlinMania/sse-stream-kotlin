// port-lint: tests lib.rs
package io.github.kotlinmania.ssestream

import kotlin.test.Test
import kotlin.test.assertEquals

class LibTest {
    @Test
    fun testFromFunctions() {
        val sse = Sse().event("ping").data("pong")
        val companionBytes = Sse.from(sse)
        val topLevelBytes = from(sse)
        assertEquals("event: ping\ndata: pong\n\n", companionBytes.decodeToString())
        assertEquals("event: ping\ndata: pong\n\n", topLevelBytes.decodeToString())
    }
}
