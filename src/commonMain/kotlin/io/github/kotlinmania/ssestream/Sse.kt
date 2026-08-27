// port-lint: source sse-stream/src/lib.rs
package io.github.kotlinmania.ssestream

import kotlin.time.Duration

// reference: https://html.spec.whatwg.org/multipage/server-sent-events.html

data class Sse(
    var event: String? = null,
    var data: String? = null,
    var id: String? = null,
    var retry: ULong? = null,
) {
    fun isEvent(): Boolean = event != null

    fun isMessage(): Boolean = event == null

    fun event(event: String): Sse {
        this.event = event
        return this
    }

    fun data(data: String): Sse {
        this.data = data
        return this
    }

    fun id(id: String): Sse {
        this.id = id
        return this
    }

    fun retry(retry: ULong): Sse {
        this.retry = retry
        return this
    }

    fun retryDuration(retry: Duration): Sse {
        this.retry = retry.inWholeMilliseconds.toULong()
        return this
    }

    companion object {
        fun from(valSse: Sse): ByteArray = valSse.toByteArray()
    }
}

// Wire-format encoding of an Sse event into UTF-8 bytes.
fun Sse.toByteArray(): ByteArray {
    val sb = StringBuilder()
    event?.let { sb.append("event: ").append(it).append('\n') }
    data?.let { sb.append("data: ").append(it).append('\n') }
    id?.let { sb.append("id: ").append(it).append('\n') }
    retry?.let { sb.append("retry: ").append(it.toString()).append('\n') }
    sb.append('\n')
    return sb.toString().encodeToByteArray()
}

fun from(valSse: Sse): ByteArray = valSse.toByteArray()
