package com.example.testdemo.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.testdemo.R
import com.example.testdemo.model.WalletItem
import com.example.testdemo.util.formatAmount

/**
 * Created by xiongjiachen on 2025/2/27
 * @author xiongjiachen@bytedance.com
 */
class WalletAdapter : ListAdapter<WalletItem, WalletAdapter.WalletViewHolder>(WalletDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WalletViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_wallet, parent, false)
        return WalletViewHolder(view)
    }

    override fun onBindViewHolder(holder: WalletViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    override fun onBindViewHolder(holder: WalletViewHolder, position: Int, payloads: List<Any>) {
        if (payloads.isEmpty()) {
            super.onBindViewHolder(holder, position, payloads)
        } else {
            // 只更新convertedAmount
            val update = payloads[0] as UpdatePayload
            if (update.updateType == UpdateType.RATE) {
                holder.updateConvertedAmount(update.newItem)
            }
        }
    }

    class WalletViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val currencyIcon: ImageView = itemView.findViewById(R.id.currencyIcon)
        private val currencyName: TextView = itemView.findViewById(R.id.currencyName)
        private val currencyBalance: TextView = itemView.findViewById(R.id.currencyBalance)
        private val convertedAmount: TextView = itemView.findViewById(R.id.convertedAmount)

        fun bind(item: WalletItem) {
            currencyName.text = item.currency.name
            currencyBalance.text = formatAmount(item)
            convertedAmount.text = formatCurrency(item)

            Glide.with(currencyIcon)
                .load(item.currency.colorfulImageUrl)
                .circleCrop()
                .into(currencyIcon)
        }

        fun updateConvertedAmount(item: WalletItem) {
            convertedAmount.text = formatCurrency(item)
        }

        private fun formatAmount(item: WalletItem): String {
            val amount = item.balance.formatAmount(item.currency.displayDecimal)
            val symbol = item.currency.symbol
            return "$amount $symbol"
        }

        private fun formatCurrency(item: WalletItem): String {
            return "$ ${item.convertedAmount.formatAmount(item.currency.displayDecimal)}"
        }
    }
}

class WalletDiffCallback : DiffUtil.ItemCallback<WalletItem>() {
    override fun areItemsTheSame(oldItem: WalletItem, newItem: WalletItem): Boolean {
        return oldItem.currency.symbol == newItem.currency.symbol
    }

    override fun areContentsTheSame(oldItem: WalletItem, newItem: WalletItem): Boolean {
        return oldItem.currency == newItem.currency &&
                oldItem.balance == newItem.balance &&
                oldItem.convertedAmount.compareTo(newItem.convertedAmount) == 0
    }

    override fun getChangePayload(oldItem: WalletItem, newItem: WalletItem): Any? {
        // 只有convertedAmount变化时，返回部分更新的payload
        if (oldItem.currency == newItem.currency &&
            oldItem.balance == newItem.balance &&
            oldItem.convertedAmount != newItem.convertedAmount
        ) {
            return UpdatePayload(UpdateType.RATE, newItem)
        }
        return null
    }
}

enum class UpdateType {
    BALANCE,
    RATE
}

data class UpdatePayload(val updateType: UpdateType, val newItem: WalletItem)