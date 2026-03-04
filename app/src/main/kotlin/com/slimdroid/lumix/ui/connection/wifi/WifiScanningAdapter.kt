package com.slimdroid.lumix.ui.connection.wifi

import android.annotation.SuppressLint
import android.net.wifi.ScanResult
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.DrawableRes
import androidx.recyclerview.widget.RecyclerView
import com.slimdroid.lumix.R
import com.slimdroid.lumix.databinding.FragmentWifiScanningItemBinding

class WifiScanningAdapter : RecyclerView.Adapter<WifiScanningAdapter.ViewHolder>() {

    private val items = mutableListOf<ScanResult>()

    private var onItemClickListener: ((item: ScanResult) -> Unit)? = null

    fun setOnItemClickListener(listener: (item: ScanResult) -> Unit) {
        onItemClickListener = listener
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setItems(items: List<ScanResult>) {
        this.items.clear()
        this.items.addAll(items)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(
            FragmentWifiScanningItemBinding
                .inflate(LayoutInflater.from(parent.context), parent, false)
        ).apply {
            itemView.setOnClickListener {
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    onItemClickListener?.invoke(items[adapterPosition])
                }
            }
        }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind()
    }

    override fun getItemCount() = items.size

    inner class ViewHolder(
        private val binding: FragmentWifiScanningItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind() {
            val item = items[adapterPosition]
            binding.apply {
                textSsid.text = item.SSID
                imageLevel.setImageResource(getLevelIcon(item.level))
                imageSecurity.visibility = if (
                    item.capabilities.contains("[WPA")
                    || item.capabilities.contains("[WEP")
                ) View.VISIBLE else View.INVISIBLE
                item.isPasspointNetwork
            }
        }

        @DrawableRes
        private fun getLevelIcon(level: Int) = when (level) {
            in -63..-1 -> R.drawable.ic_24_wifi_level_4
            in -73..-64 -> R.drawable.ic_24_wifi_level_3
            in -83..-74 -> R.drawable.ic_24_wifi_level_2
            in -93..-84 -> R.drawable.ic_24_wifi_level_1
            else -> R.drawable.ic_24_wifi_level_0
        }
    }
}

