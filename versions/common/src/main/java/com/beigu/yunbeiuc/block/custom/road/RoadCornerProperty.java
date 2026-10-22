package com.beigu.yunbeiuc.block.custom.road;

import com.google.common.collect.ImmutableList;
import net.minecraft.world.level.block.state.properties.Property;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

public class RoadCornerProperty extends Property<RoadCorner> {
    private static final Map<String, RoadCorner> BY_NAME = new HashMap<>();

    static {
        for (RoadCorner corner : RoadCorner.values()) {
            BY_NAME.put(corner.name().toLowerCase(Locale.ROOT), corner);
        }
    }

    private final ImmutableList<RoadCorner> values = ImmutableList.copyOf(Arrays.asList(RoadCorner.values()));

    public RoadCornerProperty(String name) {
        super(name, RoadCorner.class);
    }

    @Override
    public Collection<RoadCorner> getPossibleValues() {
        return values;
    }

    @Override
    public String getName(RoadCorner corner) {
        return corner.name().toLowerCase(Locale.ROOT);
    }

    @Override
    public Optional<RoadCorner> getValue(String name) {
        return Optional.ofNullable(BY_NAME.get(name));
    }
}
