package fuzs.plentyplates;

import fuzs.plentyplates.data.*;
import fuzs.puzzleslib.core.CommonFactories;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLConstructModEvent;

@Mod(PlentyPlates.MOD_ID)
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class PlentyPlatesForge {

    @SubscribeEvent
    public static void onConstructMod(final FMLConstructModEvent evt) {
        CommonFactories.INSTANCE.modConstructor(PlentyPlates.MOD_ID).accept(new PlentyPlates());
    }

    @SubscribeEvent
    public static void onGatherData(final GatherDataEvent evt) {
        DataGenerator dataGenerator = evt.getGenerator();
        final ExistingFileHelper fileHelper = evt.getExistingFileHelper();
        dataGenerator.addProvider(true, new ModBlockStateProvider(dataGenerator, PlentyPlates.MOD_ID, fileHelper));
        dataGenerator.addProvider(true, new ModBlockTagsProvider(dataGenerator, PlentyPlates.MOD_ID, fileHelper));
        dataGenerator.addProvider(true, new ModLanguageProvider(dataGenerator, PlentyPlates.MOD_ID));
        dataGenerator.addProvider(true, new ModLootTableProvider(dataGenerator, PlentyPlates.MOD_ID));
        dataGenerator.addProvider(true, new ModRecipeProvider(dataGenerator));
    }
}
