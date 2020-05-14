package com.keepcodeclean.speech.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.client.RestTemplate;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Optional;

@Service
public class SpeechRecognitionService {

    @Autowired
    private RestTemplate restTemplate;

    public Optional<String> recognize(byte[] blob) throws IOException {
        try (FileOutputStream fos = new FileOutputStream("/home/m1namoto/sound.wav")) {
            fos.write(blob);
            //fos.close(); There is no more need for this line since you had created the instance of "fos" inside the try. And this will automatically close the OutputStream
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("audio/x-raw"));

        HttpEntity<byte[]> requestEntity = new HttpEntity<>(blob, headers);

        ResponseEntity<String> response = restTemplate.exchange(
                "http://localhost:8888/client/dynamic/recognize", HttpMethod.PUT, requestEntity, String.class
        );

        return Optional.of(response.getBody());
    }
}
