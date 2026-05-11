package com.example.myapplication.domain.repository

import com.example.myapplication.data.remote.dto.AdminGenreDto

interface AdminGenreRepository {
    suspend fun getAllGenres(): Result<List<AdminGenreDto>>
}