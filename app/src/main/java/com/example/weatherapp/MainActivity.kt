package com.example.weatherapp

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import coil.load
import coil.size.Dimension
import coil.size.Scale
import com.example.weatherapp.WeatherApiService.WeatherAPiResp
import com.google.android.material.textfield.TextInputEditText
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class MainActivity : AppCompatActivity() {

    private lateinit var cityInput: TextInputEditText
    private lateinit var weatherImage: ImageView
    private lateinit var cityName: TextView
    private lateinit var temperature: TextView
    private lateinit var description: TextView
    private lateinit var searchButton: Button

    private var defaultLocation: String = "Manado"

    private val basedUrl: String = "https://api.openweathermap.org/data/2.5/"

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        cityInput = findViewById(R.id.cityInput)
        weatherImage = findViewById(R.id.weather_image)
        cityName = findViewById(R.id.city_name)
        temperature = findViewById(R.id.temperature)
        description = findViewById(R.id.description)
        searchButton = findViewById(R.id.search_button)

        getWeatherData()

        searchButton.setOnClickListener {
            defaultLocation = cityInput.text.toString()
            getWeatherData()
        }
    }

    private fun getWeatherData() {
        val retrofit = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(basedUrl)
            .build()
            .create(WeatherApiService.WeatherService::class.java)

        val response = retrofit.getWeatherData(defaultLocation)

        response.enqueue(object : Callback<WeatherAPiResp> {
            override fun onResponse(
                call: Call<WeatherAPiResp>,
                response: Response<WeatherAPiResp>
            ) {
                if (response.isSuccessful) {
                    val responseBody = response.body()!!
                    cityName.text = responseBody.name
                    temperature.text = responseBody.main.temp.toString() + "\u00B0C"
                    description.text = responseBody.weather[0].description

                    val imageUrl =
                        "https://openweathermap.org/img/wn/${responseBody.weather[0].icon}@4x.png"
                    weatherImage.load(imageUrl) {
                        scale(Scale.FILL)
                    }
                    weatherImage.contentDescription = responseBody.weather[0]?.description
                } else {
                    val msg = "Something wrong happened. Please try again!"
                    Toast.makeText(this@MainActivity, msg, Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<WeatherAPiResp>, t: Throwable) {
                val msg = "Something wrong happened. Please try again!"
                Toast.makeText(this@MainActivity, msg, Toast.LENGTH_SHORT).show()
            }

        })
    }

}