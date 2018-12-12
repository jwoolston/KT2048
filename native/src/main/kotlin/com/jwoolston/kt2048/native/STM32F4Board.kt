package com.jwoolston.kt2048.native

import com.jwoolston.kt2048.core.GameBoard
import kotlinx.cinterop.cstr
import platform.zephyr.stm32f4_disco.*

class STM32F4Board(size: Int) : GameBoard(size) {

    override fun nativePrint(array: ByteArray) {
        for (byte in array) {
            console_putchar(byte)
        }
    }

    @ExperimentalUnsignedTypes
    override fun nativePrint(message: String) {
        console_write(NULL, message.cstr, message.length.toUInt())
    }

    override fun nativeSleep(time: Int) {
        k_sleep(time)
    }

    override fun nativeGetChar(): Byte {
        return console_getchar().toByte()
    }
}