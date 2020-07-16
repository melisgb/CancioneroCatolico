package com.example.cancionerocatolico.utils

import com.example.cancionerocatolico.objects.LyricsLine

class Lyrics {
    val newLyrics = ArrayList<LyricsLine>()
    fun transformLyrics(lyrics : String) : ArrayList<LyricsLine> {
        newLyrics.clear()
        val allContent = lyrics.reader().readLines()

        for(line in allContent){
            var type =
                LyricsLine.LyricsLineType.BLANK
            val lineArr = line.trim().split(Regex("\\s+"))

            type = when {
                line.isEmpty() -> {
                    LyricsLine.LyricsLineType.BLANK
                }
                lineArr.all { word ->
                    isChord(word) } -> LyricsLine.LyricsLineType.CHORDS
                else -> LyricsLine.LyricsLineType.VERSE
            }
            newLyrics.add(
                LyricsLine(
                    line,
                    type
                )
            )
        }
        return newLyrics
    }

    fun isChord(word: String) : Boolean {
        var latinChordsPatt  = "(Do|Re|Mi|Fa|Sol|La|Si)(#|7|#m|m|b)?".toRegex(RegexOption.IGNORE_CASE)
        var ameriChordsPatt = """([A-G])(#|7|sus|#m|m|/)?""".toRegex()
        return word.matches(ameriChordsPatt) || word.matches(latinChordsPatt)
    }
}