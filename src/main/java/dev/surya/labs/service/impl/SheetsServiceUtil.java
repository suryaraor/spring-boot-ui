package dev.surya.labs.service.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver.Builder;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
@Component
public class SheetsServiceUtil {

    private static final String APPLICATION_NAME = "election-results";
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private static final String TOKENS_DIRECTORY_PATH = "tokens";

    public static Sheets getSheetsService(String credentialsFile, String oauthPort) throws IOException, GeneralSecurityException {
        // Load client secrets.
        InputStream in = SheetsServiceUtil.class.getResourceAsStream("/"+credentialsFile);
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        // Build the authorization scope.
        List<String> scopes = Collections.singletonList(SheetsScopes.SPREADSHEETS);

        // Initialize the Sheets service.
        return new Sheets.Builder(GoogleNetHttpTransport.newTrustedTransport(), JSON_FACTORY, getCredentials(clientSecrets, scopes, oauthPort))
                .setApplicationName(APPLICATION_NAME)
                .build();
    }

    private static Credential getCredentials(GoogleClientSecrets clientSecrets, List<String> scopes, String oauthPort) throws IOException, GeneralSecurityException {
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                GoogleNetHttpTransport.newTrustedTransport(), JSON_FACTORY, clientSecrets, scopes)
                .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
                .setAccessType("offline")
                .build();
        Builder builder = new LocalServerReceiver.Builder().setPort(Integer.parseInt(oauthPort));
        return new AuthorizationCodeInstalledApp(flow, builder.build()).authorize("user");
    }
}
