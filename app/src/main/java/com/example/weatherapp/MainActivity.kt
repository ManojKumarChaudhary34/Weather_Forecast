package com.example.weatherapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.SearchView
import android.widget.Toast
import com.example.weatherapp.databinding.ActivityMainBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity() {
    //4cfef716bf47b6edc517649251d9eb73
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        fetchWeatherData("Maheshpur")
        searchCity()
    }

    private fun searchCity() {
        val searchView = binding.searchView
       searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
           override fun onQueryTextSubmit(p0: String?): Boolean {
               if (p0 != null) {
                   fetchWeatherData(p0)
               }
               return true
           }

           override fun onQueryTextChange(p0: String?): Boolean {
                return true
           }

       })
    }

    private fun fetchWeatherData(cityName: String) {
        val retrofit= Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl("https://api.openweathermap.org/data/2.5/")
            .build()
            .create(ApiInterface::class.java)

        val response=retrofit.getWeatherData(cityName, "4cfef716bf47b6edc517649251d9eb73","metric")
        response.enqueue(object : Callback<weatherApp>{
            override fun onResponse(call: Call<weatherApp>, response: Response<weatherApp>) {
                val responseBody= response.body()
                if (response.isSuccessful && responseBody != null){
                    val temperature= responseBody.main.temp
                    val humid= responseBody.main.humidity
                    val windSpeed= responseBody.wind.speed
                    val condition= responseBody.weather.firstOrNull()?.main?:"unknown"
                    val sunRise= responseBody.sys.sunrise.toLong()
                    val sunSet= responseBody.sys.sunset.toLong()
                    val seaLevel= responseBody.main.pressure
                    val maxTemp= responseBody.main.temp_max
                    val minTemp= responseBody.main.temp_min
                    binding.tempId.text= "$temperature℃"
                    binding.minTemp.text= "Min Temp:$minTemp℃"
                    binding.maxTemp.text= "Max Temp:$maxTemp℃"
                    binding.weatherId.text= condition
                    binding.humidityId.text= "$humid%"
                    binding.windSpeedId.text= "$windSpeed m/s"
                    binding.sunRise.text= "${time(sunRise)}"
                    binding.sunSet.text= "${time(sunSet)}"
                    binding.seaId.text= "$seaLevel hPa"
                    binding.rainId.text= condition
                    binding.cityId.text= cityName
                    binding.dayId.text= getDay(System.currentTimeMillis())
                    binding.dateId.text= getDate()
//                    Log.d("result", "onResponse: $tempr")
                    changeBackgroundImage(condition)
                }
            }

            override fun onFailure(call: Call<weatherApp>, t: Throwable) {
                //if api call fails
            }

        })
    }

    private fun changeBackgroundImage(conditions: String) {
        when (conditions){
            "Clear Sky", "Sunny", "Clear" ->{
                binding.root.setBackgroundResource(R.drawable.sunny_background)
                binding.lottieAnimationView.setAnimation(R.raw.sun)
            }
            "Partly Clouds", "Clouds", "Overcast", "Mist", "Foggy" ->{
                binding.root.setBackgroundResource(R.drawable.cloud_background)
                binding.lottieAnimationView.setAnimation(R.raw.cloud)
            }
            "Light Rain", "Drizzle", "Moderate Rain", "Showers", "Heavy Rain", "Rain"->{
                binding.root.setBackgroundResource(R.drawable.rain_background)
                binding.lottieAnimationView.setAnimation(R.raw.rain)
            }
            "Light Snow", "Moderate Snow", "Heavy Snow", "Blizzard" ->{
                binding.root.setBackgroundResource(R.drawable.snow_background)
                binding.lottieAnimationView.setAnimation(R.raw.snow)
            }
            else -> {
                binding.root.setBackgroundResource(R.drawable.sunny_background)
                binding.lottieAnimationView.setAnimation(R.raw.sun)
            }
        }
        binding.lottieAnimationView.playAnimation()
    }

    private fun getDay(timestamp:Long): String{
        val sdf= SimpleDateFormat("EEEE", Locale.getDefault())
        return sdf.format(Date())
    }
    private fun time(timestamp:Long): String{
        val sdf= SimpleDateFormat("HH:mm", Locale.getDefault())
        return sdf.format(Date(timestamp*1000))
    }
    private fun getDate(): String{
        val sdf= SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
        return sdf.format((Date()))
    }
}