package org.example

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.runBlocking
import java.io.File
import kotlin.math.abs
import kotlin.math.exp
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.sqrt

fun main() {
    aoc11_2()
}

fun aoc1_1() {
    val file = File("./data/1/input.txt")
    val lines = file.readLines()
    var start = 50
    var result = 0
    lines.forEach { l ->
        val direction = l.first()
        val value = l.substring(1).toInt()
        if (direction == 'R') {
            start += value
        } else {
            start -= value
        }
        while (start < 0) {
            start += 100
        }
        while (start > 99) {
            start -= 100
        }
        if (start == 0) {
            result++
        }
    }
    println("Result: $result")
}

fun aoc1_2() {
    val file = File("./data/1/input.txt")
    val lines = file.readLines()
    var start = 50
    var result = 0
    lines.forEach { l ->
        val direction = l.first()
        val value = l.substring(1).toInt()

        repeat(value) {
            if (direction == 'R') {
                start++
            } else {
                start--
            }
            if (start == 100) start = 0
            if (start == -1) start = 99
            if (start == 0) result++
        }
    }
    println("Result: $result")
}

fun aoc2_1() {
    val file = File("./data/2/input.txt")
    var result = 0L
    file.readText().trim().split(',')
        .map {
            val (start, end) = it.split('-')
            for (i in start.toLong()..end.toLong()) {
                val str = i.toString()
                if (str.length % 2 != 0) continue
                if (str.substring(0, str.length / 2) == str.substring(str.length / 2)) {
                    println(i)
                    result += i
                }
            }
        }
    println("Result: $result")
}

fun aoc2_2() {
    val file = File("./data/2/input.txt")
    val invalid = mutableSetOf<Long>()
    file.readText().trim().split(',')
        .map {
            val (start, end) = it.split('-')
            for (i in start.toLong()..end.toLong()) {
                val str = i.toString()
                if (str.length < 2) continue

                for (j in 1..str.length / 2) {
                    val b = str.take(j)
                    val ex = b.repeat(str.length / b.length)
                    if (ex == str) {
                        invalid.add(i)
                    }
                }
            }
        }
    println("Invalid: ${invalid.joinToString()}")
    println("Result: ${invalid.sum()}")
}

fun aoc3_1() {
    val file = File("./data/3/input.txt")
    val result = file.readLines().mapNotNull {
        val line = it.map { it.toString().toInt() }
        if (line.size < 2) return@mapNotNull null
        val max = line.subList(0, line.size - 1).max()
        val mi = line.indexOfFirst { it == max }
        val max2 = line.subList(mi + 1, line.size).max()
        "$max$max2".toInt()
    }.sum()
    println("Result: $result")
}

fun aoc3_2() {
    val file = File("./data/3/input.txt")
    val result = file.readLines().mapNotNull { l ->
        val line = l.map { it.toString().toInt() }
        if (line.size < 12) return@mapNotNull null

        var numbers = line
        var joltage = ""
        while (joltage.length < 12) {
            var limit = numbers.size - (12 - joltage.length)
            val head = numbers.take(limit + 1)
            val max = head.max()
            val maxIndex = head.indexOfFirst { it == max }
            joltage += max.toString()
            numbers = numbers.subList(maxIndex + 1, numbers.size)
        }
        joltage.toLong()
    }.sum()
    println("Result: $result")
}

fun aoc4_1() {
    val file = File("./data/4/input.txt")
    val data = file.readLines().map { l -> l.map { it == '@' } }

    var result = 0
    for (c in data.indices) {
        for (r in data[c].indices) {
            if (!data[c][r]) continue
            var neib = 0
            for (i in c - 1..c + 1) {
                for (j in r - 1..r + 1) {
                    if (i !in data.indices || j !in data[c].indices) continue
                    if (i == c && j == r) continue
                    if (data[i][j]) neib++
                }
            }
            if (neib < 4) result++
        }
    }

    println("Result: $result")
}

