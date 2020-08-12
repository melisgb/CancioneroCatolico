package com.example.cancionerocatolico.objects

enum class Chord (val chordLat: String, val chordAme: String, val notes: Array<Note>){
    //  maj is the same as major
    C("Do", "C", arrayOf(Note.C3, Note.E3, Note.G3)),
    D("Re", "D", arrayOf(Note.D3, Note.F3Sharp, Note.A3)),
    E("Mi", "E", arrayOf(Note.E3, Note.G3Sharp, Note.B3)),
    F("Fa", "F", arrayOf(Note.F3, Note.A3, Note.C4)),
    G("Sol", "G", arrayOf(Note.G3, Note.B3, Note.D4)),
    A("La", "A", arrayOf(Note.A3, Note.C4Sharp, Note.E4)),
    B("Si", "B", arrayOf(Note.B3, Note.D4Sharp, Note.F4Sharp)),

    Cm("Dom", "Cm", arrayOf(Note.C3, Note.D3Sharp, Note.G3)),
    Dm("Rem", "Dm", arrayOf(Note.D3, Note.F3, Note.A3)),
    Em("Mim", "Em", arrayOf(Note.E3, Note.G3, Note.B3)),
    Fm("Fam", "Fm", arrayOf(Note.F3, Note.G3Sharp, Note.C4)),
    Gm("Solm", "Gm", arrayOf(Note.G3, Note.A3Sharp, Note.D4)),
    Am("Lam", "Am", arrayOf(Note.A3, Note.C4, Note.E4)),
    Bm( "Sim", "Bm",arrayOf(Note.B3, Note.D4, Note.F4Sharp)),

    C7("Do7", "C7", arrayOf(Note.C3, Note.E3, Note.G3, Note.A3Sharp)),
    D7("Re7", "D7", arrayOf(Note.D3, Note.F3Sharp, Note.A3, Note.C4)),
    E7("Mi7", "E7", arrayOf(Note.E3, Note.G3Sharp, Note.B4, Note.D4)),
    F7("Fa7", "F7", arrayOf(Note.F3, Note.A3, Note.C4, Note.D4Sharp)),
    G7("Sol7", "G7", arrayOf(Note.G3, Note.B3, Note.D4, Note.F4)),
    A7("La7", "A7", arrayOf(Note.A3, Note.C4Sharp, Note.E4, Note.G4)),
    B7( "Si7", "B7",arrayOf(Note.B3, Note.D4Sharp, Note.F4Sharp, Note.A4)),

    Cm7("Dom7", "Cm7", arrayOf(Note.C3, Note.D3Sharp, Note.G3, Note.A3Sharp)),
    Dm7("Rem7", "Dm7", arrayOf(Note.D3, Note.F3, Note.A3, Note.C4)),
    Em7("Mim7", "Em7", arrayOf(Note.E3, Note.G3, Note.B3, Note.D4)),
    Fm7("Fam7", "Fm7", arrayOf(Note.F3, Note.G3Sharp, Note.C4, Note.D4Sharp)),
    Gm7("Solm7", "Gm7",  arrayOf(Note.G3, Note.A3Sharp, Note.D4, Note.F4)),
    Am7("Lam7", "Am7", arrayOf(Note.A3, Note.C4, Note.E4, Note.G4)),
    Bm7("Sim7", "Bm7", arrayOf(Note.B3, Note.D4, Note.F4Sharp, Note.A4)),

    //Sharp is the same as bemol ---> G# = Ab
    CSharp("Do#", "C#", arrayOf(Note.C3Sharp, Note.F3, Note.G3Sharp)),
    DSharp("Re#", "D#", arrayOf(Note.D3Sharp, Note.G3, Note.A3Sharp)),
//    ESharp("Mi#", "E#", arrayOf(Note.F3, Note.A3, Note.C4)),
    FSharp("Fa#", "F#", arrayOf(Note.F3Sharp, Note.A3Sharp, Note.C4Sharp)),
    GSharp("Sol#", "G#", arrayOf(Note.G3Sharp, Note.C4, Note.D4Sharp)),
    ASharp("La#", "A#", arrayOf(Note.A3Sharp, Note.D4, Note.F4)),
//    BSharp("Si#", "B#", arrayOf(Note.C4, Note.E4, Note.G4)),

