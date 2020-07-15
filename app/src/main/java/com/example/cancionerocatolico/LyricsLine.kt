package com.example.cancionerocatolico

class LyricsLine(line: String, type: LyricsLineType) {
    val line : String = line
    val type : LyricsLineType = type

    enum class LyricsLineType {  VERSE, CHORDS, BLANK  }
}