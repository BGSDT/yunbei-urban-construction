package com.beigu.yunbeiuc.util;

import com.beigu.yunbeiuc.api.resource.Id;
import com.beigu.yunbeiuc.api.resource.IdAdapter;

public class CustomFlag {
    private final String id;
    private final String name;
    private final Id texture;
    private final int color;

    public CustomFlag(String id, String name, Id texture, int color) {
        this.id = id;
        this.name = name;
        this.texture = texture;
        this.color = color;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public Id getTexture() { return texture; }
    public int getColor() { return color; }
}
