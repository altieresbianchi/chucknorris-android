package br.com.chucknorris.feature.joke.view.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import br.com.chucknorris.R
import br.com.chucknorris.service.model.Joke
import com.squareup.picasso.Picasso

class JokeAdapter(
    private var items: List<Joke>
) : RecyclerView.Adapter<ItemViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return ItemViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.recycler_item_joke, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int {
        return items.size
    }

    fun updateList(newItems: List<Joke>) {
        this.items = newItems
        notifyDataSetChanged()
    }
}

class ItemViewHolder(
    private val view: View
) : RecyclerView.ViewHolder(view) {
    private val imageViewIcon: ImageView = view.findViewById(R.id.imageViewIcon)
    private val textViewJoke: TextView = view.findViewById(R.id.textViewJoke)

    fun bind(item: Joke) {
        Picasso.get()
            .load(item.iconUrl)
            .error(R.drawable.ic_error)
            .into(imageViewIcon)

        textViewJoke.text = item.value
    }
}
