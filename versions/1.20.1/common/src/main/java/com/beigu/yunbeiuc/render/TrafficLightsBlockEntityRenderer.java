package com.beigu.yunbeiuc.render;

import com.beigu.yunbeiuc.YunbeiUrbanConstruction;
import com.beigu.yunbeiuc.block.MunicipalBlocks;
import com.beigu.yunbeiuc.block.custom.TrafficLightsBlock;
import com.beigu.yunbeiuc.entity.TrafficLightsBlockEntity;
import com.beigu.yunbeiuc.item.ModItems;
import com.beigu.yunbeiuc.util.CustomFontRenderer;
import net.minecraft.block.Block;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.RotationAxis;
import org.joml.Matrix4f;

public class TrafficLightsBlockEntityRenderer implements BlockEntityRenderer<TrafficLightsBlockEntity> {
    private final TextRenderer textRenderer;

    public TrafficLightsBlockEntityRenderer(BlockEntityRendererFactory.Context ctx) {
        this.textRenderer = ctx.getTextRenderer();
    }

    private static final Identifier LEFT_TURN_RED = new Identifier(YunbeiUrbanConstruction.MOD_ID, "textures/block/lights/left_turn_red.png");
    private static final Identifier LEFT_TURN_YELLOW = new Identifier(YunbeiUrbanConstruction.MOD_ID, "textures/block/lights/left_turn_yellow.png");
    private static final Identifier LEFT_TURN_GREEN = new Identifier(YunbeiUrbanConstruction.MOD_ID, "textures/block/lights/left_turn_green.png");
    private static final Identifier STRAIGHT_RED = new Identifier(YunbeiUrbanConstruction.MOD_ID, "textures/block/lights/straight_red.png");
    private static final Identifier STRAIGHT_YELLOW = new Identifier(YunbeiUrbanConstruction.MOD_ID, "textures/block/lights/straight_yellow.png");
    private static final Identifier STRAIGHT_GREEN = new Identifier(YunbeiUrbanConstruction.MOD_ID, "textures/block/lights/straight_green.png");
    private static final Identifier RIGHT_TURN_RED = new Identifier(YunbeiUrbanConstruction.MOD_ID, "textures/block/lights/right_turn_red.png");
    private static final Identifier RIGHT_TURN_YELLOW = new Identifier(YunbeiUrbanConstruction.MOD_ID, "textures/block/lights/right_turn_yellow.png");
    private static final Identifier RIGHT_TURN_GREEN = new Identifier(YunbeiUrbanConstruction.MOD_ID, "textures/block/lights/right_turn_green.png");
    private static final Identifier TURN_AROUND_RED = new Identifier(YunbeiUrbanConstruction.MOD_ID, "textures/block/lights/turn_around_red.png");
    private static final Identifier TURN_AROUND_YELLOW = new Identifier(YunbeiUrbanConstruction.MOD_ID, "textures/block/lights/turn_around_yellow.png");
    private static final Identifier TURN_AROUND_GREEN = new Identifier(YunbeiUrbanConstruction.MOD_ID, "textures/block/lights/turn_around_green.png");
    private static final Identifier NON_MOTOR_VEHICLES_RED = new Identifier(YunbeiUrbanConstruction.MOD_ID, "textures/block/lights/non_motor_vehicles_red.png");
    private static final Identifier NON_MOTOR_VEHICLES_YELLOW = new Identifier(YunbeiUrbanConstruction.MOD_ID, "textures/block/lights/non_motor_vehicles_yellow.png");
    private static final Identifier NON_MOTOR_VEHICLES_GREEN = new Identifier(YunbeiUrbanConstruction.MOD_ID, "textures/block/lights/non_motor_vehicles_green.png");
    private static final Identifier PAVEMENT_RED = new Identifier(YunbeiUrbanConstruction.MOD_ID, "textures/block/lights/pavement_red.png");
    private static final Identifier PAVEMENT_GREEN = new Identifier(YunbeiUrbanConstruction.MOD_ID, "textures/block/lights/pavement_green.png");

