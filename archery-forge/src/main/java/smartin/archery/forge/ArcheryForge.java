package smartin.archery.forge;

import dev.architectury.platform.forge.EventBuses;
import smartin.archery.Archery;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(Archery.MOD_ID)
public class ArcheryForge {
    public ArcheryForge() {
		// Submit our event bus to let architectury register our content on the right time
        EventBuses.registerModEventBus(Archery.MOD_ID, FMLJavaModLoadingContext.get().getModEventBus());
            Archery.init();
    }
}