fun aoc4_2() {
    val file = File("./data/4/input.txt")
    val data = file.readLines().map { l -> l.map { it == '@' }.toMutableList() }

    var result = 0
    var skip = false
    while (!skip) {
        val removed = mutableListOf<Pair<Int, Int>>()
        for (c in data.indices) {
            for (r in data[c].indices) {
                if (!data[c][r]) continue
                var neib = 0
                for (i in c - 1..c + 1) {
                    for (j in r - 1..r + 1) {
                        if (i !in data.indices || j !in data[c].indices) continue
                        if (i == c && j == r) continue
                        if (data[i][j]) neib++
                    }
                }
                if (neib < 4) {
                    removed.add(Pair(c, r))
                }
            }
        }
        if (removed.isEmpty()) {
            skip = true
        } else {
            result += removed.size
            removed.forEach { (c, r) -> data[c][r] = false }
        }
    }

    println("Result: $result")
}

fun aoc5_1() {
    val file = File("./data/5/input.txt")
    val data = file.readLines()
    val empty = data.indexOfFirst { it.isEmpty() }
    val ranges = data.subList(0, empty).map {
        val (a, b) = it.split('-').map { it.toLong() }
        a..b
    }
    val products = data.subList(empty + 1, data.size).map { it.toLong() }

    val result = products.filter { p -> ranges.any { p in it } }.count()

    println("Result: $result")
}

fun aoc5_2() {
    val file = File("./data/5/input.txt")
    val data = file.readLines()
    val empty = data.indexOfFirst { it.isEmpty() }
    val ranges = data.subList(0, empty).map {
        val (a, b) = it.split('-').map { it.toLong() }
        a..b
    }.sortedBy { it.first }

    val joined = mutableListOf(ranges.first())
    ranges.forEach { r ->
        val j = joined.last()
        if (j.last in r) {
            joined.removeLast()
            joined.add(j.first..r.last)
        } else if (j.last < r.first) {
            joined.add(r)
        }
    }

    val result = joined.sumOf { it.last - it.first + 1 }

    println("Result: $result")
}

fun aoc6_1() {
    val file = File("./data/6/input.txt")
    val lines = file.readLines()
    val data = lines.dropLast(1)
    val op = lines.last()

    val n = data.map { it.split(' ').map { it.trim() }.filter { it.isNotEmpty() } }
    val maxLine = n.maxBy { it.size }.size

    var d = 0
    var res = 0L
    repeat(maxLine) { r ->
        val nums = mutableListOf<String>()
        repeat(n.size) { c ->
            n.getOrNull(c)?.let { it.getOrNull(r) }?.let { nums.add(it) }
        }
        val l = nums.maxOf { it.length }
        val isPlus = op.substring(d, d + l).contains("+")
        d += l + 1
        res += nums.map { it.toLong() }.reduce { acc, i -> if (isPlus) acc + i else acc * i }
    }

    println("Result: $res")
}

fun aoc6_2() {
    val file = File("./data/6/input.txt")
    val lines = file.readLines()
    val w = lines.first().length
    val h = lines.size

    val rotatedLines = mutableListOf<String>()
    repeat(w) { c ->
        val s = w - c - 1
        val str = buildString {
            repeat(h) { a -> append(lines[a][s]) }
        }
        rotatedLines.add(str)
    }
    rotatedLines.add("   ")


    var res = 0L
    var nums = mutableListOf<String>()
    rotatedLines.forEach { line ->
        if (line.isBlank()) {
            var isSum = true
            val longs = nums.map { n ->
                if (n.last() == ' ') {
                    n.trim().toLong()
                } else {
                    isSum = n.last() == '+'
                    n.take(n.length - 1).trim().toLong()
                }
            }
            res += longs.reduce { acc, i -> if (isSum) acc + i else acc * i }
            nums = mutableListOf()
        } else {
            nums.add(line)
        }
    }

    println("Result: $res")
}

fun aoc7_1() {
    val file = File("./data/7/input.txt")
    val lines = file.readLines()

    val s = lines.first().indexOf('S')

    val flows = mutableSetOf(s)
    var splits = 0
    lines.forEach { l ->
        l.forEachIndexed { i, c ->
            if (c == '^' && i in flows) {
                flows.remove(i)
                flows.add(i - 1)
                flows.add(i + 1)
                splits++
            }
        }
    }

    println("Result: $splits")
}

