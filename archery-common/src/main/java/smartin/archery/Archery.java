package smartin.archery;

import dev.architectury.platform.Platform;
import dev.architectury.registry.menu.MenuRegistry;
import dev.architectury.utils.Env;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.inventory.MenuType;
import smartin.archery.screen.FletchingTableScreen;
import smartin.archery.screen.FletchingTableScreenHandler;
import smartin.miapi.Miapi;
import smartin.miapi.registries.RegistryInventory;

public class Archery {
    public static final String MOD_ID = "tm_archery";
    public static MenuType<FletchingTableScreenHandler> FLETCHING_TABLE_SCREEN_PROVIDER;

    public static void init() {
        RegistryInventory.register(RegistryInventory.MENU_TYPE_REGISTRAR, Miapi.id(MOD_ID, "fletching_table"), () ->
                        new MenuType<>(FletchingTableScreenHandler::new, FeatureFlagSet.of()),
                scr -> {
                    FLETCHING_TABLE_SCREEN_PROVIDER = scr;
                    if (Platform.getEnvironment() == Env.CLIENT) clientInit();
                });
    }

    @Environment(EnvType.CLIENT)
    public static void clientInit() {
        MenuRegistry.registerScreenFactory(FLETCHING_TABLE_SCREEN_PROVIDER, FletchingTableScreen::new);
    }
}