package com.adzerk.android.sdk;

import android.support.annotation.Nullable;

import com.adzerk.android.sdk.rest.NativeAdService;
import com.adzerk.android.sdk.rest.Request;
import com.adzerk.android.sdk.rest.Response;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RestAdapter.Builder;
import retrofit.RetrofitError;
import retrofit.client.Client;
import retrofit.converter.GsonConverter;

/**
 * Adzerk SDK
 */
public class AdzerkSdk {

    static final String NATIVE_AD_ENDPOINT = "http://engine.adzerk.net/api/v2";

    static AdzerkSdk instance;

    NativeAdService service;
    Client client;

    public interface ResponseListener {
        //TODO: Fine for a starting place, but we should use generic args so that we aren't
        //TODO: leaking retrofit abstractions through the sdk.
        public void success(Response response);
        public void error(RetrofitError error);
    }

    /**
     * Returns the SDK instance for making Adzerk API calls.
     *
     * @return
     */
    public static AdzerkSdk getInstance() {
        if (instance == null) {
            instance = new AdzerkSdk();
        }

        return instance;
    }

    /**
     * Injection point for tests only. Not intended for public consumption.
     *
     * @param api
     * @return
     */
    public static AdzerkSdk getInstance(NativeAdService api) {
        if (instance == null) {
            instance = new AdzerkSdk(api, null);
        }

        return instance;
    }

    /**
     * Injection point for tests only. Not intended for public consumption.
     *
     * @param client - Inject http client
     * @return
     */
    public static AdzerkSdk getInstance(Client client) {
        if (instance == null) {
            instance = new AdzerkSdk(null, client);
        }

        return instance;
    }

    private AdzerkSdk() {
        service = getNativeAdsService();
    }

    private AdzerkSdk(NativeAdService service, Client client) {
        this.service = service;
        this.client = client;
    }

    /**
     * Send an ad request to the Native Ads API.
     *
     * @param request
     * @param listener Can be null, but caller will never get notifications.
     */
    public void request(Request request, @Nullable final ResponseListener listener) {
        getNativeAdsService().request(request, new Callback<Response>() {
            @Override
            public void success(Response response, retrofit.client.Response response2) {
                if (listener != null) {
                    listener.success(response);
                }
            }

            @Override
            public void failure(RetrofitError error) {
                if (listener != null) {
                    listener.error(error);
                }
            }
        });
    }


    // Create service for the Native Ads API
    private NativeAdService getNativeAdsService() {
        if (service == null ) {
            Gson gson = new GsonBuilder().create();
            Builder builder = new RestAdapter.Builder()
                    .setEndpoint(NATIVE_AD_ENDPOINT)
                    .setConverter(new GsonConverter(gson))
                    .setLogLevel(RestAdapter.LogLevel.FULL);

            if (client != null) {
                builder.setClient(client);
            }

            service = builder.build().create(NativeAdService.class);

        }

        return service;
    }
}
