package com.beigu.yunbeiuc.render;

import com.beigu.yunbeiuc.YunbeiUrbanConstruction;
import com.beigu.yunbeiuc.block.MunicipalBlocks;
import com.beigu.yunbeiuc.block.custom.traffic.TrafficLightsBlock;
import com.beigu.yunbeiuc.entity.TrafficLightsBlockEntity;
import com.beigu.yunbeiuc.item.ModItems;
import com.beigu.yunbeiuc.util.CustomFontRenderer;
import net.minecraft.world.level.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.RenderType;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.world.entity.player.Player;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.Direction;
import com.mojang.math.Vector3f;
import com.mojang.math.Matrix4f;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class TrafficLightsBlockEntityRenderer implements BlockEntityRenderer<TrafficLightsBlockEntity> {
    private final Font textRenderer;

    public TrafficLightsBlockEntityRenderer(BlockEntityRendererProvider.Context ctx) {
        this.textRenderer = ctx.getFont();
    }

    private float getZOffset(TrafficLightsBlock.MountType mountType, Block currentBlock) {
        if (currentBlock == MunicipalBlocks.TRAFFIC_LIGHTS_PAVEMENT_GREEN_TAIPEI.get()) return 0.16f;
        if (mountType == TrafficLightsBlock.MountType.POLE) {
            if (isPavementBlock(currentBlock)) return -0.46f;
            else if (currentBlock == MunicipalBlocks.TRAFFIC_LIGHTS_FOGGY.get()) return -0.68f;
            else if (currentBlock == MunicipalBlocks.TRAFFIC_LIGHTS_COUNTDOWN_TIMER.get()) return -0.74f;
            else return -0.53f;
        }
        if (mountType == TrafficLightsBlock.MountType.SIMPLE) {
            if (isPavementBlock(currentBlock)) return -0.33f;
            else if (currentBlock == MunicipalBlocks.TRAFFIC_LIGHTS_FOGGY.get()) return -0.36f;
            else if (currentBlock == MunicipalBlocks.TRAFFIC_LIGHTS_COUNTDOWN_TIMER.get()) return -0.38f;
            else return -0.33f;
        }
        return -0.53f;
    }

    private boolean isPavementBlock(Block currentBlock) {
        return currentBlock == MunicipalBlocks.TRAFFIC_LIGHTS_PAVEMENT_BLACK.get()
                || currentBlock == MunicipalBlocks.TRAFFIC_LIGHTS_PAVEMENT_GRAY.get()
                || currentBlock == MunicipalBlocks.TRAFFIC_LIGHTS_PAVEMENT_GREEN_TAIPEI.get();
    }

    private static final ResourceLocation LEFT_TURN_RED = new ResourceLocation(YunbeiUrbanConstruction.MOD_ID, "textures/block/lights/left_turn_red.png");
    private static final ResourceLocation LEFT_TURN_YELLOW = new ResourceLocation(YunbeiUrbanConstruction.MOD_ID, "textures/block/lights/left_turn_yellow.png");
    private static final ResourceLocation LEFT_TURN_GREEN = new ResourceLocation(YunbeiUrbanConstruction.MOD_ID, "textures/block/lights/left_turn_green.png");
    private static final ResourceLocation STRAIGHT_CIRCLE_RED = new ResourceLocation(YunbeiUrbanConstruction.MOD_ID, "textures/block/lights/straight_red.png");
    private static final ResourceLocation STRAIGHT_CIRCLE_YELLOW = new ResourceLocation(YunbeiUrbanConstruction.MOD_ID, "textures/block/lights/straight_yellow.png");
    private static final ResourceLocation STRAIGHT_CIRCLE_GREEN = new ResourceLocation(YunbeiUrbanConstruction.MOD_ID, "textures/block/lights/straight_green.png");
    private static final ResourceLocation STRAIGHT_ARROW_RED = new ResourceLocation(YunbeiUrbanConstruction.MOD_ID, "textures/block/lights/straight_arrow_red.png");
    private static final ResourceLocation STRAIGHT_ARROW_YELLOW = new ResourceLocation(YunbeiUrbanConstruction.MOD_ID, "textures/block/lights/straight_arrow_yellow.png");
    private static final ResourceLocation STRAIGHT_ARROW_GREEN = new ResourceLocation(YunbeiUrbanConstruction.MOD_ID, "textures/block/lights/straight_arrow_green.png");
    private static final ResourceLocation RIGHT_TURN_RED = new ResourceLocation(YunbeiUrbanConstruction.MOD_ID, "textures/block/lights/right_turn_red.png");
    private static final ResourceLocation RIGHT_TURN_YELLOW = new ResourceLocation(YunbeiUrbanConstruction.MOD_ID, "textures/block/lights/right_turn_yellow.png");
    private static final ResourceLocation RIGHT_TURN_GREEN = new ResourceLocation(YunbeiUrbanConstruction.MOD_ID, "textures/block/lights/right_turn_green.png");
    private static final ResourceLocation TURN_AROUND_RED = new ResourceLocation(YunbeiUrbanConstruction.MOD_ID, "textures/block/lights/turn_around_red.png");
    private static final ResourceLocation TURN_AROUND_YELLOW = new ResourceLocation(YunbeiUrbanConstruction.MOD_ID, "textures/block/lights/turn_around_yellow.png");
    private static final ResourceLocation TURN_AROUND_GREEN = new ResourceLocation(YunbeiUrbanConstruction.MOD_ID, "textures/block/lights/turn_around_green.png");
    private static final ResourceLocation NON_MOTOR_VEHICLES_RED = new ResourceLocation(YunbeiUrbanConstruction.MOD_ID, "textures/block/lights/non_motor_vehicles_red.png");
    private static final ResourceLocation NON_MOTOR_VEHICLES_YELLOW = new ResourceLocation(YunbeiUrbanConstruction.MOD_ID, "textures/block/lights/non_motor_vehicles_yellow.png");
    private static final ResourceLocation NON_MOTOR_VEHICLES_GREEN = new ResourceLocation(YunbeiUrbanConstruction.MOD_ID, "textures/block/lights/non_motor_vehicles_green.png");
    private static final ResourceLocation NON_MOTOR_VEHICLES_LEFT_TURN_RED = new ResourceLocation(YunbeiUrbanConstruction.MOD_ID, "textures/block/lights/non_motor_vehicles_left_turn_red.png");
    private static final ResourceLocation NON_MOTOR_VEHICLES_LEFT_TURN_YELLOW = new ResourceLocation(YunbeiUrbanConstruction.MOD_ID, "textures/block/lights/non_motor_vehicles_left_turn_yellow.png");
    private static final ResourceLocation NON_MOTOR_VEHICLES_LEFT_TURN_GREEN = new ResourceLocation(YunbeiUrbanConstruction.MOD_ID, "textures/block/lights/non_motor_vehicles_left_turn_green.png");
    private static final ResourceLocation NON_MOTOR_VEHICLES_RIGHT_TURN_RED = new ResourceLocation(YunbeiUrbanConstruction.MOD_ID, "textures/block/lights/non_motor_vehicles_right_turn_red.png");
    private static final ResourceLocation NON_MOTOR_VEHICLES_RIGHT_TURN_YELLOW = new ResourceLocation(YunbeiUrbanConstruction.MOD_ID, "textures/block/lights/non_motor_vehicles_right_turn_yellow.png");
    private static final ResourceLocation NON_MOTOR_VEHICLES_RIGHT_TURN_GREEN = new ResourceLocation(YunbeiUrbanConstruction.MOD_ID, "textures/block/lights/non_motor_vehicles_right_turn_green.png");
    private static final ResourceLocation PAVEMENT_RED = new ResourceLocation(YunbeiUrbanConstruction.MOD_ID, "textures/block/lights/pavement_red.png");
    private static final ResourceLocation PAVEMENT_GREEN = new ResourceLocation(YunbeiUrbanConstruction.MOD_ID, "textures/block/lights/pavement_green.png");
    private static final ResourceLocation PAVEMENT_RED_TAIPEI = new ResourceLocation(YunbeiUrbanConstruction.MOD_ID, "textures/block/lights/pavement_red_taipei.png");
    private static final ResourceLocation[] PAVEMENT_GREEN_TAIPEI_FRAMES = {
            new ResourceLocation(YunbeiUrbanConstruction.MOD_ID, "textures/block/lights/pavement_green_taipei_1.png"),
            new ResourceLocation(YunbeiUrbanConstruction.MOD_ID, "textures/block/lights/pavement_green_taipei_2.png"),
            new ResourceLocation(YunbeiUrbanConstruction.MOD_ID, "textures/block/lights/pavement_green_taipei_3.png"),
            new ResourceLocation(YunbeiUrbanConstruction.MOD_ID, "textures/block/lights/pavement_green_taipei_4.png"),
            new ResourceLocation(YunbeiUrbanConstruction.MOD_ID, "textures/block/lights/pavement_green_taipei_5.png")
    };
    private static final ResourceLocation SLOW_RED = new ResourceLocation(YunbeiUrbanConstruction.MOD_ID, "textures/block/lights/slow_red.png");
    private static final ResourceLocation SLOW_YELLOW = new ResourceLocation(YunbeiUrbanConstruction.MOD_ID, "textures/block/lights/slow_yellow.png");
    private static final ResourceLocation SLOW_GREEN = new ResourceLocation(YunbeiUrbanConstruction.MOD_ID, "textures/block/lights/slow_green.png");

    @Override
    public void render(TrafficLightsBlockEntity entity, float tickDelta, PoseStack matrices, MultiBufferSource vertexConsumers, int light, int overlay) {
        TrafficLightsBlockEntity.DirectionType directiontype = entity.getDirectionType();

        Direction facing = entity.getBlockState().getValue(TrafficLightsBlock.FACING);
        TrafficLightsBlock.LightState type = entity.getBlockState().getValue(TrafficLightsBlock.LIGHT_STATE);
        TrafficLightsBlock.MountType mountType = entity.getBlockState().getValue(TrafficLightsBlock.TYPE);
        Block currentBlock = entity.getBlockState().getBlock();
        renderPhaseText(entity, matrices, vertexConsumers, light, currentBlock);
        if (currentBlock == MunicipalBlocks.TRAFFIC_LIGHTS_COUNTDOWN_TIMER.get()){
            renderText(entity, matrices, vertexConsumers, light, facing, type, mountType, currentBlock);
            return;
        } else if (currentBlock == MunicipalBlocks.TRAFFIC_LIGHTS_GRAY_SHANGHAI.get() ||
                currentBlock == MunicipalBlocks.TRAFFIC_LIGHTS_BLACK_SHANGHAI.get() ||
                currentBlock == MunicipalBlocks.TRAFFIC_LIGHTS_GREEN_TAIPEI.get()) {
            renderTimeText(entity, matrices, vertexConsumers, light, facing, type, mountType, currentBlock);
        }
        renderLogo(matrices, vertexConsumers, light, overlay, facing, directiontype, type, currentBlock, mountType);

        if (isPavementBlock(currentBlock) && entity.isShowSeconds()) {
            renderPavementSeconds(entity, matrices, vertexConsumers, light, facing, type, mountType, currentBlock);
        }
    }

    private void renderPavementSeconds(TrafficLightsBlockEntity entity, PoseStack matrices, MultiBufferSource vertexConsumers, int light, Direction facing, TrafficLightsBlock.LightState lightState, TrafficLightsBlock.MountType mountType, Block currentBlock) {
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
        if (currentBlock == MunicipalBlocks.TRAFFIC_LIGHTS_PAVEMENT_GREEN_TAIPEI.get()) logoY = (lightState == TrafficLightsBlock.LightState.RED || lightState == TrafficLightsBlock.LightState.YELLOW)
                    ? 3.3875f / 16f : -3.3875f / 16f;
        float y = -logoY;
        float z = getZOffset(mountType, currentBlock);

        matrices.pushPose();

        matrices.translate(0.5, 0.5, 0.5);
        matrices.mulPose(Vector3f.YP.rotationDegrees(-facing.toYRot()));
        float x = 0f;
        if (currentBlock == MunicipalBlocks.TRAFFIC_LIGHTS_PAVEMENT_GREEN_TAIPEI.get()) x = 5f / 16f;
        matrices.translate(0.2f + x, y, z);

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
            matrices.popPose();
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

        matrices.popPose();
    }

    private void renderLogo(PoseStack matrices, MultiBufferSource vertexConsumers, int light, int overlay, Direction facing, TrafficLightsBlockEntity.DirectionType directionType, TrafficLightsBlock.LightState lightState, Block currentBlock, TrafficLightsBlock.MountType mountType) {
        ResourceLocation texture;

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
        if (isPavementBlock(currentBlock)) {
            texture = switch (lightState) {
                case RED, YELLOW -> PAVEMENT_RED;
                case GREEN -> PAVEMENT_GREEN;
                default -> null;
            };
            if (currentBlock == MunicipalBlocks.TRAFFIC_LIGHTS_PAVEMENT_GREEN_TAIPEI.get()) {
                texture = switch (lightState) {
                    case RED, YELLOW -> PAVEMENT_RED_TAIPEI;
                    case GREEN -> getAnimatedGreenTaipeiFrame();
                    default -> null;
                };
            }
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

        matrices.pushPose();

        matrices.translate(0.5, 0.5, 0.5);
        matrices.mulPose(Vector3f.YP.rotationDegrees(-facing.toYRot()));

        float arrowSize = 0.4f;
        float halfSize = arrowSize / 2f;
        float x = 0;
        float y = 0;
        float z = 0;

        if (isPavementBlock(currentBlock)) {
            if (currentBlock == MunicipalBlocks.TRAFFIC_LIGHTS_PAVEMENT_GREEN_TAIPEI.get()) x = 5f / 16f;
            y = switch (lightState) {
                case RED, YELLOW -> 3.85f / 16f;
                case GREEN -> -3.85f / 16f;
                default -> 0f;
            };
            if (currentBlock == MunicipalBlocks.TRAFFIC_LIGHTS_PAVEMENT_GREEN_TAIPEI.get()) {
                y = switch (lightState) {
                    case RED, YELLOW -> 3.3875f / 16f;
                    case GREEN -> -3.3875f / 16f;
                    default -> 0f;
                };
            }
            z = getZOffset(mountType, currentBlock);
        } else if (currentBlock == MunicipalBlocks.TRAFFIC_LIGHTS_GRAY_SINGLE_HORIZONTAL.get() ||
                currentBlock == MunicipalBlocks.TRAFFIC_LIGHTS_BLACK_SINGLE_HORIZONTAL.get() ||
                currentBlock == MunicipalBlocks.TRAFFIC_LIGHTS_FOGGY.get()) {
            // 单灯横式（含雾灯）：图案在中心 x=0, y=0
            x = 0;
            y = 0;
            z = getZOffset(mountType, currentBlock);
        } else if (currentBlock == MunicipalBlocks.TRAFFIC_LIGHTS_GRAY_SINGLE_VERTICAL.get() ||
                currentBlock == MunicipalBlocks.TRAFFIC_LIGHTS_BLACK_SINGLE_VERTICAL.get()) {
            // 单灯竖式：图案在中心 x=0, y=0
            x = 0;
            y = 0;
            z = getZOffset(mountType, currentBlock);
        } else if (currentBlock == MunicipalBlocks.TRAFFIC_LIGHTS_GRAY_HORIZONTAL.get() ||
                currentBlock == MunicipalBlocks.TRAFFIC_LIGHTS_BLACK_HORIZONTAL.get()) {
            x = switch (lightState) {
                case RED -> -7.75f / 16f;
                case GREEN -> 7.75f / 16f;
                default -> 0f;
            };
            z = getZOffset(mountType, currentBlock);
        } else if (currentBlock == MunicipalBlocks.TRAFFIC_LIGHTS_GREEN_TAIPEI.get()) {
            x = switch (lightState) {
                case RED -> -3.9f / 16f;
                case GREEN -> 11.6f / 16f;
                default -> 3.85f / 16f;
            };
            z = getZOffset(mountType, currentBlock);
        } else if (currentBlock == MunicipalBlocks.TRAFFIC_LIGHTS_GRAY_VERTICAL.get() ||
                currentBlock == MunicipalBlocks.TRAFFIC_LIGHTS_BLACK_VERTICAL.get() ||
                currentBlock == MunicipalBlocks.TRAFFIC_LIGHTS_YELLOW_VERTICAL.get() ||
                currentBlock == MunicipalBlocks.TRAFFIC_LIGHTS_GRAY_SHANGHAI.get() ||
                currentBlock == MunicipalBlocks.TRAFFIC_LIGHTS_BLACK_SHANGHAI.get()) {
            y = switch (lightState) {
                case RED -> 7.75f / 16f;
                case GREEN -> -7.75f / 16f;
                default -> 0f;
            };
            z = getZOffset(mountType, currentBlock);
        }

        matrices.translate(x, y, z);

        VertexConsumer consumer = vertexConsumers.getBuffer(RenderType.entityCutout(texture));
        Matrix4f positionMatrix = matrices.last().pose();

        consumer.vertex(positionMatrix, -halfSize, -halfSize, 0).color(255, 255, 255, 255).uv(0.0f, 1.0f).overlayCoords(overlay).uv2(light).normal(matrices.last().normal(), 0, 0, 1).endVertex();
        consumer.vertex(positionMatrix, halfSize, -halfSize, 0).color(255, 255, 255, 255).uv(1.0f, 1.0f).overlayCoords(overlay).uv2(light).normal(matrices.last().normal(), 0, 0, 1).endVertex();
        consumer.vertex(positionMatrix, halfSize, halfSize, 0).color(255, 255, 255, 255).uv(1.0f, 0.0f).overlayCoords(overlay).uv2(light).normal(matrices.last().normal(), 0, 0, 1).endVertex();
        consumer.vertex(positionMatrix, -halfSize, halfSize, 0).color(255, 255, 255, 255).uv(0.0f, 0.0f).overlayCoords(overlay).uv2(light).normal(matrices.last().normal(), 0, 0, 1).endVertex();

        matrices.popPose();
    }

    private void renderPhaseText(TrafficLightsBlockEntity entity, PoseStack matrices, MultiBufferSource vertexConsumers, int light, Block currentBlock) {
        Minecraft client = Minecraft.getInstance();
        Player player = client.player;

        if (player == null || !isHoldingWand(player)) {
            return;
        }

        if (!entity.hasTimings() || !entity.isInGroup()) {
            return;
        }

        matrices.pushPose();

        matrices.translate(0.5, 2, 0.5);

        matrices.mulPose(client.getEntityRenderDispatcher().cameraOrientation());

        float scale = 0.025f;
        matrices.scale(-scale, -scale, scale);

        String directionText = getDirectionText(entity.getDirectionType());
        if (isPavementBlock(currentBlock)) directionText = "人行道";
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

        int directionWidth = textRenderer.width(directionText);
        int phaseWidth = textRenderer.width(phaseText);
        int maxWidth = Math.max(directionWidth, phaseWidth);

        int lineHeight = textRenderer.lineHeight + 2;
        int totalHeight = lineHeight * 2;

        float padding = 4;
        float bgX1 = -maxWidth / 2f - padding;
        float bgY1 = -padding;
        float bgX2 = maxWidth / 2f + padding;
        float bgY2 = totalHeight + padding;

        Matrix4f matrix = matrices.last().pose();
        // 1.18.2 Font supplies the text background through drawInBatch.

        textRenderer.drawInBatch(
                new TextComponent(directionText),
                -directionWidth / 2f,
                0,
                0xFFFFFF,
                false,
                matrices.last().pose(),
                vertexConsumers,
                false,
                0x80000000,
                light
        );

        textRenderer.drawInBatch(
                new TextComponent(phaseText),
                -phaseWidth / 2f,
                lineHeight,
                0xFFFFFF,
                false,
                matrices.last().pose(),
                vertexConsumers,
                false,
                0x80000000,
                light
        );

        matrices.popPose();
    }

    private void renderText(TrafficLightsBlockEntity entity, PoseStack matrices, MultiBufferSource vertexConsumers, int light, Direction facing, TrafficLightsBlock.LightState lightState, TrafficLightsBlock.MountType mountType, Block currentBlock) {
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

        matrices.pushPose();

        matrices.translate(0.5, 0.5, 0.5);
        matrices.mulPose(Vector3f.YP.rotationDegrees(-facing.toYRot()));

        float scaleValue = 0.085f;
        matrices.translate(0.5f, 0.0f, getZOffset(mountType, currentBlock));
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
            matrices.popPose();
            return;
        }

        // 相位组中：判断是否显示实际数字
        if (entity.isInGroup()) {
            int displayMode = entity.getCountdownDisplayMode();
            int threshold = entity.getCountdownThreshold();
            boolean shouldShowDigits = (displayMode == 0) || (displayMode == 1 && remaining <= threshold);
            if (!shouldShowDigits) {
                matrices.popPose();
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

        matrices.popPose();
    }

    private void renderTimeText(TrafficLightsBlockEntity entity, PoseStack matrices, MultiBufferSource vertexConsumers, int light, Direction facing, TrafficLightsBlock.LightState lightState, TrafficLightsBlock.MountType mountType, Block currentBlock) {
        int remaining;
        boolean showSeconds = entity.isShowSeconds();
        boolean isTaipei = currentBlock == MunicipalBlocks.TRAFFIC_LIGHTS_GREEN_TAIPEI.get();

        // 上海红绿灯黄灯时始终隐藏读秒（台北式黄灯显示剩余秒数）
        if (lightState == TrafficLightsBlock.LightState.YELLOW && !isTaipei) {
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
        // 黄灯颜色仅台北式生效（上海式黄灯直接隐藏读秒）
        int color = switch (lightState) {
            case RED -> 0xFF0000;
            case YELLOW -> 0xFFF000;
            case GREEN -> 0x39FF00;
            default -> 0xFFFFFF;
        };

        matrices.pushPose();

        matrices.translate(0.5, 0.5, 0.5);
        matrices.mulPose(Vector3f.YP.rotationDegrees(-facing.toYRot()));

        float scaleValue = 0.085f;
        // 上海式：读秒在右侧；台北式：读秒在最左侧红灯的左边，留出间隔
        float textX = isTaipei ? -11.65f / 16f + 0.25f : 0.25f;
        // 读秒与灯面共用 getZOffset 值；读秒为双层渲染（背景88+数字），整体加偏移浮出灯面
        matrices.translate(textX, 0.0f, getZOffset(mountType, currentBlock) + 0.02f);
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
            matrices.popPose();
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

        matrices.popPose();
    }

    @Override
    public boolean shouldRenderOffScreen(TrafficLightsBlockEntity blockEntity) {
        return true;
    }

    private boolean isHoldingWand(Player player) {
        return player.getMainHandItem().is(ModItems.WAND.get()) ||
                player.getOffhandItem().is(ModItems.WAND.get());
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

    private ResourceLocation getAnimatedGreenTaipeiFrame() {
        // 动画循环：1234554321，总长10帧，每帧0.1s，总周期1s
        // cyclePosition: 0,1,2,3,4,5,6,7,8,9
        // frameIndex:    0,1,2,3,4,4,3,2,1,0
        long currentTimeMillis = System.currentTimeMillis();
        int cyclePosition = (int) ((currentTimeMillis / 100) % 10);

        int frameIndex;
        if (cyclePosition <= 4) {
            frameIndex = cyclePosition;
        } else {
            frameIndex = 9 - cyclePosition;
        }

        return PAVEMENT_GREEN_TAIPEI_FRAMES[frameIndex];
    }

    @Override
    public int getViewDistance() {
        return 256;
    }
}
