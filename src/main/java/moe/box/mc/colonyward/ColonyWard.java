package moe.box.mc.colonyward;

import com.minecolonies.api.colony.IColonyManager;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.living.MobSpawnEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod("colonyward")
public class ColonyWard {
    private static final Logger LOGGER = LogManager.getLogger();
    public static Configuration config;

    public ColonyWard(IEventBus modEventBus, ModContainer modContainer) {
        NeoForge.EVENT_BUS.register(this);
        ModConfigSpec.Builder configBuilder = new ModConfigSpec.Builder();

        config = new Configuration(configBuilder);
        modContainer.registerConfig(ModConfig.Type.COMMON, configBuilder.build());
    }

    @SubscribeEvent
    public void onEntitySpawnEvent(MobSpawnEvent.PositionCheck event) {
        Mob entity = event.getEntity();
        ResourceLocation entityKey = EntityType.getKey(entity.getType());

        LOGGER.debug("Attempting to spawn mob: " + entityKey);

        if (!config.isEntityInBlackList(entityKey, event.getSpawnType())) {
            LOGGER.debug("Mob " + entityKey + " is not in the blacklist");
            return;
        }

        if (IColonyManager.getInstance().isCoordinateInAnyColony(entity.level(), BlockPos.containing(entity.getX(), entity.getY(), entity.getZ()))) {
            LOGGER.info("Mob " + entityKey + " attempted to spawn in a colony, spawn cancelled");
            event.setResult(MobSpawnEvent.PositionCheck.Result.FAIL);
        }
    }
}
