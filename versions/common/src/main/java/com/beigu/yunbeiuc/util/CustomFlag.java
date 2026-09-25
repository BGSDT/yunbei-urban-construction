package com.beigu.yunbeiuc.util;

import net.minecraft.resources.ResourceLocation;

public class CustomFlag {
    private final String id;
    private final String name;
    private final ResourceLocation texture;
    private final int color;
    
    public CustomFlag(String id, String name, ResourceLocation texture, int color) {
        this.id = id;
        this.name = name;
        this.texture = texture;
        this.color = color;
    }
    
    // Getters
    public String getId() { return id; }
    public String getName() { return name; }
    public ResourceLocation getTexture() { return texture; }
    public int getColor() { return color; }
}