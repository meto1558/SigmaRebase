package com.mentalfrostbyte.jello.events.impl;

import com.mentalfrostbyte.jello.events.CancellableEvent;
import totalcross.json.JSONObject;

public class EventWriter extends CancellableEvent {
    private final JSONObject file;

    public EventWriter(JSONObject var1) {
        this.file = var1;
    }

    public JSONObject getFile() {
        return this.file;
    }
}
