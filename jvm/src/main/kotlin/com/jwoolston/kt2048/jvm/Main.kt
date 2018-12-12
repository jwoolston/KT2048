package com.jwoolston.kt2048.jvm

import com.jwoolston.kt2048.JVMBoard

class Main {

    companion object {

        @JvmStatic
        fun main(args: Array<String>) {

            val board = JVMBoard(4, 0)

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

        val FIRST_LINE = byteArrayOf(
            0x1B.toByte(), // Escape Char
            0x5B.toByte(), // [
            0x3F.toByte(), // ?
            0x32.toByte(), // 2
            0x35.toByte(), // 5
            0x6C.toByte(), // l
            0x1B.toByte(), // Escape Char
            0x5B.toByte(), // [
            0x32.toByte(), // 2
            0x4A.toByte()  // J
        )

        val LAST_LINE = byteArrayOf(
            0x1B.toByte(), // Escape Char
            0x5B.toByte(), // [
            0x3F.toByte(), // ?
            0x32.toByte(), // 2
            0x35.toByte(), // 5
            0x68.toByte(), // h
            0x1B.toByte(), // Escape Char
            0x5B.toByte(), // [
            0x6D.toByte()  // m
        )
    }
}