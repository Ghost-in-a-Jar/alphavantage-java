package com.crazzyghost.alphavantage.exchangerate;

import java.io.IOException;

import com.crazzyghost.alphavantage.AlphaVantageException;
import com.crazzyghost.alphavantage.Config;
import com.crazzyghost.alphavantage.Fetcher;
import com.crazzyghost.alphavantage.UrlExtractor;
import com.crazzyghost.alphavantage.parser.Parser;

import okhttp3.Call;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * Access to Stock Time Series Data
 * @author crazzyghost
 * @since 1.0.0
 */
public class ExchangeRate implements Fetcher {

    private Config config;
    private ExchangeRateRequest.Builder builder;
    private Fetcher.SuccessCallback<ExchangeRateResponse> successCallback;
    private Fetcher.FailureCallback failureCallback;

    public ExchangeRate(Config config){
        this.config = config;
        this.builder = new ExchangeRateRequest.Builder();
    }

    public ExchangeRate toCurrency(String toCurrency){
        this.builder.toCurrency(toCurrency);
        return this;
    }

    public ExchangeRate fromCurrency(String fromCurrency){
        this.builder.fromCurrency(fromCurrency);
        return this;
    }

    /**
     * @param callback successful fetch handler
     * @return current instance of {@link ExchangeRateResponse}
     */
    public ExchangeRate onSuccess(SuccessCallback<ExchangeRateResponse> callback){
        this.successCallback = callback;
        return this;
    }

    /**
     * @param callback failed fetch handler
     * @return current instance of {@link ExchangeRateResponse}
     */
    public ExchangeRate onFailure(FailureCallback callback){
        this.failureCallback = callback;
        return this;
    }

    @Override
    public void fetch() {

        Config.checkNotNullOrKeyEmpty(config);

        config.getOkHttpClient().newCall(UrlExtractor.extract(builder.build(), config.getKey())).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                if(failureCallback != null) failureCallback.onFailure(new AlphaVantageException());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if(response.isSuccessful()){
                    try(ResponseBody body = response.body()){
                        ExchangeRateResponse exchangeResponse = ExchangeRateResponse.of(Parser.parseJSON(body.string()));
                        if(exchangeResponse.getErrorMessage() != null && failureCallback != null) failureCallback.onFailure(new AlphaVantageException(exchangeResponse.getErrorMessage()));
                        if(successCallback != null) successCallback.onSuccess(exchangeResponse);
                    }
                }else{
                    if(failureCallback != null) failureCallback.onFailure(new AlphaVantageException());
                }
            }
        });
    }
}
