package com.example.tunetrail

import android.content.Context

object AttemptLogger {

    fun append(
        ctx: Context,
        kidId: Long,
        level: Int,
        game: Int,
        success: Boolean,
        moves: Int,
        timeMs: Long
    ) {
        // timestamp,kidId,level,game,success,moves,timeMs
        val line = "${System.currentTimeMillis()},$kidId,$level,$game,$success,$moves,$timeMs\n"
        ctx.openFileOutput("attempts.csv", Context.MODE_APPEND).use {
            it.write(line.toByteArray())
        }
    }
}