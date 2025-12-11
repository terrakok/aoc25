package org.example

import java.io.File
import kotlin.collections.plusAssign
import kotlin.math.pow
import kotlin.math.sqrt

object Day8 {
    fun first() {
        data class Box(val id: Int, val x: Long, val y: Long, val z: Long) {
            fun distance(box: Box): Double = sqrt(
                (box.x - x).toDouble().pow(2) +
                        (box.y - y).toDouble().pow(2) +
                        (box.z - z).toDouble().pow(2)
            )
        }

        class Dist(val i: Int, val j: Int, val d: Double)

        val file = File("./data/8/input.txt")
        val boxes = file.readLines().mapIndexed { i, l ->
            val (x, y, z) = l.split(',').map(String::toLong)
            Box(i, x, y, z)
        }

        val distances = boxes.mapIndexed { i, b1 ->
            boxes.takeLast(boxes.size - i - 1).mapIndexed { j, b2 ->
                Dist(i, j + i + 1, b1.distance(b2))
            }
        }
        val sorted = distances.flatten().sortedBy { it.d }

        val connections = mutableListOf<MutableList<Box>>()
        var connected = 0

        for (d in sorted) {
            val ic = connections.singleOrNull { it.contains(boxes[d.i]) }
            val jc = connections.singleOrNull { it.contains(boxes[d.j]) }

            connected++

            if (ic == null && jc == null) {
                connections += mutableListOf(boxes[d.i], boxes[d.j])
                println("connect: ${boxes[d.i]} - ${boxes[d.j]}")
            } else if (ic == null && jc != null) {
                jc.add(boxes[d.i])
                println("connect: ${boxes[d.i]} - ${boxes[d.j]}")
            } else if (ic != null && jc == null) {
                ic.add(boxes[d.j])
                println("connect: ${boxes[d.i]} - ${boxes[d.j]}")
            } else if (ic != null && jc != null) {
                if (ic == jc) {
                    //why?!
//                connected--
                    println("skip: ${boxes[d.i]} - ${boxes[d.j]}")
                } else {
                    ic.addAll(jc)
                    connections.remove(jc)
                    println("connect groups: ${boxes[d.i]} - ${boxes[d.j]}")
                }
            } else {
                error("Something went wrong")
            }

            if (connected == 1000) {
                break
            }
        }

        val (c1, c2, c3) = connections.map { it.size }.sortedDescending().take(3)
        println("Connected: ${connections.map { it.size }.sortedDescending().joinToString()}")

        println("Result: ${c1 * c2 * c3}")
    }

    fun second() {
        data class Box(val id: Int, val x: Long, val y: Long, val z: Long) {
            fun distance(box: Box): Double = sqrt(
                (box.x - x).toDouble().pow(2) +
                        (box.y - y).toDouble().pow(2) +
                        (box.z - z).toDouble().pow(2)
            )
        }

        class Dist(val i: Int, val j: Int, val d: Double)

        val file = File("./data/8/input.txt")
        val boxes = file.readLines().mapIndexed { i, l ->
            val (x, y, z) = l.split(',').map(String::toLong)
            Box(i, x, y, z)
        }

        val distances = boxes.mapIndexed { i, b1 ->
            boxes.takeLast(boxes.size - i - 1).mapIndexed { j, b2 ->
                Dist(i, j + i + 1, b1.distance(b2))
            }
        }
        val sorted = distances.flatten().sortedBy { it.d }

        val connections = mutableListOf<MutableList<Box>>()
        var connected = 0

        var result = 0L
        for (d in sorted) {
            val ic = connections.singleOrNull { it.contains(boxes[d.i]) }
            val jc = connections.singleOrNull { it.contains(boxes[d.j]) }

            connected++

            if (ic == null && jc == null) {
                connections += mutableListOf(boxes[d.i], boxes[d.j])
                println("connect: ${boxes[d.i]} - ${boxes[d.j]}")
            } else if (ic == null && jc != null) {
                jc.add(boxes[d.i])
                println("connect: ${boxes[d.i]} - ${boxes[d.j]}")
            } else if (ic != null && jc == null) {
                ic.add(boxes[d.j])
                println("connect: ${boxes[d.i]} - ${boxes[d.j]}")
            } else if (ic != null && jc != null) {
                if (ic == jc) {
                    //why?!
//                connected--
                    println("skip: ${boxes[d.i]} - ${boxes[d.j]}")
                } else {
                    ic.addAll(jc)
                    connections.remove(jc)
                    println("connect groups: ${boxes[d.i]} - ${boxes[d.j]}")
                }
            } else {
                error("Something went wrong")
            }

            if (connections.size == 1 && connections.first().size == boxes.size) {
                result = boxes[d.i].x * boxes[d.j].x
                break
            }
        }

        println("Result: $result")
    }
}