    CSharpm("Do#m", "C#m", arrayOf(Note.C3Sharp, Note.E3, Note.G3Sharp)),
    DSharpm("Re#m", "D#m", arrayOf(Note.D3Sharp, Note.F3Sharp, Note.A3Sharp)),
//    ESharpm("Mi#m", "E#m", arrayOf(Note.F3, Note.G3Sharp, Note.C4)),
    FSharpm("Fa#m", "F#m", arrayOf(Note.F3Sharp, Note.A3, Note.C4Sharp)),
    GSharpm("Sol#m", "G#m", arrayOf(Note.G3Sharp, Note.B3, Note.D4Sharp)),
    ASharpm("La#m", "A#m", arrayOf(Note.A3Sharp, Note.C4Sharp, Note.F4)),
//    BSharpm("Si#m", "B#m", arrayOf(Note.C4, Note.D4Sharp, Note.G4)),

    CSharp7("Do#7", "C#7", arrayOf(Note.C3Sharp, Note.F3, Note.G3Sharp, Note.B3)),
    DSharp7("Re#7", "D#7", arrayOf(Note.D3Sharp, Note.G3, Note.A3Sharp, Note.C4Sharp)),
//    ESharp7("Mi#7", "E#7", arrayOf(Note.F3, Note.A3, Note.C4, Note.D4Sharp)),
    FSharp7("Fa#7", "F#7", arrayOf(Note.F3Sharp, Note.A3Sharp, Note.C4Sharp, Note.E4)),
    GSharp7("Sol#7", "G#7", arrayOf(Note.G3Sharp, Note.C4, Note.D4Sharp, Note.F4Sharp)),
    ASharp7("La#7", "A#7", arrayOf(Note.A3Sharp, Note.D4, Note.F4, Note.G4Sharp)),
//    BSharp7("Si#7", "B#7", arrayOf(Note.C4, Note.E4, Note.G4, Note.A4Sharp)),

    CSharpm7("Do#m7", "C#m7", arrayOf(Note.C3Sharp, Note.E3, Note.G3Sharp, Note.B3)),
    DSharpm7("Re#m7", "D#m7", arrayOf(Note.D3Sharp, Note.F3Sharp, Note.A3Sharp, Note.C4Sharp)),
//    ESharpm7("Mi#m7", "E#m7", arrayOf(Note.F3, Note.G3Sharp, Note.C4, Note.D4Sharp)),
    FSharpm7("Fa#m7", "F#m7", arrayOf(Note.F3Sharp, Note.A3, Note.C4Sharp, Note.E4)),
    GSharpm7("Sol#m7", "G#m7", arrayOf(Note.G3Sharp, Note.B3, Note.D4Sharp, Note.F4Sharp)),
    ASharpm7("La#m7", "A#m7", arrayOf(Note.A3Sharp, Note.C4Sharp, Note.F4, Note.G4Sharp)),
//    BSharpm7("Si#m7", "B#m7", arrayOf(Note.C4, Note.D4Sharp, Note.G4, Note.A4Sharp)),

    // "add" is the same as sus -> Csus4 = Cadd4
    Csus4("Dosus", "Csus", arrayOf(Note.C3, Note.F3, Note.G3)),
    Dsus4("Resus", "Dsus", arrayOf(Note.D3, Note.G3, Note.A3)),
    Esus4("Misus", "Esus", arrayOf(Note.E3, Note.A3, Note.B3)),
    Fsus4("Fasus", "Fsus", arrayOf(Note.F3, Note.A3Sharp, Note.C4)),
    Gsus4("Solsus", "Gsus", arrayOf(Note.G3, Note.C4, Note.D4)),
    Asus4("Lasus", "Asus", arrayOf(Note.A3, Note.D4, Note.E4)),
    Bsus4("Sisus", "Bsus", arrayOf(Note.B3, Note.E4, Note.F4Sharp))

//    Asus("La#", "A#", R.raw.asus),
//    Bsus("Si#", "B#", R.raw.bsus),
//    Csus("Do#", "C#", R.raw.csus),
//    Dsus("Re#", "D#", R.raw.dsus),
//    Esus("Mi#", "E#", R.raw.esus),
//    Fsus("Fa#", "F#", R.raw.fsus),
//    Gsus("Sol#", "G#", R.raw.gsus)
}