package capstone.bangkit.rumahikan.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import capstone.bangkit.rumahikan.R
import capstone.bangkit.rumahikan.database.ListBucketDetail
import capstone.bangkit.rumahikan.databinding.ItemBinding
import com.bumptech.glide.Glide
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class BucketListAdapter :
    PagingDataAdapter<ListBucketDetail, BucketListAdapter.ListViewHolder>(StoryDetailDiffCallback()) {

    private lateinit var onItemClickCallback: OnItemClickCallback

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    interface OnItemClickCallback {
        fun onItemClicked(data: ListBucketDetail)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val binding =
            ItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ListViewHolder(binding)

    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        getItem(position)?.let { holder.bind(it) }
        holder.itemView.setOnClickListener {
            getItem(position)?.let { it1 -> onItemClickCallback.onItemClicked(it1) }
        }
    }

    class ListViewHolder(private var binding: ItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: ListBucketDetail) {
            binding.inputTitle.text = data.title
            Glide.with(itemView.context)
                .load(data.photoUrl)
                .placeholder(R.drawable.ic_launcher_foreground)
                .error(R.drawable.ic_launcher_foreground)
                .fallback(R.drawable.ic_launcher_foreground)
                .into(binding.inputImage)
        }
    }

    class StoryDetailDiffCallback : DiffUtil.ItemCallback<ListBucketDetail>() {
        override fun areItemsTheSame(oldItem: ListBucketDetail, newItem: ListBucketDetail): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: ListBucketDetail,
            newItem: ListBucketDetail
        ): Boolean {
            return oldItem == newItem
        }
    }

    companion object {

        @JvmStatic
        fun formatDateToString(dateString: String): String {
            val inputDateFormat =
                SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
            val outputDateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault())
            val date: Date?
            var outputDate = ""

            try {
                date = inputDateFormat.parse(dateString)
                outputDate = outputDateFormat.format(date!!)
            } catch (e: ParseException) {
                e.printStackTrace()
            }

            return outputDate
        }
    }
}