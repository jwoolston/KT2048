package com.jwoolston.kt2048.native

import com.jwoolston.kt2048.core.FIRST_LINE
import com.jwoolston.kt2048.core.LAST_LINE
import platform.zephyr.stm32f4_disco.*

fun main() {

    val board = STM32F4Board(4)
    console_init()

    board.nativePrint(FIRST_LINE)

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

    board.nativePrint(LAST_LINE)
}