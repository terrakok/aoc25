package org.example

import java.io.File
import kotlin.collections.indices

object Day4 {

    fun first() {
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

    fun second() {
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
}