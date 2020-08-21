package com.example.cancionerocatolico.objects

import com.example.cancionerocatolico.R

enum class Chord (val chordLat: String, val chordAme: String, val notes: Array<Note>, val imgPath : Int){
    //  maj is the same as major
    C("Do", "C", arrayOf(Note.C3, Note.E3, Note.G3), R.drawable.c_chord),
    D("Re", "D", arrayOf(Note.D3, Note.F3Sharp, Note.A3), R.drawable.d_chord),
    E("Mi", "E", arrayOf(Note.E3, Note.G3Sharp, Note.B3), R.drawable.e_chord),
    F("Fa", "F", arrayOf(Note.F3, Note.A3, Note.C4), R.drawable.f_chord),
    G("Sol", "G", arrayOf(Note.G3, Note.B3, Note.D4), R.drawable.g_chord),
    A("La", "A", arrayOf(Note.A3, Note.C4Sharp, Note.E4), R.drawable.a_chord),
    B("Si", "B", arrayOf(Note.B3, Note.D4Sharp, Note.F4Sharp), R.drawable.b_chord),

    Cm("Dom", "Cm", arrayOf(Note.C3, Note.D3Sharp, Note.G3), R.drawable.a_chord),
    Dm("Rem", "Dm", arrayOf(Note.D3, Note.F3, Note.A3), R.drawable.a_chord),
    Em("Mim", "Em", arrayOf(Note.E3, Note.G3, Note.B3), R.drawable.a_chord),
    Fm("Fam", "Fm", arrayOf(Note.F3, Note.G3Sharp, Note.C4), R.drawable.a_chord),
    Gm("Solm", "Gm", arrayOf(Note.G3, Note.A3Sharp, Note.D4), R.drawable.a_chord),
    Am("Lam", "Am", arrayOf(Note.A3, Note.C4, Note.E4), R.drawable.a_chord),
    Bm( "Sim", "Bm",arrayOf(Note.B3, Note.D4, Note.F4Sharp), R.drawable.a_chord),

    C7("Do7", "C7", arrayOf(Note.C3, Note.E3, Note.G3, Note.A3Sharp), R.drawable.a_chord),
    D7("Re7", "D7", arrayOf(Note.D3, Note.F3Sharp, Note.A3, Note.C4), R.drawable.a_chord),
    E7("Mi7", "E7", arrayOf(Note.E3, Note.G3Sharp, Note.B4, Note.D4), R.drawable.a_chord),
    F7("Fa7", "F7", arrayOf(Note.F3, Note.A3, Note.C4, Note.D4Sharp), R.drawable.a_chord),
    G7("Sol7", "G7", arrayOf(Note.G3, Note.B3, Note.D4, Note.F4), R.drawable.a_chord),
    A7("La7", "A7", arrayOf(Note.A3, Note.C4Sharp, Note.E4, Note.G4), R.drawable.a_chord),
    B7( "Si7", "B7",arrayOf(Note.B3, Note.D4Sharp, Note.F4Sharp, Note.A4), R.drawable.a_chord),

    Cm7("Dom7", "Cm7", arrayOf(Note.C3, Note.D3Sharp, Note.G3, Note.A3Sharp), R.drawable.a_chord),
    Dm7("Rem7", "Dm7", arrayOf(Note.D3, Note.F3, Note.A3, Note.C4), R.drawable.a_chord),
    Em7("Mim7", "Em7", arrayOf(Note.E3, Note.G3, Note.B3, Note.D4), R.drawable.a_chord),
    Fm7("Fam7", "Fm7", arrayOf(Note.F3, Note.G3Sharp, Note.C4, Note.D4Sharp), R.drawable.a_chord),
    Gm7("Solm7", "Gm7",  arrayOf(Note.G3, Note.A3Sharp, Note.D4, Note.F4), R.drawable.a_chord),
    Am7("Lam7", "Am7", arrayOf(Note.A3, Note.C4, Note.E4, Note.G4), R.drawable.a_chord),
    Bm7("Sim7", "Bm7", arrayOf(Note.B3, Note.D4, Note.F4Sharp, Note.A4), R.drawable.a_chord),

    //Sharp is the same as bemol ---> G# = Ab
    CSharp("Do#", "C#", arrayOf(Note.C3Sharp, Note.F3, Note.G3Sharp), R.drawable.a_chord),
    DSharp("Re#", "D#", arrayOf(Note.D3Sharp, Note.G3, Note.A3Sharp), R.drawable.a_chord),
//    ESharp("Mi#", "E#", arrayOf(Note.F3, Note.A3, Note.C4)),
    FSharp("Fa#", "F#", arrayOf(Note.F3Sharp, Note.A3Sharp, Note.C4Sharp), R.drawable.a_chord),
    GSharp("Sol#", "G#", arrayOf(Note.G3Sharp, Note.C4, Note.D4Sharp), R.drawable.a_chord),
    ASharp("La#", "A#", arrayOf(Note.A3Sharp, Note.D4, Note.F4), R.drawable.a_chord),
//    BSharp("Si#", "B#", arrayOf(Note.C4, Note.E4, Note.G4)),

