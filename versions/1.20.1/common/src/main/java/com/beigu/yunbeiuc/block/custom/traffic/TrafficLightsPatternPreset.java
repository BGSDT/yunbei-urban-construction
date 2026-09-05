package com.beigu.yunbeiuc.block.custom.traffic;

import com.beigu.yunbeiuc.block.MunicipalBlocks;
import com.beigu.yunbeiuc.entity.TrafficLightsBlockEntity;
import net.minecraft.block.Block;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * 数据驱动的相位分配预设：由一组"槽位"描述整个红绿灯组的分配方案。
 * 每个槽位 = 相对组几何中心的某个方位上的某个成员类型（普通灯/人行道灯/读秒器），
 * 以及该成员应使用的图案（DirectionType）与相位索引列表。
 *
 * 应用时把组内全部成员按几何位置与类型匹配到槽位上，
 * 必须一一对应（数量完全一致，即"完美嵌入"）才会真正写入。
 */
public class TrafficLightsPatternPreset {

    private String name = "";
    private int phaseCount = 4;
    private List<Slot> slots = new ArrayList<>();
    /**
     * 预设在列表中显示的"logo颜色"（不含 alpha 的 RGB）。
     * -1 表示未显式设置，此时 {@link #getDisplayColor()} 会按名称哈希派生一个
     * 固定的伪随机颜色，保证同一预设每次显示颜色一致（不会每帧变化）。
     */
    private int color = -1;
    /**
     * 预设所属的二级菜单（分类）名称。默认归入"默认分类"
     * （{@link com.beigu.yunbeiuc.util.TrafficLightsPatternCategoryManager#DEFAULT_CATEGORY}）。
     * 资源包内置预设由 {@link com.beigu.yunbeiuc.util.TrafficLightsPatternPresetLoader} 强制归入
     * "模组资源包"分类，忽略此字段的任何设置。
     */
    private String category = com.beigu.yunbeiuc.util.TrafficLightsPatternCategoryManager.DEFAULT_CATEGORY;

    public TrafficLightsPatternPreset() {
    }

