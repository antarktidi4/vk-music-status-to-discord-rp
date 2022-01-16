package com.hash

import java.nio.ByteBuffer
import java.nio.ByteOrder


data class Struct(val opCode: OPs, val data: String)


fun encode(struct: Struct): ByteArray {
    val data = struct.data.toByteArray(Charsets.UTF_8)
    val bbArray = ByteBuffer.allocate(4 + 4 + data.size).order(ByteOrder.LITTLE_ENDIAN).array()
    writeIntToBuffer(bbArray, 0, struct.opCode.op)
    writeIntToBuffer(bbArray, 4, data.size)
    data.forEachIndexed { index, element -> bbArray[8 + index] = element }
    return bbArray
}

fun decode(byteArray: ByteArray): Struct {
    return Struct(
        OPs.values()[readIntFromBuffer(byteArray, 0)],
        byteArray.copyOfRange(8, byteArray.size).decodeToString()
    )
}

fun writeIntToBuffer(buffer: ByteArray, offset: Int, data: Int) {
    buffer[offset + 0] = (data shr 0).toByte()
    buffer[offset + 1] = (data shr 8).toByte()
    buffer[offset + 2] = (data shr 16).toByte()
    buffer[offset + 3] = (data shr 24).toByte()
}

fun readIntFromBuffer(buffer: ByteArray, offset: Int): Int {
    return (buffer[offset + 3].toInt() shl 24) or
            (buffer[offset + 2].toInt() and 0xff shl 16) or
            (buffer[offset + 1].toInt() and 0xff shl 8) or
            (buffer[offset + 0].toInt() and 0xff)
}
