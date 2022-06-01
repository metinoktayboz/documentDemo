package com.documentDemo.documentDemo.controller;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.Date;
import java.util.Random;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class MainController {

    private static final byte[] secret = Base64.getDecoder().decode("AZBflz7mFtFuY2dRJOdXLhFddeezyNCY");
    Algorithm algorithm = Algorithm.HMAC256(secret);
    ObjectMapper mapper = new ObjectMapper();
    ObjectWriter objectWriter = mapper.writer().withDefaultPrettyPrinter();


    @ApiOperation("Get JWT")
    @ApiResponse(code = 200, message = "Token Created", response = String.class)
    @GetMapping(value= "/gt", produces = "application/json;charset=UTF-8")
    public ResponseEntity<?> getJWT() throws JsonProcessingException {

        Instant now = Instant.now();
        String token = JWT.create()
                .withClaim("i20r", new Random().nextInt(20))
                .withIssuedAt(new Date())
                .withExpiresAt(Date.from(now.plus(8, ChronoUnit.HOURS)))
                .sign(algorithm);


        return new ResponseEntity<>(objectWriter.writeValueAsString(token), HttpStatus.OK);
    }
}
