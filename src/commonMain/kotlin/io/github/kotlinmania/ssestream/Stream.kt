// port-lint: source sse-stream/src/stream.rs
package io.github.kotlinmania.ssestream

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.flow

typealias ByteStreamBody = Flow<ByteArray>
typealias Item = Result<Sse>

sealed class Error(
    message: String? = null,
    cause: Throwable? = null,
) : Exception(message, cause) {
    data class Body(
        val causeThrowable: Throwable,
    ) : Error(causeThrowable.message, causeThrowable)

    data object InvalidLine : Error("invalid line")

    data object DuplicatedEventLine : Error("duplicated event line")

    data object DuplicatedIdLine : Error("duplicated id line")

    data object DuplicatedRetry : Error("duplicated retry line")

    data class Utf8Parse(
        val detail: String,
    ) : Error("utf8 parse error: $detail")

    data class IntParse(
        val detail: String,
    ) : Error("int parse error: $detail")

    fun fmt(): String =
        when (this) {
            is Body -> "body error: ${causeThrowable.message}"
            is InvalidLine -> "invalid line"
            is DuplicatedEventLine -> "duplicated event line"
            is DuplicatedIdLine -> "duplicated id line"
            is DuplicatedRetry -> "duplicated retry line"
            is Utf8Parse -> "utf8 parse error: $detail"
            is IntParse -> "int parse error: $detail"
        }

    fun description(): String =
        when (this) {
            is Body -> "body error"
            is InvalidLine -> "invalid line"
            is DuplicatedEventLine -> "duplicated event line"
            is DuplicatedIdLine -> "duplicated id line"
            is DuplicatedRetry -> "duplicated retry line"
            is Utf8Parse -> "utf8 parse error"
            is IntParse -> "int parse error"
        }

    fun source(): Throwable? =
        when (this) {
            is Body -> causeThrowable
            else -> null
        }
}

typealias SseError = Error

