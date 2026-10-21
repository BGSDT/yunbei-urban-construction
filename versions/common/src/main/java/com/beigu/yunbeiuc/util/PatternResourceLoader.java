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

public final class PatternResourceLoader {

    private static final String LOG_TAG = "[PatternResourceLoader]";

    public static final String CUSTOM_UI_DIR = "ui_definitions";

    public static final String[] CUSTOM_UI_FILES = {
            "cf_yunbeiuc.json",
            "cf_ocelotsignmod.json",
            "cf_ocelotsignyunbei.json",
            "patterns.json"
    };

    public static boolean isCustomUiFile(String path) {
        String fileName = path.substring(path.lastIndexOf('/') + 1);
        for (String name : CUSTOM_UI_FILES) {
            if (name.equals(fileName)) return true;
        }
        return false;
    }

    public static final String[] CUSTOM_PATTERN_SET_FILES = {
            "cpl_yunbeiuc.json",
            "cpl_ocelotsignmod.json",
            "cpl_ocelotsignyunbei.json"
    };

    public static boolean isPatternSetFile(String path) {
        String fileName = path.substring(path.lastIndexOf('/') + 1);
        for (String name : CUSTOM_PATTERN_SET_FILES) {
            if (name.equals(fileName)) return true;
        }
        return false;
    }

    private PatternResourceLoader() {
    }

    public static void loadAllFromResourceManager() {
        loadAllFromResourceManager(Minecraft.getInstance().getResourceManager());
    }

    public static void loadAllFromResourceManager(ResourceManager manager) {
        TextureAspectCache.clear();

        for (PatternAndFontOverlay.H2Category h2 : PatternAndFontOverlay.REGISTRY) {
            for (PatternAndFontOverlay.H3Category h3 : h2.subCategories) {
                buildTextureCacheForH3(h3, manager);
            }
        }

        loadAdvancedCustomUIFromJson(manager);
        loadCustomPatternSetsFromJson(manager);
        PresetManager.loadFromResourcePacks(manager);
    }

    private static void buildTextureCacheForH3(PatternAndFontOverlay.H3Category h3, ResourceManager manager) {
        for (PatternAndFontOverlay.H4Section h4 : h3.sections) {
            buildTextureCacheDeep(h4, manager);
        }
        for (PatternAndFontOverlay.H3Category child : h3.subCategories) {
            buildTextureCacheForH3(child, manager);
        }
    }

    private static void buildTextureCacheDeep(PatternAndFontOverlay.H4Section section, ResourceManager manager) {
        buildTextureCache(section, manager);
        for (PatternAndFontOverlay.H4Section child : section.childSections) {
            buildTextureCacheDeep(child, manager);
        }
    }

    public static void buildTextureCache(PatternAndFontOverlay.H4Section section) {
        buildTextureCache(section, Minecraft.getInstance().getResourceManager());
    }

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

    private static void initCacheBuckets(PatternAndFontOverlay.H4Section section) {
        if (section.useSubfolders && !section.subFolders.isEmpty()) {
            for (PatternAndFontOverlay.SubFolderDef subDef : section.subFolders) {
                section.cachedTextures.put(subDef.dirName, new ArrayList<>());
            }
        } else {
            section.cachedTextures.put("root", new ArrayList<>());
        }
    }

    private static boolean passesExtensionFilter(PatternAndFontOverlay.H4Section section, String fileName) {
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

                    if (!root.has("sections")) continue;

                    String tabType = root.has("tab") ? root.get("tab").getAsString() : "patterns";
                    boolean isFont = tabType.equals("fonts");

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

    private static PatternAndFontOverlay.H2Category findH2ByKey(String translationKey) {
        String want = Text.translatable(translationKey).getString();
        for (PatternAndFontOverlay.H2Category h2 : PatternAndFontOverlay.REGISTRY) {
            if (h2.title.getString().equals(want)) return h2;
        }
        return null;
    }

    private static PatternAndFontOverlay.H3Category findH3ByKey(PatternAndFontOverlay.H2Category h2, String translationKey) {
        String want = Text.translatable(translationKey).getString();
        for (PatternAndFontOverlay.H3Category h3 : h2.subCategories) {
            if (h3.title.getString().equals(want)) return h3;
        }
        return null;
    }

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

    private static PatternAndFontOverlay.H4Section parsePatternSetGroup(JsonObject groupObj) {
        String title = groupObj.has("title") ? groupObj.get("title").getAsString() : "未命名图案集";
        String description = groupObj.has("description") ? groupObj.get("description").getAsString() : "";

        PatternAndFontOverlay.H4Section section = new PatternAndFontOverlay.H4Section(
                Text.literal(title), Text.literal(description),
                VersionServices.resources().create("yunbeiuc", "textures/block/sign/"))
                .setLabeledMode();

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
