package com.jwoolston.kt2048.jvm

import com.jwoolston.kt2048.core.FIRST_LINE
import com.jwoolston.kt2048.core.LAST_LINE

class Main {

    companion object {

        @JvmStatic
        fun main(args: Array<String>) {

            val board = JVMBoard(4)

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
    }
}