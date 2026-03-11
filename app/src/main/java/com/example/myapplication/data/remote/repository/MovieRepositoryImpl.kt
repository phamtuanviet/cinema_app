package com.example.myapplication.data.remote.repository

import com.example.myapplication.data.remote.api.MovieApi
import com.example.myapplication.data.remote.dto.CinemaShowtimeDto
import com.example.myapplication.data.remote.dto.MovieDetailDto
import com.example.myapplication.data.remote.dto.MovieDto
import com.example.myapplication.data.remote.dto.ShowtimeDto
import com.example.myapplication.domain.model.Movie
import com.example.myapplication.domain.model.MovieDetail
import com.example.myapplication.domain.repository.MovieRepository
import javax.inject.Inject

class MovieRepositoryImpl @Inject constructor(
    private val movieApi: MovieApi
) : MovieRepository {

    override suspend fun getBanners(): List<String> {
        return listOf(
            "https://image.tmdb.org/t/p/w780/9cqNxx0GxF0bflZmeSMuL5tnGzr.jpg",
            "https://image.tmdb.org/t/p/w780/4HodYYKEIsGOdinkGi2Ucz6X9i0.jpg",
            "https://image.tmdb.org/t/p/w780/kXfqcdQKsToO0OUXHcrrNCHDBzO.jpg"
        )
    }

    override suspend fun getNowShowingMovies(): List<Movie> {
        return listOf(
            Movie(
                id = "1",
                title = "Avengers: Endgame",
                ageRating = "T13",
                posterUrl = "https://image.tmdb.org/t/p/w500/or06FN3Dka5tukK1e9sl16pB3iy.jpg",
                genres = listOf("Action", "Sci-Fi"),
                duration = "181",
                language = "English"
            ),
            Movie(
                id = "2",
                title = "Spider-Man: No Way Home",
                ageRating = "T13",
                posterUrl = "https://image.tmdb.org/t/p/w500/1g0dhYtq4irTY1GPXvft6k4YLjm.jpg",
                genres = listOf("Action", "Adventure"),
                duration = "148",
                language = "English"
            ),
            Movie(
                id = "3",
                title = "The Batman",
                ageRating = "T16",
                posterUrl = "https://image.tmdb.org/t/p/w500/74xTEgt7R36Fpooo50r9T25onhq.jpg",
                genres = listOf("Action", "Crime"),
                duration = "176",
                language = "English"
            )
        )
    }

    override suspend fun getComingSoonMovies(): List<Movie> {
        return listOf(
            Movie(
                id = "4",
                title = "Avatar 3",
                ageRating = "T13",
                posterUrl = "https://image.tmdb.org/t/p/w500/t6HIqrRAclMCA60NsSmeqe9RmNV.jpg",
                genres = listOf("Sci-Fi", "Adventure"),
                duration = "190",
                language = "English"
            ),
            Movie(
                id = "5",
                title = "Deadpool 3",
                ageRating = "T18",
                posterUrl = "https://image.tmdb.org/t/p/w500/to0spRl1CMDvyUbOnbb4fTk3VAd.jpg",
                genres = listOf("Action", "Comedy"),
                duration = "140",
                language = "English"
            ),
            Movie(
                id = "6",
                title = "Joker 2",
                ageRating = "T18",
                posterUrl = "https://image.tmdb.org/t/p/w500/udDclJoHjfjb8Ekgsd4FDteOkCU.jpg",
                genres = listOf("Drama", "Crime"),
                duration = "138",
                language = "English"
            )
        )
    }

    override suspend fun getMovieDetail(movieId: String): MovieDetailDto {
        return MovieDetailDto(
            id = movieId,
            title = "Avengers: Endgame",
            description = "After the devastating events of Infinity War, the Avengers assemble once more to undo Thanos' actions.",
            posterUrl = "https://image.tmdb.org/t/p/w500/or06FN3Dka5tukK1e9sl16pB3iy.jpg",
            durationMinutes = 181,
            trailerUrl = "https://www.youtube.com/watch?v=eOrNdBpGMv8",
            ageRating = "PG-13",
            language = "English",
            genres = listOf("Action", "Sci-Fi"),
            releaseDate = "2019-04-26"
        )
    }

    override suspend fun getMovie(
        movieId: String
    ): MovieDto {

        return MovieDto(
            id = movieId,
            title = "Thỏ Ơi!",
            durationMinutes = 127,
            posterUrl = "https://picsum.photos/800/400",
            ageRating = "T13",
            language = "Vietnamese",
            genres = listOf("Tình cảm", "Tâm lý")
        )
    }

    override suspend fun getAvailableDates(
        movieId: String
    ): List<String> {

        return listOf(
            "2026-03-09",
            "2026-03-10",
            "2026-03-11",
            "2026-03-12",
            "2026-03-13"
        )
    }



    override suspend fun getCinemas(
        movieId: String,
        date: String,
        lat: Double?,
        lng: Double?
    ): List<CinemaShowtimeDto> {

        return listOf(

            CinemaShowtimeDto(
                cinemaId = "1",
                cinemaName = "Beta Xuân Thủy",
                distanceKm = 8.8,
                showtimes = listOf(
                    ShowtimeDto("1", "14:30"),
                    ShowtimeDto("2", "17:45"),
                    ShowtimeDto("3", "20:10")
                )
            ),

            CinemaShowtimeDto(
                cinemaId = "2",
                cinemaName = "CGV Vincom Phạm Ngọc Thạch",
                distanceKm = 10.5,
                showtimes = listOf(
                    ShowtimeDto("4", "15:00"),
                    ShowtimeDto("5", "18:30")
                )
            ),

            CinemaShowtimeDto(
                cinemaId = "3",
                cinemaName = "Lotte Cinema Liễu Giai",
                distanceKm = 12.2,
                showtimes = listOf(
                    ShowtimeDto("6", "16:00"),
                    ShowtimeDto("7", "19:20"),
                    ShowtimeDto("8", "21:45")
                )
            )
        )
    }
}