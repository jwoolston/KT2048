package com.jwoolston.kt2048

import java.io.File
import java.nio.charset.Charset
import kotlin.math.absoluteValue
import kotlin.random.Random

abstract class GameBoard(private val size: Int, private val scheme: Int) {

    private var score = 0

    private var board = Array(size) { IntArray(size) }

    val file = File("/home/jared/log.txt")

    abstract fun nativeSleep(time: Int)
    abstract fun nativeGetChar(): Byte

    fun processInput(c: Byte): Boolean {
        val success: Boolean = when (c) {
            97.toByte(), 104.toByte(), 68.toByte() -> moveLeft()    // 'a', 'h', 'left arrow' key
            100.toByte(), 108.toByte(), 67.toByte() -> moveRight()    // 'd', 'l', 'right arrow' key
            119.toByte(), 107.toByte(), 65.toByte() -> moveUp()    // 'w', 'k', 'up arrow' key
            115.toByte(), 106.toByte(), 66.toByte() -> moveDown() // 's', 'j', 'down arrow' key
            else -> {
                false
            }
        }
        if (success) {
            drawBoard()
            nativeSleep(150)
            addRandom()
            drawBoard()
            if (gameEnded()) {
                System.out.print("         GAME OVER          \n")
                return true
            }
        }
        if (c == 0x71.toByte()) { // q
            System.out.print("        QUIT? (y/n)         \n")
            val r = nativeGetChar()
            if (r == 0x79.toByte()) { // y
                return true
            }
            drawBoard()
        }
        if (c == 0x72.toByte()) {
            System.out.print("       RESTART? (y/n)       \n")
            val r = nativeGetChar()
            if (r == 0x79.toByte()) { // y
                initBoard()
            }
            drawBoard()
        }
        return false
    }

    private fun drawBoard() {
        file.appendText("Draw board\n")
        val reset = byteArrayOf(0x1B.toByte(), 0x5B.toByte(), 0x6D.toByte()) // Escape, [, m
        System.out.write(byteArrayOf(0x1B.toByte(), 0x5B.toByte(), 0x48.toByte())) // Escape, [, H

        System.out.print("2048 Kotlin $score pts\n\n")

        for (y in 0 until size) {
            file.appendText("Writing row $y\n")
            for (x in 0 until size) {
                System.out.write(getColor(board[x][y]))
                System.out.print("       ")
                System.out.write(reset)
            }
            System.out.print("\n")
            for (x in 0 until size) {
                System.out.write(getColor(board[x][y]))
                if (board[x][y] != 0) {
                    val s = 1.shl(board[x][y]).toString()
                    val t = 7 - s.length
                    val builder = StringBuilder()
                    for (i in 0 until (t - t / 2)) {
                        builder.append(' ')
                    }
                    file.appendText("Count: $s\n")
                    builder.append(s)
                    for (i in 0 until t / 2) {
                        builder.append(' ')
                    }
                    System.out.print(builder.toString())
                } else {
                    file.appendText("Count: 0\n")
                    System.out.print("   ·   ")
                }
                System.out.write(reset)
            }
            System.out.print("\n")
            for (x in 0 until size) {
                System.out.write(getColor(board[x][y]))
                System.out.print("       ")
                System.out.write(reset)
            }
            System.out.print("\n")
        }
        System.out.print("\n")
        System.out.print("        ←,↑,→,↓ or q        \n")
        System.out.write(byteArrayOf(0x1B.toByte(), 0x5B.toByte(), 0x41.toByte())) // Escape, [, A
    }

    private fun findTarget(array: IntArray, x: Int, stop: Int): Int {
        // if the position is already on the first, don't evaluate
        if (x == 0) {
            return x
        }
        for (t in x - 1 downTo 0 step 1) {
            if (array[t] != 0) {
                if (array[t] != array[x]) {
                    // merge is not possible, take next position
                    return (t + 1)
                }
                return t
            } else {
                // we should not slide further, return this one
                if (t == stop) {
                    return t
                }
            }
        }
        // we did not find a
        return x
    }

