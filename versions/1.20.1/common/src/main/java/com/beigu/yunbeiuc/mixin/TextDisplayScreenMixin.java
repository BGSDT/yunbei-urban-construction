package com.beigu.yunbeiuc.mixin;

import com.beigu.yunbeiuc.mixin_interfaces.ITextDisplayScreenMixin;
import com.beigu.yunbeiuc.screen.TextDisplayScreen;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(TextDisplayScreen.class)
public abstract class TextDisplayScreenMixin implements ITextDisplayScreenMixin {

    @Override
    public void yunbeiuc$clearCurrentLine() {
        TextDisplayScreen screen = (TextDisplayScreen) (Object) this;
        // 实现：清空当前选中行的文本
    }

    @Override
    public void yunbeiuc$appendTextToCurrentLine(String text) {
        TextDisplayScreen screen = (TextDisplayScreen) (Object) this;
        // 实现：向当前选中行末尾追加文本
    }

    @Override
    public void yunbeiuc$setCurrentLineText(String text) {
        TextDisplayScreen screen = (TextDisplayScreen) (Object) this;
        // 实现：设置当前选中行的文本
    }
}
