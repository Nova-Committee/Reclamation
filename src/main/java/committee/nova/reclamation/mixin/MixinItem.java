package committee.nova.reclamation.mixin;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PlaceOnWaterBlockItem;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.FallingBlock;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Item.class)
public abstract class MixinItem {
    @Shadow
    protected static BlockHitResult getPlayerPOVHitResult(Level level, Player player, ClipContext.Fluid ctx) {
        throw new RuntimeException();
    }

    @Inject(method = "use", at = @At("RETURN"), cancellable = true)
    private void inject$use(Level level, Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResultHolder<ItemStack>> cir) {
        if (cir.getReturnValue().getResult().consumesAction()) return;
        if (!((Item) (Object) this instanceof BlockItem b) ||
                b instanceof PlaceOnWaterBlockItem ||
                !(b.getBlock() instanceof FallingBlock)) return;
        BlockHitResult result = getPlayerPOVHitResult(level, player, ClipContext.Fluid.ANY);
        BlockHitResult result1 = result.withPosition(result.getBlockPos().above());
        InteractionResult interactionresult = b.useOn(new UseOnContext(player, hand, result1));
        cir.setReturnValue(new InteractionResultHolder<>(interactionresult, player.getItemInHand(hand)));
    }
}
