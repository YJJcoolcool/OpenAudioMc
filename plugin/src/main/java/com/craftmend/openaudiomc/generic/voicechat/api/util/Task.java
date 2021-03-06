package com.craftmend.openaudiomc.generic.voicechat.api.util;

import com.craftmend.openaudiomc.generic.networking.rest.data.RestErrorType;
import lombok.Getter;

import java.util.function.Consumer;

@Getter
public class Task<T> {

    private Consumer<RestErrorType> whenFails;
    private Consumer<T> whenSuccessful;

    public void success(T data) {
        if (whenSuccessful != null) whenSuccessful.accept(data);
    }

    public void fail(RestErrorType data) {
        if (whenFails != null) whenFails.accept(data);
    }

}
