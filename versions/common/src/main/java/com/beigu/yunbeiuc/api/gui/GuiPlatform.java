package com.beigu.yunbeiuc.api.gui;

import com.beigu.yunbeiuc.screen.ButtonWidget;
import com.beigu.yunbeiuc.screen.TextFieldWidget;
import net.minecraft.client.gui.Font;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;

import java.util.function.Consumer;

public interface GuiPlatform {
    ButtonWidget createButton(Component label, int x, int y, int width, int height,
                              Consumer<ButtonWidget> action);

    TextFieldWidget createTextField(Font font, int x, int y, int width, int height, Component label);

    void setPlaceholder(TextFieldWidget textField, Component placeholder);

    void setText(TextFieldWidget textField, String value);

    String getText(TextFieldWidget textField);

    void setMaxLength(TextFieldWidget textField, int maxLength);

    void setChangedListener(TextFieldWidget textField, Consumer<String> listener);

    void setTextFieldPosition(TextFieldWidget textField, int x, int y);

    void setButtonPosition(ButtonWidget button, int x, int y);

    void setButtonMessage(ButtonWidget button, Component message);

    // 下面两个是 S2C 包的客户端落地逻辑。放在这里而不是网络包里，是因为网络包类在**服务端**
    // 也会被加载（服务端要构造并发送它们），一旦里面出现 net.minecraft.client.* 就会崩服。

    /** 客户端：打开轨道放行相位界面。 */
    void openRailPhaseScreen(BlockPos start, BlockPos end, BlockPos groupMember,
                             int phaseCount, int[] phaseTimes, long mask);
}
