package fuzs.plentyplates.world.level.block;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.PressurePlateBlock;
import net.minecraft.world.phys.AABB;

import java.util.List;

public class ModPressurePlateBlock extends PressurePlateBlock {
    private final Sensitivity sensitivity;

    public ModPressurePlateBlock(Sensitivity sensitivity, Properties properties) {
        super(sensitivity, properties);
        this.sensitivity = sensitivity;
    }

    protected void playOnSound(LevelAccessor level, BlockPos pos) {
        level.playSound(null, pos, SoundEvents.STONE_PRESSURE_PLATE_CLICK_ON, SoundSource.BLOCKS, 0.3F, 0.6F);
    }

    protected void playOffSound(LevelAccessor level, BlockPos pos) {
        level.playSound(null, pos, SoundEvents.STONE_PRESSURE_PLATE_CLICK_OFF, SoundSource.BLOCKS, 0.3F, 0.5F);
    }

    protected int getSignalStrength(Level level, BlockPos pos) {
        AABB aABB = TOUCH_AABB.move(pos);
        List<? extends Entity> list;
        switch (this.sensitivity) {
            case EVERYTHING -> list = level.getEntities(null, aABB);
            case MOBS -> list = level.getEntitiesOfClass(LivingEntity.class, aABB);
            default -> {
                return 0;
            }
        }

        if (!list.isEmpty()) {

            for (Entity entity : list) {
                if (!entity.isIgnoringBlockTriggers()) {
                    return 15;
                }
            }
        }

        return 0;
    }
}
