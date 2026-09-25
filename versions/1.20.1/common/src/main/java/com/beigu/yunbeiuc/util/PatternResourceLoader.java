package com.beigu.yunbeiuc.util;

import com.beigu.yunbeiuc.screen.PatternAndFontOverlay;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.client.MinecraftClient;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * 图案与字体的资源包加载器。
 *
 * <p>职责：从 Minecraft 的资源管理器（{@link ResourceManager}）扫描纹理、解析自定义图案/字体 JSON
 * 与高级自定义 UI 定义 JSON，并把结果写入 {@link PatternAndFontOverlay} 的分类模型（H2/H3/H4）。
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

    private PatternResourceLoader() {
    }

    // ==================== 入口 ====================

    /** 使用当前客户端的资源管理器加载所有分类数据（纹理缓存 + 自定义图案/字体 + 高级 UI 定义）。 */
    public static void loadAllFromResourceManager() {
        loadAllFromResourceManager(MinecraftClient.getInstance().getResourceManager());
    }

    /**
     * 使用指定资源管理器加载所有分类数据。
     *
     * @param manager 资源管理器
     */
    public static void loadAllFromResourceManager(ResourceManager manager) {
        for (PatternAndFontOverlay.H2Category h2 : PatternAndFontOverlay.REGISTRY) {
            for (PatternAndFontOverlay.H3Category h3 : h2.subCategories) {
                buildTextureCacheForH3(h3, manager);
            }
        }

        loadCustomPatternsFromJson(manager);
        loadCustomFontsFromJson(manager);
        loadAdvancedCustomUIFromJson(manager);
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
        buildTextureCache(section, MinecraftClient.getInstance().getResourceManager());
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

        Map<Identifier, Resource> allResources = resourceManager.findResources(targetPath,
                id -> id.getPath().endsWith(".png"));

        initCacheBuckets(section);

        for (Identifier id : allResources.keySet()) {
            if (!id.getNamespace().equals(targetNamespace)) continue;

            String fullPath = id.getPath();
            if (!fullPath.startsWith(targetPath)) continue;

            String fileName = fullPath.substring(fullPath.lastIndexOf('/') + 1);
            if (!passesExtensionFilter(section, fileName)) continue;

            String relativePath = fullPath.substring(targetPath.length());
            if (relativePath.startsWith("/")) relativePath = relativePath.substring(1);

            distributeToBucket(section, id, relativePath);
        }

        for (List<Identifier> list : section.cachedTextures.values()) {
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
    private static void distributeToBucket(PatternAndFontOverlay.H4Section section, Identifier id, String relativePath) {
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

    // ==================== 自定义图案 / 字体 JSON ====================

    // 从 JSON 加载自定义图案
    private static void loadCustomPatternsFromJson(ResourceManager manager) {
        forEachCustomResourceSection(h2 -> h2.subCategories, "yunbeiuc.gui.tabs.patterns",
                section -> loadCustomPatternsFromJsonSection(section, manager));
    }

    // 解析自定义图案 JSON 并追加到指定 Section
    private static void loadCustomPatternsFromJsonSection(PatternAndFontOverlay.H4Section section, ResourceManager manager) {
        Identifier jsonId = new Identifier(section.customJsonPath);

        try {
            List<Resource> resources = collectAllResources(manager, jsonId);
            for (Resource resource : resources) {
                try (InputStreamReader reader = new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8)) {
                    JsonElement root = JsonParser.parseReader(reader);
                    if (root.isJsonArray()) {
                        for (JsonElement element : root.getAsJsonArray()) {
                            JsonObject obj = element.getAsJsonObject();
                            String name = obj.has("name") ? obj.get("name").getAsString() : Text.translatable("yunbeiuc.gui.unnamed").getString();
                            String texture = obj.has("texture") ? obj.get("texture").getAsString() : "";
                            String insert = obj.has("insert") ? obj.get("insert").getAsString() : "";

                            if (!texture.isEmpty()) {
                                section.addWhitelistItem(new Identifier(texture), insert, Text.literal(name));
                            }
                        }
                    }
                } catch (Exception innerE) {
                    System.err.println(LOG_TAG + " Failed to parse custom pattern JSON: " + innerE.getMessage());
                }
            }
        } catch (Exception e) {
            System.err.println(LOG_TAG + " Custom pattern JSON 读取异常: " + e.getMessage());
        }
    }

    // 从 JSON 加载自定义字体
    private static void loadCustomFontsFromJson(ResourceManager manager) {
        forEachCustomResourceSection(h2 -> h2.subCategories, "yunbeiuc.gui.tabs.fonts",
                section -> loadCustomFontsFromJsonSection(section, manager));
    }

    // 解析自定义字体 JSON 并追加到指定 Section
    private static void loadCustomFontsFromJsonSection(PatternAndFontOverlay.H4Section section, ResourceManager manager) {
        Identifier jsonId = new Identifier(section.customJsonPath);

        try {
            List<Resource> resources = collectAllResources(manager, jsonId);
            for (Resource resource : resources) {
                try (InputStreamReader reader = new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8)) {
                    JsonElement root = JsonParser.parseReader(reader);
                    if (root.isJsonArray()) {
                        for (JsonElement element : root.getAsJsonArray()) {
                            JsonObject obj = element.getAsJsonObject();
                            String fontId = obj.has("font_id") ? obj.get("font_id").getAsString() : "";
                            String name = obj.has("name") ? obj.get("name").getAsString() : Text.translatable("yunbeiuc.gui.unnamed_font").getString();

                            if (!fontId.isEmpty()) {
                                section.addFontItem(fontId, Text.literal(name));
                            }
                        }
                    }
                } catch (Exception innerE) {
                    System.err.println(LOG_TAG + " Failed to parse custom font JSON: " + innerE.getMessage());
                }
            }
        } catch (Exception e) {
            System.err.println(LOG_TAG + " Custom font JSON 读取异常: " + e.getMessage());
        }
    }

    /**
     * 收集来自所有资源包的目标资源。
     * <p>遍历所有命名空间与路径匹配的 Identifier，使用 {@link ResourceManager#getAllResources(Identifier)}
     * 逐个收集同名资源，确保来自多个资源包的同名文件都被加载。
     *
     * @param manager 资源管理器
     * @param targetId 目标资源 ID
     * @return 所有匹配的 Resource 列表
     */
    private static List<Resource> collectAllResources(ResourceManager manager, Identifier targetId) {
        List<Resource> result = new ArrayList<>();
        try {
            Map<Identifier, Resource> allById = manager.findResources(
                    targetId.getPath(),
                    id -> id.getNamespace().equals(targetId.getNamespace())
                            && id.getPath().equals(targetId.getPath())
            );
            for (Identifier id : allById.keySet()) {
                try {
                    result.addAll(manager.getAllResources(id));
                } catch (Exception ignored) {
                }
            }
        } catch (Exception ignored) {
        }
        if (result.isEmpty()) {
            try {
                result.addAll(manager.getAllResources(targetId));
            } catch (Exception ignored) {
            }
        }
        return result;
    }

    // 遍历自定义资源包 Section 并执行操作
    private static void forEachCustomResourceSection(Function<PatternAndFontOverlay.H2Category, List<PatternAndFontOverlay.H3Category>> childrenSupplier,
                                                     String tabTranslationKey,
                                                     Consumer<PatternAndFontOverlay.H4Section> sectionConsumer) {
        String customPackKey = "yunbeiuc.gui.categories.custom_resource_pack";
        for (PatternAndFontOverlay.H2Category h2 : PatternAndFontOverlay.REGISTRY) {
            if (!h2.title.getString().equals(Text.translatable(tabTranslationKey).getString())) continue;
            for (PatternAndFontOverlay.H3Category h3 : childrenSupplier.apply(h2)) {
                if (!h3.title.getString().equals(Text.translatable(customPackKey).getString())) continue;
                for (PatternAndFontOverlay.H4Section section : h3.sections) {
                    if (section.customJsonPath.isEmpty()) continue;
                    sectionConsumer.accept(section);
                }
            }
        }
    }

    // ==================== 高级自定义 UI 定义 ====================

    // 从 JSON 加载高级自定义 UI 定义
    private static void loadAdvancedCustomUIFromJson(ResourceManager manager) {
        Map<Identifier, Resource> resources = manager.findResources("ui_definitions",
                id -> id.getPath().endsWith(".json"));

        for (Map.Entry<Identifier, Resource> entry : resources.entrySet()) {
            try (InputStreamReader reader = new InputStreamReader(entry.getValue().getInputStream(), StandardCharsets.UTF_8)) {
                JsonObject root = JsonParser.parseReader(reader).getAsJsonObject();

                String tabType = root.has("tab") ? root.get("tab").getAsString() : "patterns";
                boolean isFont = tabType.equals("fonts");

                PatternAndFontOverlay.H2Category targetH2 = isFont
                        ? PatternAndFontOverlay.REGISTRY.get(1) : PatternAndFontOverlay.REGISTRY.get(0);
                PatternAndFontOverlay.H3Category customPackH3 = isFont
                        ? targetH2.subCategories.get(1) : targetH2.subCategories.get(2);

                String categoryName = root.has("category_name") ? root.get("category_name").getAsString() : "未命名分类";
                PatternAndFontOverlay.H3Category customH3 = new PatternAndFontOverlay.H3Category(Text.literal(categoryName));

                customH3.headerText = root.has("header_text")
                        ? Text.literal(root.get("header_text").getAsString())
                        : Text.translatable(isFont
                                ? "yunbeiuc.gui.sections.custom_fonts.desc"
                                : "yunbeiuc.gui.sections.custom_patterns.desc");

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

            } catch (Exception e) {
                System.err.println(LOG_TAG + " 加载自定义 UI JSON 失败: " + entry.getKey() + " | 错误: " + e.getMessage());
            }
        }
    }

    // 从 JSON 解析 Section
    private static PatternAndFontOverlay.H4Section parseSectionFromJson(JsonObject secObj) {
        Text secTitle = Text.literal(secObj.has("title") ? secObj.get("title").getAsString() : "未命名 Section");
        Text secDesc = Text.literal(secObj.has("description") ? secObj.get("description").getAsString() : "");
        Identifier basePath = new Identifier(secObj.has("basePath") ? secObj.get("basePath").getAsString() : "minecraft:empty/");

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
}
