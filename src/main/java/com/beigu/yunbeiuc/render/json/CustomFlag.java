package com.beigu.yunbeiuc.render.json;

import net.minecraft.util.Identifier;

public class CustomFlag {
    private final String id;
    private final String name;
    private final Identifier texture;
    private final int color;
    
    public CustomFlag(String id, String name, Identifier texture, int color) {
        this.id = id;
        this.name = name;
        this.texture = texture;
        this.color = color;
    }
    
    // Getters
    public String getId() { return id; }
    public String getName() { return name; }
    public Identifier getTexture() { return texture; }
    public int getColor() { return color; }
}