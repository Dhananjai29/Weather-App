package com.dhananjai.weatherapp

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import com.dhananjai.weatherapp.databinding.ActivityMainBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

//weatherApiKey = ae1d85c59fa1b538b5d6d5067428e35a

// https://api.openweathermap.org/data/2.5/weather?q={city name}&appid={API key}

class MainActivity : AppCompatActivity() {
    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        fetchWeatherData("Raipur")
        SearchCity()
    }

    private fun SearchCity() {
        val searchView = binding.searchView
        searchView.setOnQueryTextListener(object :SearchView.OnQueryTextListener,
            android.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null) {
                    fetchWeatherData(query)
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return true
            }

        })
    }

    private fun fetchWeatherData(cityName:String) {
        val retrofit = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl("https://api.openweathermap.org/data/2.5/")
            .build().create(ApiInterface::class.java)

        val response = retrofit.getWeatherData(cityName, "ae1d85c59fa1b538b5d6d5067428e35a", "metric")
        response.enqueue(object : Callback<WeatherApp>{
            override fun onResponse(call: Call<WeatherApp>, response: Response<WeatherApp>) {
                val responseBody = response.body()
                if(response.isSuccessful && responseBody != null){
                    val weather = responseBody.weather.firstOrNull()?.main?: "unknown"
                    val temperature = responseBody.main.temp.toString()
                    val humidity = responseBody.main.humidity.toString()
                    val minTemp = responseBody.main.temp_min.toString()
                    val maxTemp = responseBody.main.temp_max.toString()
                    val windSpeed = responseBody.wind.speed.toString()
                    val conditions = responseBody.main.feels_like.toString()
                    val sunrise = responseBody.sys.sunrise.toLong()
                    val sunset = responseBody.sys.sunset.toLong()
                    val seaLevel = responseBody.main.pressure.toString()

                    binding.temperature.text= "$temperature°C"
                    binding.humidityValue.text = "$humidity %"
                    binding.minTemp.text = "Min: $minTemp °C"
                    binding.maxTemp.text = "Max: $maxTemp °C"
                    binding.windSpeedBalue.text = "$windSpeed m/s"
                    binding.sunsetTime.text = "${time(sunset)}"
                    binding.sunriseTime.text = "${time(sunrise)}"
                    binding.conditionValue.text = conditions
                    binding.seaLevelValue.text = "$seaLevel hPa"
                    binding.day.text = dayName(System.currentTimeMillis())
                    binding.date.text= date()
                    binding.cityName.text = "$cityName"
                    binding.weather.text = "$weather"
                    changeImagesAccordingToWeatherCondition(weather)
                    changeImagesAccordingToTime()

                }
            }

            override fun onFailure(call: Call<WeatherApp>, t: Throwable) {
                TODO("Not yet implemented")
            }

        })

    }

    private fun changeImagesAccordingToTime() {
//        val sdf = SimpleDateFormat("HH")
        val currTime = SimpleDateFormat("HH:mm:ss ")
        val currtime = currTime.format((Date()))
//        val currentDateAndTime = sdf.format(Date())
        binding.time.text = "$currtime"
//        if(currentDateAndTime < 6.toString() || currentDateAndTime >= 18.toString()){
//            binding.root.setBackgroundResource(R.drawable.night_background)
//        }
//        else{
//            binding.root.setBackgroundResource(R.drawable.sunny_background)
//        }
    }


    private fun changeImagesAccordingToWeatherCondition(weather: String) {
        when (weather){
            "Clear SKy", "Sunny", "Clear" -> {
                binding.root.setBackgroundResource(R.drawable.sunny_background)
                binding.conditionImage.setImageResource(R.drawable.sunny)
            }

            "Partly Clouds","Mist","Clouds","Overcast","Foggy", "Haze" -> {
                binding.root.setBackgroundResource(R.drawable.colud_background)
                binding.conditionImage.setImageResource(R.drawable.cloud_black)
            }

            "Light Rain" , "Drizzle" , "Moderate Rain", "Showers", "Heavy Rain" -> {
                binding.root.setBackgroundResource(R.drawable.rain_background)
                binding.conditionImage.setImageResource(R.drawable.rain)
            }

            "Light Snow", "Moderate Snow", "Heavy Snow", "Blizzard" ->{
                binding.root.setBackgroundResource(R.drawable.snow_background)
                binding.conditionImage.setImageResource(R.drawable.snow)
            }
        }
    }



    private fun date(): String {
        val sdf = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
        return sdf.format((Date()))
    }

    private fun time(timestamp: Long): String {
        val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
        return sdf.format((Date(timestamp*1000)))
    }

    fun dayName(timestamp: Long):String{
        val sdf = SimpleDateFormat("EEEE", Locale.getDefault())
        return sdf.format((Date()))
    }
}