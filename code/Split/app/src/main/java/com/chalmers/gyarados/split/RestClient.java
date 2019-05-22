package com.chalmers.gyarados.split;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Naik on 24.02.17.
 */
public class RestClient {

    private static RestClient instance;
    private static final Object lock = new Object();

    public static RestClient getInstance() {
        RestClient instance = RestClient.instance;
        if (instance == null) {
            synchronized (lock) {
                instance = RestClient.instance;
                if (instance == null) {
                    RestClient.instance = instance = new RestClient();
                }
            }
        }
        return instance;
    }

    private final UserRespository userRepository;
    private final GroupRepository groupRepository;

    private RestClient() {
        Retrofit retrofit;
        Gson gson=new GsonBuilder()
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ss").create();
        if(Constants.develop){
            retrofit = new Retrofit.Builder().baseUrl("http://" + Constants.IP+ ":" + Constants.PORT+ "/").addConverterFactory(GsonConverterFactory.create(gson))
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .build();
        }else {

            retrofit = new Retrofit.Builder().baseUrl("http://" + Constants.deployedURL + "/").addConverterFactory(GsonConverterFactory.create(gson))
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .build();
        }

        groupRepository = retrofit.create(GroupRepository.class);
        userRepository = retrofit.create(UserRespository.class);
    }

    public GroupRepository getGroupRepository() {return groupRepository; }
    public UserRespository getUserRepository() {
        return userRepository;
    }
}