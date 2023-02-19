package fuzs.plentyplates.client;

import fuzs.plentyplates.PlentyPlates;
import fuzs.plentyplates.client.packs.TranslucentPackResources;
import fuzs.puzzleslib.client.core.ClientFactories;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.AddPackFindersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLConstructModEvent;

@Mod.EventBusSubscriber(modid = PlentyPlates.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class PlentyPlatesForgeClient {

    @SubscribeEvent
    public static void onConstructMod(final FMLConstructModEvent evt) {
        ClientFactories.INSTANCE.clientModConstructor(PlentyPlates.MOD_ID).accept(new PlentyPlatesClient());
    }

    @SubscribeEvent
    public static void onAddPackFinders(final AddPackFindersEvent evt) {
        if (evt.getPackType() == PackType.CLIENT_RESOURCES) {
            evt.addRepositorySource((packConsumer, packConstructor) -> {
                Pack pack = Pack.create(PlentyPlates.MOD_ID, true, TranslucentPackResources::new, packConstructor, Pack.Position.TOP, PackSource.BUILT_IN);
                if (pack != null) {
                    packConsumer.accept(pack);
                }
            });
        }
    }
}
