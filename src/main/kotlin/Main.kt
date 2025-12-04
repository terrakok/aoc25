package org.example

import java.io.File

fun main() {
    aoc4_2()
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