package com.example.myapplication.domain.repository

import android.content.Context
import android.net.Uri
import com.example.myapplication.data.remote.dto.AdminGenreDto
import com.example.myapplication.data.remote.dto.AdminMovieCreateRequest
import com.example.myapplication.data.remote.dto.AdminMovieDto
import com.example.myapplication.data.remote.dto.AdminMovieUpdateRequest
import com.example.myapplication.data.remote.dto.AdminPaginatedResponse

interface AdminMovieRepository {
    suspend fun getMovies(search: String?, page: Int): Result<AdminPaginatedResponse<AdminMovieDto>>

    suspend fun createMovie(request: AdminMovieCreateRequest, posterUri: Uri?, context: Context): Result<AdminMovieDto>

    suspend fun getMovieById(id: String): Result<AdminMovieDto>

    suspend fun updateMovie(id: String, request: AdminMovieUpdateRequest, posterUri: Uri?, context: Context): Result<AdminMovieDto>
}