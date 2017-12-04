/*
 * Created by Mohamed Ibrahim N
 * Created on : 18/11/17 3:14 AM
 * File name : RestUtil.java
 * Last modified by : Mohamed Ibrahim N
 * Last modified on : 18/11/17 3:14 AM
 * Project : MusicApp
 * Organization : FreeLancer trinhvanbien
 * Copyright (c) 2017. All rights reserved.
 */

package in.tr.musicapp.rest;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RestUtil {

    public static Retrofit getRetrofit() {
        return new Retrofit.Builder()
                .baseUrl(Api.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }
}
