# Immediate Actions - High-Value Files

Based on AST analysis, here are the concrete next steps.

## Summary

- **Files Present:** 3/9 (33.3%)
- **Function parity:** 18/25 matched (target 40) — 72.0%
- **Class/type parity:** 8/9 matched (target 21) — 88.9%
- **Combined symbol parity:** 26/34 matched (target 61) — 76.5%
- **Average inline-code cosine:** 0.50 (function body across 2 matched files)
- **Average documentation cosine:** 0.77 (doc text across 2 matched files)
- **Cheat-zeroed Files:** 0
- **Critical Issues:** 2 files with <0.60 function similarity

## Priority 1: Fix Incomplete High-Dependency Files

No incomplete high-dependency files detected.

## Priority 2: Port Missing High-Value Files

Critical missing files (>10 dependencies):

No missing high-value files detected.

## Detailed Work Items

Every matched file is listed below with function and type symbol parity.

### 1. sse-stream.stream

- **Target:** `ssestream.Stream`
- **Similarity:** 0.39
- **Dependents:** 1
- **Priority Score:** 1000906.1
- **Functions:** 6/6 matched (target 20)
- **Missing functions:** _none_
- **Types:** 3/3 matched (target 14)
- **Missing types:** _none_

### 2. sse-stream.body

- **Target:** `ssestream.Body`
- **Similarity:** 0.61
- **Dependents:** 0
- **Priority Score:** 11803.9
- **Functions:** 12/12 matched (target 20)
- **Missing functions:** _none_
- **Types:** 5/6 matched (target 7)
- **Missing types:** `Error`

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

### Matched

| Source | Target | Path |
|--------|--------|------|
| `sse-stream.lib` | `ssestream.Sse` | `sse-stream/src/lib` |

