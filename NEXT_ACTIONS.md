# Immediate Actions - High-Value Files

Based on AST analysis, here are the concrete next steps.

## Summary

- **Files Present:** 3/3 (100.0%)
- **Function parity:** 26/26 matched (target 54) — 100.0%
- **Class/type parity:** 9/10 matched (target 24) — 90.0%
- **Combined symbol parity:** 35/36 matched (target 78) — 97.2%
- **Average inline-code cosine:** 0.50 (function body across 2 matched files)
- **Average documentation cosine:** 0.77 (doc text across 2 matched files)
- **Cheat-zeroed Files:** 1
- **Critical Issues:** 2 files with <0.60 function similarity

## Priority 1: Fix Incomplete High-Dependency Files

No incomplete high-dependency files detected.

## Priority 2: Port Missing High-Value Files

Critical missing files (>10 dependencies):

No missing high-value files detected.

## Detailed Work Items

Every matched file is listed below with function and type symbol parity.

### 1. stream

- **Target:** `ssestream.Stream [PROVENANCE-FALLBACK]`
- **Similarity:** 0.39
- **Dependents:** 1
- **Priority Score:** 1000906.1
- **Functions:** 6/6 matched (target 20)
- **Missing functions:** _none_
- **Types:** 3/3 matched (target 14)
- **Missing types:** _none_
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `sse-stream/src/stream.rs` vs expected `stream.rs`
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `tests:sse-stream/src/stream.rs` vs expected `stream.rs`
- **Proposed provenance header:** `// port-lint: source stream.rs` (current: `// port-lint: source sse-stream/src/stream.rs`)
- **Proposed provenance header:** `// port-lint: tests stream.rs` (current: `// port-lint: tests sse-stream/src/stream.rs`)
- **Lint issues:** 2

### 2. body

- **Target:** `ssestream.Body [PROVENANCE-FALLBACK]`
- **Similarity:** 0.61
- **Dependents:** 0
- **Priority Score:** 11803.9
- **Functions:** 12/12 matched (target 20)
- **Missing functions:** _none_
- **Types:** 5/6 matched (target 7)
- **Missing types:** `Error`
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `sse-stream/src/body.rs` vs expected `body.rs`
- **Proposed provenance header:** `// port-lint: source body.rs` (current: `// port-lint: source sse-stream/src/body.rs`)
- **Lint issues:** 1

### 3. lib

- **Target:** `ssestream.Sse [STUB] [PROVENANCE-FALLBACK]`
- **Similarity:** 0.00
- **Dependents:** 0
- **Priority Score:** 910.0
- **Functions:** 8/8 matched (target 14)
- **Missing functions:** _none_
- **Types:** 1/1 matched (target 3)
- **Missing types:** _none_
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `sse-stream/src/lib.rs` vs expected `lib.rs`
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `tests:sse-stream/src/lib.rs` vs expected `lib.rs`
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `tests:sse-stream/src/lib.rs` vs expected `lib.rs`
- **Proposed provenance header:** `// port-lint: source lib.rs` (current: `// port-lint: source sse-stream/src/lib.rs`)
- **Proposed provenance header:** `// port-lint: tests lib.rs` (current: `// port-lint: tests sse-stream/src/lib.rs`)
- **Proposed provenance header:** `// port-lint: tests lib.rs` (current: `// port-lint: tests sse-stream/src/lib.rs`)
- **Lint issues:** 3

## Success Criteria

For each file to be considered "complete":
- **Similarity ≥ 0.85** (Excellent threshold)
- All public APIs ported
- All tests ported
- Documentation ported
- port-lint header present

