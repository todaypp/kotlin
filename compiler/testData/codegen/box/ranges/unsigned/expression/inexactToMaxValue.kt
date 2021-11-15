// Auto-generated by org.jetbrains.kotlin.generators.tests.GenerateRangesCodegenTestData. DO NOT EDIT!
// WITH_STDLIB


val MaxUI = UInt.MAX_VALUE
val MaxUB = UByte.MAX_VALUE
val MaxUS = UShort.MAX_VALUE
val MaxUL = ULong.MAX_VALUE

fun box(): String {
    val list1 = ArrayList<UInt>()
    val range1 = (MaxUI - 5u)..MaxUI step 3
    for (i in range1) {
        list1.add(i)
        if (list1.size > 23) break
    }
    if (list1 != listOf<UInt>(MaxUI - 5u, MaxUI - 2u)) {
        return "Wrong elements for (MaxUI - 5u)..MaxUI step 3: $list1"
    }

    val list2 = ArrayList<UInt>()
    val range2 = (MaxUB - 5u).toUByte()..MaxUB step 3
    for (i in range2) {
        list2.add(i)
        if (list2.size > 23) break
    }
    if (list2 != listOf<UInt>((MaxUB - 5u).toUInt(), (MaxUB - 2u).toUInt())) {
        return "Wrong elements for (MaxUB - 5u).toUByte()..MaxUB step 3: $list2"
    }

    val list3 = ArrayList<UInt>()
    val range3 = (MaxUS - 5u).toUShort()..MaxUS step 3
    for (i in range3) {
        list3.add(i)
        if (list3.size > 23) break
    }
    if (list3 != listOf<UInt>((MaxUS - 5u).toUInt(), (MaxUS - 2u).toUInt())) {
        return "Wrong elements for (MaxUS - 5u).toUShort()..MaxUS step 3: $list3"
    }

    val list4 = ArrayList<ULong>()
    val range4 = (MaxUL - 5u)..MaxUL step 3
    for (i in range4) {
        list4.add(i)
        if (list4.size > 23) break
    }
    if (list4 != listOf<ULong>((MaxUL - 5u), (MaxUL - 2u))) {
        return "Wrong elements for (MaxUL - 5u)..MaxUL step 3: $list4"
    }

    return "OK"
}
