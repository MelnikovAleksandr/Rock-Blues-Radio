package ru.asmelnikov.rockbluesradio.domain.model

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import ru.asmelnikov.rockbluesradio.R

enum class Genre(@field:StringRes val strRes: Int, @field:DrawableRes val drawableRes: Int) {
    Rock(R.string.rock_stations, R.drawable.guitar),
    Blues(R.string.blues_stations, R.drawable.saxophone)
}