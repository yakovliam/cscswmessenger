package com.yakovliam.cscswmessenger.machine.service;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.yakovliam.cscswmessenger.CSCSWMessengerBootstrapper;
import com.yakovliam.cscswmessenger.machine.utils.CSCSWConstants;
import com.yakovliam.cscswmessenger.machine.utils.CSCSWUtils;
import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class CSCSWTokenBroker implements CSCSWBroker<Byte[]> {

  private static final OkHttpClient CLIENT =
      new OkHttpClient.Builder().connectTimeout(10, TimeUnit.MILLISECONDS).build();
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
    Request request =
        new Request.Builder().url(CSCSWConstants.CSCSW_TOKEN_API_URL).post(body).build();
    try (Response response = CLIENT.newCall(request).execute()) {
      CSCSWMessengerBootstrapper.LOGGER.info("Got response from CSCSW API");
      String token =
          JsonParser.parseString(Objects.requireNonNull(response.body()).string()).getAsJsonObject()
              .get("token").getAsString();
      CSCSWMessengerBootstrapper.LOGGER.info("Processed token, token: {}", token);
      return token;
    } catch (IOException | NullPointerException e) {
      CSCSWMessengerBootstrapper.LOGGER.error("Failed to get token from CSCSW API: {}",
          e.getLocalizedMessage());
      throw new RuntimeException(e);
    }
  }

  @Override
  public Byte[] provideBrokered() {
//        String token = getTokenFromCSCSWAPI();
//        return CSCSWUtils.convertByteArrayPrimitiveToObjects(CSCSWUtils.convertHexStringToByteArray(token));
    // below is a valid token with $5.00 balance (enjoy...)
    return CSCSWUtils.convertByteArrayPrimitiveToObjects(CSCSWUtils.convertHexStringToByteArray(
        "000003492941f4010000221111231208000000000000050000010001a5a5a5a564cb9255079461e1294bce18e5ed6e0f759142f0241db3d35acf1325227d940a7577e61b417eb827e7fc596ff7dadc2f00b552c5f16ba621756dfd15d58ccbf1343f6cf1abd721f5ad367616e4277870f0a56887a55606c17ee78247b22064c43b9183a28fe7d0f3149151828532103fa99ee058829d35e343d0655714c4be596e324148f72f5226"));
  }
}
