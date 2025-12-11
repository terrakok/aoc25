package org.example

import java.io.File

object Day2 {

    fun first() {
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

    fun second() {
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
}