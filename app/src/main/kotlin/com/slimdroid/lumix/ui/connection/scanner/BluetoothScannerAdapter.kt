package com.slimdroid.lumix.ui.connection.scanner

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.content.pm.PackageManager
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.slimdroid.lumix.R
import com.slimdroid.lumix.databinding.FragmentDeviceScannerItemBinding

class BluetoothScannerAdapter : RecyclerView.Adapter<BluetoothScannerAdapter.ViewHolder>() {

    private val items = mutableListOf<BluetoothDevice>()

    private var onItemClickListener: ((item: BluetoothDevice) -> Unit)? = null

    fun setOnItemClickListener(listener: (item: BluetoothDevice) -> Unit) {
        onItemClickListener = listener
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setItems(items: List<BluetoothDevice>) {
        this.items.clear()
        this.items.addAll(items)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(
            FragmentDeviceScannerItemBinding
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
        private val binding: FragmentDeviceScannerItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind() {
            val item = items[adapterPosition]
            binding.apply {
                val resources = textName.context.resources
                textName.text = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    if (ContextCompat.checkSelfPermission(
                            root.context, Manifest.permission.BLUETOOTH_SCAN
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        resources.getString(R.string.device_scanner_no_permission_device_name)
                    } else {
                        item.name ?: resources.getString(R.string.device_scanner_unnamed)
                    }
                } else {
                    item.name ?: resources.getString(R.string.device_scanner_unnamed)
                }
                textAddress.text = item.address
            }
        }
    }
}
