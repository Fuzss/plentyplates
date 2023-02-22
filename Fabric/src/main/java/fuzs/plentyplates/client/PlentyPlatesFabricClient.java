package fuzs.plentyplates.client;

import fuzs.plentyplates.PlentyPlates;
import fuzs.plentyplates.client.packs.TranslucentPackResources;
import fuzs.plentyplates.mixin.client.accessor.PackRepositoryAccessor;
import fuzs.puzzleslib.client.core.ClientFactories;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.Minecraft;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.server.packs.repository.RepositorySource;

import java.util.LinkedHashSet;
import java.util.Set;

public class PlentyPlatesFabricClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        ClientFactories.INSTANCE.clientModConstructor(PlentyPlates.MOD_ID).accept(new PlentyPlatesClient());
        onAddPackFinders();
    }

    private static void onAddPackFinders() {
        PackRepository resourcePackRepository = Minecraft.getInstance().getResourcePackRepository();
        Set<RepositorySource> repositorySources = ((PackRepositoryAccessor) resourcePackRepository).plentyplates$getSources();
        // Fabric Api internally replaces the immutable set already and leaves it like that, just verify in case internal implementation changes
        if (!(repositorySources instanceof LinkedHashSet<RepositorySource>)) {
            repositorySources = new LinkedHashSet<>(repositorySources);
            ((PackRepositoryAccessor) resourcePackRepository).plentyplates$setSources(repositorySources);
        }
        repositorySources.add(TranslucentPackResources.buildRepositorySource());
    }
}