class SseStream(
    private val body: Flow<ByteArray>,
) : Flow<Result<Sse>> {
    private val parsed: ArrayDeque<Sse> = ArrayDeque()
    private var current: Sse? = null
    private var unfinishedLine: ByteArray = ByteArray(0)

    companion object {
        /**
         * Create a new [SseStream] from a stream of [ByteArray].
         */
        fun fromByteStream(stream: Flow<ByteArray>): SseStream = SseStream(stream)

        /**
         * Create a new [SseStream] from a [Flow].
         */
        fun new(body: Flow<ByteArray>): SseStream = SseStream(body)

        /**
         * Create a new [SseStream] from an [SseBody].
         */
        fun new(body: SseBody): SseStream = new(body.toByteFlow())
    }

    fun decodeChunk(chunk: ByteArray): List<Result<Sse>> {
        val results = mutableListOf<Result<Sse>>()
        if (chunk.isEmpty()) return results

        val combined =
            if (unfinishedLine.isNotEmpty()) {
                val total = unfinishedLine + chunk
                unfinishedLine = ByteArray(0)
                total
            } else {
                chunk
            }

        var start = 0
        var i = 0
        while (i < combined.size) {
            if (combined[i] == '\n'.code.toByte()) {
                val lineBytes = combined.copyOfRange(start, i)
                val line =
                    if (lineBytes.isNotEmpty() && lineBytes.last() == '\r'.code.toByte()) {
                        lineBytes.copyOfRange(0, lineBytes.size - 1)
                    } else {
                        lineBytes
                    }
                start = i + 1
                val err = processLine(line)
                if (err != null) {
                    results.add(Result.failure(err))
                } else if (line.isEmpty()) {
                    while (parsed.isNotEmpty()) {
                        results.add(Result.success(parsed.removeFirst()))
                    }
                }
            } else if (combined[i] == '\r'.code.toByte() && i + 1 < combined.size && combined[i + 1] == '\n'.code.toByte()) {
                val lineBytes = combined.copyOfRange(start, i)
                start = i + 2
                i++
                val err = processLine(lineBytes)
                if (err != null) {
                    results.add(Result.failure(err))
                } else if (lineBytes.isEmpty()) {
                    while (parsed.isNotEmpty()) {
                        results.add(Result.success(parsed.removeFirst()))
                    }
                }
            } else if (combined[i] == '\r'.code.toByte() && (i + 1 == combined.size || combined[i + 1] != '\n'.code.toByte())) {
                val lineBytes = combined.copyOfRange(start, i)
                start = i + 1
                val err = processLine(lineBytes)
                if (err != null) {
                    results.add(Result.failure(err))
                } else if (lineBytes.isEmpty()) {
                    while (parsed.isNotEmpty()) {
                        results.add(Result.success(parsed.removeFirst()))
                    }
                }
            }
            i++
        }
        if (start < combined.size) {
            unfinishedLine = combined.copyOfRange(start, combined.size)
        }

        while (parsed.isNotEmpty()) {
            results.add(Result.success(parsed.removeFirst()))
        }
        return results
    }

    fun finish(): List<Result<Sse>> {
        val results = mutableListOf<Result<Sse>>()
        if (unfinishedLine.isNotEmpty()) {
            val err = processLine(unfinishedLine)
            unfinishedLine = ByteArray(0)
            if (err != null) {
                results.add(Result.failure(err))
            }
        }
        val sse = current
        current = null
        if (sse != null) {
            results.add(Result.success(sse))
        }
        while (parsed.isNotEmpty()) {
            results.add(Result.success(parsed.removeFirst()))
        }
        return results
    }

    fun decode(bytes: ByteArray): List<Result<Sse>> {
        val out = mutableListOf<Result<Sse>>()
        out.addAll(decodeChunk(bytes))
        out.addAll(finish())
        return out
    }

    fun pollNext(chunk: ByteArray? = null): Result<Sse>? {
        if (parsed.isNotEmpty()) {
            return Result.success(parsed.removeFirst())
        }
        if (chunk == null || chunk.isEmpty()) {
            val sse = current
            current = null
            return sse?.let { Result.success(it) }
        }

        val results = decodeChunk(chunk)
        if (results.isNotEmpty()) {
            for (j in 1 until results.size) {
                val res = results[j]
                if (res.isSuccess) {
                    parsed.add(res.getOrThrow())
                }
            }
            return results[0]
        }
        return null
    }

    private fun processLine(line: ByteArray): Error? {
        if (line.isEmpty()) {
            val sse = current
            current = null
            if (sse != null) {
                parsed.add(sse)
            }
            return null
        }

        val colonIndex = line.indexOf(':'.code.toByte())
        if (colonIndex == -1) {
            return Error.InvalidLine
        }

        val fieldName = line.copyOfRange(0, colonIndex).decodeToString()
        val rawFieldValue =
            if (line.size > colonIndex + 1) {
                val valueBytes = line.copyOfRange(colonIndex + 1, line.size)
                if (valueBytes.isNotEmpty() && valueBytes[0] == ' '.code.toByte()) {
                    valueBytes.copyOfRange(1, valueBytes.size)
                } else {
                    valueBytes
                }
            } else {
                ByteArray(0)
            }

        when (fieldName) {
            "data" -> {
                val dataLine =
                    try {
                        rawFieldValue.decodeToString()
                    } catch (e: Throwable) {
                        return Error.Utf8Parse(e.message ?: "decode error")
                    }
                val cur = current
                if (cur == null) {
                    current = Sse(data = dataLine)
                } else {
                    if (cur.data == null) {
                        cur.data = dataLine
                    } else {
                        cur.data = cur.data + "\n" + dataLine
                    }
                }
            }
            "event" -> {
                val eventValue =
                    try {
                        rawFieldValue.decodeToString()
                    } catch (e: Throwable) {
                        return Error.Utf8Parse(e.message ?: "decode error")
                    }
                val cur = current
                if (cur == null) {
                    current = Sse(event = eventValue)
                } else {
                    if (cur.event != null) {
                        return Error.DuplicatedEventLine
                    } else {
                        cur.event = eventValue
                    }
                }
            }
            "id" -> {
                val idValue =
                    try {
                        rawFieldValue.decodeToString()
                    } catch (e: Throwable) {
                        return Error.Utf8Parse(e.message ?: "decode error")
                    }
                val cur = current
                if (cur == null) {
                    current = Sse(id = idValue)
                } else {
                    if (cur.id != null) {
                        return Error.DuplicatedIdLine
                    } else {
                        cur.id = idValue
                    }
                }
            }
            "retry" -> {
                val retryStr =
                    try {
                        rawFieldValue.decodeToString().trim()
                    } catch (e: Throwable) {
                        return Error.Utf8Parse(e.message ?: "decode error")
                    }
                val retryValue =
                    retryStr.toULongOrNull()
                        ?: return Error.IntParse("Invalid integer: $retryStr")
                val cur = current
                if (cur == null) {
                    current = Sse(retry = retryValue)
                } else {
                    if (cur.retry != null) {
                        return Error.DuplicatedRetry
                    } else {
                        cur.retry = retryValue
                    }
                }
            }
            "" -> {
                // Comment line, ignored
            }
            else -> {
                return Error.InvalidLine
            }
        }
        return null
    }

    override suspend fun collect(collector: FlowCollector<Result<Sse>>) {
        val decoder = SseStream(body)
        try {
            body.collect { chunk ->
                val results = decoder.decodeChunk(chunk)
                for (res in results) {
                    collector.emit(res)
                }
            }
            val finalResults = decoder.finish()
            for (res in finalResults) {
                collector.emit(res)
            }
        } catch (t: Throwable) {
            collector.emit(Result.failure(Error.Body(t)))
        }
    }

    fun asFlow(): Flow<Result<Sse>> = this
}

class SseDecoder {
    private val stream = SseStream.new(flow {})

    fun decodeChunk(chunk: ByteArray): List<Result<Sse>> = stream.decodeChunk(chunk)

    fun finish(): List<Result<Sse>> = stream.finish()

    fun decode(bytes: ByteArray): List<Result<Sse>> = stream.decode(bytes)
}

fun Flow<ByteArray>.decodeSseStream(): Flow<Result<Sse>> = SseStream.new(this)
