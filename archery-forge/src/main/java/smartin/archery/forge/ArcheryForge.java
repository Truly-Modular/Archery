package smartin.archery.forge;


import net.neoforged.fml.common.Mod;
import smartin.archery.Archery;

@Mod(Archery.MOD_ID)
public class ArcheryForge {
    public ArcheryForge() {
        Archery.init();
    }
}