// Auto-generated by GenerateSteppedRangesCodegenTestData. Do not edit!
// WITH_STDLIB
import kotlin.test.*

fun box(): String {
    assertFailsWith<IllegalArgumentException> {
        for (i in 1u until 8u step 2 step 0) {
        }
    }

    assertFailsWith<IllegalArgumentException> {
        for (i in 1uL until 8uL step 2L step 0L) {
        }
    }

    return "OK"
}