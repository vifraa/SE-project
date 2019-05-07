package com.chalmers.gyarados.split;

import com.chalmers.gyarados.split.model.User;

import java.util.HashMap;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Single;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface LoginRespository {
    @POST("users/handle-login")
    Single<HashMap<String, Object>> sendRestEcho(@Body User user);
}
