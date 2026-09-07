package rosh.gambar

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.GridLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import rosh.gambar.lib.Folder
import rosh.gambar.lib.MediaImage

class Gambar : AppCompatActivity() {

    private lateinit var pusat: DrawerLayout
    private lateinit var tempatFile: LinearLayout
    private lateinit var tempatFolder: GridLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.gambar)

        PasangId()
        Tombol()
        Awal()
    }

    private fun PasangId() {
        pusat = findViewById(R.id.pusat)
        tempatFile = findViewById(R.id.tempat_file)
        tempatFolder = findViewById(R.id.tempat_folder)
    }

    private fun Awal() {
        MuatFolder()
        MuatFile(null)
    }

    private fun Keluar() {}
    private fun Tombol() {}

    private fun MuatFolder() {
        tempatFolder.removeAllViews()
        val daftarFolder = MediaImage(this).getFolder()
        
        for (folder in daftarFolder) {
            val item = LayoutInflater.from(this).inflate(R.layout.item_tempat_folder, tempatFolder, false)
            val nama = item.findViewById<TextView>(R.id.nama)
            
            // Mengambil property nama dari data class Folder
            nama.text = folder.nama 
            
            item.setOnClickListener {
                // Muat file dari folder yang dipilih dan otomatis tutup drawer
                val fileDiFolder = MediaImage(this@Gambar).getFile(folder)
                MuatFile(fileDiFolder)
                pusat.closeDrawer(GravityCompat.START)
            }
            tempatFolder.addView(item)
        }
    }

    private fun MuatFile(terima: List<Uri>?) {
        tempatFile.removeAllViews()
        
        if (terima.isNullOrEmpty()) {
            // Perbaikan typo: R.layout_item_list_kosonh
            val item = LayoutInflater.from(this).inflate(R.layout.item_list_kosong, tempatFile, false)
            tempatFile.addView(item)
            item.setOnClickListener {
                pusat.openDrawer(GravityCompat.START)
            }
        } else {
            // Buat Grid SATU kali saja sebagai wadah semua gambar
            val itemGrid = LayoutInflater.from(this).inflate(R.layout.item_list_gambar, tempatFile, false)
            val grid = itemGrid.findViewById<GridLayout>(R.id.grid)
            tempatFile.addView(itemGrid)

            for (isi in terima) {
                val ig = LayoutInflater.from(this).inflate(R.layout.item_hanya_gambar, grid, false)
                val g = ig.findViewById<ImageView>(R.id.gambar)
                
                // Set gambar ke ImageView (Sebaiknya gunakan Glide/Coil di tahap produksi)
                g.setImageURI(isi)
                
                g.setOnClickListener {
                    // Pastikan PenampilGambar(this) sudah diimplementasi di file terpisah
                     PenampilGambar(this).show(isi)
                }
                grid.addView(ig)
            }
        }
    }
}
