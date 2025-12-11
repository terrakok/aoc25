package org.example

import java.io.File

object Day5 {

    fun first() {
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

    fun second() {
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
}