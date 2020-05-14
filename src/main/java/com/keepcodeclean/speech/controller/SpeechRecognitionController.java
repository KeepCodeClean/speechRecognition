package com.keepcodeclean.speech.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.keepcodeclean.speech.ajax.AjaxRecognizedSpeechResponse;
import com.keepcodeclean.speech.service.SpeechRecognitionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Controller
public class SpeechRecognitionController {

    @Autowired
    private SpeechRecognitionService speechRecognitionService;

    @RequestMapping("/")
    public String speechRecognitionPage() {
        return "speech";
    }

    @ResponseBody
    @RequestMapping(value="/recognize", method=POST)
    public AjaxRecognizedSpeechResponse recognize(@RequestPart("blob") MultipartFile blob) {
        String recognizedText = null;
        try {
            Optional<String> recognizedOutputJsonOpt = speechRecognitionService.recognize(blob.getBytes());
            if (recognizedOutputJsonOpt.isPresent()) {
                ObjectMapper mapper = new ObjectMapper();
                JsonNode rootNode = mapper.readTree(recognizedOutputJsonOpt.get());

                JsonNode hypothesesNode = rootNode.get("hypotheses");
                if (hypothesesNode.isArray()) {
                    ArrayNode hypotheses = (ArrayNode) hypothesesNode;
                    if (hypotheses.size() > 0) {
                        recognizedText = hypotheses.get(0).get("utterance").textValue();
                    }
                }
            } else {
                return new AjaxRecognizedSpeechResponse(false, "Could not recognized speech");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        if (recognizedText == null) {
            return new AjaxRecognizedSpeechResponse(false, "Could not recognize speech");
        }

        return new AjaxRecognizedSpeechResponse(true, recognizedText);
    }

}
