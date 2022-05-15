package com.example.backend.firebase;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.FileInputStream;

@Service
public class FCMInitializer {

    Logger logger = LoggerFactory.getLogger(FCMInitializer.class);
    
    @PostConstruct
    public void initialize() throws Exception {
        try {
            FileInputStream serviceAccount = new FileInputStream("./neocafe-38ef1-firebase-adminsdk-zs7ot-78c2b63b02.json");
            FirebaseOptions options = new FirebaseOptions.Builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .setDatabaseUrl("https://firebase-url.firebaseio.com").build();
            FirebaseApp.initializeApp(options);
        } catch (Exception e) {
            logger.error("error connecting firebase : {} ", e.getMessage());
        }
    }



}