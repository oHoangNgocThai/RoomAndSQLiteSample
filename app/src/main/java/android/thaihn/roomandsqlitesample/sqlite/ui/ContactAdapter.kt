package android.thaihn.roomandsqlitesample.sqlite.ui

import android.thaihn.roomandsqlitesample.databinding.ItemContactBinding
import android.thaihn.roomandsqlitesample.sqlite.entity.Contact
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class ContactAdapter(
        private val items: ArrayList<Contact>,
        private val listener: ContactListener
) : RecyclerView.Adapter<ContactAdapter.ViewHolder>() {

    interface ContactListener {
        fun editContact(item: Contact)
        fun deleteContact(item: Contact)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflate = LayoutInflater.from(parent.context)
        return ViewHolder(ItemContactBinding.inflate(layoutInflate, parent, false), listener)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindData(items[position])
    }

    class ViewHolder(
            private val binding: ItemContactBinding,
            private val listener: ContactListener
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bindData(item: Contact) {
            binding.contact = item

            binding.imgEdit.setOnClickListener {
                listener.editContact(item)
            }

            binding.imgDelete.setOnClickListener {
                listener.deleteContact(item)
            }

            binding.executePendingBindings()
        }
    }

    fun addAllContact(contacts: List<Contact>) {
        items.clear()
        items.addAll(contacts)
        notifyDataSetChanged()
    }

    fun addContact(item: Contact) {
        items.add(0, item)
        notifyItemInserted(0)
    }

    fun updateContact(item: Contact) {
        val index = items.indexOfFirst {
            it.id == item.id
        }
        if (index != -1) {
            items[index] = item
        }
        notifyItemChanged(index)
    }

    fun deleteContact(item: Contact) {
        val index = items.indexOfFirst {
            it.id == item.id
        }
        if (index != -1) {
            items.remove(item)
        }
        notifyItemRemoved(index)
    }
}