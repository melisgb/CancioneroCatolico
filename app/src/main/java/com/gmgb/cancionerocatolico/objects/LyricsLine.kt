package com.gmgb.cancionerocatolico.objects

class LyricsLine(line: String, type: LyricsLineType) {
    val line : String = line
    val type : LyricsLineType = type

    enum class LyricsLineType {  VERSE, CHORDS, BLANK  }
}