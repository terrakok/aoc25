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

            val clicks = machines.mapIndexed { index, machine ->
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
        val N = expected.size
        val M = switches.size
        if (N == 0 || M == 0) return -1

        val model = CpModel()

        val maxClickCount = expected.sum().toLong()
        val xVariables = List(M) { model.newIntVar(0, maxClickCount, "x_$it") }

        for (i in 0 until N) {
            val coefficients = mutableListOf<Long>()
            val variables = mutableListOf<IntVar>()

            for (j in 0 until M) {
                val coefficient = switches[j][i].toLong()
                if (coefficient > 0) {
                    coefficients.add(coefficient)
                    variables.add(xVariables[j])
                }
            }

            val linearExpr = LinearExpr.newBuilder()
                .addWeightedSum(variables.toTypedArray(), coefficients.toLongArray())
                .build()

            model.addEquality(linearExpr, expected[i].toLong())
        }

        val objective = LinearExpr.sum(xVariables.toTypedArray())
        model.minimize(objective)

        val solver = CpSolver()
        solver.getParameters().setMaxTimeInSeconds(5.0)

        val status = solver.solve(model)

        if (status == CpSolverStatus.OPTIMAL || status == CpSolverStatus.FEASIBLE) {
            return solver.objectiveValue().toInt()
        } else {
            return -1
        }
    }

}