package rosh.gambar

import rosh.gambar.lib.Folder
import rosh.gambar.lib.MediaImage
import rosh.gambar.lib.Klik

import android.graphics.*
import android.net.Uri
import android.os.*
import android.provider.MediaStore
import android.util.Size
import android.view.LayoutInflater
import android.widget.*
import android.content.*
import android.content.res.Configuration

import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.*
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.AppCompatDelegate
import androidx.activity.enableEdgeToEdge
import androidx.activity.OnBackPressedCallback

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
        Izin()
        pusat.post { BacaIntent() }
    }

    private fun PasangId() {
        pusat = findViewById(R.id.pusat)
        tempatFile = findViewById(R.id.tempat_file)
        tempatFolder = findViewById(R.id.tempat_folder)
    }

    private fun Awal() {
        ViewCompat.setOnApplyWindowInsetsListener(pusat) { view, insets ->
            val bars = insets.getInsets( WindowInsetsCompat.Type.systemBars() )

            view.setPadding( bars.left, bars.top, bars.right, bars.bottom )
            insets
        }
    }

    private fun Keluar() {finish()}
    
    private fun GantiTema(){
        val dark = (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES

        AppCompatDelegate.setDefaultNightMode(
            if (dark) AppCompatDelegate.MODE_NIGHT_NO
            else AppCompatDelegate.MODE_NIGHT_YES
        )
    }

    private fun MuatFolder() {
        tempatFolder.removeAllViews()
        val daftarFolder = MediaImage(this).getFolder()
        
        for (folder in daftarFolder) {
            val item = LayoutInflater.from(this).inflate(R.layout.item_tempat_folder, tempatFolder, false)
            val nama = item.findViewById<TextView>(R.id.nama)
            val jumlah = item.findViewById<TextView>(R.id.jumlah)
            val satu = item.findViewById<ImageView>(R.id.satu)
            val dua = item.findViewById<ImageView>(R.id.dua)
            val tiga = item.findViewById<ImageView>(R.id.tiga)
            val empat = item.findViewById<ImageView>(R.id.empat)
            
            nama.text = folder.nama 
            val fileDiFolder = MediaImage(this@Gambar).getFile(folder)
            jumlah.text = fileDiFolder.size.toString()
            
            val wadahGambar = listOf(satu, dua, tiga, empat)
            for (i in wadahGambar.indices) {
                if (i < fileDiFolder.size) {
                    MuatBipmap(fileDiFolder[i], wadahGambar[i])
                } else {
                    wadahGambar[i].setImageDrawable(null)
                }
            }
            
            item.setOnClickListener {
                MuatFile(fileDiFolder)
                pusat.closeDrawer(GravityCompat.START)
            }
            tempatFolder.addView(item)
        }
    }

    private fun MuatFile(terima: List<Uri>?) {
        tempatFile.removeAllViews()
        
        if (terima.isNullOrEmpty()) {
            val item = LayoutInflater.from(this).inflate(R.layout.item_list_kosong, tempatFile, false)
            tempatFile.addView(item)
            item.setOnClickListener {
                pusat.openDrawer(GravityCompat.START)
            }
        } else {
            val itemGrid = LayoutInflater.from(this).inflate(R.layout.item_list_gambar, tempatFile, false)
            val grid = itemGrid.findViewById<GridLayout>(R.id.grid)
            tempatFile.addView(itemGrid)

            for (isi in terima) {
                val ig = LayoutInflater.from(this).inflate(R.layout.item_hanya_gambar, grid, false)
                val g = ig.findViewById<ImageView>(R.id.gambar)
                
                MuatBipmap(isi, g)
                
                g.setOnClickListener {
                      PenampilGambar(this).show(isi)
                }
                grid.addView(ig)
            }
        }
    }

    private fun MuatBipmap(uri: Uri, imageView: ImageView) {
        Thread {
            try {
                val bitmap: Bitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    contentResolver.loadThumbnail(uri, Size(300, 300), null)
                } else {
                    MediaStore.Images.Media.getBitmap(contentResolver, uri)
                }
                
                runOnUiThread {
                    imageView.setImageBitmap(bitmap)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }.start()
    }
    
    private fun Tombol(){
        onBackPressedDispatcher.addCallback(this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if(pusat.isDrawerOpen(GravityCompat.START)){
                        pusat.closeDrawer(GravityCompat.START)
                    }else{Keluar()}
                }
            }
        )
        Klik(findViewById<ImageView>(R.id.nav)).sekali{
            pusat.openDrawer(GravityCompat.START)
        }
        
        Klik(findViewById<ImageView>(R.id.tutup)).sekali{
            pusat.closeDrawer(GravityCompat.START)
        }
        
        Klik(findViewById<ImageView>(R.id.keluar)).sekali{
            Keluar()
        }
        
        Klik(findViewById<LinearLayout>(R.id.tema)).sekali{
            GantiTema()
        }
    }
    
    private fun Izin() {
    if (!BacaIzin()) {
        MintaIzin()
    }else{
        MuatFolder()
        MuatFile(null)
    }
}

private fun BacaIzin(): Boolean {

    return if (Build.VERSION.SDK_INT >= 34) {
        val gambar = checkSelfPermission(
            android.Manifest.permission.READ_MEDIA_IMAGES
        ) == android.content.pm.PackageManager.PERMISSION_GRANTED

        val pilihan = checkSelfPermission(
            android.Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED
        ) == android.content.pm.PackageManager.PERMISSION_GRANTED
        gambar || pilihan

    } else if (Build.VERSION.SDK_INT >= 33) {
        checkSelfPermission(
            android.Manifest.permission.READ_MEDIA_IMAGES
        ) == android.content.pm.PackageManager.PERMISSION_GRANTED
    } else {
        checkSelfPermission(
            android.Manifest.permission.READ_EXTERNAL_STORAGE
        ) == android.content.pm.PackageManager.PERMISSION_GRANTED
    }
}

private fun MintaIzin() {
    if (Build.VERSION.SDK_INT >= 34) {
        requestPermissions(
            arrayOf(
                android.Manifest.permission.READ_MEDIA_IMAGES,
                android.Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED
            ), 100
        )

    } else if (Build.VERSION.SDK_INT >= 33) {
        requestPermissions(
            arrayOf(
                android.Manifest.permission.READ_MEDIA_IMAGES
            ), 100
        )
    } else {
        requestPermissions(
            arrayOf(
                android.Manifest.permission.READ_EXTERNAL_STORAGE
            ), 100
        )
    }
        if(BacaIzin()){
            MuatFolder()
            MuatFile(null)
        }
    }
    
    private fun BacaIntent() {

    val aksi = intent.action

    when (aksi) {

        Intent.ACTION_VIEW -> {
            val isi = intent.data

            if (isi != null) {
                PenampilGambar(this).show(isi)
            }
        }

        Intent.ACTION_SEND -> {
            val isi =
                intent.getParcelableExtra<Uri>(
                    Intent.EXTRA_STREAM
                )

            if (isi != null) {
                PenampilGambar(this).show(isi)
            }
        }

        Intent.ACTION_SEND_MULTIPLE -> {
            val isi =
                intent.getParcelableArrayListExtra<Uri>(
                    Intent.EXTRA_STREAM
                )

            if (!isi.isNullOrEmpty()) {
                MuatFile(isi)
            }
        }
    }
}
}
