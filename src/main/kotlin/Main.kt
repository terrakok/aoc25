package org.example

import java.io.File
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sqrt

fun main() {
    aoc8_2()
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