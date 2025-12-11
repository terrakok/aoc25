package org.example

import java.io.File

fun aoc11_1() {
    val file = File("./data/11/input.txt")

    val data = mutableMapOf<String, List<String>>()
    file.readLines().forEach { l ->
        val name = l.substringBefore(':')
        val paths = l.substringAfter(':').split(' ').filter { it.isNotEmpty() }
        data[name] = paths
    }

    tailrec fun Map<String, List<String>>.findAllPaths(
        paths: List<List<String>>,
        end: String
    ): List<List<String>> {
        if (paths.isEmpty()) return emptyList()
        val done = paths.filter { it.last() == end }
        val progress = paths.filter { it.last() != end }

        log("done: ${done.size} progress: ${progress.size}")

        if (progress.isEmpty()) return done

        val nn = progress.flatMap { p ->
            val next = this[p.last()]
            next?.map { p + it } ?: run {
                if (p.last() != end) return@flatMap emptyList()
                listOf(p)
            }
        }.filter { it.size > 1 && it.toSet().size == it.size }

        return findAllPaths(done + nn, end)
    }

    fun Map<String, List<String>>.findAllPaths(start: String, end: String) = findAllPaths(listOf(listOf(start)),  end)

    val res = data.findAllPaths("you", "out")

    println("Result: ${res.size}")
}

fun aoc11_2() {
    val file = File("./data/11/input2.txt")

    val data = mutableMapOf<String, List<String>>()
    file.readLines().forEach { l ->
        val name = l.substringBefore(':')
        val paths = l.substringAfter(':').split(' ').filter { it.isNotEmpty() }
        data[name] = paths
    }

    val ways = mutableMapOf<String, Long>()

    class Walker(val start: String, val num: Int)

    tailrec fun Map<String, List<String>>.findAllPaths2(
        walkers: List<Walker>,
        finish: String,
        deathWay: String
    ) {
        walkers.forEach { w ->
            ways[w.start] = (ways[w.start] ?: 0) + w.num
        }

        val newWalkers = walkers
            .filter { w -> w.start != finish && w.start != deathWay }
            .mapNotNull { w -> this[w.start]?.map { Walker(it, w.num) } }
            .flatten()
            .groupBy({ it.start }, { it.num })
            .entries.map { (start, list) -> Walker(start, list.sum()) }

        if (newWalkers.isEmpty()) return

        findAllPaths2(newWalkers, finish, deathWay)
    }

    fun Map<String, List<String>>.findAllPaths2(start: String, finish: String, deathWay: String) =
        findAllPaths2(listOf(Walker(start, 1)), finish, deathWay)

    log("svr -> fft")
    ways.clear()
    data.findAllPaths2("svr", "fft", "dac")
    val w1 = ways["fft"]!!

    log("fft -> dac")
    ways.clear()
    data.findAllPaths2("fft", "dac", "")
    val w2 = ways["dac"]!!

    log("dac -> out")
    ways.clear()
    data.findAllPaths2("dac", "out", "")
    val w3 = ways["out"]!!

    val res1 = w1 * w2 * w3

    println("Result: $res1")
}

