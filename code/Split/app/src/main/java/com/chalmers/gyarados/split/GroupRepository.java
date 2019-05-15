package com.chalmers.gyarados.split;

import com.chalmers.gyarados.split.model.Group;

import io.reactivex.Single;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface GroupRepository {

    @GET("groups/{id}")
    Single<Group> getGroup(@Path("id") String groupID);
}
