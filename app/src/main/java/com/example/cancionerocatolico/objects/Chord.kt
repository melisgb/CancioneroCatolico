package com.example.cancionerocatolico.objects

import com.example.cancionerocatolico.R

enum class Chord (val chordLat: String, val chordAme: String, val chordUrl: Int
){
    A("La", "A", R.raw.a),
    B("Si", "B", R.raw.b),
    C("Do", "C", R.raw.c),
    D("Re", "D", R.raw.d),
    E("Mi", "E", R.raw.e),
    F("Fa", "F", R.raw.f),
    G("Sol", "G", R.raw.g),
    A7("La7", "A7", R.raw.a7),
    B7( "Si7", "B7", R.raw.b7),
    C7("Do7", "C7", R.raw.c7),
    D7("Re7", "D7", R.raw.d7),
    E7("Mi7", "E7", R.raw.e7),
    F7("Fa7", "F7", R.raw.f7),
    G7("Sol7", "G7", R.raw.g7),
    Am("Lam", "Am", R.raw.am),
    Bm( "Sim", "Bm", R.raw.bm),
    Cm("Dom", "Cm", R.raw.cm),
    Dm("Rem", "Dm", R.raw.dm),
    Em("Mim", "Em", R.raw.em),
    Fm("Fam", "Fm", R.raw.fm),
    Gm("Solm", "Gm", R.raw.gm),
//    Am7("Lam7", "Am7", R.raw.am7),
//    Bm7( "Sim7", "Bm7", R.raw.bm7),
//    Cm7("Dom7", "Cm7", R.raw.cm7),
//    Dm7("Rem7", "Dm7", R.raw.dm7),
//    Em7("M7im7", "Em7", R.raw.em7),
//    Fm7("Fam7", "Fm7", R.raw.fm7),
//    Gm7("Solm7", "Gm7", R.raw.gm7),
//    Asus("La#", "A#", R.raw.asus),
//    Bsus("Si#", "B#", R.raw.bsus),
//    Csus("Do#", "C#", R.raw.csus),
//    Dsus("Re#", "D#", R.raw.dsus),
//    Esus("Mi#", "E#", R.raw.esus),
//    Fsus("Fa#", "F#", R.raw.fsus),
//    Gsus("Sol#", "G#", R.raw.gsus)
}