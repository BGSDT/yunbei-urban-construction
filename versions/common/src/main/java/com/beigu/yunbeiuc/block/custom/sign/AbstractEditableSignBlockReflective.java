package com.beigu.yunbeiuc.block.custom.sign;

import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Property;

public abstract class AbstractEditableSignBlockReflective extends AbstractEditableSignBlockWithTooltip {
    public AbstractEditableSignBlockReflective(Properties properties, String tooltipKey) {
        super(properties, tooltipKey);
    }

    @Override
    protected Type determineType(BlockState behindState) {
        for (Property<?> property : behindState.getProperties()) {
            if (property.getName().equals("type") && property instanceof EnumProperty) {
                Comparable<?> value = behindState.getValue(property);
                if (value instanceof StringRepresentable) {
                    String typeName = ((StringRepresentable) value).getSerializedName();
                    return switch (typeName) {
                        case "pole_l" -> Type.POLE_L;
                        case "pole_h" -> Type.POLE_H;
                        case "normal" -> Type.NORMAL;
                        default -> Type.NORMAL;
                    };
                }
            }
        }
        return Type.NORMAL;
    }
}
