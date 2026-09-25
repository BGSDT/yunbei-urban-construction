package com.beigu.yunbeiuc.api.gui;

/** Version-neutral GUI operations. Each Minecraft version supplies an implementation. */
public interface GuiPlatform {
    Object createButton(String id, String label, int x, int y, int width, int height, Runnable action);
    Object createTextField(String id, String placeholder, int x, int y, int width, int height);
    void setPlaceholder(Object textField, String placeholder);
    void setValue(Object textField, String value);
    String getValue(Object textField);
}
