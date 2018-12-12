package com.jwoolston.kt2048

class JVMBoard(size: Int, scheme: Int) : GameBoard(size, scheme) {

    override fun nativeSleep(time: Int) {
        Thread.sleep(time.toLong())
    }

    override fun nativeGetChar(): Byte {
        return System.console().reader().read().toByte()
    }
}