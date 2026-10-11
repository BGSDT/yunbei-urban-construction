package com.beigu.yunbeiuc.util;

import com.beigu.yunbeiuc.api.mapper.VersionServices;
import com.beigu.yunbeiuc.api.text.Text;
import com.beigu.yunbeiuc.screen.PatternAndFontOverlay;
import com.beigu.yunbeiuc.screen.TextureAspectCache;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * 图案、字体与预设的资源包加载器。
 *
 * <p>职责：从 Minecraft 的资源管理器（{@link ResourceManager}）扫描纹理、解析自定义 UI 定义 JSON
 * （图案 / 字体 / 预设），并把结果写入 {@link PatternAndFontOverlay} 的分类模型（H2/H3/H4）。
 *
 * <p>自定义资源包统一使用 {@code assets/<命名空间>/ui_definitions/cf_<modid>.json}
 * （见 {@link #CUSTOM_UI_DIR} 与 {@link #CUSTOM_UI_FILES}），同时兼容历史文件名
 * {@code patterns.json}。三个命名空间（yunbeiuc / ocelotsignmod / ocelotsignyunbei）
 * 的接口与 JSON 结构完全一致，各自读取自己那份文件。
 *
 * <p>图案集另有独立接口 {@code ui_definitions/cpl_<modid>.json}
 * （见 {@link #CUSTOM_PATTERN_SET_FILES}）：{@code cf_} 管「图案 / 字体注册」，
 * {@code cpl_} 管「图案集」（按选项名组织的带标签按钮组，追加到「图案集整合」分类）。
 *
 * <p>分类结构与内置素材清单（白名单/排除前缀等）仍由
 * {@code com.beigu.yunbeiuc.screen.PatternRegistry} 定义，本类只承担“加载”职责，
 * 不改变任何分类规则。
 *
 * @see com.beigu.yunbeiuc.screen.PatternRegistry
 * @see PatternAndFontOverlay
 */
public final class PatternResourceLoader {

    /** 日志前缀（原实现位于 {@code PatternRegistry}，迁移后改用本类名）。 */
    private static final String LOG_TAG = "[PatternResourceLoader]";

    /**
     * 自定义资源包定义目录：读取所有命名空间下的 {@code assets/<命名空间>/ui_definitions/}。
     * 图案、字体、预设共用同一套接口与 JSON 结构。
     */
    public static final String CUSTOM_UI_DIR = "ui_definitions";

    /**
     * 需要读取的自定义资源包接口文件名：三个命名空间各一个
     * （yunbeiuc / ocelotsignmod / ocelotsignyunbei，接口与 JSON 结构完全一致）。
     *
     * <p>另兼容历史文件名 {@code patterns.json}（与 {@code cf_<modid>.json} 结构一致），
     * 因此两者都会读取，其余 {@code *.json} 一律忽略。
     */
    public static final String[] CUSTOM_UI_FILES = {
            "cf_yunbeiuc.json",
            "cf_ocelotsignmod.json",
            "cf_ocelotsignyunbei.json",
            "patterns.json"
    };

    /** 判断资源路径的文件名是否为约定的接口文件（{@code cf_<modid>.json} 或 {@code patterns.json}）。 */
    public static boolean isCustomUiFile(String path) {
        String fileName = path.substring(path.lastIndexOf('/') + 1);
        for (String name : CUSTOM_UI_FILES) {
            if (name.equals(fileName)) return true;
        }
        return false;
    }

    /**
     * 图案集（「图案集整合」）接口文件名：三个命名空间各一个。
     *
     * <p>与 {@link #CUSTOM_UI_FILES} 分工不同：{@code cf_<modid>.json} 负责「图案 / 字体注册」
     * （按纹理目录划分区），{@code cpl_<modid>.json} 负责「图案集」——按选项名把若干纹理
     * 组织成一组带标签的按钮，追加到「图案集整合」分类中。两套文件互不影响，可同时存在。
     */
    public static final String[] CUSTOM_PATTERN_SET_FILES = {
            "cpl_yunbeiuc.json",
            "cpl_ocelotsignmod.json",
            "cpl_ocelotsignyunbei.json"
    };

    /** 判断资源路径的文件名是否为约定的图案集接口文件（{@code cpl_<modid>.json}）。 */
    public static boolean isPatternSetFile(String path) {
        String fileName = path.substring(path.lastIndexOf('/') + 1);
        for (String name : CUSTOM_PATTERN_SET_FILES) {
            if (name.equals(fileName)) return true;
        }
        return false;
    }

    private PatternResourceLoader() {
    }

    // ==================== 入口 ====================

    /** 使用当前客户端的资源管理器加载所有分类数据（纹理缓存 + 自定义图案/字体 + 高级 UI 定义）。 */
    public static void loadAllFromResourceManager() {
        loadAllFromResourceManager(Minecraft.getInstance().getResourceManager());
    }

    /**
     * 使用指定资源管理器加载所有分类数据。
     *
     * @param manager 资源管理器
     */
    public static void loadAllFromResourceManager(ResourceManager manager) {
        // 重新扫描纹理前清空尺寸缓存：资源包可能替换了同名贴图，
        // 沿用旧尺寸会导致缩略图按错误比例绘制
        TextureAspectCache.clear();

        for (PatternAndFontOverlay.H2Category h2 : PatternAndFontOverlay.REGISTRY) {
            for (PatternAndFontOverlay.H3Category h3 : h2.subCategories) {
                buildTextureCacheForH3(h3, manager);
            }
        }

        loadAdvancedCustomUIFromJson(manager);
        loadCustomPatternSetsFromJson(manager);
        // 资源包预设与图案/字体共用 ui_definitions 目录（见 CUSTOM_UI_DIR）
        PresetManager.loadFromResourcePacks(manager);
    }

    // ==================== 纹理缓存 ====================

    // 递归构建 H3 分类下的纹理缓存
    private static void buildTextureCacheForH3(PatternAndFontOverlay.H3Category h3, ResourceManager manager) {
        for (PatternAndFontOverlay.H4Section h4 : h3.sections) {
            buildTextureCacheDeep(h4, manager);
        }
        for (PatternAndFontOverlay.H3Category child : h3.subCategories) {
            buildTextureCacheForH3(child, manager);
        }
    }

    // 递归构建分区及其所有子分区的纹理缓存（子分区不构建的话，内容区会显示"暂无图片"）
    private static void buildTextureCacheDeep(PatternAndFontOverlay.H4Section section, ResourceManager manager) {
        buildTextureCache(section, manager);
        for (PatternAndFontOverlay.H4Section child : section.childSections) {
            buildTextureCacheDeep(child, manager);
        }
    }

    /**
     * 根据当前 Minecraft 实例的资源管理器扫描并分类 H4Section 的纹理。
     *
     * @param section 目标 Section
     */
    public static void buildTextureCache(PatternAndFontOverlay.H4Section section) {
        buildTextureCache(section, Minecraft.getInstance().getResourceManager());
    }

    /**
     * 根据 H4Section 的配置扫描资源包并按子文件夹/过滤器分类纹理。
     *
     * @param section 目标 Section
     * @param resourceManager 资源管理器
     */
    public static void buildTextureCache(PatternAndFontOverlay.H4Section section, ResourceManager resourceManager) {
        section.cachedTextures.clear();

        String targetNamespace = section.basePath.getNamespace();
        String targetPath = section.basePath.getPath();

        if (targetPath.endsWith("/")) {
            targetPath = targetPath.substring(0, targetPath.length() - 1);
        }

        List<ResourceLocation> allResources = VersionServices.resources().listResources(resourceManager, targetPath,
                id -> id.getPath().endsWith(".png"));

        initCacheBuckets(section);

        for (ResourceLocation id : allResources) {
            if (!id.getNamespace().equals(targetNamespace)) continue;

            String fullPath = id.getPath();
            if (!fullPath.startsWith(targetPath)) continue;

            String fileName = fullPath.substring(fullPath.lastIndexOf('/') + 1);
            if (!passesExtensionFilter(section, fileName)) continue;

            String relativePath = fullPath.substring(targetPath.length());
            if (relativePath.startsWith("/")) relativePath = relativePath.substring(1);

            distributeToBucket(section, id, relativePath);
        }

        for (List<ResourceLocation> list : section.cachedTextures.values()) {
            list.sort((id1, id2) -> id1.getPath().compareTo(id2.getPath()));
        }
    }

    // 初始化缓存桶
    private static void initCacheBuckets(PatternAndFontOverlay.H4Section section) {
        if (section.useSubfolders && !section.subFolders.isEmpty()) {
            for (PatternAndFontOverlay.SubFolderDef subDef : section.subFolders) {
                section.cachedTextures.put(subDef.dirName, new ArrayList<>());
            }
        } else {
            section.cachedTextures.put("root", new ArrayList<>());
        }
    }

    // 判断文件名是否通过过滤器
    private static boolean passesExtensionFilter(PatternAndFontOverlay.H4Section section, String fileName) {
        // 附加排除优先：命中即剔除，不受主过滤器影响
        if (section.extExcludeNames.contains(fileName)) {
            return false;
        }
        if (!section.extExcludePrefixes.isEmpty()
                && section.extExcludePrefixes.stream().anyMatch(fileName::startsWith)) {
            return false;
        }
        switch (section.extFilterMode) {
            case WHITELIST:
                return section.extFilterList.stream().anyMatch(fileName::equals);
            case BLACKLIST:
                return section.extFilterList.stream().noneMatch(fileName::equals);
            case PREFIX:
                return section.extFilterList.stream().anyMatch(fileName::startsWith);
            case PREFIX_EXCLUDE:
                return section.extFilterList.stream().noneMatch(fileName::startsWith);
            default:
                return true;
        }
    }

    // 将纹理分配到缓存桶
    private static void distributeToBucket(PatternAndFontOverlay.H4Section section, ResourceLocation id, String relativePath) {
        String[] pathSegments = relativePath.split("/");
        if (section.useSubfolders) {
            if (pathSegments.length == 2 && section.cachedTextures.containsKey(pathSegments[0])) {
                section.cachedTextures.get(pathSegments[0]).add(id);
            }
        } else {
            if (pathSegments.length == 1 && section.cachedTextures.containsKey("root")) {
                section.cachedTextures.get("root").add(id);
            }
        }
    }

    // ==================== 高级自定义 UI 定义 ====================

    /**
     * 从所有命名空间下的 {@code ui_definitions/cf_<modid>.json} 加载自定义分类。
     *
     * <p>只读取 {@link #CUSTOM_UI_FILES} 中约定的三个文件名；JSON 结构原样解析，不做命名空间改写。
     * 文件结构（图案 / 字体共用；三个命名空间一致）：
     * <pre>
     * {
     *   "tab": "patterns" | "fonts",
     *   "category_name": "资源包附加图案",
     *   "header_text": "自定义告示牌图案资源包",
     *   "header_text_enabled": true,
     *   "sections": [ { "title": "...", "description": "...", "basePath": "yunbeiuc:textures/.../", ... } ]
     * }
     * </pre>
     *
     * <p>只含 {@code presets} 块、没有 {@code sections} 的文件由
     * {@link PresetManager#loadFromResourcePacks(ResourceManager)} 处理，此处跳过。
     */
    private static void loadAdvancedCustomUIFromJson(ResourceManager manager) {
        List<ResourceLocation> resources = VersionServices.resources().listResources(manager, CUSTOM_UI_DIR,
                id -> isCustomUiFile(id.getPath()));

        for (ResourceLocation resourceId : resources) {
            try {
                InputStream stream = VersionServices.resources().openIfPresent(manager, resourceId);
                if (stream == null) continue;
                try (InputStream in = stream;
                     InputStreamReader reader = new InputStreamReader(in, StandardCharsets.UTF_8)) {
                    JsonObject root = new JsonParser().parse(reader).getAsJsonObject();

                    // 只含 presets 的文件交给 PresetManager，不作为图案/字体分类
                    if (!root.has("sections")) continue;

                    String tabType = root.has("tab") ? root.get("tab").getAsString() : "patterns";
                    boolean isFont = tabType.equals("fonts");

                    // 用翻译键定位 H2/H3，避免依赖 REGISTRY 与 subCategories 的固定下标
                    PatternAndFontOverlay.H2Category targetH2 = findH2ByKey(isFont
                            ? "yunbeiuc.gui.tabs.fonts" : "yunbeiuc.gui.tabs.patterns");
                    if (targetH2 == null) continue;
                    PatternAndFontOverlay.H3Category customPackH3 = findH3ByKey(targetH2,
                            "yunbeiuc.gui.categories.custom_resource_pack");
                    if (customPackH3 == null) continue;

                    String categoryName = root.has("category_name") && !root.get("category_name").getAsString().isEmpty()
                            ? root.get("category_name").getAsString()
                            : "未命名分类";
                    PatternAndFontOverlay.H3Category customH3 = new PatternAndFontOverlay.H3Category(Text.literal(categoryName));

                    // header_text_enabled 为 false 或 header_text 为空时不显示说明横幅
                    boolean headerEnabled = !root.has("header_text_enabled") || root.get("header_text_enabled").getAsBoolean();
                    String headerText = root.has("header_text") ? root.get("header_text").getAsString() : "";
                    customH3.headerText = (headerEnabled && !headerText.isEmpty()) ? Text.literal(headerText) : null;

                    if (root.has("sections") && root.get("sections").isJsonArray()) {
                        JsonArray sectionsArray = root.getAsJsonArray("sections");
                        for (JsonElement secElement : sectionsArray) {
                            JsonObject secObj = secElement.getAsJsonObject();
                            PatternAndFontOverlay.H4Section newSection = parseSectionFromJson(secObj);
                            customH3.addSection(newSection);
                            buildTextureCache(newSection, manager);
                        }
                    }

                    customPackH3.addSubCategory(customH3);
                }
            } catch (Exception e) {
                System.err.println(LOG_TAG + " 加载自定义 UI JSON 失败: " + resourceId + " | 错误: " + e.getMessage());
            }
        }
    }

    // 按翻译键查找顶层 H2 分类
    private static PatternAndFontOverlay.H2Category findH2ByKey(String translationKey) {
        String want = Text.translatable(translationKey).getString();
        for (PatternAndFontOverlay.H2Category h2 : PatternAndFontOverlay.REGISTRY) {
            if (h2.title.getString().equals(want)) return h2;
        }
        return null;
    }

    // 按翻译键查找指定 H2 下的 H3 分类
    private static PatternAndFontOverlay.H3Category findH3ByKey(PatternAndFontOverlay.H2Category h2, String translationKey) {
        String want = Text.translatable(translationKey).getString();
        for (PatternAndFontOverlay.H3Category h3 : h2.subCategories) {
            if (h3.title.getString().equals(want)) return h3;
        }
        return null;
    }

    // 从 JSON 解析 Section
    private static PatternAndFontOverlay.H4Section parseSectionFromJson(JsonObject secObj) {
        Component secTitle = Text.literal(secObj.has("title") ? secObj.get("title").getAsString() : "未命名 Section");
        Component secDesc = Text.literal(secObj.has("description") ? secObj.get("description").getAsString() : "");
        ResourceLocation basePath = VersionServices.resources().parse(
                secObj.has("basePath") ? secObj.get("basePath").getAsString() : "minecraft:empty/");

        PatternAndFontOverlay.H4Section newSection = new PatternAndFontOverlay.H4Section(secTitle, secDesc, basePath);

        if (secObj.has("useSubfolders") && secObj.get("useSubfolders").getAsBoolean()) {
            newSection.enableSubfolders();
            if (secObj.has("subFolders")) {
                for (JsonElement subEl : secObj.getAsJsonArray("subFolders")) {
                    JsonObject subObj = subEl.getAsJsonObject();
                    newSection.addSubFolder(subObj.get("dirName").getAsString(),
                            Text.literal(subObj.get("displayName").getAsString()));
                }
            }
        }

        if (secObj.has("filterMode")) {
            PatternAndFontOverlay.FilterMode mode =
                    PatternAndFontOverlay.FilterMode.valueOf(secObj.get("filterMode").getAsString().toUpperCase());
            List<String> filters = new ArrayList<>();
            if (secObj.has("filterList")) {
                for (JsonElement filterEl : secObj.getAsJsonArray("filterList")) {
                    filters.add(filterEl.getAsString());
                }
            }
            newSection.setExtensionFilter(mode, filters.toArray(new String[0]));
        }

        if (secObj.has("isFontMode") && secObj.get("isFontMode").getAsBoolean()) {
            newSection.setFontMode();
            if (secObj.has("insertTemplate")) {
                newSection.setFontInsertTemplate(secObj.get("insertTemplate").getAsString());
            }

            if (secObj.has("fontList") && secObj.get("fontList").isJsonArray()) {
                for (JsonElement fontEl : secObj.getAsJsonArray("fontList")) {
                    JsonObject fontObj = fontEl.getAsJsonObject();
                    String fId = fontObj.get("fontId").getAsString();
                    String dName = fontObj.get("displayName").getAsString();
                    newSection.addFontItem(fId, Text.literal(dName));
                }
            }
        }

        return newSection;
    }

    // ==================== 图案集（cpl_<modid>.json） ====================

    /**
     * 从 {@code ui_definitions/cpl_<modid>.json} 加载资源包提供的图案集分组，
     * 追加到「图案集整合」分类（排在内置 6 组之后）。
     *
     * <p>与 {@code cf_<modid>.json} 的分工：{@code cf_} 注册「按纹理目录划分」的图案 / 字体分区，
     * 本文件则定义「按选项名组织」的图案集——每组是一排带中文标签的按钮，点击插入对应纹理。
     * 两者可同时存在，互不影响。
     *
     * <p>文件结构：
     * <pre>
     * {
     *   "groups": [
     *     {
     *       "title": "豹猫指示牌图案集",
     *       "description": "可选，分组下方的说明",
     *       "match": ["箭头"],
     *       "keys": ["arrow1", "arrow2"],
     *       "items": [
     *         { "label": "左转", "texture": "ocelotsign:textures/block/sign/xxx.png" },
     *         { "label": "禁停", "texture": "...", "insert": "-texture ..." }
     *       ]
     *     }
     *   ]
     * }
     * </pre>
     *
     * <p>{@code texture} 必填（缺失的项会被跳过）；{@code insert} 可选，
     * 留空时按 {@code -texture <texture>} 插入。
     *
     * <p>{@code match} / {@code keys} 可选，两者都只影响「text 编辑面板里这一组什么时候出现」：
     * <ul>
     *   <li>{@code match}：实体选项组标题（如 {@code "箭头"}）。当前选中文本行含占位符、
     *       且这块牌子自己声明了同名选项组时，本组才出现在面板里。</li>
     *   <li>{@code keys}：占位符键（如 {@code "arrow1"}）。当前选中文本行含其中任一键即出现。</li>
     * </ul>
     * 两者都不写时，本组只在图案库里出现，不会出现在 text 编辑面板。
     */
    private static void loadCustomPatternSetsFromJson(ResourceManager manager) {
        List<ResourceLocation> resources = VersionServices.resources().listResources(manager, CUSTOM_UI_DIR,
                id -> isPatternSetFile(id.getPath()));

        for (ResourceLocation resourceId : resources) {
            try {
                InputStream stream = VersionServices.resources().openIfPresent(manager, resourceId);
                if (stream == null) continue;
                try (InputStream in = stream;
                     InputStreamReader reader = new InputStreamReader(in, StandardCharsets.UTF_8)) {
                    JsonObject root = new JsonParser().parse(reader).getAsJsonObject();

                    if (!root.has("groups") || !root.get("groups").isJsonArray()) continue;

                    PatternAndFontOverlay.H2Category patternTab = findH2ByKey("yunbeiuc.gui.tabs.patterns");
                    if (patternTab == null) continue;
                    PatternAndFontOverlay.H3Category patternSets = findH3ByKey(patternTab,
                            "yunbeiuc.gui.categories.pattern_sets");
                    if (patternSets == null) continue;

                    for (JsonElement groupEl : root.getAsJsonArray("groups")) {
                        if (!groupEl.isJsonObject()) continue;
                        PatternAndFontOverlay.H4Section section = parsePatternSetGroup(groupEl.getAsJsonObject());
                        if (section != null) patternSets.addSection(section);
                    }
                }
            } catch (Exception e) {
                System.err.println(LOG_TAG + " 加载图案集 JSON 失败: " + resourceId + " | 错误: " + e.getMessage());
            }
        }
    }

    // 解析一个图案集分组；无有效条目时返回 null（不往分类里塞空分组）
    private static PatternAndFontOverlay.H4Section parsePatternSetGroup(JsonObject groupObj) {
        String title = groupObj.has("title") ? groupObj.get("title").getAsString() : "未命名图案集";
        String description = groupObj.has("description") ? groupObj.get("description").getAsString() : "";

        PatternAndFontOverlay.H4Section section = new PatternAndFontOverlay.H4Section(
                Text.literal(title), Text.literal(description),
                VersionServices.resources().create("yunbeiuc", "textures/block/sign/"))
                .setLabeledMode();

        // 可选：绑定实体选项组标题（match）/ 占位符键（keys），决定在 text 编辑面板里何时列出本组
        if (groupObj.has("match") && groupObj.get("match").isJsonArray()) {
            for (JsonElement t : groupObj.getAsJsonArray("match")) {
                if (t.isJsonPrimitive()) section.addMatchTitle(t.getAsString());
            }
        }
        if (groupObj.has("keys") && groupObj.get("keys").isJsonArray()) {
            for (JsonElement k : groupObj.getAsJsonArray("keys")) {
                if (k.isJsonPrimitive()) section.addBindKeys(k.getAsString());
            }
        }

        if (groupObj.has("items") && groupObj.get("items").isJsonArray()) {
            for (JsonElement itemEl : groupObj.getAsJsonArray("items")) {
                if (!itemEl.isJsonObject()) continue;
                JsonObject itemObj = itemEl.getAsJsonObject();

                String texture = itemObj.has("texture") ? itemObj.get("texture").getAsString() : "";
                if (texture.isEmpty()) continue;

                String label = itemObj.has("label") ? itemObj.get("label").getAsString() : texture;
                String insert = itemObj.has("insert") ? itemObj.get("insert").getAsString() : "";

                section.addLabeledItem(VersionServices.resources().parse(texture), insert, Text.literal(label));
            }
        }

        return section.labeledItems.isEmpty() ? null : section;
    }
}