    private fun slideArray(array: IntArray): Boolean {
        var success = false
        var stop = 0

        for (x in 0 until size) {
            if (array[x] != 0) {
                val t = findTarget(array, x, stop)
                // if target is not original position, then move or merge
                if (t != x) {
                    // if target is zero, this is a move
                    if (array[t] == 0) {
                        array[t] = array[x]
                    } else if (array[t] == array[x]) {
                        // merge (increase power of two)
                        array[t]++
                        // increase score
                        score += array[t].shl(1)
                        // set stop to avoid double merge
                        stop = t + 1
                    }
                    array[x] = 0
                    success = true
                }
            }
        }
        return success
    }

    private fun rotateBoard() {
        for (i in 0 until size / 2 step 1) {
            for (j in i until size - i - 1 step 1) {
                val tmp = board[i][j]
                board[i][j] = board[j][size - i - 1]
                board[j][size - i - 1] = board[size - i - 1][size - j - 1]
                board[size - i - 1][size - j - 1] = board[size - j - 1][i]
                board[size - j - 1][i] = tmp
            }
        }
    }

    private fun moveUp(): Boolean {
        var success = false
        for (x in 0 until size) {
            success = success || slideArray(board[x])
        }
        return success
    }

    private fun moveLeft(): Boolean {
        rotateBoard()
        val success = moveUp()
        rotateBoard()
        rotateBoard()
        rotateBoard()
        return success
    }

    private fun moveDown(): Boolean {
        rotateBoard()
        rotateBoard()
        val success = moveUp()
        rotateBoard()
        rotateBoard()
        return success
    }

    private fun moveRight(): Boolean {
        rotateBoard()
        rotateBoard()
        rotateBoard()
        val success = moveUp()
        rotateBoard()
        return success
    }

    private fun findPairDown(): Boolean {
        for (x in 0 until size) {
            for (y in 0 until size) {
                if (board[x][y] == board[x][y + 1]) return true
            }
            return false
        }
        return false
    }

    private fun countEmpty(): Int {
        var count = 0
        for (x in 0 until size) {
            for (y in 0 until size) {
                if (board[x][y] == 0) {
                    count++
                }
            }
            return count
        }
        return count
    }

    private fun gameEnded(): Boolean {
        var ended = true
        if (countEmpty() > 0) return false
        if (findPairDown()) return false
        rotateBoard()
        if (findPairDown()) ended = false
        rotateBoard()
        rotateBoard()
        rotateBoard()
        return ended
    }

    private fun addRandom() {
        var len = 0
        val list = Array(size * size) { IntArray(2) }

        for (x in 0 until size) {
            for (y in 0 until size) {
                if (board[x][y] == 0) {
                    list[len][0] = x
                    list[len][1] = y
                    len++
                }
            }

            if (len > 0) {
                val r = Random.nextInt().absoluteValue % len
                val new_x = list[r][0]
                val new_y = list[r][1]
                val n = (Random.nextInt().absoluteValue % 10) / 9 + 1
                board[new_x][new_y] = n
            }
        }
    }

    fun initBoard() {
        for (x in 0 until size) {
            for (y in 0 until size) {
                board[x][y] = 0
            }
        }
        addRandom()
        addRandom()
        drawBoard()
        score = 0
    }

    private fun getColor(value: Int): ByteArray {
        val colors = arrayOf(
            8, //0
            255,
            9, // 2
            255,
            10, // 8
            255,
            11, // 16
            255,
            12, // 32
            255,
            13, // 64
            255,
            14, // 128
            0,
            0, // 256
            255,
            5, // 512
            255,
            6, // 1024
            255,
            202, // 2048
            0,
            12,
            0,
            13,
            0,
            14,
            0,
            255,
            0,
            255,
            0
        )

        var backgroundIdx = 2 * value
        var foregroundIdx = backgroundIdx + 1
        file.appendText("Value: $value Background Color: ${colors[backgroundIdx]}\n")
        return byteArrayOf(
            0x1B.toByte(), // Escape Char
            0x5B.toByte(), // [
            0x33.toByte(), // 3
            0x38.toByte(), // 8
            0x3B.toByte(), // ;
            0x35.toByte(), // 5
            0x3B.toByte(), // ;
            *colors[foregroundIdx].toString(10).toByteArray(Charset.forName("ASCII")),
            0x3B.toByte(), // ;
            0x34.toByte(), // 4
            0x38.toByte(), // 8
            0x3B.toByte(), // ;
            0x35.toByte(), // 5
            0x3B.toByte(), // ;
            *colors[backgroundIdx].toString(10).toByteArray(Charset.forName("ASCII")),
            0x6D.toByte() // m
        )
    }
}