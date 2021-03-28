package dan.betterbench;

import net.minecraft.init.Blocks;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.Logger;

@Mod(   modid = ExampleMod.MODID,
        name = ExampleMod.NAME,
        version = ExampleMod.VERSION,
        acceptedMinecraftVersions = "[1.12,)",
        useMetadata = true)
public class ExampleMod {
    public static final String MODID = "betterbench";
    public static final String NAME = "dan's Better Bench";
    public static final String VERSION = "@VERSION@";

    private static Logger logger;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        logger = event.getModLog();
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        // some example code
        logger.info("DIRT BLOCK >> {}", Blocks.DIRT.getRegistryName());
    }
}