    public TrafficLightsPatternPreset(String name, int phaseCount, List<Slot> slots) {
        this.name = name;
        this.phaseCount = phaseCount;
        this.slots = slots;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPhaseCount() {
        return phaseCount;
    }

    public void setPhaseCount(int phaseCount) {
        this.phaseCount = phaseCount;
    }

    public List<Slot> getSlots() {
        return slots;
    }

    public void setSlots(List<Slot> slots) {
        this.slots = slots;
    }

    /**
     * 获取显式设置的颜色，-1 表示未设置。
     */
    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    /**
     * 用于列表展示的实际颜色：已显式设置则直接返回，否则按名称哈希派生一个
     * 固定的伪随机颜色（同名预设每次结果一致，避免列表渲染时颜色闪烁）。
     */
    public int getDisplayColor() {
        if (color != -1) {
            return color & 0xFFFFFF;
        }
        return hashColor(name);
    }

    /**
     * 按任意字符串哈希派生一个固定的伪随机颜色，供编辑器在"随机"状态下预览用。
     */
    public static int hashColor(String seed) {
        int hash = seed.hashCode();
        float hue = ((hash & 0x7FFFFFFF) % 360) / 360f;
        return java.awt.Color.HSBtoRGB(hue, 0.55f, 0.9f) & 0xFFFFFF;
    }

    /**
     * 槽位：相对组中心的方位 + 成员类型 + 图案 + 相位索引列表。
     */
    public static class Slot {
        private Direction8 direction = Direction8.N;
        private MemberKind kind = MemberKind.NORMAL;
        private String directionType = TrafficLightsBlockEntity.DirectionType.STRAIGHT_CIRCLE.getName();
        private List<Integer> phaseIndices = new ArrayList<>(List.of(0));
        /**
         * 同方位+同类型桶内的排序号（从 1 开始）。
         * 桶内有多个槽位时，用它显式指定该槽位对应实际组内按坐标排序后的第几个成员，
         * 避免只靠隐式几何排序导致作者无法预知匹配结果。
         */
        private int order = 1;

        public Slot() {
        }

        public Slot(Direction8 direction, MemberKind kind, TrafficLightsBlockEntity.DirectionType directionType, List<Integer> phaseIndices) {
            this(direction, kind, directionType, phaseIndices, 1);
        }

        public Slot(Direction8 direction, MemberKind kind, TrafficLightsBlockEntity.DirectionType directionType, List<Integer> phaseIndices, int order) {
            this.direction = direction;
            this.kind = kind;
            this.directionType = directionType.getName();
            this.phaseIndices = new ArrayList<>(phaseIndices);
            this.order = order;
        }

        public Direction8 getDirection() {
            return direction;
        }

        public void setDirection(Direction8 direction) {
            this.direction = direction;
        }

        public MemberKind getKind() {
            return kind;
        }

        public void setKind(MemberKind kind) {
            this.kind = kind;
        }

        public TrafficLightsBlockEntity.DirectionType getDirectionType() {
            return TrafficLightsBlockEntity.DirectionType.fromName(directionType);
        }

        public void setDirectionType(TrafficLightsBlockEntity.DirectionType directionType) {
            this.directionType = directionType.getName();
        }

        public List<Integer> getPhaseIndices() {
            return phaseIndices;
        }

        public void setPhaseIndices(List<Integer> phaseIndices) {
            this.phaseIndices = new ArrayList<>(phaseIndices);
        }

        public int getOrder() {
            return order;
        }

        public void setOrder(int order) {
            this.order = order;
        }
    }

    /**
     * 相对组几何中心的 8 方位（Minecraft 坐标：+X=东，+Z=南，-X=西，-Z=北）。
     */
    public enum Direction8 {
        N("北", 0, -1), E("东", 1, 0), S("南", 0, 1), W("西", -1, 0);

        private final String label;
        // 由组质心指向该方位的朝外向量（只用符号，不需要归一化）
        private final double outX;
        private final double outZ;

        Direction8(String label, double outX, double outZ) {
            this.label = label;
            this.outX = outX;
            this.outZ = outZ;
        }

        public String getLabel() {
            return label;
        }

        public double getOutX() {
            return outX;
        }

        public double getOutZ() {
            return outZ;
        }

        /**
         * 按相对质心的偏移量分类到最近的四个主方位。
         */
        public static Direction8 classify(double dx, double dz) {
            double ax = Math.abs(dx);
            double az = Math.abs(dz);
            if (ax > az) {
                return dx > 0 ? E : W;
            } else {
                return dz > 0 ? S : N;
            }
        }

        /**
         * "从左到右"排序键：以站在组中心朝该方位看过去的视角为基准，
         * 左手侧数值小，右手侧数值大（右向量由朝外向量顺时针旋转 90° 得到）。
         */
        public double leftToRightKey(double relX, double relZ) {
            double rightX = -outZ;
            double rightZ = outX;
            return relX * rightX + relZ * rightZ;
        }
    }

    /**
     * 组内成员类型。SHANGHAI 变体归入普通红绿灯。
     */
    public enum MemberKind {
        NORMAL("普通红绿灯"), PAVEMENT("人行道红绿灯"), COUNTDOWN_TIMER("读秒器");

        private final String label;

        MemberKind(String label) {
            this.label = label;
        }

        public String getLabel() {
            return label;
        }

        public static MemberKind fromBlock(Block block) {
            if (block == MunicipalBlocks.TRAFFIC_LIGHTS_GRAY_VERTICAL.get()
                    || block == MunicipalBlocks.TRAFFIC_LIGHTS_BLACK_VERTICAL.get()
                    || block == MunicipalBlocks.TRAFFIC_LIGHTS_GRAY_HORIZONTAL.get()
                    || block == MunicipalBlocks.TRAFFIC_LIGHTS_BLACK_HORIZONTAL.get()
                    || block == MunicipalBlocks.TRAFFIC_LIGHTS_GRAY_SHANGHAI.get()
                    || block == MunicipalBlocks.TRAFFIC_LIGHTS_BLACK_SHANGHAI.get()) {
                return NORMAL;
            }
            if (block == MunicipalBlocks.TRAFFIC_LIGHTS_PAVEMENT_GRAY.get()
                    || block == MunicipalBlocks.TRAFFIC_LIGHTS_PAVEMENT_BLACK.get()) {
                return PAVEMENT;
            }
            if (block == MunicipalBlocks.TRAFFIC_LIGHTS_COUNTDOWN_TIMER.get()) {
                return COUNTDOWN_TIMER;
            }
            return null;
        }
    }

    private static class Member {
        final TrafficLightsBlockEntity entity;
        final BlockPos pos;

        Member(TrafficLightsBlockEntity entity, BlockPos pos) {
            this.entity = entity;
            this.pos = pos;
        }
    }

    /**
     * 尝试把预设应用到整个链接组。
     * 先做"完美嵌入"校验：组内每个成员必须能唯一对应到一个槽位，
     * 且每个槽位都必须有成员填充。同方位同类型有多个成员时，按"从左到右"
     * （面朝该方位站立时的左右手方向）排序后与槽位的 order（第几个）顺序对应，
     * 让预设作者能在编辑器里明确指定对应关系，而不是依赖隐式几何排序。
     * 校验全部通过才写入，否则不做任何修改并提示原因。
     *
     * @return true 表示应用成功
     */
    public boolean tryApplyToGroup(World world, List<TrafficLightsBlockEntity> members, List<BlockPos> positions, @Nullable PlayerEntity notifyPlayer) {
        if (members.isEmpty() || members.size() != positions.size()) {
            send(notifyPlayer, "§c链接组成员数据无效！");
            return false;
        }

        // 组相位数校验：预设用到的相位索引必须在组的相位范围内
        int groupPhaseCount = members.get(0).getPhaseCount();
        if (groupPhaseCount <= 0) {
            send(notifyPlayer, "§c该组还没有设置时间表，请先设置时间表！");
            return false;
        }
        for (Slot slot : slots) {
            for (int index : slot.phaseIndices) {
                if (index < 0 || index >= groupPhaseCount) {
                    send(notifyPlayer, "§c预设中存在相位 " + (index + 1) + "，但当前组只有 " + groupPhaseCount + " 个相位，无法应用！");
                    return false;
                }
            }
        }

        // 成员类型归类，未知方块直接失败
        List<Member> memberList = new ArrayList<>();
        for (int i = 0; i < members.size(); i++) {
            MemberKind kind = MemberKind.fromBlock(members.get(i).getCachedState().getBlock());
            if (kind == null) {
                send(notifyPlayer, "§c组内包含预设无法识别的红绿灯类型，无法应用！");
                return false;
            }
            memberList.add(new Member(members.get(i), positions.get(i)));
        }

        // 质心
        double centroidX = 0, centroidZ = 0;
        for (BlockPos pos : positions) {
            centroidX += pos.getX();
            centroidZ += pos.getZ();
        }
        centroidX /= positions.size();
        centroidZ /= positions.size();

        // 预设槽位按 方位 -> 类型 分桶，桶内按 order（第几个）排序，
        // 与旋转无关，只需构建一次
        Map<Direction8, Map<MemberKind, List<Slot>>> slotBuckets = new EnumMap<>(Direction8.class);
        for (Slot slot : slots) {
            slotBuckets.computeIfAbsent(slot.direction, d -> new EnumMap<>(MemberKind.class))
                    .computeIfAbsent(slot.kind, k -> new ArrayList<>())
                    .add(slot);
        }
        for (Map<MemberKind, List<Slot>> byKind : slotBuckets.values()) {
            for (List<Slot> list : byKind.values()) {
                list.sort(Comparator.comparingInt(s -> s.order));
                if (list.size() > 1) {
                    for (int i = 1; i < list.size(); i++) {
                        if (list.get(i).order == list.get(i - 1).order) {
                            send(notifyPlayer, "§c预设中" + list.get(i).direction.label + "方" + list.get(i).kind.label
                                    + "存在重复的\"第几个\"序号 " + list.get(i).order + "，请在编辑器中修正！");
                            return false;
                        }
                    }
                }
            }
        }

        // 依次尝试组的 4 种朝向（0°/90°/180°/270°），只要有一种能与预设"完美嵌入"就采用它，
        // 这样预设不必严格要求成员摆放的绝对朝向与编辑时完全一致（例如编辑时是北/东/西，
        // 实际链接组整体旋转 90° 后变成东/南/北，也能识别出契合并应用）。
        MatchResult fallback = null;
        for (int rotation = 0; rotation < 4; rotation++) {
            MatchResult result = matchWithRotation(memberList, centroidX, centroidZ, slotBuckets, rotation);
            if (rotation == 0) {
                fallback = result;
            }
            if (result.success) {
                for (int i = 0; i < result.targets.size(); i++) {
                    result.targets.get(i).setDirectionType(result.targetTypes.get(i));
                    result.targets.get(i).setPhaseIndices(result.targetPhases.get(i), null);
                }
                if (notifyPlayer != null && !world.isClient()) {
                    send(notifyPlayer, "§a已将预设 §6" + name + " §a应用到链接组，共 " + result.targets.size() + " 个红绿灯");
                }
                return true;
            }
        }

        send(notifyPlayer, "§c预设无法完美嵌入当前链接组：");
        for (int i = 0; i < fallback.errors.size() && i < 5; i++) {
            send(notifyPlayer, "§7- §e" + fallback.errors.get(i));
        }
        if (fallback.errors.size() > 5) {
            send(notifyPlayer, "§7- §e……共 " + fallback.errors.size() + " 处不匹配");
        }
        return false;
    }

    /**
     * 按给定的旋转步数（每步 90°，沿 N->E->S->W 方向循环）尝试匹配实际成员与预设槽位。
     * 只做计算与校验，不写入任何数据；成功时把待写入的目标一并算好交给调用方统一应用。
     */
    private MatchResult matchWithRotation(List<Member> memberList, double centroidX, double centroidZ,
                                           Map<Direction8, Map<MemberKind, List<Slot>>> slotBuckets, int rotation) {
        Map<Direction8, Map<MemberKind, List<Member>>> memberBuckets = new EnumMap<>(Direction8.class);
        Map<Direction8, Direction8> actualDirOf = new EnumMap<>(Direction8.class);
        for (Member m : memberList) {
            Direction8 actualDir = Direction8.classify(m.pos.getX() - centroidX, m.pos.getZ() - centroidZ);
            Direction8 key = rotate(actualDir, rotation);
            actualDirOf.put(key, actualDir);
            memberBuckets.computeIfAbsent(key, d -> new EnumMap<>(MemberKind.class))
                    .computeIfAbsent(kindOf(m), k -> new ArrayList<>())
                    .add(m);
        }
        for (Map.Entry<Direction8, Map<MemberKind, List<Member>>> dirEntry : memberBuckets.entrySet()) {
            Direction8 actualDir = actualDirOf.get(dirEntry.getKey());
            for (List<Member> list : dirEntry.getValue().values()) {
                list.sort(Comparator
                        .comparingDouble((Member m) -> actualDir.leftToRightKey(m.pos.getX() - centroidX, m.pos.getZ() - centroidZ))
                        .thenComparingInt(m -> m.pos.getX())
                        .thenComparingInt(m -> m.pos.getZ()));
            }
        }

        // "完美嵌入"校验：键的并集内每个桶的数量必须完全一致
        List<String> errors = new ArrayList<>();
        java.util.Set<Direction8> allDirs = new java.util.LinkedHashSet<>();
        allDirs.addAll(memberBuckets.keySet());
        allDirs.addAll(slotBuckets.keySet());
        for (Direction8 dir : allDirs) {
            Map<MemberKind, List<Member>> mb = memberBuckets.getOrDefault(dir, new EnumMap<>(MemberKind.class));
            Map<MemberKind, List<Slot>> sb = slotBuckets.getOrDefault(dir, new EnumMap<>(MemberKind.class));
            java.util.Set<MemberKind> allKinds = new java.util.LinkedHashSet<>();
            allKinds.addAll(mb.keySet());
            allKinds.addAll(sb.keySet());
            for (MemberKind kind : allKinds) {
                int actual = mb.getOrDefault(kind, new ArrayList<>()).size();
                int wanted = sb.getOrDefault(kind, new ArrayList<>()).size();
                if (actual > wanted) {
                    errors.add(dir.label + "方" + kind.label + "多出 " + (actual - wanted) + " 个");
                } else if (actual < wanted) {
                    errors.add(dir.label + "方缺少 " + (wanted - actual) + " 个" + kind.label);
                }
            }
        }

        MatchResult result = new MatchResult();
        if (!errors.isEmpty()) {
            result.success = false;
            result.errors = errors;
            return result;
        }

        List<TrafficLightsBlockEntity> targets = new ArrayList<>();
        List<TrafficLightsBlockEntity.DirectionType> targetTypes = new ArrayList<>();
        List<List<Integer>> targetPhases = new ArrayList<>();
        for (Map.Entry<Direction8, Map<MemberKind, List<Slot>>> dirEntry : slotBuckets.entrySet()) {
            Map<MemberKind, List<Member>> mb = memberBuckets.get(dirEntry.getKey());
            for (Map.Entry<MemberKind, List<Slot>> kindEntry : dirEntry.getValue().entrySet()) {
                List<Member> actual = mb.get(kindEntry.getKey());
                List<Slot> wanted = kindEntry.getValue();
                for (int i = 0; i < wanted.size(); i++) {
                    Slot slot = wanted.get(i);
                    Member member = actual.get(i);
                    targets.add(member.entity);
                    targetTypes.add(slot.getDirectionType());
                    targetPhases.add(new ArrayList<>(slot.phaseIndices));
                }
            }
        }

        result.success = true;
        result.targets = targets;
        result.targetTypes = targetTypes;
        result.targetPhases = targetPhases;
        return result;
    }

    /**
     * 把方位沿 N->E->S->W->N 顺序旋转 steps 个 90° 步长（Direction8 的枚举顺序恰好是这个循环顺序）。
     */
    private static Direction8 rotate(Direction8 dir, int steps) {
        Direction8[] values = Direction8.values();
        return values[(dir.ordinal() + steps) % values.length];
    }

    private static MemberKind kindOf(Member m) {
        return MemberKind.fromBlock(m.entity.getCachedState().getBlock());
    }

    private static class MatchResult {
        boolean success;
        List<String> errors;
        List<TrafficLightsBlockEntity> targets;
        List<TrafficLightsBlockEntity.DirectionType> targetTypes;
        List<List<Integer>> targetPhases;
    }

    private static void send(@Nullable PlayerEntity player, String message) {
        if (player != null && !player.getWorld().isClient()) {
            player.sendMessage(Text.literal(message), false);
        }
    }
}
