package org.example

import java.io.File


data class Point(val r: Int, val c: Int)

typealias Fig = List<Point> //3x3
fun Fig.rotate() = map { Point(it.c, 2-it.r) }
fun Fig.flip() = map { Point(it.r, 2-it.c) }
fun Fig.variations(): List<Fig> {
    val unique = HashSet<Fig>()
    var current = this

    repeat(4) {
        unique.add(current)
        unique.add(current.flip())
        current = current.rotate()
    }

    return unique.toList()
}

object Day12 {

    fun first() {
        val file = File("./data/12/input.txt")

        val blocks = file.readText().trim().split("\n\n")
        val figs = blocks.dropLast(1).map { b ->
            b.lines().drop(1).flatMapIndexed { r, l ->
                l.mapIndexed { c, it ->
                    if (it == '#') Point(r, c) else null
                }
            }.filterNotNull()
        }

        val fields = blocks.last().lines().map { l ->
            val (st, fg) = l.split(": ")
            val (w, h) = st.split("x").map { it.toInt() }
            val fs = fg.split(' ').flatMapIndexed { index, string ->
                val count = string.toInt()
                val f = figs[index]
                List(count) { f }
            }
            Field(w, h, fs)
        }

        val res = fields.map { it.solve() }.count { it }

        println("Result: $res")
    }

    class Field(
        val w: Int,
        val h: Int,
        val figs: List<Fig>
    ) {
        private val board = Array(h) { BooleanArray(w) }
        private val processedFigures = figs.map { it.variations() }

        fun solve(): Boolean {
            val totalFigArea = processedFigures.sumOf { it.first().size }
            if (totalFigArea > w * h) return false

            return backtrack(0)
        }

        private fun backtrack(figIndex: Int): Boolean {
            if (figIndex == processedFigures.size) return true

            val variations = processedFigures[figIndex]

            for (fig in variations) {
                for (r in 0 until h) {
                    for (c in 0 until w) {
                        if (canPlace(fig, r, c)) {
                            place(fig, r, c, true)
                            if (backtrack(figIndex + 1)) return true
                            place(fig, r, c, false)
                        }
                    }
                }
            }
            return false
        }

        private fun canPlace(fig: Fig, r: Int, c: Int): Boolean {
            for (p in fig) {
                val nr = r + p.r
                val nc = c + p.c
                if (nr !in 0 until h || nc !in 0 until w || board[nr][nc]) {
                    return false
                }
            }
            return true
        }

        private fun place(fig: Fig, r: Int, c: Int, state: Boolean) {
            for (p in fig) {
                board[r + p.r][c + p.c] = state
            }
        }
    }
}