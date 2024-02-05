package fuzs.plentyplates;

import fuzs.plentyplates.data.*;
import fuzs.puzzleslib.api.core.v1.ModConstructor;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLConstructModEvent;

import java.util.concurrent.CompletableFuture;

@Mod(PlentyPlates.MOD_ID)
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class PlentyPlatesForge {

    @SubscribeEvent
    public static void onConstructMod(final FMLConstructModEvent evt) {
        ModConstructor.construct(PlentyPlates.MOD_ID, PlentyPlates::new);
    }

    @SubscribeEvent
    public static void onGatherData(final GatherDataEvent evt) {
        final DataGenerator dataGenerator = evt.getGenerator();
        final PackOutput packOutput = dataGenerator.getPackOutput();
        final CompletableFuture<HolderLookup.Provider> lookupProvider = evt.getLookupProvider();
        final ExistingFileHelper fileHelper = evt.getExistingFileHelper();
        dataGenerator.addProvider(true, new ModBlockStateProvider(packOutput, PlentyPlates.MOD_ID, fileHelper));
        dataGenerator.addProvider(true, new ModBlockTagsProvider(packOutput, lookupProvider, PlentyPlates.MOD_ID, fileHelper));
        dataGenerator.addProvider(true, new ModLanguageProvider(packOutput, PlentyPlates.MOD_ID));
        dataGenerator.addProvider(true, new ModBlockLootProvider(packOutput, PlentyPlates.MOD_ID));
        dataGenerator.addProvider(true, new ModRecipeProvider(packOutput));
        dataGenerator.addProvider(true, new ModSpriteSourceProvider(packOutput, PlentyPlates.MOD_ID, fileHelper));
    }
}
