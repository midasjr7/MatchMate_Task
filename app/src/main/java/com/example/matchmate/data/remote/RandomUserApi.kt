package com.example.matchmate.data.remote

import retrofit2.http.GET

interface RandomUserApi {
    @GET("api/?results=10&inc=login,name,dob,location,email,phone,picture")
    suspend fun getMatches(): RandomUserResponse
}

data class RandomUserResponse(val results: List<UserDto>)
data class UserDto(
    val login: LoginDto,
    val name: NameDto,
    val dob: DobDto,
    val location: LocationDto,
    val email: String,
    val phone: String,
    val picture: PictureDto,
)
data class LoginDto(val uuid: String)
data class NameDto(val first: String, val last: String)
data class DobDto(val age: Int)
data class LocationDto(val city: String, val state: String, val country: String)
data class PictureDto(val large: String)
