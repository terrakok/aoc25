package org.example

import com.google.ortools.sat.*
import com.sun.jna.platform.mac.SystemB
import java.io.File

object Day10 {
    fun first() {
        val file = File("./data/10/input.txt")

        class Line(
            val expected: Int,
            val switches: List<Int>
        )

        val machines = file.readLines().map { l ->
            val data = l.split(" ")
            val expBin = data[0].let { s ->
                s.substring(1, s.length - 1)
                    .map { if (it == '.') 0 else 1 }
                    .joinToString("")
            }
            val switchesBin = data.drop(1).dropLast(1).map { s ->
                val nums = s.substring(1, s.length - 1).split(',').map { it.toInt() }
                var switchBin = ""
                for (i in expBin.indices) {
                    switchBin += if (nums.contains(i)) "1" else "0"
                }
                switchBin
            }
            Line(expBin.toInt(2), switchesBin.map { it.toInt(2) })
        }

        val min = machines.map { machine ->

            val allComb = allSubsets(machine.switches)

            allComb.sortedBy { it.size }
                .first { comb ->
                    comb.fold(0) { acc, i -> acc.xor(i) } == machine.expected
                }
                .size
        }

        println("Result: ${min.sum()}")
    }

    fun <T> allSubsets(items: List<T>): List<List<T>> {
        val n = items.size
        val result = ArrayList<List<T>>(1.shl(n) - 1)
        for (mask in 1 until (1 shl n)) {
            val subset = ArrayList<T>()
            var i = 0
            var m = mask
            while (m != 0) {
                if ((m and 1) == 1) subset.add(items[i])
                i++
                m = m ushr 1
            }
            result.add(subset)
        }
        return result
    }

    fun second() {
        val file = File("./data/10/input.txt")

        data class Machine(
            val expected: List<Int>,
            val switches: List<List<Int>>
        )

        val machines = file.readLines().map { l ->
            val data = l.split(" ")
            val expected = data.last().let { s ->
                s.substring(1, s.length - 1)
                    .split(',')
                    .map { it.toInt() }
            }

            val switchesBin = data.drop(1).dropLast(1).map { s ->
                val nums = s.substring(1, s.length - 1).split(',').map { it.toInt() }
                val switchBin = mutableListOf<Int>()
                for (i in expected.indices) {
                    switchBin.add(if (nums.contains(i)) 1 else 0)
                }
                switchBin
            }

            Machine(expected, switchesBin)
        }

        val clicks = machines.map { machine ->
            val (expected, switches) = machine
            minClicks(expected, switches)
        }

        println("Result: ${clicks.sum()}")
    }

    fun minClicks(expected: List<Int>, switches: List<List<Int>>): Int {
        val matrix = List(expected.size) { l ->
            List(switches.size + 1) { i ->
                (if (i < switches.size) switches[i] else expected)[l]
            }
        }

        val rref = matrix.rref()

        if (rref.singleSolution()) {
            return rref.sumOf { it.last() }
        } else {
            println()
            println("MATRIX:\n${matrix.joinToString("\n") { it.joinToString(", ") }}")
            println("RREF:\n${rref.joinToString("\n") { it.joinToString(", ") { it.toString().padStart(2) } }}")

            return -1
        }
    }


    fun List<List<Int>>.rref(): List<List<Int>> {
        val matrix = this.map { it.map { it.toDouble() }.toMutableList() }.toMutableList()
        val numRows = matrix.size
        val numCols = matrix[0].size
        var lead = 0

        for (r in 0 until numRows) {
            if (lead >= numCols) {
                break
            }

            var i = r
            while (i < numRows && matrix[i][lead].toInt() == 0) {
                i++
            }

            if (i == numRows) {
                lead++
                continue
            }

            // Поменять строки
            val temp = matrix[i]
            matrix[i] = matrix[r]
            matrix[r] = temp

            // Нормализовать ведущий элемент
            val lv = matrix[r][lead]
            for (j in matrix[r].indices) {
                matrix[r][j] /= lv
            }

            // Обнулить другие элементы в ведущем столбце
            for (i in 0 until numRows) {
                if (i != r) {
                    val coeff = matrix[i][lead]
                    for (j in matrix[i].indices) {
                        matrix[i][j] -= coeff * matrix[r][j]
                    }
                }
            }

            lead++
        }

        return matrix.map { it.map { it.toInt() } }
    }

    fun List<List<Int>>.singleSolution(): Boolean {
        val m = filterNot { it.all { it == 0 } }
        val l = m.map { it.dropLast(1) }
        val r = m.map { it.last() }
        return r.all { it >= 0 } && l.isE()
    }

    fun List<List<Int>>.isE(): Boolean {
        val s = this.size
        if (this.first().size < s) return false
        for (i in 0 until s) {
            for (j in 0 until s) {
                if (i == j && this[i][j] != 1) return false
                if (i != j && this[i][j] != 0) return false
            }
        }
        return true
    }
}
