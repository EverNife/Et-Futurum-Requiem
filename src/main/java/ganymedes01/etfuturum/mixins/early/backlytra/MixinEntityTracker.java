package ganymedes01.etfuturum.mixins.early.backlytra;

import ganymedes01.etfuturum.elytra.IElytraEntityTracker;
import net.minecraft.entity.EntityTracker;
import net.minecraft.entity.EntityTrackerEntry;
import net.minecraft.util.IntHashMap;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(EntityTracker.class)
public class MixinEntityTracker implements IElytraEntityTracker {

	@Shadow
	private IntHashMap trackedEntityIDs;

	@Override
	public EntityTrackerEntry etfu$getEntityTracker(int entityId) {
		return (EntityTrackerEntry) trackedEntityIDs.lookup(entityId);
	}

}
