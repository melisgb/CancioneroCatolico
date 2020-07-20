package com.example.cancionerocatolico.utils

import android.util.Log
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
        var latinChordsPatt  = "(Do|Re|Mi|Fa|Sol|La|Si)(#|b)?(m)?(sus|maj)?([2-9])?(/(Do|Re|Mi|Fa|Sol|La|Si)(#|b)?(m)?(sus|maj)?([2-9])?)?".toRegex(RegexOption.IGNORE_CASE)
        var ameriChordsPatt = """([A-G])(#|b)?(m)?(sus|maj)?([2-9])?(/([A-G])(#|b)?(m)?(sus|maj)?([2-9])?)?""".toRegex()
        return word.matches(ameriChordsPatt) || word.matches(latinChordsPatt)
    }


    fun increaseSemiNote(line : String) : String {
        val chordsMap = HashMap<String, String>()
        chordsMap["Do"] = "Do#"
        chordsMap["Do#"] = "Re"
        chordsMap["Re"] = "Re#"
        chordsMap["Re#"] = "Mi"
        chordsMap["Mi"] = "Fa"
        chordsMap["Fa"] = "Fa#"
        chordsMap["Fa#"] = "Sol"
        chordsMap["Sol"] = "Sol#"
        chordsMap["Sol#"] = "La"
        chordsMap["La"] = "La#"
        chordsMap["La#"] = "Si"
        chordsMap["Si"] = "Do"
        chordsMap["C"] = "C#"
        chordsMap["C#"] = "D"
        chordsMap["D"] = "D#"
        chordsMap["D#"] = "E"
        chordsMap["E"] = "F"
        chordsMap["F"] = "F#"
        chordsMap["F#"] = "G"
        chordsMap["G"] = "G#"
        chordsMap["G#"] = "A"
        chordsMap["A"] = "A#"
        chordsMap["A#"] = "B"
        chordsMap["B"] = "C"

        val chordsPattern = """(D[oO]|R[eE]|M[iI]|F[aA]|S(ol|OL)|L[aA]|S[iI]|A|B|C|D|E|F|G)(b|#)?""".toRegex(RegexOption.IGNORE_CASE)
        val ocurrences = chordsPattern.findAll(line)
        var chordLine = line

        for(occur in ocurrences.toList().reversed() ){
            if(occur.value.endsWith("b")) {
                chordLine= chordLine.replaceRange(occur.range, occur.value.replace("b", ""))
            }
            else {
                chordLine = chordLine.replaceRange(occur.range, chordsMap[occur.value.toLowerCase().capitalize()]!!)

            }

//        val oldChordLen = occur.value.length
//        val newChordLen = chordsMap[occur.value]!!.length
//
//        if(oldChordLen < newChordLen){
//            // La < Sol# -->
//            var newEnd = occur.range.endInclusive
//            chordLine = chordLine.replaceRange(occur.range, chordsMap[occur.value]!!)
//        }
//        else if(oldChordLen > newChordLen) {
//            chordLine = chordLine.replaceRange(occur.range, chordsMap[occur.value]!! + " ".repeat(oldChordLen-newChordLen))
//        }
//        else{
//            chordLine = chordLine.replaceRange(occur.range, chordsMap[occur.value]!!)
//        }
        }
        return chordLine
    }
    /***
     * Do  - Do#
     * Do# - Re
     * Re - Re#
     * Re# - Mi
     * Mi - Fa
     * Fa - Fa#
     * Fa# - Sol
     * Sol - Sol#
     * Sol# - La
     * La - La#
     * La# - Si
     * Si - Do
     *
     * menor
     * Dom  - Do#m
     * Do#m - Rem
     * Rem - Re#m
     * Re#m - Mim
     * Mim - Fam
     * Fam - Fa#m
     * Fa#m - Solm
     * Solm - Sol#m
     * Sol#m - Lam
     * Lam - La#m
     * La#m - Sim
     * Sim - Dom
     *
     *
     * Si es bemol - quitar la b
     * Ej. Solb (Fa#)  - Sol
     *
     * Si es suspendida - ignorar lo de suspendida, se mantiene igual
     *
     *
     * C - C#
     * C# - D
     * D - D#
     * D# - E
     * E - F
     * F - F#
     * F# - G
     * G - G#
     * G# - A
     * A - A#
     * A# - B
     * B - C
     *
     * menor
     * Cm  - C#m
     * C#m - Dm
     * Dm - D#m
     * D#m - Em
     * Em - Fm
     * Fm - F#m
     * F#m - Gm
     * Gm - G#m
     * G#m - Am
     * Am - A#m
     * A#m - Bm
     * Bm - Cm
     *
     * DECREASE
     * C# - C
     * D - C#
     * D# - D
     * E - D#
     * F - E
     * F# - F
     * G - F#
     * G# - G
     * A - G#
     * A# - A
     * B - A#
     * C - B
     *
    */
}