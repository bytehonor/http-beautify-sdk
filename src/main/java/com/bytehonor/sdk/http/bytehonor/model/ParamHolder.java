package com.bytehonor.sdk.http.bytehonor.model;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ParamHolder {

    private Map<String, String> map;

    public ParamHolder() {
        this.map = new HashMap<String, String>();
    }
    
    public static ParamHolder make() {
        return new ParamHolder();
    }
    
    public ParamHolder put(String key, Integer value) {
        Objects.requireNonNull(key, "key");
        if (value != null) {
            this.map.put(key, value.toString());
        }
        return this;
    }
    
    public ParamHolder put(String key, Long value) {
        Objects.requireNonNull(key, "key");
        if (value != null) {
            this.map.put(key, value.toString());
        }
        return this;
    }

    public ParamHolder put(String key, String value) {
        Objects.requireNonNull(key, "key");
        if (value != null) {
            this.map.put(key, value);
        }
        return this;
    }

    public Map<String, String> getMap() {
        return map;
    }

    public void setMap(Map<String, String> map) {
        this.map = map;
    }

}
