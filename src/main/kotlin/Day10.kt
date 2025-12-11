package org.example

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.runBlocking
import java.io.File

object Day10 {
    fun first() {
        val file = File("./data/10/input.txt")

        class Line(
            val expected: Int,
            val switches: List<Int>
        )
        val machines = file.readLines().map { l ->
            val data = l.split(" ")
            val expBin = data[0].let { s ->
                s.substring(1, s.length - 1)
                    .map { if (it == '.') 0 else 1 }
                    .joinToString("")
            }
            val switchesBin = data.drop(1).dropLast(1).map { s ->
                val nums = s.substring(1, s.length - 1).split(',').map { it.toInt() }
                var switchBin = ""
                for (i in expBin.indices) {
                    switchBin += if (nums.contains(i)) "1" else "0"
                }
                switchBin
            }
            Line(expBin.toInt(2), switchesBin.map { it.toInt(2) })
        }

        val min = machines.map { machine ->

            val allComb = allSubsets(machine.switches)

            allComb.sortedBy { it.size }
                .first { comb ->
                    comb.fold(0) { acc, i -> acc.xor(i) } == machine.expected
                }
                .size
        }

        println("Result: ${min.sum()}")
    }

    fun second() = runBlocking {
        val file = File("./data/10/input.txt")

        class Line(
            val expected: List<Int>,
            val switches: List<List<Boolean>>
        )
        val machines = file.readLines().map { l ->
            val data = l.split(" ")
            val expected = data.last().let { s ->
                s.substring(1, s.length - 1)
                    .split(',')
                    .map { it.toInt() }
            }

            val switchesBin = data.drop(1).dropLast(1).map { s ->
                val nums = s.substring(1, s.length - 1).split(',').map { it.toInt() }
                val switchBin = mutableListOf<Boolean>()
                for (i in expected.indices) {
                    switchBin += nums.contains(i)
                }
                switchBin
            }

            Line(expected, switchesBin)
        }.sortedByDescending { it.expected.max() }

        val res = coroutineScope {
            val mins = machines.mapIndexed { ii, machine ->
                async(Dispatchers.Default) {
                    println("machine[$ii]: ${machine.expected} - ${machine.switches.map { it.joinToString("") { if (it) "1" else "0"} }}")
                    findCLicksW(Int.MAX_VALUE, listOf(OneTask(0, machine.expected, machine.switches))).also { println("${ii} DONE=$it") }!!
                }
            }

            val result = mins.awaitAll().sum()
            result
        }
        println("Result: $res")
    }

    fun <T> allSubsets(items: List<T>): List<List<T>> {
        val n = items.size
        val result = ArrayList<List<T>>(1.shl(n) - 1)
        for (mask in 1 until (1 shl n)) {
            val subset = ArrayList<T>()
            var i = 0
            var m = mask
            while (m != 0) {
                if ((m and 1) == 1) subset.add(items[i])
                i++
                m = m ushr 1
            }
            result.add(subset)
        }
        return result
    }

    class OneTask(
        val clicks: Int = 0,
        val expected: List<Int>,
        val switches: List<List<Boolean>>,
    )

    fun log(msg: String) {
        println(msg)
    }

    fun findCLicksW(
        minSucks: Int,
        input: List<OneTask>,
    ): Int? {
//    val done = input.filter { it.expected.all { n -> n == 0 } }.sortedBy { it.clicks }
        val currentMinClicks = input.minOf { it.clicks }
        if (minSucks <= currentMinClicks) return currentMinClicks

//    done.forEach { d ->
//        if (d.clicks <= currentMinClicks) return@findCLicksW d.clicks
//    }

        val tasks = input//.filter { t -> t !in done }

        require(tasks.isNotEmpty()) { "No tasks" }

        tasks.firstOrNull { t ->
            t.expected.all { n -> n == 0 }
        }?.let {
            error("WTF!")
        }

        log("tasks: ${tasks.size} =============================")
        var sucks = minSucks

        val wtasks = tasks.mapNotNull { t ->
            val expected = t.expected
            val switches = t.switches

            log("expected: $expected switches: ${switches.map { it.joinToString("") { if (it) "1" else "0"} }}")

            val num = expected.filter { it > 0 }.min()
            val numIndex = expected.indexOf(num)
            require(numIndex >= 0)

            val numActionSwitchesIndexes = switches
                .mapIndexed { index, booleans -> index to booleans }
                .filter { (index, booleans) -> booleans[numIndex] }
                .map { it.first }

            val combo = combinationsWithRepetition(numActionSwitchesIndexes, num)

            val new = combo.mapNotNull { clickList ->
                val indexToCount = clickList.groupBy { it }.map { (index, list) -> index to list.size }
                val newExpected = expected.toMutableList()
                indexToCount.forEach { (index, count) ->
                    val click = switches[index]
                    click.forEachIndexed { i, b ->
                        if (b) {
                            newExpected[i] -= count
                            if (newExpected[i] < 0) return@mapNotNull null
                        }
                    }
                }

                newExpected
            }

            log("num: $num -> ${new.size}")

            if (new.isEmpty()) {
                return@mapNotNull null
            }

            val clickCount = t.clicks + num
            val success = new.firstOrNull { n -> n.all { it == 0 } }

            if (success != null) {
                sucks = minOf(sucks, clickCount)
                listOf(OneTask(clickCount, success, switches))
            } else {
                new.map { OneTask(clickCount, it, switches) }
            }
        }.flatten()
        if (wtasks.isEmpty()) {
//        if (done.isEmpty()) {
//            log("ERROR: ${tasks.joinToString("\n") { it.expected.joinToString(",") + " -> " + it.switches.joinToString { it.joinToString("") { if (it) "1" else "0"} } }}")
//            return null
//        }

            return sucks //done.first().clicks
        }

        return findCLicksW(sucks, wtasks)
    }

    fun combinationsWithRepetition(switches: List<Int>, count: Int): List<List<Int>> {
        val n = switches.size
        val k = count
        if (k <= 0 || n == 0) return emptyList()
        val result = mutableListOf<List<Int>>()

        val indices = MutableList(k) {0}

        while (true) {
            val combo = List(k) { i -> switches[indices[i]] }
            result.add(combo)
            var pos = k - 1
            while (pos >= 0 && indices[pos] == n - 1) pos--
            if (pos < 0) break
            indices[pos]++
            val fill = indices[pos]
            for (j in pos + 1 until k) indices[j] = fill
        }

        return result
    }
}