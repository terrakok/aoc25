package org.example

import java.io.File
import kotlin.math.abs
import kotlin.math.round

object Day10X {
    private const val INF = 1e18
    private const val EPS = 1e-9

    // --- Simplex Algorithm Implementation ---
    private fun simplex(A: List<DoubleArray>, C: List<Double>): Pair<Double, DoubleArray?> {
        val m = A.size
        val n = A[0].size - 1

        // Индексы небазисных переменных (0..n-1), последний элемент -1 (фиктивный)
        val N = IntArray(n + 1) { if (it < n) it else -1 }
        // Индексы базисных переменных (изначально n..n+m-1)
        val B = IntArray(m) { n + it }

        // Таблица D (Simplex Tableau)
        // Размер (m + 2) x (n + 2).
        // Последний столбец - свободные члены.
        // Предпоследняя строка - целевая функция C.
        // Последняя строка - вспомогательная целевая функция для 1-й фазы.
        val D = Array(m + 2) { DoubleArray(n + 2) }

        // Копируем A в D
        for (i in 0 until m) {
            for (j in 0 until n) {
                D[i][j] = A[i][j]
            }
            D[i][n + 1] = A[i][n]
            D[i][n] = -1.0
        }

        for (j in 0 until n) {
            D[m][j] = C[j]
        }

        fun pivot(r: Int, s: Int) {
            val k = 1.0 / D[r][s]
            for (i in 0 until m + 2) {
                if (i == r) continue
                for (j in 0 until n + 2) {
                    if (j != s) {
                        D[i][j] -= D[r][j] * D[i][s] * k
                    }
                }
            }
            for (j in 0 until n + 2) {
                D[r][j] *= k
            }
            for (i in 0 until m + 2) {
                D[i][s] *= -k
            }
            D[r][s] = k

            val temp = B[r]
            B[r] = N[s]
            N[s] = temp
        }

        // p=1 для фазы 1 (поиск допустимого базиса), p=0 для фазы 2 (оптимизация)
        fun find(p: Int): Boolean {
            while (true) {
                // Выбор входящей переменной (столбец s) по правилу Блэнда
                var s = -1
                var minVal: Double? = null
                var minIdx = -1 // Для разрешения ничьих по индексу

                // Ищем s
                for (j in 0..n) {
                    if (p != 0 || N[j] != -1) {
                        // Python: min key=(D[m+p][x], N[x])
                        val dVal = D[m + p][j]
                        val nVal = N[j]

                        // Ищем минимум
                        if (s == -1 || dVal < minVal!! || (abs(dVal - minVal!!) < EPS && nVal < minIdx)) {
                            minVal = dVal
                            minIdx = nVal
                            s = j
                        }
                    }
                }

                if (D[m + p][s] > -EPS) return true

                // Выбор выходящей переменной (строка r)
                var r = -1
                var minRatio: Pair<Double, Int>? = null

                for (i in 0 until m) {
                    if (D[i][s] > EPS) {
                        // Python key: (D[x][-1]/D[x][s], B[x])
                        // D[x][-1] в Python это D[x][n+1] в Kotlin (RHS)
                        val ratio = D[i][n + 1] / D[i][s]
                        val bVal = B[i]

                        if (r == -1 || ratio < minRatio!!.first - EPS || (abs(ratio - minRatio!!.first) < EPS && bVal < minRatio!!.second)) {
                            minRatio = ratio to bVal
                            r = i
                        }
                    }
                }

                if (r == -1) return false
                pivot(r, s)
            }
        }

        // Phase 1 initialization
        // D[-1][n] = 1 -> D[m+1][n] = 1
        D[m + 1][n] = 1.0

        // r = min(range(m), key=lambda x: D[x][-1]) -> ищем строку с минимальным RHS
        var r = 0
        for (i in 1 until m) {
            if (D[i][n + 1] < D[r][n + 1]) {
                r = i
            }
        }

        if (D[r][n + 1] < -EPS) {
            pivot(r, n) // Pivot on auxiliary variable
            if (!find(1) || D[m + 1][n + 1] < -EPS) {
                return -INF to null
            }
        }

        // Restore proper basis if necessary
        for (i in 0 until m) {
            if (B[i] == -1) {
                // pivot(i, min(range(n), key=lambda x: (D[i][x], N[x])))
                var s = -1
                var minKey: Pair<Double, Int>? = null

                for (j in 0 until n) {
                    val dVal = D[i][j]
                    val nVal = N[j]
                    if (s == -1 || dVal < minKey!!.first - EPS || (abs(dVal - minKey!!.first) < EPS && nVal < minKey!!.second)) {
                        minKey = dVal to nVal
                        s = j
                    }
                }
                pivot(i, s)
            }
        }

        // Phase 2
        if (find(0)) {
            val x = DoubleArray(n)
            for (i in 0 until m) {
                if (B[i] in 0 until n) {
                    x[B[i]] = D[i][n + 1]
                }
            }
            var sum = 0.0
            for (i in 0 until n) {
                sum += C[i] * x[i]
            }
            return sum to x
        } else {
            return -INF to null
        }
    }

