package com.beigu.yunbeiuc.block.custom.sign;

import net.minecraft.block.BlockState;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.util.StringIdentifiable;

public abstract class AbstractEditableSignBlockReflective extends AbstractEditableSignBlockWithTooltip {
    public AbstractEditableSignBlockReflective(Settings settings, String tooltipKey) {
        super(settings, tooltipKey);
    }

    @Override
    protected Type determineType(BlockState behindState) {
        for (net.minecraft.state.property.Property<?> property : behindState.getProperties()) {
            if (property.getName().equals("type") && property instanceof EnumProperty) {
                Comparable<?> value = behindState.get(property);
                if (value instanceof StringIdentifiable) {
                    String typeName = ((StringIdentifiable) value).asString();
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
