package org.example

import java.io.File

object Day7 {

    fun first() {
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

    fun second() {
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

}