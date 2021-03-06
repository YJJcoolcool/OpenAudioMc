package com.craftmend.openaudiomc.generic.networking.rest;

import com.craftmend.openaudiomc.OpenAudioMc;
import com.craftmend.openaudiomc.generic.core.logging.OpenAudioLogger;
import com.craftmend.openaudiomc.generic.networking.rest.endpoints.RestEndpoint;
import com.craftmend.openaudiomc.generic.networking.rest.interfaces.ApiResponse;
import com.craftmend.openaudiomc.generic.state.states.IdleState;
import lombok.Setter;
import okhttp3.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class RestRequest {

    public static final OkHttpClient client = new OkHttpClient();
    private final String endpoint;
    @Setter private String body = null;
    private final Map<String, String> variables = new HashMap<>();

    public RestRequest(RestEndpoint endpoint) {
        this.endpoint = endpoint.getURL();
    }

    public RestRequest setQuery(String key, String value) {
        variables.put(key, value);
        return this;
    }

    public RestRequest setBody(Object object) {
        this.body = OpenAudioMc.getGson().toJson(object);
        return this;
    }

    public CompletableFuture<ApiResponse> execute() {
        CompletableFuture<ApiResponse> response = new CompletableFuture<>();
        OpenAudioMc.getInstance().getTaskProvider().runAsync(() -> {
            response.complete(executeSync());
        });
        return response;
    }

    public ApiResponse executeSync() {
        try {
            String url = getUrl();
            String output = readHttp(url);
            try {
                return OpenAudioMc.getGson().fromJson(output, ApiResponse.class);
            } catch (Exception e) {
                OpenAudioLogger.toConsole("Failed to handle output: " + output);
            }
        } catch (Exception e) {
            OpenAudioMc.getInstance().getStateService().setState(new IdleState("Net exception"));
            e.printStackTrace();
        }
        return null;
    }

    private String getUrl() {
        String url = this.endpoint;
        if (variables.size() != 0) {
            url+= '?';
            for (Map.Entry<String, String> entry : variables.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();
                url = url + ("&" + key + "=" + value);
            }
        }
        return url;
    }

    private String readHttp(String url) throws IOException {
        Request.Builder request = new Request.Builder()
                .url(url);

        if (this.body == null) {
            request = request.get();
        } else {
            RequestBody body = RequestBody.create(
                    MediaType.parse("application/json"), this.body);
            request = request.post(body);
        }

        Call call = client.newCall(request.build());
        Response response = call.execute();
        return response.body().string();
    }

}
