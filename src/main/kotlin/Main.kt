package org.example

//fun main() {
//    Day10.second()
//}

import java.io.File
import java.util.Scanner
import kotlin.math.abs
import kotlin.math.round
import kotlin.math.roundToInt

const val INF = 1e18 // Достаточно большое число
const val EPS = 1e-9

// --- Simplex Algorithm Implementation ---

/**
 * Решает задачу линейного программирования методом Симплекса.
 * @param A Матрица ограничений (включая свободные члены в последнем столбце).
 * @param C Коэффициенты целевой функции.
 * @return Pair(Значение целевой функции, Вектор решения X) или Pair(-INF, null) если решения нет.
 */
fun simplex(A: List<DoubleArray>, C: List<Double>): Pair<Double, DoubleArray?> {
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
        // В Python коде D[i][-1] это последний элемент, D[i][-2] предпоследний.
        // Изначально A[i][-1] это RHS.
        // Python: [*A[i], -1] -> A[i][0..n-1], RHS, -1
        // Потом swap: D[i][-2], D[i][-1] = D[i][-1], D[i][-2]
        // Значит в итоге: A[i]..., -1, RHS
        // В Kotlin D[i][n] это предпоследний, D[i][n+1] это последний.
        D[i][n + 1] = A[i][n] // RHS
        D[i][n] = -1.0        // Вспомогательная переменная для 1-й фазы
    }

    // Заполняем строку целевой функции (C)
    for (j in 0 until n) {
        D[m][j] = C[j]
    }
    // Остальные элементы (включая последнюю строку) инициализируются нулями по умолчанию

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

        // Swap basic and non-basic indices
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

fun f(initialA: List<DoubleArray>): Int {
    val n = initialA[0].size - 1
    var bVal = Double.POSITIVE_INFINITY

    // Рекурсивная функция ветвления
    fun branch(A: List<DoubleArray>) {
        val objectiveC = List(n) { 1.0 } // Целевая функция [1, 1, ..., 1] (минимизация суммы)
        val (validationVal, x) = simplex(A, objectiveC)

        // Pruning (отсечение)
        if (validationVal == -INF || validationVal + EPS >= bVal) {
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
            if (validationVal + EPS < bVal) {
                bVal = validationVal
            }
        } else {
            // Ветвление: x_k <= v  OR  x_k >= v + 1
            // 1. x_k <= floor(val)  ->  x_k + slack = floor(val) -> но симплекс работает с равенствами Ax=b.
            // В Python коде: s = [0]*n+[v]; s[k] = 1 -> добавляется строка: 1*x_k + 0*... = v.
            // Это ограничение равенства?
            // Смотрим Python: A+[s]. s[k]=1. s[-1]=v. Это уравнение x_k = v ?
            // В контексте ILP обычно добавляют неравенства. Но здесь используется симплекс.
            // Если добавить x_k = 0, а потом x_k = 1, это перебор значений (так как переменные 0/1).
            // В общем случае:
            // Python s = [0]*n+[v]; s[k]=1. Это x_k = v.
            // Python s = [0]*n+[~v]; s[k]=-1. ~v = -v-1. -x_k = -v-1 => x_k = v+1.
            // Да, это фиксация переменных на 0 или 1 (так как задача бинарная).

            // Ветвь 1: x_k = floor(val)
            val s1 = DoubleArray(n + 1)
            s1[k] = 1.0
            s1[n] = v
            branch(A + listOf(s1))

            // Ветвь 2: x_k = floor(val) + 1
            // Python: s = [0]*n+[~v]; s[k] = -1.
            // ~v (bitwise not) для int v=0 -> -1. Для v=1 -> -2.
            // Уравнение: -1 * x_k = ~v
            // Если v=0: -x_k = -1 => x_k = 1.
            // Если v=1: -x_k = -2 => x_k = 2.
            // Это реализация x_k >= v + 1, но в формате равенства для бинарных переменных это фиксация.
            val s2 = DoubleArray(n + 1)
            s2[k] = -1.0
            s2[n] = (v.toInt().inv()).toDouble() // ~v
            branch(A + listOf(s2))
        }
    }

    branch(initialA)
    return round(bVal).toInt()
}

// --- Main Parsing and Logic ---

