package com.yakovliam.cscswmessenger.machine.service;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.yakovliam.cscswmessenger.CSCSWMessengerBootstrapper;
import com.yakovliam.cscswmessenger.machine.utils.CSCSWConstants;
import com.yakovliam.cscswmessenger.machine.utils.CSCSWUtils;
import okhttp3.*;

import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class CSCSWTokenBroker implements CSCSWBroker<Byte[]> {

    private static final OkHttpClient CLIENT = new OkHttpClient.Builder().connectTimeout(10, TimeUnit.MILLISECONDS).build();
    private static final Gson GSON = new Gson();
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    private String getTokenFromCSCSWAPI() {
        CSCSWMessengerBootstrapper.LOGGER.info("Getting token from CSCSW API");
        // construct a request body (json) from the constants
        JsonObject requestBody = new JsonObject();
        requestBody.addProperty("user_id", CSCSWConstants.CSCSW_TOKEN_API_REQUEST_USER_ID);
        requestBody.addProperty("uuid", CSCSWConstants.CSCSW_TOKEN_API_REQUEST_UUID);
        requestBody.addProperty("token", CSCSWConstants.CSCSW_TOKEN_API_REQUEST_TOKEN);

        // construct a request with the okhttp client
        RequestBody body = RequestBody.create(requestBody.toString(), JSON);
        Request request = new Request.Builder().url(CSCSWConstants.CSCSW_TOKEN_API_URL).post(body).build();
        try (Response response = CLIENT.newCall(request).execute()) {
            String token = JsonParser.parseString(Objects.requireNonNull(response.body()).string()).getAsJsonObject().get("token").getAsString();
            CSCSWMessengerBootstrapper.LOGGER.info("Got response from CSCSW API, Token: {}", token);
            return token;
        } catch (IOException | NullPointerException e) {
            CSCSWMessengerBootstrapper.LOGGER.error("Failed to get token from CSCSW API: {}", e.getLocalizedMessage());
            throw new RuntimeException(e);
        }
    }

    @Override
    public Byte[] provideBrokered() {
        String token = getTokenFromCSCSWAPI();
        return CSCSWUtils.convertByteArrayPrimitiveToObjects(CSCSWUtils.convertHexStringToByteArray(token));
    }
}
