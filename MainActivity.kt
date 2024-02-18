package com.example.weatherapp

import android.nfc.Tag
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.SearchView
import com.example.weatherapp.databinding.ActivityMainBinding
import retrofit2.*
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Query
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.*

//71e4ea9a6f8f1518d2b8b54ba0564dec
class MainActivity : AppCompatActivity() {
    private  val binding:ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        fetchWeatherData("jaipur")
        SearchCity()
    }

    private fun SearchCity() {
        val searchView=binding.searchView
        searchView.setOnQueryTextListener(object:SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(p0: String?): Boolean {
           if(p0!=null){
               fetchWeatherData(p0)
           }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
               return true
            }

        })
    }

    private fun fetchWeatherData(cityName:String) {
        val retrofit=Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl("https://api.openweathermap.org/data/2.5/")
            .build().create(ApiInterface::class.java)
        val response = retrofit.getWeatherData(cityName, "71e4ea9a6f8f1518d2b8b54ba0564dec", "metric")


        response.enqueue(object :Callback<weatherApp>{
            override fun onResponse(call: Call<weatherApp>, response: Response<weatherApp>) {
              val responseBody=response.body()
                if (response.isSuccessful&& responseBody!=null){
                    val temprature=responseBody.main.temp.toString()
                    val humidity=responseBody.main.humidity
                 //   val weather=responseBody.weather
                    val windSpeed=responseBody.wind.speed
                    val sunRise=responseBody.sys.sunrise;
                    val sunSet=responseBody.sys.sunset
                    val seaLevel=responseBody.main.pressure
                    val condition=responseBody.weather.firstOrNull()?.main?:"unknown"
                    val maxTemp=responseBody.main.temp_max;
                    val minTemp=responseBody.main.temp_min


                    binding.textView5.text="$temprature °C"
                    binding.textView7.text="Max Temp:$maxTemp °C"
                    binding.textView6.text=condition
                    binding.textView8.text="Min Temp:$minTemp °C"
                    binding.humidity.text="$humidity %"
                    binding.wind.text="$windSpeed m/s"
                    binding.sunset.text="$sunSet"
                    binding.sea.text="$seaLevel hPa"
                    binding.textView9.text=dayName(System.currentTimeMillis())
                    binding.textView10.text=date()
                   //binding.textView5.text="$cityName"
                    binding.textView4.text="$cityName"


                changeImageAccToWeatherCond(condition)

                   // Log.d("TAG", "onResponse:  $ temperature")

                }

            }

            override fun onFailure(call: Call<weatherApp>, t: Throwable) {
                TODO("Not yet implemented")
            }

        })

    }

    private fun changeImageAccToWeatherCond(conditions:String) {
        when(conditions){
            "Clear Sky","Sunny","Clear"->{
                binding.root.setBackgroundResource(R.drawable.sunny_background)
                binding.lottieAnimationView.setAnimation(R.raw.sun)

            }
            "Partially Cloud","Cloud","Overcast","Mist","Fogg"->{
                binding.root.setBackgroundResource(R.drawable.colud_background)
                binding.lottieAnimationView.setAnimation(R.raw.cloud)

            }
            "Light Rain","Rain","Drizzle","Moderate Rain","Heavy Rain","Showers"->{
                binding.root.setBackgroundResource(R.drawable.rain_background)
                binding.lottieAnimationView.setAnimation(R.raw.rain)

            }
            "Light Snow","Moderate Snow","Heavy Snow","Blizzard"->{
                binding.root.setBackgroundResource(R.drawable.snow_background)
                binding.lottieAnimationView.setAnimation(R.raw.snow)

            }else->{
            binding.root.setBackgroundResource(R.drawable.sunny_background)
            binding.lottieAnimationView.setAnimation(R.raw.sun)
            }


        }
        binding.lottieAnimationView.playAnimation()
    }

    private fun date():String{
        val sdf=SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
        return sdf.format((Date()))
    }
    fun dayName(timestamp: Long):String{
        val sdf=SimpleDateFormat("EEEE", Locale.getDefault())
        return sdf.format((Date()))
    }
}