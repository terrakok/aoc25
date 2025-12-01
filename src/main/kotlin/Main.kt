package org.example

import java.io.File

fun main() {
    aoc2()
}

fun aoc1() {
    val file = File("/Users/konstantin.tskhovrebov/Documents/AoC25/1/input.txt")
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

fun aoc2() {
    val file = File("/Users/konstantin.tskhovrebov/Documents/AoC25/1/input.txt")
    val lines = file.readLines()
    var start = 50
    var result = 0
    lines.forEach { l ->
        val direction = l.first()
        val value = l.substring(1).toInt()

        repeat(value) {
            if (direction == 'R') { start++ } else { start-- }
            if (start == 100) start = 0
            if (start == -1) start = 99
            if (start == 0) result++
        }
    }
    println("Result: $result")
}