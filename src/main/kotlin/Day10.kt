package org.example

import com.google.ortools.sat.*
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
        }.sortedBy { it.expected.max() }

        loadOrTools()
        val clicks = machines.map { machine ->
            val (expected, switches) = machine
            minClicks(expected, switches)
        }

        println("Result: ${clicks.sum()}")
    }

    fun loadOrTools() {
        try {
            System.loadLibrary("jniortools")
        } catch (e: UnsatisfiedLinkError) {
            com.google.ortools.Loader.loadNativeLibraries()
        }
    }

    fun minClicks(expected: List<Int>, switches: List<List<Int>>): Int {
        val model = CpModel()

        val maxClickCount = expected.max().toLong()
        val xVariables = Array(switches.size) { model.newIntVar(0, maxClickCount, "x_$it") }

        expected.mapIndexed { index, target ->
            val linearExpr = LinearExpr.newBuilder().addWeightedSum(
                /* exprs = */ xVariables,
                /* coeffs = */ LongArray(switches.size) { i -> switches[i][index].toLong() }
            ).build()

            model.addEquality(linearExpr, target.toLong())
        }

        model.minimize(LinearExpr.sum(xVariables))

        val solver = CpSolver()
        solver.solve(model)
        return solver.objectiveValue().toInt()
    }

}