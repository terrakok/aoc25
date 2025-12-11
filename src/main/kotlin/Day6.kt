package org.example

import java.io.File

object Day6 {

    fun first() {
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

    fun second() {
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
}