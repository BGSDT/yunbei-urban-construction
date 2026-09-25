package com.beigu.yunbeiuc.mixin_interfaces;

public interface ITextDisplayScreenMixin {
    void yunbeiuc$clearCurrentLine();
    void yunbeiuc$appendTextToCurrentLine(String text);
    void yunbeiuc$setCurrentLineText(String text);
}
