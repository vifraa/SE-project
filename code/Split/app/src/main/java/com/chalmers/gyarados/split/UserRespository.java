package com.chalmers.gyarados.split;

import com.chalmers.gyarados.split.model.Group;
import com.chalmers.gyarados.split.model.Review;
import com.chalmers.gyarados.split.model.User;

import java.util.HashMap;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Single;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface UserRespository {
    @POST("users/handle-login")
    Single<HashMap<String, Object>> login(@Body User user);

    @POST("users/{id}/review")
    Single<User> giveReview(@Path("id") String userID ,@Body Review review);

    @GET("users/{id}")
    Single<User> getUser(@Path("id") String userID);

}
