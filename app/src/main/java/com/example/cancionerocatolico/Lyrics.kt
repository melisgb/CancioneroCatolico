package com.example.cancionerocatolico

class Lyrics {
    val newLyrics = ArrayList<LyricsLine>()
    fun transformLyrics(lyrics : String) : ArrayList<LyricsLine> {
        newLyrics.clear()

        var latinChordsPatt  = "(Do|Re|Mi|Fa|Sol|La|Si)(#|7)?".toRegex()
        val allContent = lyrics.reader().readLines()

        for(line in allContent){
            var type = ""
            val lineArr = line.split(Regex("\\s+"))
            for(word in lineArr) {
                type = when {
                    word == "" -> "Blank"
                    word.matches("""([A-G])(#|7)?""".toRegex()) || latinChordsPatt.matches(word) -> "Chords"
                    //".*\\d.*".toRegex()
                    else -> "Verse"
                }
            }
            if(type == "Blank"){
                newLyrics.add(LyricsLine(line, LyricsLine.LyricsLineType.BLANK))
            }
            else if(type =="Chords"){
                newLyrics.add(LyricsLine(line, LyricsLine.LyricsLineType.CHORDS))
            }
            else{
                newLyrics.add(LyricsLine(line, LyricsLine.LyricsLineType.VERSE))
            }
        }
        return newLyrics
    }
}