package com.hash

import java.io.RandomAccessFile
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.*


class DiscordRichPresence(private val clientID: String) {
    private val pipe = RandomAccessFile("\\\\?\\pipe\\discord-ipc-0", "rw")
    private val pid = ProcessHandle.current().pid()
    private val uuid = UUID.randomUUID()
    private var work = true
    private var presence = ""

    init {
        this.handshake()
    }

    private fun send(byteArray: ByteArray) {
        pipe.write(byteArray)
    }

    private fun read(): Struct {
        val header = ByteBuffer.allocate(8).order(ByteOrder.LITTLE_ENDIAN).array()
        pipe.read(header)
        val data = ByteBuffer.allocate(readIntFromBuffer(header, 4)).order(ByteOrder.LITTLE_ENDIAN).array()
        pipe.read(data)
        return decode(header + data)
    }

    private fun sendAndRead(byteArray: ByteArray): Struct {
        send(byteArray)
        return read()
    }

    private fun handshake() {
        val data = """{"v": 1, "client_id": "$clientID"}"""
        val response = this.sendAndRead(encode(Struct(OPs.OP_HANDSHAKE, data)))
    }

    private fun update(presence: String) {
        val data = """{
            "cmd": "SET_ACTIVITY",
            "nonce" : "$uuid",
            "args": {
            "pid": $pid,
            "activity": $presence
            }
        }"""
        this.sendAndRead(encode(Struct(OPs.OP_FRAME, data)))
    }

    fun setPresence(presence: String) {
        this.presence = presence
    }

    fun run(){
        Thread {
            while (work) {
                update(presence)
                Thread.sleep(900)
            }
        }.start()

    }

    fun stop() {
        work = false
        pipe.close()
    }
}