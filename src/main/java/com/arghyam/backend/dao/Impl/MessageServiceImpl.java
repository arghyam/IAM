package com.arghyam.backend.dao.Impl;

import com.arghyam.backend.dao.MessageService;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;


@Component
@Service
public class MessageServiceImpl implements MessageService {

    @Override
    public void sendMessage(String message, String phoneNumber) {
        String phone = phoneNumber;
        if (phoneNumber.contains("+91")) {
            phone = phoneNumber.replace("+91", "");
        }
        message = message.trim();
        message = message.replaceAll("\\s", "%20");

        try {
            HttpResponse<String> response = Unirest.get("http://api.msg91.com/api/sendhttp.php?" +
                    "route=4&sender=Frwatr&mobiles=" + phoneNumber +
                    "&authkey=270241AX2UK15p65ca1ad62"+
                    "&message=" + URLEncoder.encode(message) +
                    "&country=91").asString();
            System.out.println("Response===" + response);
        } catch (Exception e) {
//            LOGGER.info("error while sending message", e.getMessage());
            System.out.println("Message cannot be sent " + e.getMessage());
        }
    }
}
