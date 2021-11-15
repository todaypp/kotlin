// WITH_STDLIB
// WITH_COROUTINES
import helpers.*
// WITH_CONTINUATION
import kotlin.coroutines.*
import kotlin.coroutines.intrinsics.*

suspend fun <V> suspendHere(v: V): V = suspendCoroutineUninterceptedOrReturn { x ->
    x.resume(v)
    COROUTINE_SUSPENDED
}

fun builder(c: suspend () -> String): String {
    var result = "fail"
    c.startCoroutine(handleResultContinuation {
        result = it
    })

    return result
}

fun foo(): String = builder {
    // A piece of code for next statement:
    // INVOKEVIRTUAL suspendHere
    // CHECKCAST [B
    //
    // Analyzer uses `newValue` method for estimation of type generated by CHECKCAST insn,
    // but for arrays `newValue` returned just Basic.REFERENCE_VALUE, thus we didn't add necessary checkcasts
    // for variables spilled into fields
    val data2 = suspendHere(ByteArray(2))

    suspendHere("<ignored>")
    data2.size.toString()
}

fun box(): String {
    if (foo() != "2") return "fail 1"
    return "OK"
}
