package com.example.cancionerocatolico.utils

import android.util.Log
import com.example.cancionerocatolico.objects.Chord
import com.example.cancionerocatolico.objects.LyricsLine

class Lyrics {
    companion object {
        fun parseLyricsFromSong(lyrics: String): ArrayList<LyricsLine> {
            val newLyrics = ArrayList<LyricsLine>()
            val allContent = lyrics.reader().readLines()

            for (line in allContent) {
                var type =
                    LyricsLine.LyricsLineType.BLANK
                val lineArr = line.trim().split(Regex("\\s+"))

                type = when {
                    line.isEmpty() -> {
                        LyricsLine.LyricsLineType.BLANK
                    }
                    lineArr.all { word ->
                        isChord(word)
                    } -> LyricsLine.LyricsLineType.CHORDS
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

        private fun isChord(word: String): Boolean {
            var latinChordsPatt =
                "(Do|Re|Mi|Fa|Sol|La|Si)(#|b)?(m)?(sus|maj)?([2-9])?(/(Do|Re|Mi|Fa|Sol|La|Si)(#|b)?(m)?(sus|maj)?([2-9])?)?".toRegex(
                    RegexOption.IGNORE_CASE
                )
            var ameriChordsPatt =
                """([A-G])(#|b)?(m)?(sus|maj)?([2-9])?(/([A-G])(#|b)?(m)?(sus|maj)?([2-9])?)?""".toRegex()
            return word.matches(ameriChordsPatt) || word.matches(latinChordsPatt)
        }

        fun increaseSemiNoteMultipleTimes(line: String, level: Int): String {
            var newChords = line
            for (x in 1..level) {
                newChords = increaseSemiNote(newChords)
            }
            return newChords
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
        private fun increaseSemiNote(line: String): String {
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

            val chordsPattern =
                """(D[oO]|R[eE]|M[iI]|F[aA]|S(ol|OL)|L[aA]|S[iI]|A|B|C|D|E|F|G)(b|#)?""".toRegex(
                    RegexOption.IGNORE_CASE
                )
            val ocurrences = chordsPattern.findAll(line)
            var chordLine = line

            for (occur in ocurrences.toList().reversed()) {
                val oldChord = occur.value
                val newChord = if(occur.value.endsWith("b")) occur.value.replace("b", "") else chordsMap[occur.value.toLowerCase().capitalize()]!!
                val oldChordLen = oldChord.length
                val newChordLen = newChord.length

                if (oldChordLen < newChordLen) {
                    // Fa -> Sol#
                    var lenDiff = newChordLen - oldChordLen
                    var newEnd =
                        occur.range.endInclusive // newEnd es la posicion donde termina el string
                    var newStart = occur.range.start
                    while (lenDiff > 0) {
                        if (newEnd < chordLine.length - 1 && chordLine[newEnd + 1] == ' ' && (newEnd >= chordLine.length - 2 || chordLine[newEnd + 2] == ' ')) {
                            //si existe una posicion porque newEnd no es la ultima Y la proxima posicion es espacio Y (es el ultimo acorde en la linea o hay dos espacios despues del acorde)
                            newEnd++
                            lenDiff--
                        } else if (newStart > 0 && chordLine[newStart - 1] == ' ' && (newStart < 2 || chordLine[newStart - 2] == ' ')) {
                            //si no es la primera posicion Y existe un espacio antes Y (es el primer acorde o hay dos espacios vacios antes del acorde)
                            newStart--
                            lenDiff--
                        } else {
                            break
                        }
                    }
                    chordLine = chordLine.replaceRange(newStart,newEnd + 1, newChord )
                }
                else if (oldChordLen > newChordLen) {
                    // Sol# -> La
                    if (occur.range.endInclusive < chordLine.length - 1 && chordLine[occur.range.endInclusive + 1] == ' ') {
                        chordLine = chordLine.replaceRange(
                            occur.range,
                            newChord + " ".repeat(oldChordLen - newChordLen)
                        )
                    } else {
                        chordLine = chordLine.replaceRange(occur.range," ".repeat(oldChordLen - newChordLen) + newChord )
                    }
                } else {
                    chordLine = chordLine.replaceRange( occur.range,newChord)
                }
            }
            return chordLine
        }

        fun changeLanguage(line: String): String {
            val chordsPattern =
                """(D[oO]|R[eE]|M[iI]|F[aA]|S(ol|OL)|L[aA]|S[iI]|A|B|C|D|E|F|G)(b|#)?""".toRegex(
                    RegexOption.IGNORE_CASE
                )
            val ocurrences = chordsPattern.findAll(line)
            var chordLine = line

            for (occur in ocurrences.toList().reversed()) {
                val oldChord = occur.value
                val language = getLanguage(oldChord)
                val newLanguage = if(language == Language.American) Language.Latin else Language.American
                val newChord = translateChord(oldChord, newLanguage)
                chordLine = chordLine.replaceRange( occur.range,newChord)
            }
            return chordLine
        }
        fun getLanguage(chord: String) : Language {
            val language : Language
            if(Chord.values().find { thisChord -> thisChord.chordLat == chord } == null) language = Language.American
            else language = Language.Latin
            return language
        }

        fun translateChord(chord: String, toLanguage: Language) : String {
            val replacingChord : String
            if(toLanguage == Language.American){
                replacingChord = Chord.values().find { thisChord -> thisChord.chordLat == chord }!!.chordAme
            }
            else {
                replacingChord = Chord.values().find { thisChord -> thisChord.chordAme == chord }!!.chordLat
            }
            return replacingChord
        }

        enum class Language { American, Latin }
    }
}