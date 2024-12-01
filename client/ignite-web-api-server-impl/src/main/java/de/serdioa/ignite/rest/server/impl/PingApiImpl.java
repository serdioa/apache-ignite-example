package de.serdioa.ignite.rest.server.impl;

import java.time.OffsetDateTime;
import java.util.Optional;

import de.serdioa.ignite.rest.server.api.PingApiDelegate;
import de.serdioa.ignite.rest.server.model.Ping;
import de.serdioa.ignite.rest.server.model.Pong;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.NativeWebRequest;


@Service
public class PingApiImpl implements PingApiDelegate {

    private static final String DEFAULT_TOKEN = "pong";

    private final NativeWebRequest request;


    public PingApiImpl(NativeWebRequest request) {
        this.request = request;
    }


    @Override
    public Optional<NativeWebRequest> getRequest() {
        return Optional.ofNullable(this.request);
    }


    @Override
    public ResponseEntity<Pong> restV1PingGet() {
        Pong pong = new Pong();
        pong.setTimestamp(OffsetDateTime.now());
        pong.setToken(DEFAULT_TOKEN);

        return ResponseEntity.ok(pong);
    }


    @Override
    public ResponseEntity<Pong> restV1PingPost(String token) {
        Pong pong = new Pong();
        pong.setTimestamp(OffsetDateTime.now());
        pong.setToken(token != null ? token : DEFAULT_TOKEN);

        if ("error".equals(token)) {
            return ResponseEntity.badRequest().body(pong);
        } else {
            return ResponseEntity.ok(pong);
        }
    }


    @Override
    public ResponseEntity<Pong> restV1PingbodyPost(Ping ping) {
        String token = ping.getToken();

        Pong pong = new Pong();
        pong.setTimestamp(OffsetDateTime.now());
        pong.setToken(token != null ? token : DEFAULT_TOKEN);

        if (token.startsWith("error")) {
            return ResponseEntity.badRequest().body(pong);
        } else {
            return ResponseEntity.ok(pong);
        }
    }
}
