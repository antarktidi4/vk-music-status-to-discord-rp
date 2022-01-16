package com.hash


class Main {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            val clientID = args[0]
            val vkToken = args[1]
            val vkTargetID = args[2]

            val vk = VkExample(vkToken, vkTargetID, clientID)
            vk.start()
        }
    }
}