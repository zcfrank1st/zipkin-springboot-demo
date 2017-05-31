package com.chaos;

import okhttp3.Call;
import okhttp3.Request;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

/**
 * Created by zcfrank1st on 27/05/2017.
 */
@RestController
@Configuration
public class HelloController {
    private final
    Call.Factory callFactory;

    @Autowired
    public HelloController(Call.Factory callFactory) {
        this.callFactory = callFactory;
    }

    private static final Logger logger = LoggerFactory.getLogger(HelloController.class);

    @GetMapping("/call")
    public ResponseEntity call() throws IOException {

        logger.info("call");

        Request request1 = new Request.Builder()
                .url("http://localhost:8090/hello1")
                .build();
        callFactory.newCall(request1).execute();

        Request request2 = new Request.Builder()
                .url("http://localhost:8090/hello2")
                .build();
        callFactory.newCall(request2).execute();

        return ResponseEntity.ok(null);
    }
}
