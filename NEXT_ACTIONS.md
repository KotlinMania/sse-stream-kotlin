# Immediate Actions - High-Value Files

Based on AST analysis, here are the concrete next steps.

## Summary

- **Files Present:** 3/9 (33.3%)
- **Function parity:** 26/33 matched (target 54) — 78.8%
- **Class/type parity:** 9/10 matched (target 24) — 90.0%
- **Combined symbol parity:** 35/43 matched (target 78) — 81.4%
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

### 3. sse-stream.lib

- **Target:** `ssestream.Sse [STUB]`
- **Similarity:** 0.00
- **Dependents:** 0
- **Priority Score:** 910.0
- **Functions:** 8/8 matched (target 14)
- **Missing functions:** _none_
- **Types:** 1/1 matched (target 3)
- **Missing types:** _none_

## Success Criteria

For each file to be considered "complete":
- **Similarity ≥ 0.85** (Excellent threshold)
- All public APIs ported
- All tests ported
- Documentation ported
- port-lint header present

