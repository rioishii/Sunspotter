package rioishii.uw.edu.sunspotter

import android.content.Context
import android.content.res.Configuration
import android.graphics.Color
import android.net.Uri
import android.nfc.Tag
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.*
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONException
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class MainActivity : AppCompatActivity() {

    private var weatherArray = ArrayList<ForecastData>()
    private lateinit var forecastAdapter: CustomAdapter
    private var dateFormat: SimpleDateFormat = SimpleDateFormat("EEE, h:mm a", Locale.getDefault())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (savedInstanceState != null) {
            val input = savedInstanceState.getString("input")!!
            getWeatherData(input)
        }

        initRecyclerView()

        button_id.setOnClickListener {
            hideKeyBoard(it)
            val searchQuery = text_input.text.toString()
            getWeatherData(searchQuery)
        }
    }

    private fun initRecyclerView() {
        val orientation = resources.configuration.orientation
        recycler_view.apply {
            when (orientation) {
                Configuration.ORIENTATION_PORTRAIT -> layoutManager = LinearLayoutManager(this@MainActivity)
                Configuration.ORIENTATION_LANDSCAPE -> layoutManager = GridLayoutManager(this@MainActivity, 4)
            }
            val topSpacingItemDecoration = TopSpacingItemDecoration(5)
            addItemDecoration(topSpacingItemDecoration)
            forecastAdapter = CustomAdapter()
            adapter = forecastAdapter
        }
    }

    private fun hideKeyBoard(view: View) {
        view.apply {
            val manager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            manager.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        val search = text_input.text.toString()
        outState.putString("input", search)
        super.onSaveInstanceState(outState)
    }

    private fun getWeatherData(searchQuery: String) {
        val key = getString(R.string.OPEN_WEATHER_MAP_API_KEY)
        val urlBuilder = Uri.Builder()
        urlBuilder.scheme("https")
            .authority("api.openweathermap.org")
            .appendPath("data")
            .appendPath("2.5")
            .appendPath("forecast")
            .appendQueryParameter("q", searchQuery)
            .appendQueryParameter("format", "json")
            .appendQueryParameter("units", "imperial")
            .appendQueryParameter("appid", key)

        val jsonObjectRequest = JsonObjectRequest(Request.Method.GET, urlBuilder.toString(), null,
            Response.Listener<JSONObject> { response ->
                try {
                    weatherArray.clear()
                    forecastAdapter.notifyDataSetChanged()

                    var isSunny = false
                    var sunnyTime = ""

                    val results = response.getJSONArray("list")
                    for (i in 0 until results.length()) {
                        val icon = results.getJSONObject(i).getJSONArray("weather").getJSONObject(0).getString("icon")
                        val weather = results.getJSONObject(i).getJSONArray("weather").getJSONObject(0).getString("main")
                        val dt = results.getJSONObject(i).getInt("dt")
                        val temp = results.getJSONObject(i).getJSONObject("main").getString("temp")

                        val date = Date(dt * 1000L)
                        val dateString = dateFormat.format(date)

                        if (weather == "Clear" && !isSunny) {
                            isSunny = true
                            sunnyTime = dateString
                        }

                        val drawableId = resources.getIdentifier("icon$icon", "drawable", packageName)
                        val drawableIcon = getDrawable(drawableId)!!

                        weatherArray.add(ForecastData(drawableIcon, weather, dateString, temp))
                    }

                    forecastAdapter.submitList(weatherArray)

                    if (isSunny) {
                        result_image.setImageResource(R.drawable.ic_check_circle_black_24dp)
                        result_image.setColorFilter(Color.GREEN)
                        result_header.text = getString(R.string.sunny_text)
                        result_label.text = "At $sunnyTime"
                    } else {
                        result_image.setImageResource(R.drawable.ic_highlight_off_black_24dp)
                        result_image.setColorFilter(Color.GRAY)
                        result_header.text = getString(R.string.not_sunny_text)
                        result_label.text = ""
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            },
            Response.ErrorListener { error ->
                val errorMsg = "ERROR: %s".format(error.toString())
                Toast.makeText(this, errorMsg, Toast.LENGTH_LONG).show()
            }
        )

        RequestSingleton.getInstance(this).addToRequestQueue(jsonObjectRequest)
    }

}