    CSharpm("Do#m", "C#m", arrayOf(Note.C3Sharp, Note.E3, Note.G3Sharp), R.drawable.a_chord),
    DSharpm("Re#m", "D#m", arrayOf(Note.D3Sharp, Note.F3Sharp, Note.A3Sharp), R.drawable.a_chord),
//    ESharpm("Mi#m", "E#m", arrayOf(Note.F3, Note.G3Sharp, Note.C4)),
    FSharpm("Fa#m", "F#m", arrayOf(Note.F3Sharp, Note.A3, Note.C4Sharp), R.drawable.a_chord),
    GSharpm("Sol#m", "G#m", arrayOf(Note.G3Sharp, Note.B3, Note.D4Sharp), R.drawable.a_chord),
    ASharpm("La#m", "A#m", arrayOf(Note.A3Sharp, Note.C4Sharp, Note.F4), R.drawable.a_chord),
//    BSharpm("Si#m", "B#m", arrayOf(Note.C4, Note.D4Sharp, Note.G4)),

    CSharp7("Do#7", "C#7", arrayOf(Note.C3Sharp, Note.F3, Note.G3Sharp, Note.B3), R.drawable.a_chord),
    DSharp7("Re#7", "D#7", arrayOf(Note.D3Sharp, Note.G3, Note.A3Sharp, Note.C4Sharp), R.drawable.a_chord),
//    ESharp7("Mi#7", "E#7", arrayOf(Note.F3, Note.A3, Note.C4, Note.D4Sharp)),
    FSharp7("Fa#7", "F#7", arrayOf(Note.F3Sharp, Note.A3Sharp, Note.C4Sharp, Note.E4), R.drawable.a_chord),
    GSharp7("Sol#7", "G#7", arrayOf(Note.G3Sharp, Note.C4, Note.D4Sharp, Note.F4Sharp), R.drawable.a_chord),
    ASharp7("La#7", "A#7", arrayOf(Note.A3Sharp, Note.D4, Note.F4, Note.G4Sharp), R.drawable.a_chord),
//    BSharp7("Si#7", "B#7", arrayOf(Note.C4, Note.E4, Note.G4, Note.A4Sharp)),

    CSharpm7("Do#m7", "C#m7", arrayOf(Note.C3Sharp, Note.E3, Note.G3Sharp, Note.B3), R.drawable.a_chord),
    DSharpm7("Re#m7", "D#m7", arrayOf(Note.D3Sharp, Note.F3Sharp, Note.A3Sharp, Note.C4Sharp), R.drawable.a_chord),
//    ESharpm7("Mi#m7", "E#m7", arrayOf(Note.F3, Note.G3Sharp, Note.C4, Note.D4Sharp)),
    FSharpm7("Fa#m7", "F#m7", arrayOf(Note.F3Sharp, Note.A3, Note.C4Sharp, Note.E4), R.drawable.a_chord),
    GSharpm7("Sol#m7", "G#m7", arrayOf(Note.G3Sharp, Note.B3, Note.D4Sharp, Note.F4Sharp), R.drawable.a_chord),
    ASharpm7("La#m7", "A#m7", arrayOf(Note.A3Sharp, Note.C4Sharp, Note.F4, Note.G4Sharp), R.drawable.a_chord),
//    BSharpm7("Si#m7", "B#m7", arrayOf(Note.C4, Note.D4Sharp, Note.G4, Note.A4Sharp)),

    // "add" is the same as sus -> Csus4 = Cadd4
    Csus4("Dosus", "Csus", arrayOf(Note.C3, Note.F3, Note.G3), R.drawable.a_chord),
    Dsus4("Resus", "Dsus", arrayOf(Note.D3, Note.G3, Note.A3), R.drawable.a_chord),
    Esus4("Misus", "Esus", arrayOf(Note.E3, Note.A3, Note.B3), R.drawable.a_chord),
    Fsus4("Fasus", "Fsus", arrayOf(Note.F3, Note.A3Sharp, Note.C4), R.drawable.a_chord),
    Gsus4("Solsus", "Gsus", arrayOf(Note.G3, Note.C4, Note.D4), R.drawable.a_chord),
    Asus4("Lasus", "Asus", arrayOf(Note.A3, Note.D4, Note.E4), R.drawable.a_chord),
    Bsus4("Sisus", "Bsus", arrayOf(Note.B3, Note.E4, Note.F4Sharp), R.drawable.a_chord),

//    Asus("La#", "A#", R.raw.asus),
//    Bsus("Si#", "B#", R.raw.bsus),
//    Csus("Do#", "C#", R.raw.csus),
//    Dsus("Re#", "D#", R.raw.dsus),
//    Esus("Mi#", "E#", R.raw.esus),
//    Fsus("Fa#", "F#", R.raw.fsus),
//    Gsus("Sol#", "G#", R.raw.gsus)
}