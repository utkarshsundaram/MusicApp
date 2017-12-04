/*
 * Created by Mohamed Ibrahim N
 * Created on : 18/11/17 1:12 AM
 * File name : Api.java
 * Last modified by : Mohamed Ibrahim N
 * Last modified on : 18/11/17 1:12 AM
 * Project : MusicApp
 * Organization : FreeLancer trinhvanbien
 * Copyright (c) 2017. All rights reserved.
 */

package in.tr.musicapp.rest;

import java.util.List;

import in.tr.musicapp.model.Music;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface Api {
    String BASE_URL = "http://158.69.194.21/Dev/";

    @GET("dev.jsp")
    Call<List<Music>> getMusics(@Query("keyword") String keyword);
}
