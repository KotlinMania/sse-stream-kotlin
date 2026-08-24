# Immediate Actions - High-Value Files

Based on AST analysis, here are the concrete next steps.

## Summary

- **Files Present:** 3/9 (33.3%)
- **Function parity:** 26/33 matched (target 53) — 78.8%
- **Class/type parity:** 9/10 matched (target 23) — 90.0%
- **Combined symbol parity:** 35/43 matched (target 76) — 81.4%
- **Average inline-code cosine:** 0.58 (function body across 3 matched files)
- **Average documentation cosine:** 0.51 (doc text across 3 matched files)
- **Cheat-zeroed Files:** 0
- **Critical Issues:** 1 files with <0.60 function similarity

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
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `stream.rs` vs expected `stream.rs`
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `tests:stream.rs` vs expected `stream.rs`
- **Proposed provenance header:** `// port-lint: source stream.rs` (current: `// port-lint: source stream.rs`)
- **Proposed provenance header:** `// port-lint: tests stream.rs` (current: `// port-lint: tests stream.rs`)
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
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `body.rs` vs expected `body.rs`
- **Proposed provenance header:** `// port-lint: source body.rs` (current: `// port-lint: source body.rs`)
- **Lint issues:** 1

### 3. lib

- **Target:** `ssestream.Sse [PROVENANCE-FALLBACK]`
- **Similarity:** 0.72
- **Dependents:** 0
- **Priority Score:** 902.8
- **Functions:** 8/8 matched (target 13)
- **Missing functions:** _none_
- **Types:** 1/1 matched (target 2)
- **Missing types:** _none_
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `lib.rs` vs expected `lib.rs`
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `tests:lib.rs` vs expected `lib.rs`
- **Proposed provenance header:** `// port-lint: source lib.rs` (current: `// port-lint: source lib.rs`)
- **Proposed provenance header:** `// port-lint: tests lib.rs` (current: `// port-lint: tests lib.rs`)
- **Lint issues:** 2

## Success Criteria

For each file to be considered "complete":
- **Similarity ≥ 0.85** (Excellent threshold)
- All public APIs ported
- All tests ported
- Documentation ported
- port-lint header present

## Reexport / Wiring Modules

These files match `reexport_modules` patterns in `.ast_distance_config.json`. They are filtered out of
normal priority and missing-file ladders because they are wiring
modules, not direct logic ports. Consult them for call-site routing;
do not treat them as the next implementation target by default.

### Missing

| Source | Expected target | Deps | Source path | Expected path |
|--------|-----------------|------|-------------|---------------|
| `sse_server_side.mod` | `tests.sseserverside.Mod` | 0 | `tests/sse_server_side/mod.rs` | `tests/sseserverside/Mod.kt` |

