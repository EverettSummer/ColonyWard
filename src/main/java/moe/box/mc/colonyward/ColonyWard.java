package moe.box.mc.colonyward;

import com.minecolonies.api.colony.IColonyManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.network.FMLNetworkConstants;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

// The value here should match an entry in the META-INF/mods.toml file
@Mod("colonyward")
public class ColonyWard {
    // Directly reference a log4j logger.
    private static final Logger LOGGER = LogManager.getLogger();
    public static Configuration config;

    public ColonyWard() {
        //Make sure the mod being absent on the other network side does not cause the client to display the server as incompatible
        ModLoadingContext.get().registerExtensionPoint(ExtensionPoint.DISPLAYTEST, () -> Pair.of(() -> FMLNetworkConstants.IGNORESERVERONLY, (a, b) -> true));
        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);
        ForgeConfigSpec.Builder configBuilder = new ForgeConfigSpec.Builder();

        config = new Configuration(configBuilder);

        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, configBuilder.build());
    }

    @SubscribeEvent
    public void onEntitySpawnEvent(LivingSpawnEvent.CheckSpawn event) {
        // event.setResult(Result.DENY) for inside colony
        Entity entity = event.getEntity();
        ResourceLocation rl = entity.getType().getRegistryName();
        if (rl == null) {
            return;
        }
        String mobFullName = rl.toString();
        if (!IsEntityInBlackList(mobFullName)) {
            return;
        }
        double x = entity.getX();
        double y = entity.getY();
        double z = entity.getZ();

        IWorld world = event.getWorld();
        if (world instanceof World) {

            boolean isInColony = IColonyManager.getInstance().isCoordinateInAnyColony((World)world, new BlockPos(x, y, z));
//            System.out.println(mobFullName + " at x: " + x + ", z: " + z + " isInColony: " + isInColony);
            if (isInColony) {
                event.setResult(Event.Result.DENY);
            }
        }
    }

    private boolean IsEntityInBlackList(String entityName) {
        List<? extends String> mobList = config.mobList.get();
        if (mobList.contains(entityName)) {
            return true;
        }
        String[] nameArray = entityName.split(":");
        if (nameArray.length > 1) {
            String modId = nameArray[0];
            List<? extends String> modList = config.modList.get();
            return modList.contains(modId);
        }
        return false;
    }
}
