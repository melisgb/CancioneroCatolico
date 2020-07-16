package com.example.cancionerocatolico.objects

class ListSongs(listSongsID: Int, listSongsName: String, songsList: HashMap<Int, Song>) {
    var listSongsID = listSongsID
    var listSongsName = listSongsName
    var songs = songsList
}