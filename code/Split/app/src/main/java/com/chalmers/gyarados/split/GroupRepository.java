package com.chalmers.gyarados.split;

import com.chalmers.gyarados.split.model.Group;

import com.chalmers.gyarados.split.model.Message;

import java.util.Date;

import com.chalmers.gyarados.split.model.User;

import java.util.List;

import io.reactivex.Single;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface GroupRepository {

    @GET("groups/{id}")
    Single<Group> getGroup(@Path("id") String groupID);


    @GET("groups/{id}/message_after_date/{date}")
    Single<List<Message>> getMessagesAfterDate(@Path("id") String groupId, @Path("date") String date);

    // Returns an list of the previous users.
    @GET("groups/{id}/review")
    Single<List<User>> getPreviousMembers(@Path("id") String groupID);

}
