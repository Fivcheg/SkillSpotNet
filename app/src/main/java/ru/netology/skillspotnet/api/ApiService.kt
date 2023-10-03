package ru.netology.skillspotnet.api

import okhttp3.Interceptor
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import ru.netology.skillspotnet.BuildConfig
import ru.netology.skillspotnet.dto.Event
import ru.netology.skillspotnet.dto.Job
import ru.netology.skillspotnet.dto.Media
import ru.netology.skillspotnet.dto.Post
import ru.netology.skillspotnet.dto.PushToken
import ru.netology.skillspotnet.dto.Token
import ru.netology.skillspotnet.dto.User

private const val BASE_URL = "${BuildConfig.BASE_URL}/api/"

fun okhttp(vararg interceptors: Interceptor): OkHttpClient = OkHttpClient.Builder()
    .apply {
        interceptors.forEach {
            this.addInterceptor(it)
        }
    }
    .build()

fun retrofit(client: OkHttpClient): Retrofit = Retrofit.Builder()
    .addConverterFactory(GsonConverterFactory.create())
    .baseUrl(BASE_URL)
    .client(client)
    .build()


interface ApiService {
    @POST("users/push-tokens")
    suspend fun save(@Body pushToken: PushToken): Response<Unit>

    @GET("posts")
    suspend fun getAll(): Response<List<Post>>

    @GET("posts/{id}/newer")
    suspend fun getNewer(@Path("id") id: Long): Response<List<Post>>

    @GET("posts/{id}/before")
    suspend fun getBefore(
        @Path("id") id: Long,
        @Query("count") count: Int
    ): Response<List<Post>>

    @GET("posts/{id}/after")
    suspend fun getAfter(
        @Path("id") id: Long,
        @Query("count") count: Int
    ): Response<List<Post>>

    @GET("posts/latest")
    suspend fun getLatest(@Query("count") count: Int): Response<List<Post>>

    @GET("posts/{id}")
    suspend fun getById(@Path("id") id: Long): Response<Post>

    @POST("posts")
    suspend fun save(@Body post: Post): Response<Post>

    @DELETE("posts/{id}")
    suspend fun removeById(@Path("id") id: Long): Response<Unit>

    @POST("posts/{id}/likes")
    suspend fun likeById(@Path("id") id: Long): Response<Post>

    @DELETE("posts/{id}/likes")
    suspend fun dislikeById(@Path("id") id: Long): Response<Post>

    @Multipart
    @POST("media")
    suspend fun upload(@Part media: MultipartBody.Part): Response<Media>

    //-------------- Users -------------------//

    @GET("users")
    suspend fun getUsers(): Response<List<User>>

    @GET("users/{id}")
    suspend fun getUserById(@Path("id") id: Long): Response<User>

    @POST("users/push-tokens")
    suspend fun sendPushToken(@Body pushToken: PushToken): Response<Unit>

    @FormUrlEncoded
    @POST("users/authentication")
    suspend fun updateUser(
        @Field("login") login: String,
        @Field("password") password: String,
    ): Response<Token>

    @Multipart
    @POST("users/registration")
    suspend fun registrationUser(
        @Part("login") login: String,
        @Part("password") pass: String,
        @Part("name") name: String,
        // @Part image: MultipartBody.Part?,
    ): Response<Token>

    //-------------- Job -------------------//

    @GET("{id}/jobs")
    suspend fun getJobByUserId(@Path("id") id: Long): Response<List<Job>>

    @POST("my/jobs")
    suspend fun saveJob(@Body job: Job): Response<Job>

    @DELETE("my/jobs/{id}")
    suspend fun removeJobById(@Path("id") id: Long): Response<Unit>


    //-------------- Event -------------------//


    @GET("events")
    suspend fun getAllEvents(): Response<List<Event>>

    @GET("events/{id}/before")
    suspend fun getEventBefore(
        @Path("id") id: Long,
        @Query("count") count: Int,
    ): Response<List<Event>>

    @GET("events/{id}/after")
    suspend fun getEventAfter(
        @Path("id") id: Long,
        @Query("count") count: Int,
    ): Response<List<Event>>

    @GET("events/{id}/newer")
    suspend fun getNewerEvents(@Path("id") id: Long): Response<List<Event>>

    @GET("events/latest")
    suspend fun getEventLatest(@Query("count") count: Int): Response<List<Event>>

    @POST("events")
    suspend fun saveEvent(@Body event: Event): Response<Event>

    @DELETE("events/{id}")
    suspend fun removeEventById(@Path("id") id: Long): Response<Unit>

    @POST("events/{id}/likes")
    suspend fun likeEventById(@Path("id") id: Long): Response<Event>

    @DELETE("events/{id}/likes")
    suspend fun dislikeEventById(@Path("id") id: Long): Response<Event>

    @POST("events/{id}/participants")
    suspend fun participate(@Path("id") id: Long): Response<Event>

    @DELETE("events/{id}/participants")
    suspend fun doNotParticipate(@Path("id") id: Long): Response<Event>

    @Multipart
    @POST("media")
    suspend fun uploadMedia(@Part media: MultipartBody.Part): Response<Media>

    //-------------- Wall -------------------//

    @GET("{authorId}/wall")
    suspend fun getWall(
        @Path("authorId") authorId: Long
    ): Response<List<Post>>

    @GET("{authorId}/wall/latest")
    suspend fun getWallLatest(
        @Path("authorId") authorId: Long,
        @Query("count") count: Int,
    ): Response<List<Post>>

    @GET("{authorId}/wall/{id}/before")
    suspend fun getWallBefore(
        @Path("authorId") authorId: Long,
        @Path("id") id: Long,
        @Query("count") count: Int,
    ): Response<List<Post>>

    @GET("{authorId}/wall/{id}/after")
    suspend fun getWallAfter(
        @Path("authorId") authorId: Long,
        @Path("id") id: Long,
        @Query("count") count: Int,
    ): Response<List<Post>>



}

