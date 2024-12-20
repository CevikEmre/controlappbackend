package com.noronsoft.noroncontrolapp.pusher;

import com.pusher.rest.Pusher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PusherHttpConfig {
    @Bean
    public Pusher pusherHttp() {
        Pusher pusher = new Pusher("1914961", "06ddc5e2718127744886", "8a6245a9cde652c764f8");
        pusher.setCluster("eu");
        pusher.setEncrypted(false);
        return pusher;
    }
}
