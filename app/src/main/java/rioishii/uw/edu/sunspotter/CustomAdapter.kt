package rioishii.uw.edu.sunspotter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import kotlinx.android.synthetic.main.list_item.view.*


class CustomAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var items: ArrayList<ForecastData> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolder (
            LayoutInflater.from(parent.context).inflate(R.layout.list_item, parent, false)
        )
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(holder) {
            is ViewHolder -> {
                holder.bind(items[position])
            }
        }
    }

    override fun getItemCount(): Int = items.size

    fun submitList(forecastData: ArrayList<ForecastData>) {
        this.items = forecastData
    }


    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val icon = itemView.item_icon
        private val weather = itemView.item_weather
        private val time = itemView.item_time
        private val temp = itemView.item_temp

        fun bind(forecastData: ForecastData) {
            weather.text = "${forecastData.weather}"
            time.text = "@ ${forecastData.time}"
            temp.text = "(${forecastData.temp}°)"

            val requestOptions = RequestOptions()
                .placeholder(R.drawable.ic_launcher_background)
                .error(R.drawable.ic_launcher_background)

            Glide.with(itemView.context)
                .applyDefaultRequestOptions(requestOptions)
                .load(forecastData.icon)
                .into(icon)
        }
    }

}
