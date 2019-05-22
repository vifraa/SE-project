package com.chalmers.gyarados.split;

import com.chalmers.gyarados.split.model.Group;
import com.chalmers.gyarados.split.model.Message;

import java.util.Date;
import java.util.List;

import io.reactivex.Single;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface GroupRepository {

    @GET("groups/{id}")
    Single<Group> getGroup(@Path("id") String groupID);

    @GET("groups/{id}/messages")
    Single<List<Message>> getGroupChatMessage(@Path("id") String id);
}
