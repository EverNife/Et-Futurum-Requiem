package ganymedes01.etfuturum.mixins.early.playerssleepingpercentage;

import ganymedes01.etfuturum.core.utils.Utils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.profiler.Profiler;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.WorldServer;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.storage.ISaveHandler;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Iterator;
import java.util.List;

import static ganymedes01.etfuturum.gamerule.PlayersSleepingPercentage.GAMERULE_NAME;
import static ganymedes01.etfuturum.gamerule.PlayersSleepingPercentage.INSTANCE;

@Mixin(WorldServer.class)
public abstract class MixinWorldServer extends World {
	@Shadow
	private boolean allPlayersSleeping;

	public MixinWorldServer(ISaveHandler p_i45368_1_, String p_i45368_2_, WorldProvider p_i45368_3_, WorldSettings p_i45368_4_, Profiler p_i45368_5_) {
		super(p_i45368_1_, p_i45368_2_, p_i45368_3_, p_i45368_4_, p_i45368_5_);
		throw new ArithmeticException("2 + 2 = 5 ???");
	}

	@Inject(method = "updateAllPlayersSleepingFlag", at = @At("HEAD"), cancellable = true)
	public void hhheheheheeh(CallbackInfo ctx) {
		INSTANCE.percentrillo = Integer.parseInt(this.getGameRules().getGameRuleStringValue(GAMERULE_NAME));
		if (INSTANCE.percentrillo > 100) {
			this.allPlayersSleeping = false;
			ctx.cancel(/* /r/nosleep, vanilla behaviour */);
		} else if (INSTANCE.percentrillo < 100) {
			INSTANCE.sleepyPlayers.clear();
			List<EntityPlayer> real = Utils.getListWithoutSpectators(this.playerEntities);
			int cap = (int) Math.ceil(real.size() * INSTANCE.percentrillo * 0.01f);
			for (EntityPlayer player : this.playerEntities) {
				if (player.isPlayerSleeping()) {
					INSTANCE.sleepyPlayers.add(player);
					if (INSTANCE.sleepyPlayers.size() >= cap) {
						this.allPlayersSleeping = true;
						break;
					}
				}
			}

			if (!INSTANCE.sleepyPlayers.isEmpty() && cap > 0) {
				for (EntityPlayer paypiggy : this.playerEntities) {
					paypiggy.addChatMessage(new ChatComponentTranslation("sleep.players_sleeping", INSTANCE.sleepyPlayers.size(), cap));
				}
			}
			ctx.cancel();
		}
	}

	@Redirect(method = "areAllPlayersAsleep", at = @At(value = "FIELD", target = "Lnet/minecraft/world/WorldServer;playerEntities:Ljava/util/List;", opcode = Opcodes.GETFIELD))
	public List<EntityPlayer> baited(WorldServer instance) {
		return INSTANCE.sleepyPlayers.isEmpty() ? this.playerEntities : INSTANCE.sleepyPlayers;
	}

	@Inject(method = "areAllPlayersAsleep", at = @At(value = "INVOKE", target = "Ljava/util/List;iterator()Ljava/util/Iterator;"), cancellable = true)
	public void turbofast(CallbackInfoReturnable<Boolean> ctx) {
		if (INSTANCE.percentrillo < 1) ctx.setReturnValue(true);
	}

	@Inject(method = "wakeAllPlayers", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/EntityPlayer;wakeUpPlayer(ZZZ)V"), locals = LocalCapture.CAPTURE_FAILHARD)
	public void broadcast(CallbackInfo ctx, Iterator iterator, EntityPlayer player) {
		if (INSTANCE.percentrillo > 0 && INSTANCE.percentrillo < 100) {
			player.addChatMessage(new ChatComponentTranslation("sleep.skipping_night"));
		}
	}
}
