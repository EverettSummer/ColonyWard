package moe.box.mc.colonyward;

import com.minecolonies.api.colony.IColonyManager;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.IExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.network.NetworkConstants;
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
        MinecraftForge.EVENT_BUS.register(this);
        ForgeConfigSpec.Builder configBuilder = new ForgeConfigSpec.Builder();

        config = new Configuration(configBuilder);
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, configBuilder.build());
    }

    @SubscribeEvent
    public void onEntitySpawnEvent(LivingSpawnEvent.CheckSpawn event) {
        Entity entity = event.getEntity();
        ResourceLocation entityKey = EntityType.getKey(entity.getType());

        if(!config.isEntityInBlackList(entityKey, event.getSpawnReason()))
            return;

        if(IColonyManager.getInstance().isCoordinateInAnyColony(entity.getLevel(), new BlockPos(entity.getX(), entity.getY(), entity.getZ())))
            event.setResult(Event.Result.DENY);
    }
}
