package smartin.archery.fabric;

import smartin.archery.Archery;
import net.fabricmc.api.ModInitializer;

public class ArcheryFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        Archery.init();
    }
}