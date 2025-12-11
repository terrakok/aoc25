package org.example

import java.io.File
import kotlin.collections.component1
import kotlin.collections.component2
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

object Day9 {

    fun first() {
        val file = File("./data/9/input.txt")

        data class Point(val id: Int, val x: Long, val y: Long) {
            fun sq(p: Point) = abs(x - p.x + 1) * abs(y - p.y + 1)
        }

        val points = file.readLines().mapIndexed { index, s ->
            val (x, y) = s.split(',')
            Point(index, x.toLong(), y.toLong())
        }

        val res = points.flatMap { p1 -> points.map { p2 -> p1.sq(p2) }.filter { it > 0 } }.max()

        println("Result: $res")
    }

    fun second() {
        val file = File("./data/9/input.txt")

        data class Point(val x: Int, val y: Int)
        data class Rect(val p1: Point, val p2: Point) {
            val tl = Point(min(p1.x, p2.x), min(p1.y, p2.y))
            val br = Point(max(p1.x, p2.x), max(p1.y, p2.y))
            fun sq() = (abs(p2.x - p1.x) + 1) * (abs(p2.y - p1.y) + 1)

            override fun toString(): String = "${p1.x},${p1.y}_${p2.x},${p2.y}"
        }

        val points = file.readLines().map { s ->
            val (x, y) = s.split(',')
            Point(x.toInt(), y.toInt())
        }

        val gates = mutableListOf<Pair<Int, List<IntRange>>>()
        points.groupBy { it.y }.entries.sortedBy { it.key }.forEach { (y, points) ->
            val sortedPoint = points.sortedBy { it.x }
            val lineR = List(sortedPoint.size / 2) { sortedPoint[2 * it].x..sortedPoint[2 * it + 1].x }
            val prev = gates.lastOrNull()?.second.orEmpty()
            val next = prev.apply(lineR)
            gates.add(y to next)
        }


        val allRects = points.flatMapIndexed { index, p1 ->
            points.subList(index + 1, points.size).map { p2 -> Rect(p1, p2) }
        }

        val goodRects = allRects.filter { rect ->
            val (tl, br) = rect.let { it.tl to it.br }
            val w = tl.x..br.x
            val road = gates.filter { (y, ranges) -> y in tl.y until br.y }.map { it.second }

            road.all { line ->
                line.any { range -> w.first in range && w.last in range }
            }
        }

        val res = goodRects.maxOfOrNull { it.sq() }
        println("RESULT: $res")
    }

    private fun List<IntRange>.apply(next: List<IntRange>): List<IntRange> {
        val current = this
        if (current.isEmpty()) return next

        val active = current.toMutableList()
        next.forEach { lineR ->
            val n1 = active.singleOrNull { lineR.first == it.first }
            if (n1 != null) {
                require(lineR.last <= n1.last)
                active.remove(n1)
                if (lineR.last < n1.last) {
                    active.add(lineR.last..n1.last)
                }
            } else {
                val n2 = active.singleOrNull { lineR.last == it.last }
                if (n2 != null) {
                    require(lineR.first >= n2.first)
                    active.remove(n2)
                    active.add(n2.first..lineR.first)
                } else {
                    val n3 = active.singleOrNull { lineR.first == it.last }
                    val n4 = active.singleOrNull { lineR.last == it.first }
                    if (n3 != null && n4 != null) {
                        active.remove(n3)
                        active.remove(n4)
                        active.add(n3.first..n4.last)
                    } else if (n3 != null) {
                        active.remove(n3)
                        active.add(n3.first..lineR.last)
                    } else if (n4 != null) {
                        active.remove(n4)
                        active.add(lineR.first..n4.last)
                    } else {
                        active.add(lineR)
                    }
                }
            }
        }

        return active.distinct().sortedBy { it.first }
    }
}