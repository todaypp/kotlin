// EXPECTED_REACHABLE_NODES: 1302
// SKIP_IR_INCREMENTAL_CHECKS
// WITH_STDLIB
// FILE: a.kt
import kotlin.coroutines.*
import kotlin.coroutines.intrinsics.*

suspend fun suspendThere(v: String): String = suspendCoroutineUninterceptedOrReturn { x ->
    x.resume(v)
    COROUTINE_SUSPENDED
}

suspend fun suspendHere(): String = suspendThere("O") + suspendThere("K")

// FILE: b.kt
// RECOMPILE
import kotlin.coroutines.*
import kotlin.coroutines.intrinsics.*

fun builder(c: suspend () -> Unit) {
    c.startCoroutine(object : Continuation<Unit> {
        override val context = EmptyCoroutineContext

        override fun resumeWith(result: Result<Unit>) {}
    })
}

fun box(): String {
    var result = ""

    builder {
        result = suspendHere()
    }

    return result
}
