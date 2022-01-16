package com.hash

enum class OPs(val op: Int) {
    OP_HANDSHAKE(0),
    OP_FRAME(1),
    OP_CLOSE(2),
    OP_PING(3),
    OP_PONG(4);
}