    // --- Branch and Bound ---
    private fun magic(initialA: List<DoubleArray>): Int {
        val n = initialA[0].size - 1
        var result = Double.POSITIVE_INFINITY

        // Рекурсивная функция ветвления
        fun branch(A: List<DoubleArray>) {
            val objectiveC = List(n) { 1.0 } // Целевая функция [1, 1, ..., 1] (минимизация суммы)
            val (validationVal, x) = simplex(A, objectiveC)

            // Pruning (отсечение)
            if (validationVal == -INF || validationVal + EPS >= result) {
                return
            }

            // Поиск первой нецелой переменной
            var k = -1
            var v = 0.0

            for (i in x!!.indices) {
                val e = x[i]
                if (abs(e - round(e)) > EPS) {
                    k = i
                    v = e.toInt().toDouble() // floor для положительных чисел, в данном контексте 0/1 работает как int cast
                    break
                }
            }

            if (k == -1) {
                // Целочисленное решение найдено, обновляем лучший результат
                if (validationVal + EPS < result) {
                    result = validationVal
                }
            } else {
                // Ветвь 1: x_k = floor(val)
                val s1 = DoubleArray(n + 1)
                s1[k] = 1.0
                s1[n] = v
                branch(A + listOf(s1))

                // Ветвь 2: x_k = floor(val) + 1
                val s2 = DoubleArray(n + 1)
                s2[k] = -1.0
                s2[n] = (v.toInt().inv()).toDouble() // ~v
                branch(A + listOf(s2))
            }
        }

        branch(initialA)
        return round(result).toInt()
    }

    fun second() {
        val file = File("./data/10/input.txt")

        data class Machine(
            val expected: List<Int>,
            val buttons: List<List<Int>>
        )
        val machines = file.readLines().map { l ->
            val data = l.split(" ")
            val expected = data.last().let { s ->
                s.substring(1, s.length - 1)
                    .split(',')
                    .map { it.toInt() }
            }

            val buttons = data.drop(1).dropLast(1).map { s ->
                s.substring(1, s.length - 1).split(',').map { it.toInt() }
            }

            Machine(expected, buttons)
        }

        val minClicks = machines.map { machine ->
            val (expected, buttons) = machine

            val expectedSize = expected.size
            val buttonsSize = buttons.size

            val rows = 2 * expectedSize + buttonsSize
            val cols = buttonsSize + 1
            val A = Array(rows) { DoubleArray(cols) }

            for (i in 0 until buttonsSize) {
                A[rows - 1 - i][i] = -1.0
            }

            for (i in 0 until buttonsSize) {
                for (e in buttons[i]) {
                    A[e][i] = 1.0
                    A[e + expectedSize][i] = -1.0
                }
            }

            for (i in 0 until expectedSize) {
                val cVal = expected[i].toDouble()
                A[i][cols - 1] = cVal
                A[i + expectedSize][cols - 1] = -cVal
            }

            val aList = A.map { it.clone() }.toMutableList()

            magic(aList)
        }

        println("Part 2: ${minClicks.sum()}")
    }
}