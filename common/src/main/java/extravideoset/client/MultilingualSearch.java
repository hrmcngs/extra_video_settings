package extravideoset.client;

import com.mojang.logging.LogUtils;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.item.ItemStack;

import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/** Search aliases only; never changes the language used to render the UI. */
public final class MultilingualSearch {
    private static volatile Map<String, List<String>> translations = Map.of();

    private MultilingualSearch() {}

    public static void reload(ResourceManager resources) {
        Map<String, Set<String>> names = new LinkedHashMap<>();
        resources.listResourceStacks("lang", id -> id.getPath().endsWith(".json"))
                .forEach((id, stack) -> {
                    // Resolve pack priority per language file before collecting aliases.
                    Map<String, String> merged = new LinkedHashMap<>();
                    for (var resource : stack) {
                        try (InputStream input = resource.open()) {
                            Language.loadFromJson(input, merged::put);
                        } catch (Exception e) {
                            LogUtils.getLogger().warn("Unable to read search translations: {}", id, e);
                        }
                    }
                    merged.forEach((key, value) -> names.computeIfAbsent(key,
                            ignored -> new LinkedHashSet<>()).add(value.toLowerCase(Locale.ROOT)));
                });
        Map<String, List<String>> index = new LinkedHashMap<>();
        names.forEach((key, values) -> index.put(key, List.copyOf(values)));
        translations = Map.copyOf(index);
    }

    public static List<String> names(ItemStack stack) {
        if (stack.isEmpty()) return List.of();
        Set<String> result = new LinkedHashSet<>(translations.getOrDefault(
                stack.getItem().getDescriptionId(stack), List.of()));
        // Includes variant-specific names, e.g. potions, when supplied by the item.
        if (stack.getHoverName().getContents() instanceof TranslatableContents translated) {
            result.addAll(translations.getOrDefault(translated.getKey(), List.of()));
        }
        return List.copyOf(result);
    }
}
