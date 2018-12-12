package com.jwoolston.kt2048.jvm

import com.jwoolston.kt2048.core.GameBoard

class JVMBoard(size: Int) : GameBoard(size) {

    override fun nativeSleep(time: Int) {
        Thread.sleep(time.toLong())
    }

    override fun nativeGetChar(): Byte {
        return System.console().reader().read().toByte()
    }
}