package com.jwoolston.kt2048.native

import com.jwoolston.kt2048.core.FIRST_LINE
import com.jwoolston.kt2048.core.LAST_LINE

fun main(args: Array<String>) {

    val board = STM32F4Board(4)

    System.out.write(FIRST_LINE)

    board.initBoard()

    while (true) {
        val c = board.nativeGetChar()
        if (c == (-1).toByte()) {
            break
        }
        if (board.processInput(c)) {
            break
        }
    }

    System.out.write(LAST_LINE)
}