package moe.box.mc.colonyward;

import com.google.common.collect.Lists;
import net.minecraftforge.common.ForgeConfigSpec;

import java.util.ArrayList;
import java.util.List;

public class Configuration {
    public ForgeConfigSpec.ConfigValue<List<? extends String>> mobList;
    public ForgeConfigSpec.ConfigValue<List<? extends String>> modList;
    public Configuration(ForgeConfigSpec.Builder builder) {
        builder.comment("General Settings").push("general");
        mobList = builder.comment("mob id list that want to prevent from spawning inside colony")
                .defineList("mobNameList", Lists.newArrayList("lycanitesmobs:roc"), o -> o instanceof String);
        modList = builder.comment("mod id list that want to prevent from spawning inside colony")
                .defineList("modIdList", Lists.newArrayList("lycanitesmobs"), o -> o instanceof String);
        builder.pop();
    }
}
