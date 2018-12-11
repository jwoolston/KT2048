package com.jwoolston.kt2048

import kotlin.math.absoluteValue
import kotlin.random.Random

abstract class GameBoard(private val size: Int, private val scheme: Int) {

    private var score = 0

    private var board = Array(size) { IntArray(size) }

    abstract fun nativeSleep(time: Int)
    abstract fun nativeGetChar(): Int

    fun processInput(c: Int): Boolean {
        val success: Boolean = when (c) {
            97, 104, 68 -> moveLeft()    // 'a', 'h', 'left arrow' key
            100, 108, 67 -> moveRight()    // 'd', 'l', 'right arrow' key
            119, 107, 65 -> moveUp()    // 'w', 'k', 'up arrow' key
            115, 106, 66 -> moveDown() // 's', 'j', 'down arrow' key
            else -> {
                false
            }
        }
        if (success) {
            drawBoard()
            nativeSleep(150000)
            addRandom()
            drawBoard()
            if (gameEnded()) {
                print("         GAME OVER          \n")
                return true
            }
        }
        if (c == 'q'.toInt()) {
            print("        QUIT? (y/n)         \n")
            val r = nativeGetChar()
            if (r == 'y'.toInt()) {
                return true
            }
            drawBoard()
        }
        if (c == 'r'.toInt()) {
            print("       RESTART? (y/n)       \n")
            val r = nativeGetChar()
            if (r == 'y'.toInt()) {
                initBoard()
            }
            drawBoard()
        }
        return false
    }

    private fun drawBoard() {
        var color: CharArray
        val reset = charArrayOf(0x1B.toChar(), '[', 'm')
        print(charArrayOf(0x1B.toChar(), '[', 'H'))

        print("2048.c $score pts\n\n")

        for (y in 0 until size) {
            for (x in 0 until size) {
                color = getColor(board[x][y])
                print(color)
                print("       ")
                print(reset)
            }
            print("\n")
            for (x in 0 until size) {
                color = getColor(board[x][y])
                print(color)
                if (board[x][y] != 0) {
                    val s = board[x][y].shl(1).toString()
                    val t = 7 - s.length
                    val builder = StringBuilder()
                    for (i in 0 until (t - t / 2)) {
                        builder.append(' ')
                    }
                    builder.append(s)
                    for (i in 0 until t / 2) {
                        builder.append(' ')
                    }
                    print(builder.toString())
                } else {
                    print("   ·   ")
                }
                print(reset)
            }
            print("\n")
            for (x in 0 until size) {
                color = getColor(board[x][y])
                print(color)
                print("       ")
                print(reset)
            }
            print("\n")
        }
        print("\n")
        print("        ←,↑,→,↓ or q        \n")
        print(charArrayOf(0x1B.toChar(), '[', 'A'))
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

    private fun getColor(value: Int): CharArray {
        val original = arrayOf(
            8,
            255,
            1,
            255,
            2,
            255,
            3,
            255,
            4,
            255,
            5,
            255,
            6,
            255,
            7,
            255,
            9,
            0,
            10,
            0,
            11,
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
        val blackwhite = arrayOf(
            232,
            255,
            234,
            255,
            236,
            255,
            238,
            255,
            240,
            255,
            242,
            255,
            244,
            255,
            246,
            0,
            248,
            0,
            249,
            0,
            250,
            0,
            251,
            0,
            252,
            0,
            253,
            0,
            254,
            0,
            255,
            0
        )
        val bluered = arrayOf(
            235,
            255,
            63,
            255,
            57,
            255,
            93,
            255,
            129,
            255,
            165,
            255,
            201,
            255,
            200,
            255,
            199,
            255,
            198,
            255,
            197,
            255,
            196,
            255,
            196,
            255,
            196,
            255,
            196,
            255,
            196,
            255
        )
        val schemes = arrayOf(original, blackwhite, bluered)
        val background = schemes[scheme] + 0
        val foreground = schemes[scheme] + 1
        var count = value
        var backgroundIdx = 0
        var foregroundIdx = 0
        if (count > 0) {
            while (value > 0) {
                if ((backgroundIdx + 2) < (schemes[scheme][original.size - 1])) {
                    backgroundIdx += 2
                    foregroundIdx += 2
                }
                count--
            }
        }
        return charArrayOf(
            0x1B.toChar(),
            '[',
            '3',
            '8',
            ';',
            '5',
            ';',
            foreground[foregroundIdx].toChar(),
            ';',
            '4',
            '8',
            ';',
            '5',
            ';',
            background[backgroundIdx].toChar(),
            'm'
        )
    }
}