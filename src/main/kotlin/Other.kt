package org.example

import kotlinx.coroutines.CoroutineStart
import java.io.File

fun aoc11_1() {
    val file = File("./data/11/test.txt")

    val data = mutableMapOf<String, List<String>>()
    file.readLines().forEach { l ->
        val name = l.substringBefore(':')
        val paths = l.substringAfter(':').split(' ').filter { it.isNotEmpty() }
        data[name] = paths
    }

    val res = finAllPaths("you", data, "out")

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

//    val res = finAllPaths("svr", data, "fft").size
    val res = finAllPaths("svr", data, "dac")
        .size

    println("Result: $res")
}


fun finAllPaths(start: String, graph: Map<String, List<String>>, end: String): List<List<String>> =
    finAllPaths(listOf(listOf(start)), graph, end)
tailrec fun finAllPaths(paths: List<List<String>>, graph: Map<String, List<String>>, end: String): List<List<String>> {
//    log("PATHS: \n${paths.joinToString(separator = "\n")}")

    if (paths.isEmpty()) return emptyList()
    val done = paths.filter { it.last() == end }
    val progress = paths.filter { it.last() != end }

    if (progress.isEmpty()) return done

    val nn = progress.flatMap { p ->
        val next = graph[p.last()]
        next?.map { p + it } ?: run {
            if (p.last() != end) return@flatMap emptyList()
            listOf(p)
        }
    }.filter { it.isNotEmpty() }

    return finAllPaths(done + nn, graph, end)
}