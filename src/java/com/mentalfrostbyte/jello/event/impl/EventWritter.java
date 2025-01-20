package com.mentalfrostbyte.jello.event.impl;

import totalcross.json.JSONObject;
import com.mentalfrostbyte.jello.event.CancellableEvent;


public class EventWritter extends CancellableEvent {
    private final JSONObject file;

    public EventWritter(JSONObject var1) {
        this.file = var1;
    }

    public JSONObject getFile() {
        return this.file;
    }
}
