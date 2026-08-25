// port-lint: source body.rs
package io.github.kotlinmania.ssestream

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

typealias Data = ByteArray
typealias Output = Unit
typealias Error = Throwable

interface Timer {
    fun reset(duration: Duration)
}

class NeverTimer : Timer {
    override fun reset(duration: Duration) {
        if (duration < Duration.ZERO) return
    }

    fun poll(): Unit? = null

    companion object {
        fun fromDuration(duration: Duration): NeverTimer {
            if (duration < Duration.ZERO) return NeverTimer()
            return NeverTimer()
        }
    }
}

/**
 * Configure the interval between keep-alive messages, the content
 * of each message, and the associated stream.
 */
data class KeepAlive(
    var event: ByteArray = ":\n\n".encodeToByteArray(),
    var maxInterval: Duration = 15.seconds,
) {
    companion object {
        /**
         * Create a new `KeepAlive`.
         */
        fun new(): KeepAlive = KeepAlive()

        fun default(): KeepAlive = new()
    }

    /**
     * Customize the interval between keep-alive messages.
     *
     * Default is 15 seconds.
     */
    fun interval(time: Duration): KeepAlive {
        this.maxInterval = time
        return this
    }

    /**
     * Customize the event of the keep-alive message.
     *
     * Default is an empty comment.
     */
    fun event(event: Sse): KeepAlive {
        this.event = event.toByteArray()
        return this
    }

    /**
     * Customize the event of the keep-alive message with a comment.
     */
    fun comment(comment: String): KeepAlive {
        this.event = ": $comment\n\n".encodeToByteArray()
        return this
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is KeepAlive) return false
        return event.contentEquals(other.event) && maxInterval == other.maxInterval
    }

    override fun hashCode(): Int {
        var result = event.contentHashCode()
        result = 31 * result + maxInterval.hashCode()
        return result
    }
}

class KeepAliveStream(
    val keepAlive: KeepAlive,
    val aliveTimer: Timer = NeverTimer.fromDuration(keepAlive.maxInterval),
) {
    companion object {
        fun new(keepAlive: KeepAlive): KeepAliveStream = KeepAliveStream(keepAlive)
    }

    fun reset() {
        aliveTimer.reset(keepAlive.maxInterval)
    }

    fun pollEvent(): ByteArray? {
        val evt = keepAlive.event.copyOf()
        reset()
        return evt
    }
}

class SseBody(
    val eventStream: Flow<Sse>,
    val keepAlive: KeepAliveStream? = null,
) {
    companion object {
        fun new(stream: Flow<Sse>): SseBody = SseBody(stream)

        fun fromEvents(stream: Flow<Sse>): SseBody = SseBody(stream)

        fun newKeepAlive(
            stream: Flow<Sse>,
            keepAlive: KeepAlive,
        ): SseBody = SseBody(stream, KeepAliveStream.new(keepAlive))
    }

    fun withKeepAlive(keepAlive: KeepAlive): SseBody = SseBody(eventStream, KeepAliveStream.new(keepAlive))

    fun pollFrame(): ByteArray? {
        val ka = keepAlive
        return if (ka != null) {
            ka.pollEvent()
        } else {
            null
        }
    }

    fun toByteFlow(): Flow<ByteArray> =
        flow {
            eventStream.collect { sse ->
                keepAlive?.reset()
                emit(sse.toByteArray())
            }
        }
}

fun Flow<Sse>.toSseBody(keepAlive: KeepAlive? = null): SseBody =
    SseBody(this, keepAlive?.let { KeepAliveStream.new(it) })
