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

    val ways = data.keys.associateWith { mutableListOf(0) }

    var paths = listOf<List<String>>()
    val result = mutableListOf<List<String>>()
    val knownvertexes = mutableSetOf<String>()

    tailrec fun Map<String, List<String>>.findAllPaths2(end: String) {
        log("paths: ${paths.size} result: ${result.size}")
        val newPaths = mutableListOf<List<String>>()
        paths.filter { p ->
            val last = p.last()
            if (last == end) {
                result.add(p)
                knownvertexes.addAll(p)
                false
            } else {
                if (knownvertexes.contains(last)) {
                    val tails = result.mapNotNull { r ->
                        r.indexOf(last).takeIf { it != -1 }?.let { r.subList(it, r.size) }
                    }
                    println("tails: ${tails.size}")
                    tails.forEach { tail -> result.add(p + tail) }
                    false
                } else {
                    true
                }
            }
        }.forEach { p ->
            val last = p.last()
            val vertexes = this[last]
            if (vertexes == null) {
                //final
            } else {
                vertexes.forEach { v ->
                    if (p.contains(v)) {
                        //internal cycle
                    } else {
                        newPaths.add(p + v)
                    }
                }
            }
        }

        paths = newPaths
        if (paths.isEmpty()) return

        findAllPaths2(end)
    }

    paths = listOf(listOf("svr"))
    data.findAllPaths2("fft")

    println("Result: ${result.size}")
}


fun finAllPaths(start: String, graph: Map<String, List<String>>, end: String): List<List<String>> =
    finAllPaths(listOf(listOf(start)), graph, end)

tailrec fun finAllPaths(paths: List<List<String>>, graph: Map<String, List<String>>, end: String): List<List<String>> {
    log("=============")

    if (paths.isEmpty()) return emptyList()
    val done = paths.filter { it.last() == end }
    val progress = paths.filter { it.last() != end }


    log("done: ${done.size} progress: ${progress.size}")

    if (progress.isEmpty()) return done

    val nn = progress.flatMap { p ->
        val next = graph[p.last()]
        next?.map { p + it } ?: run {
            if (p.last() != end) return@flatMap emptyList()
            listOf(p)
        }
    }.filter { it.size > 1 && it.toSet().size == it.size }

    return finAllPaths(done + nn, graph, end)
}

fun Map<String, List<String>>.findAllPaths(start: String, end: String, known: Set<String>): List<List<String>>? {
    if (start == end) return listOf(listOf(end))

    val nextVertexes = this[start]
    return nextVertexes
        ?.mapNotNull { vertex ->
            if (vertex !in known) {
                findAllPaths(vertex, end, known + vertex)?.map { listOf(start) + it }
            } else null
        }
        ?.flatten()
}
