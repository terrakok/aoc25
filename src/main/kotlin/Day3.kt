package org.example

import java.io.File

object Day3 {

    fun first() {
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

    fun second() {
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
}