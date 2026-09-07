package rosh.gambar

import android.app.Activity
import android.net.Uri
import android.view.*
import android.widget.*

class PenampilGambar(private val kelas: Activity) {
    fun show(file: Uri) {
        val item = LayoutInflater.from(kelas).inflate(R.layout.pop_penampil_gambar, null)
        val pop = PopupWindow( item, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, true )
        val root = kelas.window.decorView.rootView
        val tutup = item.findViewById<TextView>(R.id.tutup)
        val nama = item.findViewById<TextView>(R.id.nama_file)
        val gambar = item.findViewById<ImageView>(R.id.gambar_file)
        gambar.setImageURI(file)

        tutup.setOnClickListener { pop.dismiss() }
        pop.showAtLocation( root, Gravity.CENTER, 0, 0 )
    }
}