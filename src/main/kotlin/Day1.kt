package org.example

import java.io.File

object Day1 {
    fun first() {
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

    fun second() {
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
}