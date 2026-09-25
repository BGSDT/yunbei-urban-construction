package com.beigu.yunbeiuc.mixin;

import com.beigu.yunbeiuc.mixin_interfaces.ITextDisplayScreenMixin;
import com.beigu.yunbeiuc.screen.TextDisplayScreen;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(TextDisplayScreen.class)
public abstract class TextDisplayScreenMixin implements ITextDisplayScreenMixin {
    @Override
    public void yunbeiuc$clearCurrentLine() {
    }

    @Override
    public void yunbeiuc$appendTextToCurrentLine(String text) {
    }

    @Override
    public void yunbeiuc$setCurrentLineText(String text) {
    }
}