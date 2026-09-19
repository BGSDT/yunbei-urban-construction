package com.beigu.yunbeiuc.screen;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

/**
 * 承载图案与字体选择浮层的空白界面。
 *
 * <p>自身不绘制任何背景，仅把渲染与输入事件转发给 {@link PatternAndFontOverlay}，
 * 使浮层可以在没有告示牌编辑界面时独立打开。
 */
public class PatternAndFontBlankScreen extends Screen {

    public PatternAndFontBlankScreen() {
        super(Text.empty());
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    @Override
    public void renderBackground(DrawContext context) {
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        if (PatternAndFontOverlay.isVisible) {
            PatternAndFontOverlay.render(context, mouseX, mouseY);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (PatternAndFontOverlay.isVisible) {
            PatternAndFontOverlay.mouseClicked(mouseX, mouseY, button);
            // 浮层在插入内容或点击返回按钮后会自行关闭，此时同步关闭本界面
            if (!PatternAndFontOverlay.isVisible) {
                close();
            }
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (PatternAndFontOverlay.isVisible) {
            PatternAndFontOverlay.mouseReleased(mouseX, mouseY, button);
            return true;
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        if (PatternAndFontOverlay.isVisible) {
            PatternAndFontOverlay.mouseScrolled(mouseX, mouseY, amount);
            return true;
        }
        return super.mouseScrolled(mouseX, mouseY, amount);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (PatternAndFontOverlay.isVisible) {
            if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
                PatternAndFontOverlay.isVisible = false;
                close();
            }
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public void close() {
        PatternAndFontOverlay.closeOverlay();
        super.close();
    }
}
