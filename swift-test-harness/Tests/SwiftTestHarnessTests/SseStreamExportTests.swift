import SseStream
import Testing

@Suite
struct SseStreamExportTests {
    @Test
    func swiftModuleLoads() {
        #expect(true, "SseStream swift module imported cleanly")
    }
}

