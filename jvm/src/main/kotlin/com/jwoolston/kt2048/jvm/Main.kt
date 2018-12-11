package com.jwoolston.kt2048.jvm

import com.jwoolston.kt2048.JVMBoard

class Main {

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {

            val board = JVMBoard(4, 0)

            var c: Int

            board.initBoard()

            /*for (char in FIRST_LINE) {
                print(char)
            }*/
            print(FIRST_LINE)

            while (true) {
                c = board.nativeGetChar()
                if (c == -1) {
                    println("\nError! Cannot read keyboard input!")
                    break
                }
                if (board.processInput(c)) {
                    break
                }
            }

            /*for (char in LAST_LINE) {
                print(char)
            }*/
            print(LAST_LINE)
        }

        val FIRST_LINE = charArrayOf(
            0x1B.toChar(),
            '[',
            '?',
            '2',
            '5',
            'l',
            0x1B.toChar(),
            '[',
            '2',
            'J'
        )

        val LAST_LINE = charArrayOf(
            0x1B.toChar(),
            '[',
            '?',
            '2',
            '5',
            'h',
            0x1B.toChar(),
            '[',
            'm'
        )
    }
}