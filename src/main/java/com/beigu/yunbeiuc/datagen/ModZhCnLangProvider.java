package com.beigu.yunbeiuc.datagen;

import com.beigu.yunbeiuc.block.ModBlocks;
import com.beigu.yunbeiuc.item.ModItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;

public class ModZhCnLangProvider extends FabricLanguageProvider {
    public ModZhCnLangProvider(FabricDataOutput dataOutput) {
        super(dataOutput,"zh_cn");
    }

    @Override
    public void generateTranslations(TranslationBuilder translationBuilder) {
        translationBuilder.add(ModItems.WAND,"魔杖");
        translationBuilder.add(ModItems.TREE_WAND,"树木魔杖");
        translationBuilder.add("item.yunbeiuc.tree_wand.tooltip","右键点击草方块或泥土方块以种植一棵橡树");
        translationBuilder.add("item.yunbeiuc.tree_wand.success","成功种植了一棵橡树!");
        translationBuilder.add("item.yunbeiuc.tree_wand.planted_sapling","种植了一棵橡树苗!");
        translationBuilder.add("item.yunbeiuc.tree_wand.failed","无法在此处生成橡树!");
        translationBuilder.add("item.yunbeiuc.tree_wand.no_space","没有足够的空间种植树木!");
        translationBuilder.add("item.yunbeiuc.tree_wand.invalid_block","无法在这个方块上种植树木!");
        translationBuilder.add(ModItems.WATER_WAND,"水源魔杖");
        translationBuilder.add("item.yunbeiuc.water_wand.tooltip","右键点击3*3*3范围内替换为水源");
        translationBuilder.add("item.yunbeiuc.water_wand.success","成功替换成水源!");
        translationBuilder.add(ModItems.ROTATED_WAND,"旋转魔杖");
        translationBuilder.add("item.yunbeiuc.rotated_wand.tooltip","右键点击方块以顺时针旋转90度");

        // 道路标识/标线类
        translationBuilder.add(ModBlocks.LEFT_TURN_GROUND_MARK, "左转地面标识");
        translationBuilder.add(ModBlocks.STRAIGHT_GROUND_MARK, "直行地面标识");
        translationBuilder.add(ModBlocks.RIGHT_TURN_GROUND_MARK, "右转地面标识");
        translationBuilder.add(ModBlocks.LEFT_TURN_AROUND_GROUND_MARK, "左转和掉头地面标识");
        translationBuilder.add(ModBlocks.RIGHT_TURN_AROUND_GROUND_MARK, "右转和掉头地面标识");
        translationBuilder.add(ModBlocks.STRAIGHT_LEFT_TURN_GROUND_MARK, "直行左转地面标识");
        translationBuilder.add(ModBlocks.STRAIGHT_RIGHT_TURN_GROUND_MARK, "直行右转地面标识");
        translationBuilder.add(ModBlocks.STRAIGHT_LEFT_RIGHT_TURN_GROUND_MARK, "直行左右转地面标识");
        translationBuilder.add(ModBlocks.LEFT_TURN_MERGE_GROUND_MARK, "左转汇入地面标识");
        translationBuilder.add(ModBlocks.RIGHT_TURN_MERGE_GROUND_MARK, "右转汇入地面标识");
        translationBuilder.add(ModBlocks.LEFT_TURN_AROUND_SINGLE_GROUND_MARK, "左转掉头地面标识");
        translationBuilder.add(ModBlocks.RIGHT_TURN_AROUND_SINGLE_GROUND_MARK, "右转掉头地面标识");
        translationBuilder.add(ModBlocks.SLOWDOWN_ANNOUNCEMENT_GROUND_MARK, "减速预告地面标识");
        translationBuilder.add(ModBlocks.SLOWDOWN_YIELD_GROUND_MARK, "减速避让地面标识");
        translationBuilder.add(ModBlocks.MANHOLE_COVER, "井盖");

// 交通信号灯类
        translationBuilder.add(ModBlocks.TRAFFIC_LIGHTS_STRAIGHT, "直行交通信号灯");
        translationBuilder.add(ModBlocks.TRAFFIC_LIGHTS_LEFT, "左转交通信号灯");
        translationBuilder.add(ModBlocks.TRAFFIC_LIGHTS_PAVEMENT, "人行道交通信号灯");
        translationBuilder.add("block.yunbeiuc.traffic_lights.tooltip","手持魔杖右键点击以切换交通信号灯的状态");

// 道路设施类
        translationBuilder.add(ModBlocks.ROAD_FLOWER_BOX_1, "道路花箱1型");
        translationBuilder.add(ModBlocks.ROAD_FLOWER_BOX_2, "道路花箱2型");
        translationBuilder.add(ModBlocks.ROAD_FLOWER_BOX_2_FENCE, "道路花箱2型围栏");

// 道路监控设备类
        translationBuilder.add(ModBlocks.ROAD_DETECTION_CAMERA, "道路检测/测速监控摄像头");
        translationBuilder.add(ModBlocks.ROAD_LIGHTING_LAMP, "道路补光灯");
        translationBuilder.add("block.yunbeiuc.roadway_lighting_lamp.tooltip","手持魔杖右键点击以切换灯光状态");
        translationBuilder.add(ModBlocks.ROAD_RADAR_SPEED_DETECTOR, "雷达测速器");
        translationBuilder.add("block.yunbeiuc.radar_speed_detector.tooltip","手持魔杖右键点击以切换灯光状态");

// 道路安全设施类
        translationBuilder.add(ModBlocks.TRAFFIC_CONE, "交通锥");
        translationBuilder.add(ModBlocks.ROAD_COLLISION_BARREL, "道路防撞桶");
        translationBuilder.add(ModBlocks.WATER_SAFETY_BARRIER_RED, "红色水上安全护栏");

// 环卫设施类
        translationBuilder.add(ModBlocks.RUBBISH_BIN_WHITE, "白色垃圾桶");
        translationBuilder.add(ModBlocks.RUBBISH_BIN_GRAY_GREEN, "灰绿色垃圾桶");
        translationBuilder.add("block.yunbeiuc.rubbish_bin.tooltip","手持魔杖右键清空垃圾桶");

// 道路标线/障碍类
        translationBuilder.add(ModBlocks.SPEED_BUMP, "减速带");
        translationBuilder.add(ModBlocks.VIBRATION_MARKING_LINE, "振动标线");
        translationBuilder.add(ModBlocks.PARKING_SPACE_BARRIER, "车位地锁");

// 龙门架类
        translationBuilder.add(ModBlocks.GANTRY_FRAME_SIDE, "龙门架侧架");
        translationBuilder.add(ModBlocks.GANTRY_FRAME_CONNECTION, "龙门架连接件");
        translationBuilder.add(ModBlocks.GANTRY_FRAME_MAIN, "龙门架主架");
        translationBuilder.add(ModBlocks.GANTRY_FRAME_RAILING, "龙门架栏杆");
        translationBuilder.add(ModBlocks.GANTRY_FRAME_LADDER, "龙门架爬梯");
        translationBuilder.add(ModBlocks.GANTRY_FRAME_LED_SIDE, "龙门架侧装LED屏");
        translationBuilder.add(ModBlocks.GANTRY_FRAME_LED_MAIN, "龙门架主装LED屏");

// 防眩设施类
        translationBuilder.add(ModBlocks.ANTI_GLARE_NET, "防眩网");
        translationBuilder.add(ModBlocks.ANTI_GLARE_NET_POLE, "防眩网立柱");
        translationBuilder.add(ModBlocks.ANTI_GLARE_VERSION, "防眩板");

// 交通护栏类
        translationBuilder.add(ModBlocks.TRAFFIC_BARRIER, "交通护栏");
        translationBuilder.add(ModBlocks.TRAFFIC_BARRIER_YELLOW_DOUBLE, "黄色双体交通护栏");
        translationBuilder.add(ModBlocks.TRAFFIC_BARRIER_YELLOW, "黄色交通护栏");
        translationBuilder.add(ModBlocks.TRAFFIC_BARRIER_RED, "红色交通护栏");
        translationBuilder.add(ModBlocks.TRAFFIC_BARRIER_RED_DOUBLE, "红色双体交通护栏");
        translationBuilder.add(ModBlocks.TRAFFIC_BARRIER_OBLIQUE, "斜向交通护栏");
        translationBuilder.add(ModBlocks.TRAFFIC_BARRIER_GRAY, "灰色交通护栏");
        translationBuilder.add(ModBlocks.TRAFFIC_BARRIER_GRAY_OBLIQUE, "灰色斜向交通护栏");
        translationBuilder.add(ModBlocks.TRAFFIC_BARRIER_GRAY_RED, "红灰色交通护栏");
        translationBuilder.add(ModBlocks.TRAFFIC_BARRIER_GRAY_RED_OBLIQUE,"红灰色斜向交通护栏");
        translationBuilder.add(ModBlocks.TRAFFIC_BARRIER_GRAY_YELLOW, "黄灰色交通护栏");
        translationBuilder.add(ModBlocks.TRAFFIC_BARRIER_GRAY_YELLOW_OBLIQUE, "黄灰色斜向交通护栏");
        translationBuilder.add(ModBlocks.TRAFFIC_BARRIER_GRAY_SLANT, "灰色倾斜交通护栏");
        translationBuilder.add(ModBlocks.TRAFFIC_BARRIER_GRAY_SLANT_YELLOW, "黄灰色倾斜交通护栏");
        translationBuilder.add(ModBlocks.TRAFFIC_BARRIER_GRAY_SLANT_RED, "红灰色倾斜交通护栏");
        translationBuilder.add(ModBlocks.TRAFFIC_BARRIER_GRAY_SLANT_OBLIQUE, "灰色斜向倾斜交通护栏");

// 道路警示柱类
        translationBuilder.add(ModBlocks.ROAD_WARNING_POLE_RED, "红色道路警示柱");
        translationBuilder.add(ModBlocks.ROAD_WARNING_POLE_YELLOW, "黄色道路警示柱");
        translationBuilder.add(ModBlocks.ROAD_WARNING_POLE_GREEN, "绿色道路警示柱");

// 铁马护栏类
        translationBuilder.add(ModBlocks.IRON_HORSE_YELLOW, "黄色铁马护栏");
        translationBuilder.add(ModBlocks.IRON_HORSE_RED, "红色铁马护栏");
        translationBuilder.add(ModBlocks.IRON_HORSE_WHITE, "白色铁马护栏");
        translationBuilder.add(ModBlocks.IRON_HORSE_GRAY, "灰色铁马护栏");

// 反光标识类
        translationBuilder.add(ModBlocks.REFLECTIVE_SIGN_YELLOW_ALL_1, "黄色全反光标识1型");
        translationBuilder.add(ModBlocks.REFLECTIVE_SIGN_YELLOW_ALL_2, "黄色全反光标识2型");
        translationBuilder.add(ModBlocks.REFLECTIVE_SIGN_RED_ALL_1, "红色全反光标识1型");
        translationBuilder.add(ModBlocks.REFLECTIVE_SIGN_RED_ALL_2, "红色全反光标识2型");

// 仪器杆类
        translationBuilder.add(ModBlocks.INSTRUMENT_POLE_FOUNDATIONS, "仪器杆基础");
        translationBuilder.add(ModBlocks.INSTRUMENT_POLE_LONGITUDINAL, "纵向仪器杆");
        translationBuilder.add(ModBlocks.INSTRUMENT_CAMERA, "仪器摄像头");

// 安全岛类
        translationBuilder.add(ModBlocks.SAFETY_ISLAND_YELLOW_1, "黄色安全岛1型");
        translationBuilder.add(ModBlocks.SAFETY_ISLAND_YELLOW_2, "黄色安全岛2型");
        translationBuilder.add(ModBlocks.SAFETY_ISLAND_YELLOW_3, "黄色安全岛3型");
        translationBuilder.add(ModBlocks.SAFETY_ISLAND_YELLOW_4, "黄色安全岛4型");
        translationBuilder.add(ModBlocks.SAFETY_ISLAND_GRAY, "灰色安全岛");
        translationBuilder.add(ModBlocks.SAFETY_ISLAND_YELLOW_OBLIQUE_1, "黄色斜向安全岛1型");
        translationBuilder.add(ModBlocks.SAFETY_ISLAND_YELLOW_OBLIQUE_2, "黄色斜向安全岛2型");
        translationBuilder.add(ModBlocks.SAFETY_ISLAND_GRAY_OBLIQUE, "灰色斜向安全岛");
        translationBuilder.add(ModBlocks.SAFETY_ISLAND_YELLOW_SLAB_EDGE_1, "黄色安全岛石板边缘1型");
        translationBuilder.add(ModBlocks.SAFETY_ISLAND_YELLOW_SLAB_EDGE_2, "黄色安全岛石板边缘2型");
        translationBuilder.add(ModBlocks.SAFETY_ISLAND_YELLOW_SLAB_EDGE_3, "黄色安全岛石板边缘3型");
        translationBuilder.add(ModBlocks.SAFETY_ISLAND_YELLOW_SLAB_EDGE_4, "黄色安全岛石板边缘4型");
        translationBuilder.add(ModBlocks.SAFETY_ISLAND_GRAY_SLAB_EDGE, "灰色安全岛石板边缘");
        translationBuilder.add(ModBlocks.SAFETY_ISLAND_YELLOW_SLAB_EDGE_OBLIQUE_1, "黄色斜向安全岛石板边缘1型");
        translationBuilder.add(ModBlocks.SAFETY_ISLAND_YELLOW_SLAB_EDGE_OBLIQUE_2, "黄色斜向安全岛石板边缘2型");
        translationBuilder.add(ModBlocks.SAFETY_ISLAND_GRAY_SLAB, "灰色安全岛石板");
        translationBuilder.add(ModBlocks.SAFETY_ISLAND_GRAY_SLAB_EDGE_OBLIQUE, "灰色斜向安全岛石板边缘");

// 道路栏杆类
        translationBuilder.add(ModBlocks.ROAD_RAILINGS_IRON, "铁质道路栏杆");
        translationBuilder.add(ModBlocks.ROAD_RAILINGS_IRON_ENDING_1, "铁质道路栏杆端头1型");
        translationBuilder.add(ModBlocks.ROAD_RAILINGS_IRON_ENDING_2, "铁质道路栏杆端头2型");
        translationBuilder.add(ModBlocks.ROAD_RAILINGS_IRON_POLE, "铁质道路栏杆立柱");
        translationBuilder.add(ModBlocks.ROAD_RAILINGS_IRON_OBLIQUE, "斜向铁质道路栏杆");
        translationBuilder.add(ModBlocks.ROAD_RAILINGS_GREEN, "绿色道路栏杆");
        translationBuilder.add(ModBlocks.ROAD_RAILINGS_GREEN_ENDING_1, "绿色道路栏杆端头1型");
        translationBuilder.add(ModBlocks.ROAD_RAILINGS_GREEN_ENDING_2, "绿色道路栏杆端头2型");
        translationBuilder.add(ModBlocks.ROAD_RAILINGS_GREEN_POLE, "绿色道路栏杆立柱");
        translationBuilder.add(ModBlocks.ROAD_RAILINGS_GREEN_OBLIQUE, "斜向绿色道路栏杆");

// 道路封闭护栏类
        translationBuilder.add(ModBlocks.ROAD_CLOSED_BARRICADE_GUARDRAIL_1, "道路封闭护栏1型");
        translationBuilder.add(ModBlocks.ROAD_CLOSED_BARRICADE_GUARDRAIL_2, "道路封闭护栏2型");

// 道路杆件类
        translationBuilder.add(ModBlocks.ROAD_POLE_FOUNDATIONS, "道路杆件基础");
        translationBuilder.add(ModBlocks.ROAD_POLE_LONGITUDINAL, "纵向道路杆件");
        translationBuilder.add(ModBlocks.ROAD_POLE_HORIZONTAL, "横向道路杆件");
        translationBuilder.add(ModBlocks.ROAD_POLE_TSHAPE, "T型道路杆件");
        translationBuilder.add(ModBlocks.ROAD_LIGHT, "道路照明灯");
        translationBuilder.add("block.yunbeiuc.road_light.tooltip","手持魔杖右键点击以切换灯光状态");

// 道路及标线类
        translationBuilder.add(ModBlocks.ROAD_BLOCK, "道路块");
        translationBuilder.add(ModBlocks.ROAD_FULL_OF_WHITE,"全白道路");
        translationBuilder.add(ModBlocks.ROAD_FULL_OF_YELLOW,"全黄道路");
        translationBuilder.add(ModBlocks.ROAD_WHITE_YELLOW,"白黄道路");
        translationBuilder.add(ModBlocks.ROAD_WITH_WHITE_LINE, "白色标线道路");
        translationBuilder.add(ModBlocks.ROAD_WITH_WHITE_DOUBLE_LINE, "白色双实线道路");
        translationBuilder.add(ModBlocks.ROAD_WITH_WHITE_THICK_LINE, "白色粗标线道路");
        translationBuilder.add(ModBlocks.ROAD_WITH_YELLOW_LINE, "黄色标线道路");
        translationBuilder.add(ModBlocks.ROAD_WITH_YELLOW_DOUBLE_LINE, "黄色双实线道路");
        translationBuilder.add(ModBlocks.ROAD_WITH_YELLOW_THICK_LINE, "黄色粗标线道路");
        translationBuilder.add(ModBlocks.ROAD_WITH_WHITE_YELLOW_DOUBLE_LINE, "白黄双实线道路");
        translationBuilder.add(ModBlocks.ROAD_WITH_WHITE_HALF_DOUBLE_LINE, "白色半双实线道路");
        translationBuilder.add(ModBlocks.ROAD_WITH_YELLOW_HALF_DOUBLE_LINE, "黄色半双实线道路");
        translationBuilder.add(ModBlocks.ROAD_WITH_WHITE_OFFSET_LINE, "白色偏移标线道路");
        translationBuilder.add(ModBlocks.ROAD_WITH_YELLOW_OFFSET_LINE, "黄色偏移标线道路");
        translationBuilder.add(ModBlocks.ROAD_WITH_WHITE_RIGHTANGLE_LINE, "白色直角标线道路");
        translationBuilder.add(ModBlocks.ROAD_WITH_YELLOW_RIGHTANGLE_LINE, "黄色直角标线道路");
        translationBuilder.add(ModBlocks.ROAD_WITH_WHITE_YELLOW_RIGHTANGLE_LINE, "白黄直角标线道路");
        translationBuilder.add(ModBlocks.ROAD_WITH_WHITETHICK_NORMAL_RIGHTANGLE_LINE, "白色粗+普通直角标线道路");
        translationBuilder.add(ModBlocks.ROAD_WITH_WHITETHICK_YELLOW_RIGHTANGLE_LINE, "白色粗+黄色直角标线道路");
        translationBuilder.add(ModBlocks.ROAD_WITH_WHITETHICK_YELLOWDOUBLE_RIGHTANGLE_LINE, "白色粗+黄色双实直角标线道路");
        translationBuilder.add(ModBlocks.ROAD_WITH_WHITE_YELLOWDOUBLE_RIGHTANGLE_LINE, "白色+黄色双实直角标线道路");
        translationBuilder.add(ModBlocks.ROAD_WITH_WHITE_BEVEL_LINE, "白色斜角标线道路");
        translationBuilder.add(ModBlocks.ROAD_WITH_WHITE_BEVEL_DOUBLE_LINE, "白色斜角双实线道路");
        translationBuilder.add(ModBlocks.ROAD_WITH_WHITE_BEVEL_THICK_LINE, "白色粗斜角标线道路");
        translationBuilder.add(ModBlocks.ROAD_WITH_WHITE_OFFSET_OUT_BEVEL_RIGHTANGLE_LINE, "白色外偏移斜角直角标线道路");
        translationBuilder.add(ModBlocks.ROAD_WITH_WHITE_OFFSET_IN_BEVEL_RIGHTANGLE_LINE, "白色内偏移斜角直角标线道路");
        translationBuilder.add(ModBlocks.ROAD_WITH_YELLOW_BEVEL_LINE, "黄色斜角标线道路");
        translationBuilder.add(ModBlocks.ROAD_WITH_YELLOW_BEVEL_DOUBLE_LINE, "黄色斜角双实线道路");
        translationBuilder.add(ModBlocks.ROAD_WITH_YELLOW_BEVEL_THICK_LINE, "黄色粗斜角标线道路");
        translationBuilder.add(ModBlocks.ROAD_WITH_YELLOW_OFFSET_OUT_BEVEL_RIGHTANGLE_LINE, "黄色外偏移斜角直角标线道路");
        translationBuilder.add(ModBlocks.ROAD_WITH_YELLOW_OFFSET_IN_BEVEL_RIGHTANGLE_LINE, "黄色内偏移斜角直角标线道路");
        translationBuilder.add(ModBlocks.ROAD_WITH_WHITE_RIGHTANGLE_LINE_OFFSET_OUT, "白色直角外偏移标线道路");
        translationBuilder.add(ModBlocks.ROAD_WITH_WHITE_RIGHTANGLE_LINE_OFFSET_IN, "白色直角内偏移标线道路");
        translationBuilder.add(ModBlocks.ROAD_WITH_WHITE_OFFSET_OUT_RIGHTANGLE_LINE, "白色外偏移直角标线道路");
        translationBuilder.add(ModBlocks.ROAD_WITH_WHITE_OFFSET_IN_RIGHTANGLE_LINE, "白色内偏移直角标线道路");
        translationBuilder.add(ModBlocks.ROAD_WITH_YELLOW_RIGHTANGLE_LINE_OFFSET_OUT, "黄色直角外偏移标线道路");
        translationBuilder.add(ModBlocks.ROAD_WITH_YELLOW_RIGHTANGLE_LINE_OFFSET_IN, "黄色直角内偏移标线道路");
        translationBuilder.add(ModBlocks.ROAD_WITH_YELLOW_OFFSET_OUT_RIGHTANGLE_LINE, "黄色外偏移直角标线道路");
        translationBuilder.add(ModBlocks.ROAD_WITH_YELLOW_OFFSET_IN_RIGHTANGLE_LINE, "黄色内偏移直角标线道路");
        translationBuilder.add(ModBlocks.ROAD_WITH_WHITE_TSHAPE_LINE, "白色T型标线道路");
        translationBuilder.add(ModBlocks.ROAD_WITH_YELLOW_TSHAPE_LINE, "黄色T型标线道路");
        translationBuilder.add(ModBlocks.ROAD_WITH_WHITE_TSHAPE_DOUBLE_LINE, "白色T型双实线道路");
        translationBuilder.add(ModBlocks.ROAD_WITH_WHITE_TSHAPE_THICK_LINE, "白色粗T型标线道路");
        translationBuilder.add(ModBlocks.ROAD_WITH_WHITE_DOUBLE_TSHAPE_LINE, "白色双实T型标线道路");
        translationBuilder.add(ModBlocks.ROAD_WITH_WHITE_THICK_TSHAPE_LINE, "白色粗实T型标线道路");
        translationBuilder.add(ModBlocks.ROAD_WITH_WHITE_TSHAPE_YELLOW_LINE, "白色T型+黄色标线道路");
        translationBuilder.add(ModBlocks.ROAD_WITH_YELLOW_TSHAPE_WHITE_LINE, "黄色T型+白色标线道路");
        translationBuilder.add(ModBlocks.ROAD_WITH_WHITE_TSHAPE_YELLOWDOUBLE_LINE, "白色T型+黄色双实标线道路");
        translationBuilder.add(ModBlocks.ROAD_WITH_WHITE_THICK_TSHAPE_YELLOW_LINE, "白色粗T型+黄色标线道路");
        translationBuilder.add(ModBlocks.ROAD_WITH_WHITETHICK_TSHAPE_YELLOWDOUBLE_LINE, "白色粗T型+黄色双实标线道路");
        translationBuilder.add(ModBlocks.ROAD_WITH_WHITE_TSHAPE_OFFSET_LINE, "白色T型偏移标线道路");
        translationBuilder.add(ModBlocks.ROAD_WITH_YELLOW_TSHAPE_OFFSET_LINE, "黄色T型偏移标线道路");
        translationBuilder.add(ModBlocks.ROAD_WITH_WHITEDOUBLE_TSHAPE_OFFSET_LINE, "白色双实T型偏移标线道路");
        translationBuilder.add(ModBlocks.ROAD_WITH_WHITETHICK_TSHAPE_OFFSET_LINE, "白色粗T型偏移标线道路");
        translationBuilder.add(ModBlocks.ROAD_WITH_WHITETHICK_TSHAPE_OFFSET_YELLOW_LINE, "白色粗T型偏移+黄色标线道路");
        translationBuilder.add(ModBlocks.ROAD_WITH_YELLOW_TSHAPE_OFFSET_WHITE_LINE, "黄色T型偏移+白色标线道路");
        translationBuilder.add(ModBlocks.ROAD_WITH_WHITE_TSHAPE_OFFSET_YELLOW_LINE, "白色T型偏移+黄色标线道路");
        translationBuilder.add(ModBlocks.ROAD_WITH_WHITE_BEVEL_DB_LINE, "白色斜角双实标线道路");
        translationBuilder.add(ModBlocks.ROAD_WITH_YELLOW_BEVEL_DB_LINE, "黄色斜角双实标线道路");
        translationBuilder.add(ModBlocks.ROAD_WITH_WHITENORMAL_AND_BEVEL_DB_LINE, "白色普通+斜角双实标线道路");
        translationBuilder.add(ModBlocks.ROAD_WITH_YELLOWNORMAL_AND_BEVEL_DB_LINE, "黄色普通+斜角双实标线道路");
        translationBuilder.add(ModBlocks.ROAD_WITH_WHITENORMAL_BEVEL_LINE, "白色普通+斜角标线道路");
        translationBuilder.add(ModBlocks.ROAD_WITH_YELLOWNORMAL_BEVEL_LINE, "黄色普通+斜角标线道路");
        translationBuilder.add(ModBlocks.ROAD_WITH_WHITENORMAL_BEVEL_YELLOW_LINE, "白色普通+斜角+黄色标线道路");
        translationBuilder.add(ModBlocks.ROAD_WITH_YELLOWNORMAL_BEVEL_WHITE_LINE, "黄色普通+斜角+白色标线道路");
        translationBuilder.add(ModBlocks.ROAD_WITH_WHITETHICK_BEVEL_LINE, "白色粗+斜角标线道路");
        translationBuilder.add(ModBlocks.ROAD_WITH_YELLOWTHICK_BEVEL_LINE, "黄色粗+斜角标线道路");
        translationBuilder.add(ModBlocks.ROAD_WITH_WHITETHICK_BEVEL_YELLOW_LINE, "白色粗+斜角+黄色标线道路");
        translationBuilder.add(ModBlocks.ROAD_WITH_YELLOWTHICK_BEVEL_WHITE_LINE, "黄色粗+斜角+白色标线道路");
        translationBuilder.add(ModBlocks.ROAD_WITH_WHITE_CROSS_LINE, "白色十字标线道路");
        translationBuilder.add(ModBlocks.ROAD_WITH_YELLOW_CROSS_LINE, "黄色十字标线道路");
        translationBuilder.add(ModBlocks.ROAD_WITH_AUTO_BEVEL_LINE, "自动斜角标线道路");
        translationBuilder.add(ModBlocks.ROAD_WITH_AUTO_RIGHTANGLE_LINE, "自动直角标线道路");

// 道路杆件附属类
        translationBuilder.add(ModBlocks.ROAD_POLE_TEXT_DISPLAY, "道路杆件文字显示屏");
        translationBuilder.add(ModBlocks.ROAD_POLE_FLAG, "道路杆件旗帜");

        translationBuilder.add("itemGroup.yunbeiuc_rb_group","云北城建 | 道路方块");
        translationBuilder.add("itemGroup.yunbeiuc_sings_group","云北城建 | 道路标识");
    }
}
