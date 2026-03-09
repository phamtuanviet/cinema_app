package com.example.myapplication.domain.model

data class MovieDetail(

    val id: String,

    val title: String,

    val description: String,

    val posterUrl: String,

    val backdropUrl: String,

    val duration: Int,

    val director: String,

    val cast: List<String>,

    val genres: List<String>,

    val trailerUrl: String,

    val releaseDate: String

)