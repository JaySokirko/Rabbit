package com.jay.rabbit.notifications;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface ApiService {

    @Headers({
                    "Content-Type:application/json",
                    "Authorization:key=AAAAchSa-Bs:APA91bHp4QiHmzp8OE6w9bAx4eJ5H7oQ1zcpMGT594AsBaTU" +
                            "1KFeAl0r5oHSHWEOWvMKYHRAc-RqElPpmIyA1MDyzMFGOdUfMX8_1E8he" +
                            "BU6TQLbrN3pfgbqjXXMotY6XfpsrICeaiBu"
            })
    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body Sender sender);
}
