package com.jwoolston.kt2048.native

import com.jwoolston.kt2048.core.GameBoard

class STM32F4Board(size: Int) : GameBoard(size) {

    override fun nativeSleep(time: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun nativeGetChar(): Byte {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}