package com.gmgb.cancionerocatolico

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.GridView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.children
import com.gmgb.cancionerocatolico.R
import com.gmgb.cancionerocatolico.adapter.ChordAdapter
import com.gmgb.cancionerocatolico.objects.Chord
import com.google.android.material.chip.Chip

class PlayChordActivity : AppCompatActivity() {
    private val selectedFilters = HashSet<String>()
    private var chordsAdapter : ChordAdapter? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_play_chords)

        val chordsList = Chord.values().sortedBy { it.name }.toList()

        val gridView = findViewById<GridView>(R.id.gridvChords)
        chordsAdapter =
            ChordAdapter(this, chordsList)
        gridView.adapter = chordsAdapter
        chordsAdapter?.loadAllNotes()
    }

    fun chipClicked(view: View) {
        val chipFilter = findViewById<Chip>(view.id)
        val chipText = chipFilter.text.toString()

        if(selectedFilters.contains(chipText)){
            selectedFilters.remove(chipText)
            chipFilter.isSelected = false
        }
        else{
            chipFilter.isSelected = true
            chipFilter.setOnCloseIconClickListener { chipClicked(view) }
            selectedFilters.add(chipText)
        }
        filterByChord()
    }

    private fun filterByChord(){
        val newListChords = ArrayList<Chord>()
        if(selectedFilters.isNotEmpty()){
            for(key in selectedFilters){
                newListChords.addAll(Chord.values().filter { chord -> chord.chordAme.contains(key) }.sortedBy {  it.name })
            }
        }
        else{
            //selectedFilters is empty. Show all chords.
            newListChords.addAll(Chord.values().sortedBy { it.name })
        }
        chordsAdapter!!.chordsList = newListChords
        chordsAdapter!!.notifyDataSetChanged()
    }


    override fun onCreateOptionsMenu( menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.play_chord_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem) : Boolean {
        return when (item.itemId) {
            R.id.action_refresh_filters -> {
                val chordsChips = findViewById<ViewGroup>(R.id.chordsChipGroup)
                val chordsChipsList = chordsChips.children.toList()
                for(v in chordsChipsList){
                    val chip = v as Chip
                    chip.isSelected = false
                }
                selectedFilters.clear()
                filterByChord()
                true
            }
            else -> false
        }
    }

}