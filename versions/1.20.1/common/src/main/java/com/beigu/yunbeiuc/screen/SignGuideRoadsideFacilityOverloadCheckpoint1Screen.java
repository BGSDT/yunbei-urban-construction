package com.beigu.yunbeiuc.screen;

import com.beigu.yunbeiuc.entity.SignGuideRoadsideFacilityOverloadCheckpoint1Entity;
import com.beigu.yunbeiuc.network.ModMessages;
import com.beigu.yunbeiuc.network.SignGuideRoadsideFacilityOverloadCheckpoint1UpdatePacket;
import dev.architectury.networking.NetworkManager;
import io.netty.buffer.Unpooled;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;

import java.util.function.Consumer;

public class SignGuideRoadsideFacilityOverloadCheckpoint1Screen extends AbstractSignFormScreen {
    private SignGuideRoadsideFacilityOverloadCheckpoint1Entity.Unit unit1;
    private TextFieldWidget length1TextField;

    private static final int PANEL_WIDTH = 320;
    private static final int PANEL_HEIGHT = 195;
    private static final int INPUT_WIDTH = 70;
    private static final int INPUT_HEIGHT = 20;
    private static final int UNIT_BUTTON_WIDTH = 45;
    private static final int UNIT_BUTTON_HEIGHT = 20;

    public SignGuideRoadsideFacilityOverloadCheckpoint1Screen(BlockPos pos) {
        super(pos, "text.yunbeiuc.sign_guide_roadside_facility_overload_checkpoint_1");
    }

    @Override
    protected void init() {
        super.init();

        this.unit1 = SignGuideRoadsideFacilityOverloadCheckpoint1Entity.Unit.METRE;
        String existingLength1 = "";

        if (this.client != null && this.client.world != null) {
            if (this.client.world.getBlockEntity(this.pos) instanceof SignGuideRoadsideFacilityOverloadCheckpoint1Entity entity) {
                this.unit1 = entity.getUnit1();
                existingLength1 = entity.getLength1();
            }
        }

        int panelX = (this.width - PANEL_WIDTH) / 2;
        int panelY = (this.height - PANEL_HEIGHT) / 2;

        // Length text field
        this.length1TextField = createTextField(panelX + 10, panelY + 40, existingLength1);

        // Unit buttons
        createUnitButtons(panelX + 100, panelY + 40, unit -> this.unit1 = unit);

        // 保存和取消按钮
        int buttonY = panelY + 160;
        this.addDrawableChild(
                ButtonWidget.builder(Text.translatable("text.yunbeiuc.sign_guide_roadside_facility_overload_checkpoint_1.save"), button -> this.saveAndClose())
                        .dimensions(panelX + 100, buttonY, 90, 24)
                        .build()
        );

        this.addDrawableChild(
                ButtonWidget.builder(Text.translatable("text.yunbeiuc.sign_guide_roadside_facility_overload_checkpoint_1.cancel"), button -> this.close())
                        .dimensions(panelX + 210, buttonY, 90, 24)
                        .build()
        );
    }

    private TextFieldWidget createTextField(int x, int y, String existingText) {
        TextFieldWidget field = new TextFieldWidget(
                this.textRenderer,
                x, y,
                INPUT_WIDTH, INPUT_HEIGHT,
                Text.translatable("text.yunbeiuc.sign_guide_roadside_facility_overload_checkpoint_1.length_1")
        );
        field.setMaxLength(256);
        field.setText(existingText);
        field.setPlaceholder(Text.translatable("text.yunbeiuc.sign_guide_roadside_facility_overload_checkpoint_1.placeholder"));
        this.addSelectableChild(field);
        return field;
    }

    private void createUnitButtons(int x, int y, Consumer<SignGuideRoadsideFacilityOverloadCheckpoint1Entity.Unit> unitConsumer) {
        this.addDrawableChild(
                ButtonWidget.builder(Text.translatable("text.yunbeiuc.unit.metre"), button -> {
                    unitConsumer.accept(SignGuideRoadsideFacilityOverloadCheckpoint1Entity.Unit.METRE);
                }).dimensions(x, y, UNIT_BUTTON_WIDTH, UNIT_BUTTON_HEIGHT).build()
        );
        this.addDrawableChild(
                ButtonWidget.builder(Text.translatable("text.yunbeiuc.unit.kilometre"), button -> {
                    unitConsumer.accept(SignGuideRoadsideFacilityOverloadCheckpoint1Entity.Unit.KILOMETRE);
                }).dimensions(x + 50, y, 55, UNIT_BUTTON_HEIGHT).build()
        );
    }

    @Override
    protected void saveAndClose() {
        if (this.client != null && this.client.world != null) {
            String length1 = getTextSafely(this.length1TextField);

            SignGuideRoadsideFacilityOverloadCheckpoint1UpdatePacket packet =
                    new SignGuideRoadsideFacilityOverloadCheckpoint1UpdatePacket(pos, unit1, length1);
            PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
            packet.write(buf);
            NetworkManager.sendToServer(ModMessages.UPDATE_SIGN_GUIDE_ROADSIDE_FACILITY_OVERLOAD_CHECKPOINT_1, buf);
        }
        this.close();
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context);

        int panelX = (this.width - PANEL_WIDTH) / 2;
        int panelY = (this.height - PANEL_HEIGHT) / 2;

        renderPanelBackground(context, panelX, panelY, PANEL_WIDTH, PANEL_HEIGHT);

        // 第一行标签
        renderLabel(context, panelX, panelY, "1_name", 28);

        // 状态显示
        renderUnitStatus(context, panelX, panelY, unit1);

        renderTextField(this.length1TextField, context, mouseX, mouseY, delta);

        super.render(context, mouseX, mouseY, delta);
    }

    private void renderUnitStatus(DrawContext context, int panelX, int panelY,
                                  SignGuideRoadsideFacilityOverloadCheckpoint1Entity.Unit unit) {
        context.drawTextWithShadow(
                this.textRenderer,
                Text.translatable("text.yunbeiuc.unit." + unit.getName()),
                panelX + 210, panelY + 42,
                0xFFFFFF00
        );
    }
}