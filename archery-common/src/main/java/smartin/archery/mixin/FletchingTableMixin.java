package smartin.archery.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.FletchingTableBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import smartin.archery.screen.FletchingTableScreenHandler;

@Mixin(FletchingTableBlock.class)
public class FletchingTableMixin {
    @Unique
    private static final Component CONTAINER_TITLE = Component.translatable("container.crafting");

    @Inject(
            method = "useWithoutItem(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/phys/BlockHitResult;)Lnet/minecraft/world/InteractionResult;",
            at = @At("HEAD"),
            cancellable = true
    )
    private void fixAp(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult, CallbackInfoReturnable<InteractionResult> cir) {
        if (level.isClientSide) {
            cir.setReturnValue(InteractionResult.SUCCESS);
        } else {
            player.openMenu(new SimpleMenuProvider((i, arg3, arg4) -> {
                return new FletchingTableScreenHandler(i,arg3);
            }, CONTAINER_TITLE));
            //player.awardStat(Stats.INTERACT_WITH_CRAFTING_TABLE);
            cir.setReturnValue(InteractionResult.CONSUME);
        }
    }
}