fun aoc7_2() {
    val file = File("./data/7/input.txt")
    val lines = file.readLines()

    val w = lines.first().length
    val s = lines.first().indexOf('S')

    val flows = mutableSetOf(s)
    val timelines = mutableMapOf(s to 1L)

    lines.forEach { l ->
        l.forEachIndexed { i, c ->
            if (c == '^' && i in flows) {
                require(flows == timelines.keys)

                val count = timelines[i]!!

                flows.remove(i)
                timelines.remove(i)

                if (i > 0) {
                    flows.add(i - 1)
                    val new = timelines.getOrDefault(i - 1, 0L) + count
                    timelines[i - 1] = new
                }
                if (i < w - 1) {
                    flows.add(i + 1)
                    val new = timelines.getOrDefault(i + 1, 0L) + count
                    timelines[i + 1] = new
                }
            }
        }
    }

    val res = timelines.values.sum()
    println("Result: $res")
}

fun aoc8_1() {
    data class Box(val id: Int, val x: Long, val y: Long, val z: Long) {
        fun distance(box: Box): Double = sqrt(
            (box.x - x).toDouble().pow(2) +
                    (box.y - y).toDouble().pow(2) +
                    (box.z - z).toDouble().pow(2)
        )
    }

    class Dist(val i: Int, val j: Int, val d: Double)

    val file = File("./data/8/input.txt")
    val boxes = file.readLines().mapIndexed { i, l ->
        val (x, y, z) = l.split(',').map(String::toLong)
        Box(i, x, y, z)
    }

    val distances = boxes.mapIndexed { i, b1 ->
        boxes.takeLast(boxes.size - i - 1).mapIndexed { j, b2 ->
            Dist(i, j + i + 1, b1.distance(b2))
        }
    }
    val sorted = distances.flatten().sortedBy { it.d }

    val connections = mutableListOf<MutableList<Box>>()
    var connected = 0

    for (d in sorted) {
        val ic = connections.singleOrNull { it.contains(boxes[d.i]) }
        val jc = connections.singleOrNull { it.contains(boxes[d.j]) }

        connected++

        if (ic == null && jc == null) {
            connections += mutableListOf(boxes[d.i], boxes[d.j])
            println("connect: ${boxes[d.i]} - ${boxes[d.j]}")
        } else if (ic == null && jc != null) {
            jc.add(boxes[d.i])
            println("connect: ${boxes[d.i]} - ${boxes[d.j]}")
        } else if (ic != null && jc == null) {
            ic.add(boxes[d.j])
            println("connect: ${boxes[d.i]} - ${boxes[d.j]}")
        } else if (ic != null && jc != null) {
            if (ic == jc) {
                //why?!
//                connected--
                println("skip: ${boxes[d.i]} - ${boxes[d.j]}")
            } else {
                ic.addAll(jc)
                connections.remove(jc)
                println("connect groups: ${boxes[d.i]} - ${boxes[d.j]}")
            }
        } else {
            error("Something went wrong")
        }

        if (connected == 1000) {
            break
        }
    }

    val (c1, c2, c3) = connections.map { it.size }.sortedDescending().take(3)
    println("Connected: ${connections.map { it.size }.sortedDescending().joinToString()}")

    println("Result: ${c1 * c2 * c3}")
}

fun aoc8_2() {
    data class Box(val id: Int, val x: Long, val y: Long, val z: Long) {
        fun distance(box: Box): Double = sqrt(
            (box.x - x).toDouble().pow(2) +
                    (box.y - y).toDouble().pow(2) +
                    (box.z - z).toDouble().pow(2)
        )
    }

    class Dist(val i: Int, val j: Int, val d: Double)

    val file = File("./data/8/input.txt")
    val boxes = file.readLines().mapIndexed { i, l ->
        val (x, y, z) = l.split(',').map(String::toLong)
        Box(i, x, y, z)
    }

    val distances = boxes.mapIndexed { i, b1 ->
        boxes.takeLast(boxes.size - i - 1).mapIndexed { j, b2 ->
            Dist(i, j + i + 1, b1.distance(b2))
        }
    }
    val sorted = distances.flatten().sortedBy { it.d }

    val connections = mutableListOf<MutableList<Box>>()
    var connected = 0

    var result = 0L
    for (d in sorted) {
        val ic = connections.singleOrNull { it.contains(boxes[d.i]) }
        val jc = connections.singleOrNull { it.contains(boxes[d.j]) }

        connected++

        if (ic == null && jc == null) {
            connections += mutableListOf(boxes[d.i], boxes[d.j])
            println("connect: ${boxes[d.i]} - ${boxes[d.j]}")
        } else if (ic == null && jc != null) {
            jc.add(boxes[d.i])
            println("connect: ${boxes[d.i]} - ${boxes[d.j]}")
        } else if (ic != null && jc == null) {
            ic.add(boxes[d.j])
            println("connect: ${boxes[d.i]} - ${boxes[d.j]}")
        } else if (ic != null && jc != null) {
            if (ic == jc) {
                //why?!
//                connected--
                println("skip: ${boxes[d.i]} - ${boxes[d.j]}")
            } else {
                ic.addAll(jc)
                connections.remove(jc)
                println("connect groups: ${boxes[d.i]} - ${boxes[d.j]}")
            }
        } else {
            error("Something went wrong")
        }

        if (connections.size == 1 && connections.first().size == boxes.size) {
            result = boxes[d.i].x * boxes[d.j].x
            break
        }
    }

    println("Result: $result")
}

