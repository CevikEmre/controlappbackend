package com.noronsoft.noroncontrolapp.pusher;

import com.pusher.rest.Pusher;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/pusher")
public class PusherController {
    private final Pusher pusher;

    public PusherController(Pusher pusher) {
        this.pusher = pusher;
    }

    @PostMapping("/trigger")
    public ResponseEntity<String> triggerEvent(@RequestBody Map<String, String> payload) {
        String channel = payload.get("channel");
        String event = payload.get("event");
        Map<String, Object> data = Map.of("message", payload.get("message"));

        pusher.trigger(channel, event, data);
        return ResponseEntity.ok("Event triggered successfully!");
    }
}
