package com.jwoolston.kt2048

import java.util.*

class JVMBoard(size: Int, scheme: Int) : GameBoard(size, scheme) {

    val scanner = Scanner(System.`in`)

    override fun nativeSleep(time: Int) {
        Thread.sleep(time.toLong())
    }

    override fun nativeGetChar(): Int {
        return scanner.nextByte().toInt()
    }
}