fun main() {

    val file = File("./data/10/input.txt")

    val scanner = file.reader()
    var p1Total = 0
    var p2Total = 0

    for (line in scanner.readLines()) {
        if (line.isBlank()) continue

        // Python: m, *p, c = l.split()
        // Пример строки ожидается: "####. (0,1) (1,2) 0,0,0,1"
        // Точный формат строки важен. Исходя из Python:
        // m = первый токен
        // p = токены посередине
        // c = последний токен

        val parts = line.trim().split(Regex("\\s+"))
        val mStr = parts[0]
        val cStr = parts.last()
        val pStrs = parts.subList(1, parts.size - 1)

        val n = mStr.length - 2 // Python: n = len(m)-2 (из-за кавычек или мусора? Нет, скорее всего длина строки минус что-то)
        // В Python m[-2:0:-1].
        // Если строка '.....', то len=5. m[-2:0:-1] берет слайс.
        // Предполагаем, что формат строки это что-то типа битовой карты.
        // Давайте просто следовать логике кода.

        // Python: q = [*map(lambda x: eval(x[:-1]+',)'), p)]
        // p - это строки вида "(0,1". eval делает из них tuple (0,1).
        // Значит pStrs это список индексов.
        val q = pStrs.map { token ->
            // token looks like "(0,1" or "(2,3,4"
            // remove '('
            val cleaned = token.replace("(", "").replace(")", "")
            cleaned.split(",").map { it.toInt() }
        }

        // c = [*map(int, c[1:-1].split(','))]
        // cStr looks like "0,1,0,1" probably wrapped in something?
        // Python code says c[1:-1]. Это убирает первый и последний символ.
        // Скорее всего cStr в формате "0,1,0,1" без скобок, но Python код зачем-то делает слайс.
        // Или cStr это "(0,1,0,1)".
        // Допустим cStr это "0,1,0,1".
        val cVals = cStr.substring(1, cStr.length - 1).split(",").map { it.toInt() }

        // --- Part 1: BFS ---
        val B = IntArray(1 shl n) { -1 }
        B[0] = 0

        // p = [*map(lambda x: sum(1<<i for i in x), q)]
        val pMasks = q.map { indices ->
            indices.fold(0) { acc, idx -> acc or (1 shl idx) }
        }

        // m = int(m[-2:0:-1].replace('#', '1').replace('.', '0'), 2)
        // Интерпретация строки m. Срез от предпоследнего до 0 (не включая) в обратном порядке.
        // Если строка "ABCDE", [-2:0:-1] -> D, C, B.
        // Это очень специфично для Advent of Code.
        val mSlice = mStr.substring(1, mStr.length - 1).reversed()
        val mMask = mSlice.replace('#', '1').replace('.', '0').toInt(2)

        val Q = java.util.ArrayDeque<Int>()
        Q.add(0)

        // BFS Loop
        // В Python итерация по списку, который дополняется. В Kotlin используем Queue.
        // Но Python код: for u in Q: ... Q.append(...). Это работает как очередь.
        while (!Q.isEmpty()) {
            val u = Q.pollFirst() // pollFirst == pop(0)

            // Если мы достигли цели mMask?
            // В Python: p1 += B[m]. Цикл идет до конца всех достижимых состояний.
            // Это странно, обычно BFS прерывают. Но тут заполняется массив расстояний B.
            // B[m] потом считывается.

            for (v in pMasks) {
                val nextState = u xor v
                // ~B[...] проверка на -1 (в Python ~-1 == 0 (false), ~0 == -1 (true)).
                // Тут B инициализирован -1.
                if (B[nextState] == -1) {
                    B[nextState] = B[u] + 1
                    Q.add(nextState)
                }
            }
        }
        p1Total += B[mMask]

        // --- Part 2: Branch-and-Bound ILP ---

        // Python: A = [[0]*-~len(p) for _ in range(2*n+len(p))]
        // len(p) это количество паттернов (переменных). n это длина целевого вектора.
        val numVars = q.size // len(p)
        // Python: -~x = x + 1. Cols = numVars + 1 (last is RHS)
        // Rows = 2*n + numVars
        val rows = 2 * n + numVars
        val cols = numVars + 1

        val A = Array(rows) { DoubleArray(cols) }

        // for i in range(len(q)): A[~i][i] = -1
        // ~i в Python это index from end: -1-i.
        // A[rows - 1 - i][i] = -1
        for (i in 0 until numVars) {
            A[rows - 1 - i][i] = -1.0
        }

        // for i in range(len(q)): for e in q[i]: A[e][i] = 1; A[e+n][i] = -1
        for (i in 0 until numVars) {
            for (e in q[i]) {
                A[e][i] = 1.0
                A[e + n][i] = -1.0
            }
        }

        // for i in range(n): A[i][-1] = c[i]; A[i+n][-1] = -c[i]
        // A[i][cols-1] = c[i]
        for (i in 0 until n) {
            val cVal = cVals[i].toDouble()
            A[i][cols - 1] = cVal
            A[i + n][cols - 1] = -cVal
        }

        // Convert Array to List for our function signature
        val aList = A.map { it.clone() }.toMutableList()

        p2Total += f(aList)
    }

    println("Part 1: $p1Total")
    println("Part 2: $p2Total")
}