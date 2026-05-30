# sse-stream-kotlin in Kotlin

[![GitHub link](https://img.shields.io/badge/GitHub-KotlinMania%2Fsse--stream--kotlin-blue.svg)](https://github.com/KotlinMania/sse-stream-kotlin)
[![Maven Central](https://img.shields.io/maven-central/v/io.github.kotlinmania/sse-stream-kotlin)](https://central.sonatype.com/artifact/io.github.kotlinmania/sse-stream-kotlin)
[![Build status](https://img.shields.io/github/actions/workflow/status/KotlinMania/sse-stream-kotlin/ci.yml?branch=main)](https://github.com/KotlinMania/sse-stream-kotlin/actions)

This is a Kotlin Multiplatform line-by-line transliteration port of [`4t145/sse-stream`](https://github.com/4t145/sse-stream/).

**Original Project:** This port is based on [`4t145/sse-stream`](https://github.com/4t145/sse-stream/). All design credit and project intent belong to the upstream authors; this repository is a faithful port to Kotlin Multiplatform with no behavioural changes intended.

### Porting status

This is an **in-progress port**. The goal is feature parity with the upstream Rust crate while providing a native Kotlin Multiplatform API. Every Kotlin file carries a `// port-lint: source <path>` header naming its upstream Rust counterpart so the AST-distance tool can track provenance.

---

## Upstream README — `4t145/sse-stream`

> The text below is reproduced and lightly edited from [`https://github.com/4t145/sse-stream/`](https://github.com/4t145/sse-stream/). It is the upstream project's own description and remains under the upstream authors' authorship; links have been rewritten to absolute upstream URLs so they continue to resolve from this repository.

## SSE Stream

[![Crates.io Version](https://img.shields.io/crates/v/sse-stream)](https://crates.io/crates/sse-stream)
![Release status](https://github.com/4t145/sse-stream/actions/workflows/release.yml/badge.svg)
[![docs.rs](https://img.shields.io/docsrs/sse-stream)](https://docs.rs/sse-stream/latest/sse-stream)


A SSE decoder/encoder for Http body


## Decode
```rust
# use sse_stream::SseStream;
# use http_body_util::Full;
# use bytes::Bytes;
# use futures_util::StreamExt;
const SSE_BODY: &str =
r#"
retry: 1000
event: userconnect
data: {"username": "bobby", "time": "02:33:48"}

data: Here's a system message of some kind that will get used
data: to accomplish some task.
"#;

let body = Full::<Bytes>::from(SSE_BODY);
let mut sse_body = SseStream::new(body);
async {
    while let Some(sse) = sse_body.next().await {
        println!("{:?}", sse.unwrap());
    }
};
```

## Encode
```rust
# use std::convert::Infallible;
# use futures_util::StreamExt;
# use sse_stream::{Sse, SseBody};

let stream = futures_util::stream::iter([
    Sse::default().event("1").data("....."),
    Sse::default().event("2").data("....."),
    Sse::default().event("3").data("....."),
])
.map(Result::<Sse, Infallible>::Ok);
let body = SseBody::new(stream);
```

---

## About this Kotlin port

### Installation

```kotlin
dependencies {
    implementation("io.github.kotlinmania:sse-stream-kotlin:0.1.1")
}
```

### Building

```bash
./gradlew build
./gradlew test
```

### Targets

- macOS arm64
- Linux x64
- Windows mingw-x64
- iOS arm64 / simulator-arm64 (Swift export + XCFramework)
- JS (browser + Node.js)
- Wasm-JS (browser + Node.js)
- Android (API 24+)

### Porting guidelines

See [AGENTS.md](AGENTS.md) and [CLAUDE.md](CLAUDE.md) for translator discipline, port-lint header convention, and Rust → Kotlin idiom mapping.

### License

This Kotlin port is distributed under the same MIT license as the upstream [`4t145/sse-stream`](https://github.com/4t145/sse-stream/). See [LICENSE](LICENSE) (and any sibling `LICENSE-*` / `NOTICE` files mirrored from upstream) for the full text.

Original work copyrighted by the sse-stream authors.  
Kotlin port: Copyright (c) 2026 Sydney Renee and The Solace Project.

### Acknowledgments

Thanks to the [`4t145/sse-stream`](https://github.com/4t145/sse-stream/) maintainers and contributors for the original Rust implementation. This port reproduces their work in Kotlin Multiplatform; bug reports about upstream design or behavior should go to the upstream repository.
