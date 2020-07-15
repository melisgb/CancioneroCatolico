package com.example.cancionerocatolico

class Lyrics {
    val newLyrics = ArrayList<LyricsLine>()
    fun transformLyrics(lyrics : String) : ArrayList<LyricsLine> {
        newLyrics.clear()

        var latinChordsPatt  = "(Do|Re|Mi|Fa|Sol|La|Si)(#|7)?".toRegex()
        val allContent = lyrics.reader().readLines()

        for(line in allContent){
            var type = LyricsLine.LyricsLineType.BLANK
            val lineArr = line.split(Regex("\\s+"))
            for(word in lineArr) {
                type = when {
                    word == "" -> LyricsLine.LyricsLineType.BLANK
                    word.matches("""([A-G])(#|7)?""".toRegex(RegexOption.IGNORE_CASE)) || latinChordsPatt.matches(word) -> LyricsLine.LyricsLineType.CHORDS
                    //".*\\d.*".toRegex()
                    else -> LyricsLine.LyricsLineType.VERSE
                }
            }
            newLyrics.add(LyricsLine(line, type))
        }
        return newLyrics
    }
}