package com.chalmers.gyarados.split;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface LoginRespository {
    @POST("hello-convert-and-send")
    Completable sendRestEcho(@Query("msg") String message);
}
