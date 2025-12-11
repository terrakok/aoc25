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

    var paths = listOf<List<String>>()
    val result = mutableListOf<List<String>>()
    tailrec fun Map<String, List<String>>.findAllPaths2(end: String) {
        val newPaths = mutableListOf<List<String>>()
        paths.filter {
            if (it.last() == end) {
                result.add(it)
                false
            } else {
                true
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
                        val known = result.map { it.indexOf(v) to it }.filter { it.first != -1 }
                        if (known.isNotEmpty()) {
                            known.forEach { (i, kn) ->
                                //known path
                                result.add(p + kn.subList(i, kn.size))
                            }
                        } else {
                            newPaths.add(p + v)
                        }
                    }
                }
            }
        }

        paths = newPaths

        log("paths: ${paths.size} done: ${result.size}")
        if (paths.isEmpty()) return

        findAllPaths2(end)
    }

    paths = listOf(listOf("fft"))
    data.findAllPaths2("dac")

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