fun aoc9_1() {
    val file = File("./data/9/input.txt")

    data class Point(val id: Int, val x: Long, val y: Long) {
        fun sq(p: Point) = abs(x - p.x + 1) * abs(y - p.y + 1)
    }

    val points = file.readLines().mapIndexed { index, s ->
        val (x, y) = s.split(',')
        Point(index, x.toLong(), y.toLong())
    }

    val res = points.flatMap { p1 -> points.map { p2 -> p1.sq(p2) }.filter { it > 0 } }.max()

    println("Result: $res")
}

fun aoc9_2() {
    val file = File("./data/9/input.txt")

    data class Point(val x: Int, val y: Int)
    data class Rect(val p1: Point, val p2: Point) {
        val tl = Point(min(p1.x, p2.x), min(p1.y, p2.y))
        val br = Point(max(p1.x, p2.x), max(p1.y, p2.y))
        fun sq() = (abs(p2.x - p1.x) + 1) * (abs(p2.y - p1.y) + 1)

        override fun toString(): String = "${p1.x},${p1.y}_${p2.x},${p2.y}"
    }

    val points = file.readLines().map { s ->
        val (x, y) = s.split(',')
        Point(x.toInt(), y.toInt())
    }

    val gates = mutableListOf<Pair<Int, List<IntRange>>>()
    points.groupBy { it.y }.entries.sortedBy { it.key }.forEach { (y, points) ->
        val sortedPoint = points.sortedBy { it.x }
        val lineR = List(sortedPoint.size / 2) { sortedPoint[2 * it].x..sortedPoint[2 * it + 1].x }
        val prev = gates.lastOrNull()?.second.orEmpty()
        val next = prev.apply(lineR)
        gates.add(y to next)
    }


    val allRects = points.flatMapIndexed { index, p1 ->
        points.subList(index + 1, points.size).map { p2 -> Rect(p1, p2) }
    }

    val goodRects = allRects.filter { rect ->
        val (tl, br) = rect.let { it.tl to it.br }
        val w = tl.x..br.x
        val road = gates.filter { (y, ranges) -> y in tl.y until br.y }.map { it.second }

        road.all { line ->
            line.any { range -> w.first in range && w.last in range }
        }
    }

    val res = goodRects.maxOfOrNull { it.sq() }
    println("RESULT: $res")
}

private fun List<IntRange>.apply(next: List<IntRange>): List<IntRange> {
    val current = this
    if (current.isEmpty()) return next

    val active = current.toMutableList()
    next.forEach { lineR ->
        val n1 = active.singleOrNull { lineR.first == it.first }
        if (n1 != null) {
            require(lineR.last <= n1.last)
            active.remove(n1)
            if (lineR.last < n1.last) {
                active.add(lineR.last..n1.last)
            }
        } else {
            val n2 = active.singleOrNull { lineR.last == it.last }
            if (n2 != null) {
                require(lineR.first >= n2.first)
                active.remove(n2)
                active.add(n2.first..lineR.first)
            } else {
                val n3 = active.singleOrNull { lineR.first == it.last }
                val n4 = active.singleOrNull { lineR.last == it.first }
                if (n3 != null && n4 != null) {
                    active.remove(n3)
                    active.remove(n4)
                    active.add(n3.first..n4.last)
                } else if (n3 != null) {
                    active.remove(n3)
                    active.add(n3.first..lineR.last)
                } else if (n4 != null) {
                    active.remove(n4)
                    active.add(lineR.first..n4.last)
                } else {
                    active.add(lineR)
                }
            }
        }
    }

    return active.distinct().sortedBy { it.first }
}

fun aoc10_1() {
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

fun aoc10_2() = runBlocking {
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