    @Override
    public void render(TrafficLightsBlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        TrafficLightsBlockEntity.DirectionType directiontype = entity.getDirectionType();

        Direction facing = entity.getCachedState().get(TrafficLightsBlock.FACING);
        TrafficLightsBlock.LightState type = entity.getCachedState().get(TrafficLightsBlock.LIGHT_STATE);
        Block currentBlock = entity.getCachedState().getBlock();
        renderPhaseText(entity, matrices, vertexConsumers, light, currentBlock);
        if (currentBlock == MunicipalBlocks.TRAFFIC_LIGHTS_COUNTDOWN_TIMER.get()){
            renderText(entity, matrices, vertexConsumers, light, facing, type);
            return;
        } else if (currentBlock == MunicipalBlocks.TRAFFIC_LIGHTS_GRAY_SHANGHAI.get() ||
                currentBlock == MunicipalBlocks.TRAFFIC_LIGHTS_BLACK_SHANGHAI.get()) {
            renderTimeText(entity, matrices, vertexConsumers, light, facing, type);
        }
        renderLogo(matrices, vertexConsumers, light, overlay, facing, directiontype, type, currentBlock);
    }

    private void renderLogo(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay, Direction facing, TrafficLightsBlockEntity.DirectionType directionType, TrafficLightsBlock.LightState lightState, Block currentBlock) {
        Identifier texture;

        if (currentBlock == MunicipalBlocks.TRAFFIC_LIGHTS_PAVEMENT_BLACK.get() || currentBlock == MunicipalBlocks.TRAFFIC_LIGHTS_PAVEMENT_GRAY.get()) {
            texture = switch (lightState) {
                case RED, YELLOW -> PAVEMENT_RED;
                case GREEN -> PAVEMENT_GREEN;
                default -> null;
            };
        } else {
            texture = switch (directionType) {
                case LEFT_TURN -> switch (lightState) {
                    case RED -> LEFT_TURN_RED;
                    case YELLOW -> LEFT_TURN_YELLOW;
                    case GREEN -> LEFT_TURN_GREEN;
                    case GRAY -> null;
                };
                case STRAIGHT -> switch (lightState) {
                    case RED -> STRAIGHT_RED;
                    case YELLOW -> STRAIGHT_YELLOW;
                    case GREEN -> STRAIGHT_GREEN;
                    case GRAY -> null;
                };
                case RIGHT_TURN -> switch (lightState) {
                    case RED -> RIGHT_TURN_RED;
                    case YELLOW -> RIGHT_TURN_YELLOW;
                    case GREEN -> RIGHT_TURN_GREEN;
                    case GRAY -> null;
                };
                case TURN_AROUND -> switch (lightState) {
                    case RED -> TURN_AROUND_RED;
                    case YELLOW -> TURN_AROUND_YELLOW;
                    case GREEN -> TURN_AROUND_GREEN;
                    case GRAY -> null;
                };
                case NON_MOTOR_VEHICLES -> switch (lightState) {
                    case RED -> NON_MOTOR_VEHICLES_RED;
                    case YELLOW -> NON_MOTOR_VEHICLES_YELLOW;
                    case GREEN -> NON_MOTOR_VEHICLES_GREEN;
                    case GRAY -> null;
                };
            };
        }

        if (texture == null) {
            return;
        }

        matrices.push();

        matrices.translate(0.5, 0.5, 0.5);
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-facing.asRotation()));

        float arrowSize = 0.4f;
        float halfSize = arrowSize / 2f;
        float x = 0;
        float y = 0;
        float z = 0;

        if (currentBlock == MunicipalBlocks.TRAFFIC_LIGHTS_PAVEMENT_BLACK.get() || currentBlock == MunicipalBlocks.TRAFFIC_LIGHTS_PAVEMENT_GRAY.get()) {
            y = switch (lightState) {
                case RED, YELLOW -> 3.85f / 16f;
                case GREEN -> -3.85f / 16f;
                default -> 0f;
            };
            z = -0.46f;
        } else if (currentBlock == MunicipalBlocks.TRAFFIC_LIGHTS_GRAY_HORIZONTAL.get() ||
                currentBlock == MunicipalBlocks.TRAFFIC_LIGHTS_BLACK_HORIZONTAL.get()) {
            x = switch (lightState) {
                case RED -> -7.75f / 16f;
                case GREEN -> 7.75f / 16f;
                default -> 0f;
            };
            z = -0.53f;
        } else if (currentBlock == MunicipalBlocks.TRAFFIC_LIGHTS_GRAY_VERTICAL.get() ||
                currentBlock == MunicipalBlocks.TRAFFIC_LIGHTS_BLACK_VERTICAL.get() ||
                currentBlock == MunicipalBlocks.TRAFFIC_LIGHTS_GRAY_SHANGHAI.get() ||
                currentBlock == MunicipalBlocks.TRAFFIC_LIGHTS_BLACK_SHANGHAI.get()) {
            y = switch (lightState) {
                case RED -> 7.75f / 16f;
                case GREEN -> -7.75f / 16f;
                default -> 0f;
            };
            z = -0.53f;
        }

        matrices.translate(x, y, z);

        VertexConsumer consumer = vertexConsumers.getBuffer(RenderLayer.getEntityCutout(texture));
        Matrix4f matrix = matrices.peek().getPositionMatrix();

        consumer.vertex(matrix, -halfSize, -halfSize, 0).color(255, 255, 255, 255).texture(0.0f, 1.0f).overlay(overlay).light(light).normal(0, 0, 1).next();
        consumer.vertex(matrix, halfSize, -halfSize, 0).color(255, 255, 255, 255).texture(1.0f, 1.0f).overlay(overlay).light(light).normal(0, 0, 1).next();
        consumer.vertex(matrix, halfSize, halfSize, 0).color(255, 255, 255, 255).texture(1.0f, 0.0f).overlay(overlay).light(light).normal(0, 0, 1).next();
        consumer.vertex(matrix, -halfSize, halfSize, 0).color(255, 255, 255, 255).texture(0.0f, 0.0f).overlay(overlay).light(light).normal(0, 0, 1).next();

        matrices.pop();
    }

    private void renderPhaseText(TrafficLightsBlockEntity entity, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, Block currentBlock) {
        MinecraftClient client = MinecraftClient.getInstance();
        PlayerEntity player = client.player;

        if (player == null || !isHoldingWand(player)) {
            return;
        }

        if (!entity.hasTimings() || !entity.isInGroup()) {
            return;
        }

        matrices.push();

        matrices.translate(0.5, 2, 0.5);

        matrices.multiply(client.getEntityRenderDispatcher().getRotation());

        float scale = 0.025f;
        matrices.scale(-scale, -scale, scale);

        String directionText = getDirectionText(entity.getDirectionType());
        if (currentBlock == MunicipalBlocks.TRAFFIC_LIGHTS_PAVEMENT_BLACK.get() || currentBlock == MunicipalBlocks.TRAFFIC_LIGHTS_PAVEMENT_GRAY.get()) directionText = "人行道";
        if (currentBlock == MunicipalBlocks.TRAFFIC_LIGHTS_COUNTDOWN_TIMER.get()) directionText = "倒计时器";
        String phaseText = "相位: " + (entity.getPhaseIndex() + 1) + " / " + entity.getPhaseCount();

        int directionWidth = textRenderer.getWidth(Text.literal(directionText));
        int phaseWidth = textRenderer.getWidth(Text.literal(phaseText));
        int maxWidth = Math.max(directionWidth, phaseWidth);

        int lineHeight = textRenderer.fontHeight + 2;
        int totalHeight = lineHeight * 2;

        float padding = 4;
        float bgX1 = -maxWidth / 2f - padding;
        float bgY1 = -padding;
        float bgX2 = maxWidth / 2f + padding;
        float bgY2 = totalHeight + padding;

        Matrix4f matrix = matrices.peek().getPositionMatrix();
        VertexConsumer bgConsumer = vertexConsumers.getBuffer(RenderLayer.getTextBackground());
        bgConsumer.vertex(matrix, bgX1, bgY1, 0).color(0, 0, 0, 128).light(light).next();
        bgConsumer.vertex(matrix, bgX1, bgY2, 0).color(0, 0, 0, 128).light(light).next();
        bgConsumer.vertex(matrix, bgX2, bgY2, 0).color(0, 0, 0, 128).light(light).next();
        bgConsumer.vertex(matrix, bgX2, bgY1, 0).color(0, 0, 0, 128).light(light).next();

        textRenderer.draw(
                Text.literal(directionText),
                -directionWidth / 2f,
                0,
                0xFFFFFF,
                false,
                matrices.peek().getPositionMatrix(),
                vertexConsumers,
                TextRenderer.TextLayerType.SEE_THROUGH,
                0,
                light
        );

        textRenderer.draw(
                Text.literal(phaseText),
                -phaseWidth / 2f,
                lineHeight,
                0xFFFFFF,
                false,
                matrices.peek().getPositionMatrix(),
                vertexConsumers,
                TextRenderer.TextLayerType.SEE_THROUGH,
                0,
                light
        );

        matrices.pop();
    }

    private void renderText(TrafficLightsBlockEntity entity, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, Direction facing, TrafficLightsBlock.LightState lightState) {
        TrafficLightsBlockEntity.LightTimingInfo info = entity.getLightTimingInfo();
        int remaining = info.getActiveRemaining();

        int color;
        if (info.isTransition()) {
            color = switch (info.getTransitionColor()) {
                case "red" -> 0xFF0000;
                case "yellow" -> 0xFFF000;
                case "green" -> 0x39FF00;
                default -> 0xFFFFFF;
            };
        } else {
            color = switch (lightState) {
                case RED -> 0xFF0000;
                case YELLOW -> 0xFFF000;
                case GREEN -> 0x39FF00;
                default -> 0xFFFFFF;
            };
        }

        matrices.push();

        matrices.translate(0.5, 0.5, 0.5);
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-facing.asRotation()));

        float scaleValue = 0.085f;
        matrices.translate(0.5f, 0.0f, -0.74f);
        matrices.scale(scaleValue, -scaleValue, scaleValue);

        CustomFontRenderer.renderText(
                matrices, vertexConsumers, "88", 0X2e3134,
                0, -6f, 0,
                0.07f,
                light,
                CustomFontRenderer.TextAlignment.RIGHT,
                "ds_digital",
                1,
                1.4f
        );

        if (lightState == TrafficLightsBlock.LightState.GRAY){
            matrices.pop();
            return;
        }

        matrices.translate(0f, 0.0f, 0.01f);

        String text = String.valueOf(remaining);
        if (remaining >= 99) text = "99";
        CustomFontRenderer.renderText(
                matrices, vertexConsumers, text, color,
                0, -6f, 0,
                0.07f,
                light,
                CustomFontRenderer.TextAlignment.RIGHT,
                "ds_digital",
                1,
                1.4f
        );

        matrices.pop();
    }

    private void renderTimeText(TrafficLightsBlockEntity entity, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, Direction facing, TrafficLightsBlock.LightState lightState) {
        TrafficLightsBlockEntity.LightTimingInfo info = entity.getLightTimingInfo();
        int remaining = info.getActiveRemaining();

        int color;
        if (info.isTransition()) {
            color = switch (info.getTransitionColor()) {
                case "red" -> 0xFF0000;
                case "yellow" -> 0xFFF000;
                case "green" -> 0x39FF00;
                default -> 0xFFFFFF;
            };
        } else {
            color = switch (lightState) {
                case RED -> 0xFF0000;
                case YELLOW -> 0xFFF000;
                case GREEN -> 0x39FF00;
                default -> 0xFFFFFF;
            };
        }

        matrices.push();

        matrices.translate(0.5, 0.5, 0.5);
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-facing.asRotation()));

        float scaleValue = 0.085f;
        matrices.translate(0.25f, 0.0f, -0.53f);
        matrices.scale(scaleValue, -scaleValue, scaleValue);

        if (lightState == TrafficLightsBlock.LightState.YELLOW){
            matrices.pop();
            return;
        }

        CustomFontRenderer.renderText(
                matrices, vertexConsumers, "88", 0X2e3134,
                0, -2.5f, 0,
                0.035f,
                light,
                CustomFontRenderer.TextAlignment.RIGHT,
                "ds_digital",
                1,
                1.4f
        );

        if (lightState == TrafficLightsBlock.LightState.GRAY || remaining >= 15 || (color == 0xFFF000 && remaining == 3)){
            matrices.pop();
            return;
        }

        matrices.translate(0f, 0.0f, 0.01f);

        String text = String.valueOf(remaining);
        CustomFontRenderer.renderText(
                matrices, vertexConsumers, text, color,
                0, -2.5f, 0,
                0.035f,
                light,
                CustomFontRenderer.TextAlignment.RIGHT,
                "ds_digital",
                1,
                1.4f
        );

        matrices.pop();
    }

    @Override
    public boolean rendersOutsideBoundingBox(TrafficLightsBlockEntity blockEntity) {
        return true;
    }

    private boolean isHoldingWand(PlayerEntity player) {
        return player.getMainHandStack().isOf(ModItems.WAND.get()) ||
                player.getOffHandStack().isOf(ModItems.WAND.get());
    }

    private String getDirectionText(TrafficLightsBlockEntity.DirectionType type) {
        return switch (type) {
            case STRAIGHT -> "直行";
            case LEFT_TURN -> "左转";
            case RIGHT_TURN -> "右转";
            case TURN_AROUND -> "掉头";
            case NON_MOTOR_VEHICLES -> "非机动车";
        };
    }

    @Override
    public int getRenderDistance() {
        return 256;
    }
}