package com.beigu.yunbeiuc.render;

import com.beigu.yunbeiuc.YunbeiUrbanConstruction;
import com.beigu.yunbeiuc.block.MunicipalBlocks;
import com.beigu.yunbeiuc.block.custom.traffic.TrafficLightsBlock;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class TrafficLightsBlockEntityRenderer implements BlockEntityRenderer<TrafficLightsBlockEntity> {
    private final TextRenderer textRenderer;

    public TrafficLightsBlockEntityRenderer(BlockEntityRendererFactory.Context ctx) {
        this.textRenderer = ctx.getTextRenderer();
    }

    private float getZOffset(TrafficLightsBlock.MountType mountType) {
        return switch (mountType) {
            case POLE -> -0.46f;
            case SIMPLE -> -0.33f;
        };
    }

    private static final Identifier LEFT_TURN_RED = new Identifier(YunbeiUrbanConstruction.MOD_ID, "textures/block/lights/left_turn_red.png");
    private static final Identifier LEFT_TURN_YELLOW = new Identifier(YunbeiUrbanConstruction.MOD_ID, "textures/block/lights/left_turn_yellow.png");
    private static final Identifier LEFT_TURN_GREEN = new Identifier(YunbeiUrbanConstruction.MOD_ID, "textures/block/lights/left_turn_green.png");
    private static final Identifier STRAIGHT_CIRCLE_RED = new Identifier(YunbeiUrbanConstruction.MOD_ID, "textures/block/lights/straight_red.png");
    private static final Identifier STRAIGHT_CIRCLE_YELLOW = new Identifier(YunbeiUrbanConstruction.MOD_ID, "textures/block/lights/straight_yellow.png");
    private static final Identifier STRAIGHT_CIRCLE_GREEN = new Identifier(YunbeiUrbanConstruction.MOD_ID, "textures/block/lights/straight_green.png");
    private static final Identifier STRAIGHT_ARROW_RED = new Identifier(YunbeiUrbanConstruction.MOD_ID, "textures/block/lights/straight_arrow_red.png");
    private static final Identifier STRAIGHT_ARROW_YELLOW = new Identifier(YunbeiUrbanConstruction.MOD_ID, "textures/block/lights/straight_arrow_yellow.png");
    private static final Identifier STRAIGHT_ARROW_GREEN = new Identifier(YunbeiUrbanConstruction.MOD_ID, "textures/block/lights/straight_arrow_green.png");
    private static final Identifier RIGHT_TURN_RED = new Identifier(YunbeiUrbanConstruction.MOD_ID, "textures/block/lights/right_turn_red.png");
    private static final Identifier RIGHT_TURN_YELLOW = new Identifier(YunbeiUrbanConstruction.MOD_ID, "textures/block/lights/right_turn_yellow.png");
    private static final Identifier RIGHT_TURN_GREEN = new Identifier(YunbeiUrbanConstruction.MOD_ID, "textures/block/lights/right_turn_green.png");
    private static final Identifier TURN_AROUND_RED = new Identifier(YunbeiUrbanConstruction.MOD_ID, "textures/block/lights/turn_around_red.png");
    private static final Identifier TURN_AROUND_YELLOW = new Identifier(YunbeiUrbanConstruction.MOD_ID, "textures/block/lights/turn_around_yellow.png");
    private static final Identifier TURN_AROUND_GREEN = new Identifier(YunbeiUrbanConstruction.MOD_ID, "textures/block/lights/turn_around_green.png");
    private static final Identifier NON_MOTOR_VEHICLES_RED = new Identifier(YunbeiUrbanConstruction.MOD_ID, "textures/block/lights/non_motor_vehicles_red.png");
    private static final Identifier NON_MOTOR_VEHICLES_YELLOW = new Identifier(YunbeiUrbanConstruction.MOD_ID, "textures/block/lights/non_motor_vehicles_yellow.png");
    private static final Identifier NON_MOTOR_VEHICLES_GREEN = new Identifier(YunbeiUrbanConstruction.MOD_ID, "textures/block/lights/non_motor_vehicles_green.png");
    private static final Identifier NON_MOTOR_VEHICLES_LEFT_TURN_RED = new Identifier(YunbeiUrbanConstruction.MOD_ID, "textures/block/lights/non_motor_vehicles_left_turn_red.png");
    private static final Identifier NON_MOTOR_VEHICLES_LEFT_TURN_YELLOW = new Identifier(YunbeiUrbanConstruction.MOD_ID, "textures/block/lights/non_motor_vehicles_left_turn_yellow.png");
    private static final Identifier NON_MOTOR_VEHICLES_LEFT_TURN_GREEN = new Identifier(YunbeiUrbanConstruction.MOD_ID, "textures/block/lights/non_motor_vehicles_left_turn_green.png");
    private static final Identifier NON_MOTOR_VEHICLES_RIGHT_TURN_RED = new Identifier(YunbeiUrbanConstruction.MOD_ID, "textures/block/lights/non_motor_vehicles_right_turn_red.png");
    private static final Identifier NON_MOTOR_VEHICLES_RIGHT_TURN_YELLOW = new Identifier(YunbeiUrbanConstruction.MOD_ID, "textures/block/lights/non_motor_vehicles_right_turn_yellow.png");
    private static final Identifier NON_MOTOR_VEHICLES_RIGHT_TURN_GREEN = new Identifier(YunbeiUrbanConstruction.MOD_ID, "textures/block/lights/non_motor_vehicles_right_turn_green.png");
    private static final Identifier PAVEMENT_RED = new Identifier(YunbeiUrbanConstruction.MOD_ID, "textures/block/lights/pavement_red.png");
    private static final Identifier PAVEMENT_GREEN = new Identifier(YunbeiUrbanConstruction.MOD_ID, "textures/block/lights/pavement_green.png");
    private static final Identifier SLOW_RED = new Identifier(YunbeiUrbanConstruction.MOD_ID, "textures/block/lights/slow_red.png");
    private static final Identifier SLOW_YELLOW = new Identifier(YunbeiUrbanConstruction.MOD_ID, "textures/block/lights/slow_yellow.png");
    private static final Identifier SLOW_GREEN = new Identifier(YunbeiUrbanConstruction.MOD_ID, "textures/block/lights/slow_green.png");

    @Override
    public void render(TrafficLightsBlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        TrafficLightsBlockEntity.DirectionType directiontype = entity.getDirectionType();

        Direction facing = entity.getCachedState().get(TrafficLightsBlock.FACING);
        TrafficLightsBlock.LightState type = entity.getCachedState().get(TrafficLightsBlock.LIGHT_STATE);
        TrafficLightsBlock.MountType mountType = entity.getCachedState().get(TrafficLightsBlock.TYPE);
        Block currentBlock = entity.getCachedState().getBlock();
        renderPhaseText(entity, matrices, vertexConsumers, light, currentBlock);
        if (currentBlock == MunicipalBlocks.TRAFFIC_LIGHTS_COUNTDOWN_TIMER.get()){
            renderText(entity, matrices, vertexConsumers, light, facing, type);
            return;
        } else if (currentBlock == MunicipalBlocks.TRAFFIC_LIGHTS_GRAY_SHANGHAI.get() ||
                currentBlock == MunicipalBlocks.TRAFFIC_LIGHTS_BLACK_SHANGHAI.get()) {
            renderTimeText(entity, matrices, vertexConsumers, light, facing, type);
        }
        renderLogo(matrices, vertexConsumers, light, overlay, facing, directiontype, type, currentBlock, mountType);

        if ((currentBlock == MunicipalBlocks.TRAFFIC_LIGHTS_PAVEMENT_BLACK.get() || currentBlock == MunicipalBlocks.TRAFFIC_LIGHTS_PAVEMENT_GRAY.get())
                && entity.isShowSeconds()) {
            renderPavementSeconds(entity, matrices, vertexConsumers, light, facing, type, mountType);
        }
    }

    private void renderPavementSeconds(TrafficLightsBlockEntity entity, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, Direction facing, TrafficLightsBlock.LightState lightState, TrafficLightsBlock.MountType mountType) {
        int remaining;
        if (entity.isInGroup()) {
            TrafficLightsBlockEntity.LightTimingInfo info = entity.getLightTimingInfo();
            remaining = info.getActiveRemaining();
            if (remaining < 0) {
                return;
            }
        } else {
            remaining = entity.getFixedSeconds();
        }

        int color = (lightState == TrafficLightsBlock.LightState.RED) ? 0xFF0000 : 0x39FF00;

        float logoY = (lightState == TrafficLightsBlock.LightState.RED || lightState == TrafficLightsBlock.LightState.YELLOW)
                ? 3.85f / 16f : -3.85f / 16f;
        float y = -logoY;
        float z = getZOffset(mountType);

        matrices.push();

        matrices.translate(0.5, 0.5, 0.5);
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-facing.asRotation()));
        matrices.translate(0.2f, y, z);

        float scaleValue = 0.07f;
        matrices.scale(scaleValue, -scaleValue, scaleValue);

        String text = String.valueOf(remaining);
        if (remaining >= 99) text = "99";

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

        if (lightState == TrafficLightsBlock.LightState.GRAY){
            matrices.pop();
            return;
        }

        matrices.translate(0f, 0.0f, 0.01f);

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

    private void renderLogo(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay, Direction facing, TrafficLightsBlockEntity.DirectionType directionType, TrafficLightsBlock.LightState lightState, Block currentBlock, TrafficLightsBlock.MountType mountType) {
        Identifier texture;

        // COLOR_FLASH 图案的慢闪逻辑：与灯的GRAY闪烁同步（0.5s一闪）
        if (directionType == TrafficLightsBlockEntity.DirectionType.COLOR_FLASH) {
            // 使用与BlockEntity.tick()相同的闪烁逻辑
            // FLASH_INTERVAL = 10 ticks = 0.5s
            long currentTimeMillis = System.currentTimeMillis();
            int flashPhase = (int) ((currentTimeMillis / 500) % 2);
            if (flashPhase == 0) {
                // GRAY相位（隐藏阶段），不渲染
                return;
            }
            // flashPhase == 1 时为GREEN相位（显示阶段），继续渲染
        }

        // SLOW_FLASH 图案的慢闪逻辑：与灯的GRAY闪烁同步（0.5s一闪）
        if (directionType == TrafficLightsBlockEntity.DirectionType.SLOW_FLASH) {
            long currentTimeMillis = System.currentTimeMillis();
            int flashPhase = (int) ((currentTimeMillis / 500) % 2);
            if (flashPhase == 0) {
                // GRAY相位（隐藏阶段），不渲染
                return;
            }
            // flashPhase == 1 时为GREEN相位（显示阶段），继续渲染
        }

        // 人行道红绿灯的特殊处理
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
                case STRAIGHT_CIRCLE, COLOR_FLASH -> switch (lightState) {
                    case RED -> STRAIGHT_CIRCLE_RED;
                    case YELLOW -> STRAIGHT_CIRCLE_YELLOW;
                    case GREEN -> STRAIGHT_CIRCLE_GREEN;
                    case GRAY -> null;
                };
                case STRAIGHT_ARROW -> switch (lightState) {
                    case RED -> STRAIGHT_ARROW_RED;
                    case YELLOW -> STRAIGHT_ARROW_YELLOW;
                    case GREEN -> STRAIGHT_ARROW_GREEN;
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
                case NON_MOTOR_VEHICLES_LEFT_TURN -> switch (lightState) {
                    case RED -> NON_MOTOR_VEHICLES_LEFT_TURN_RED;
                    case YELLOW -> NON_MOTOR_VEHICLES_LEFT_TURN_YELLOW;
                    case GREEN -> NON_MOTOR_VEHICLES_LEFT_TURN_GREEN;
                    case GRAY -> null;
                };
                case NON_MOTOR_VEHICLES_RIGHT_TURN -> switch (lightState) {
                    case RED -> NON_MOTOR_VEHICLES_RIGHT_TURN_RED;
                    case YELLOW -> NON_MOTOR_VEHICLES_RIGHT_TURN_YELLOW;
                    case GREEN -> NON_MOTOR_VEHICLES_RIGHT_TURN_GREEN;
                    case GRAY -> null;
                };
                case SLOW_FLASH -> switch (lightState) {
                    case RED -> SLOW_RED;
                    case YELLOW -> SLOW_YELLOW;
                    case GREEN -> SLOW_GREEN;
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
            z = getZOffset(mountType);
        } else if (currentBlock == MunicipalBlocks.TRAFFIC_LIGHTS_GRAY_SINGLE_HORIZONTAL.get() ||
                currentBlock == MunicipalBlocks.TRAFFIC_LIGHTS_BLACK_SINGLE_HORIZONTAL.get() ||
                currentBlock == MunicipalBlocks.TRAFFIC_LIGHTS_FOGGY.get()) {
            // 单灯横式（含雾灯）：图案在中心 x=0, y=0
            x = 0;
            y = 0;
            z = -0.68f;
        } else if (currentBlock == MunicipalBlocks.TRAFFIC_LIGHTS_GRAY_SINGLE_VERTICAL.get() ||
                currentBlock == MunicipalBlocks.TRAFFIC_LIGHTS_BLACK_SINGLE_VERTICAL.get()) {
            // 单灯竖式：图案在中心 x=0, y=0
            x = 0;
            y = 0;
            z = -0.53f;
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
        Matrix4f positionMatrix = matrices.peek().getPositionMatrix();
        org.joml.Vector3f normalVec = matrices.peek().getNormalMatrix().transform(new org.joml.Vector3f(0, 0, 1));

        consumer.vertex(positionMatrix, -halfSize, -halfSize, 0).color(255, 255, 255, 255).texture(0.0f, 1.0f).overlay(overlay).light(light).normal(normalVec.x, normalVec.y, normalVec.z).next();
        consumer.vertex(positionMatrix, halfSize, -halfSize, 0).color(255, 255, 255, 255).texture(1.0f, 1.0f).overlay(overlay).light(light).normal(normalVec.x, normalVec.y, normalVec.z).next();
        consumer.vertex(positionMatrix, halfSize, halfSize, 0).color(255, 255, 255, 255).texture(1.0f, 0.0f).overlay(overlay).light(light).normal(normalVec.x, normalVec.y, normalVec.z).next();
        consumer.vertex(positionMatrix, -halfSize, halfSize, 0).color(255, 255, 255, 255).texture(0.0f, 0.0f).overlay(overlay).light(light).normal(normalVec.x, normalVec.y, normalVec.z).next();

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
        // 单灯红绿灯直接显示图案文本，不显示"单灯横式/竖式"
        String phaseText;
        List<Integer> phaseIndices = new ArrayList<>(entity.getPhaseIndices());
        Collections.sort(phaseIndices);
        if (phaseIndices.isEmpty()) {
            phaseText = "相位: - / " + entity.getPhaseCount();
        } else {
            String joined = phaseIndices.stream().map(i -> String.valueOf(i + 1)).collect(Collectors.joining(","));
            phaseText = "相位: " + joined + " / " + entity.getPhaseCount();
        }

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
                TextRenderer.TextLayerType.NORMAL,
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
                TextRenderer.TextLayerType.NORMAL,
                0,
                light
        );

        matrices.pop();
    }

    private void renderText(TrafficLightsBlockEntity entity, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, Direction facing, TrafficLightsBlock.LightState lightState) {
        int remaining;
        boolean showSeconds = entity.isShowSeconds();

        // 如果在相位组中，使用相位剩余时间；否则使用静态固定秒数
        if (entity.isInGroup()) {
            TrafficLightsBlockEntity.LightTimingInfo info = entity.getLightTimingInfo();
            remaining = info.getActiveRemaining();
            if (remaining < 0) {
                return;
            }
        } else {
            // 静态状态：如果关闭显示读秒，直接返回
            if (!showSeconds) {
                return;
            }
            // 如果是黄灯，不显示读秒
            if (lightState == TrafficLightsBlock.LightState.YELLOW) {
                return;
            }
            remaining = entity.getFixedSeconds();
        }

        // 颜色直接由方块状态中的 LIGHT_STATE 决定，与灯模型同步
        int color = switch (lightState) {
            case RED -> 0xFF0000;
            case YELLOW -> 0xFFF000;
            case GREEN -> 0x39FF00;
            default -> 0xFFFFFF;
        };

        matrices.push();

        matrices.translate(0.5, 0.5, 0.5);
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-facing.asRotation()));

        float scaleValue = 0.085f;
        matrices.translate(0.5f, 0.0f, -0.74f);
        matrices.scale(scaleValue, -scaleValue, scaleValue);

        // 背景 88 始终显示
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

        // 相位组中：判断是否显示实际数字
        if (entity.isInGroup()) {
            int displayMode = entity.getCountdownDisplayMode();
            int threshold = entity.getCountdownThreshold();
            boolean shouldShowDigits = (displayMode == 0) || (displayMode == 1 && remaining <= threshold);
            if (!shouldShowDigits) {
                matrices.pop();
                return;
            }
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
        int remaining;
        boolean showSeconds = entity.isShowSeconds();

        // 上海红绿灯黄灯时始终隐藏读秒
        if (lightState == TrafficLightsBlock.LightState.YELLOW) {
            return;
        }

        // 如果在相位组中，使用相位剩余时间；否则使用静态固定秒数
        if (entity.isInGroup()) {
            TrafficLightsBlockEntity.LightTimingInfo info = entity.getLightTimingInfo();
            remaining = info.getActiveRemaining();
            if (remaining < 0) {
                return;
            }
        } else {
            // 静态状态：如果关闭显示读秒，直接返回
            if (!showSeconds) {
                return;
            }
            remaining = entity.getFixedSeconds();
        }

        // 颜色直接由方块状态中的 LIGHT_STATE 决定，与灯模型同步
        int color = switch (lightState) {
            case RED -> 0xFF0000;
            case YELLOW -> 0xFFF000;
            case GREEN -> 0x39FF00;
            default -> 0xFFFFFF;
        };

        matrices.push();

        matrices.translate(0.5, 0.5, 0.5);
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-facing.asRotation()));

        float scaleValue = 0.085f;
        matrices.translate(0.25f, 0.0f, -0.53f);
        matrices.scale(scaleValue, -scaleValue, scaleValue);

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

        if (lightState == TrafficLightsBlock.LightState.GRAY || (entity.isInGroup() && remaining >= 15)){
            matrices.pop();
            return;
        }

        matrices.translate(0f, 0.0f, 0.01f);

        String text = String.valueOf(remaining);
        if (remaining > 99) text = "99";
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
            case STRAIGHT_CIRCLE -> "直行（圆形）";
            case STRAIGHT_ARROW -> "直行（箭头）";
            case LEFT_TURN -> "左转";
            case RIGHT_TURN -> "右转";
            case TURN_AROUND -> "掉头";
            case NON_MOTOR_VEHICLES -> "非机动车";
            case NON_MOTOR_VEHICLES_LEFT_TURN -> "非机动车（左转）";
            case NON_MOTOR_VEHICLES_RIGHT_TURN -> "非机动车（右转）";
            case COLOR_FLASH -> "色闪";
            case SLOW_FLASH -> "慢闪";
        };
    }

    @Override
    public int getRenderDistance() {
        return 256;
    }
}