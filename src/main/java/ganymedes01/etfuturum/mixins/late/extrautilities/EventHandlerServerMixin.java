package ganymedes01.etfuturum.mixins.late.extrautilities;

import com.rwtema.extrautils.ActivationRitual;
import com.rwtema.extrautils.EventHandlerServer;
import com.rwtema.extrautils.ExtraUtils;
import com.rwtema.extrautils.item.ItemDivisionSigil;
import com.rwtema.extrautils.network.NetworkHandler;
import com.rwtema.extrautils.network.packets.PacketTempChat;
import ganymedes01.etfuturum.ModBlocks;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = EventHandlerServer.class, remap = false)
public class EventHandlerServerMixin {

    @Inject(method = "ActivationRitual(Lnet/minecraftforge/event/entity/living/LivingDeathEvent;)V", at = @At("HEAD"), cancellable = true)
    public void ActivationRitual(LivingDeathEvent event, CallbackInfo ci) {

        if (ExtraUtils.divisionSigil != null || ExtraUtils.cursedEarth != null) {
            if (event.source.getSourceOfDamage() instanceof EntityPlayer) {
                World world = event.entityLiving.worldObj;

                if (!world.isRemote) {
                    return;
                }

                EntityPlayer player = (EntityPlayer) event.source.getSourceOfDamage();

                int slot;
                if (ExtraUtils.divisionSigil != null) {
                    boolean sigilFound = false;

                    for (slot = 0; slot < player.inventory.getSizeInventory(); ++slot) {
                        ItemStack item = player.inventory.getStackInSlot(slot);
                        if (item != null && item.getItem() == ExtraUtils.divisionSigil) {
                            sigilFound = true;
                            break;
                        }
                    }

                    if (!sigilFound) {
                        return;
                    }
                }

                int x = (int) event.entityLiving.posX;
                int y = (int) event.entityLiving.boundingBox.minY;
                int z = (int) event.entityLiving.posZ;
                boolean found = false;

                long time = world.getWorldTime() % 24000L;

                if (time >= 12000L && time <= 24000L) {

                    int i;
                    int j;
                    for (i = -2; !found & i <= 2; ++i) {
                        for (j = -2; !found & j <= 0; ++j) {
                            for (int dz = -2; !found & dz <= 2; ++dz) {

                                Block block = world.getBlock(x + i, y + j, z + dz);
                                if (block == Blocks.enchanting_table || block == ModBlocks.ENCHANTMENT_TABLE.get()) {
                                    found = true;
                                    x += i;
                                    y += j;
                                    z += dz;
                                }
                            }
                        }
                    }

                    if (found) {
                        Block block = world.getBlock(x, y, z);
                        if (block == Blocks.enchanting_table || block == ModBlocks.ENCHANTMENT_TABLE.get()) {
                            if (ActivationRitual.redstoneCirclePresent(world, x, y, z)) {
                                if (!ActivationRitual.altarCanSeeMoon(world, x, y, z)) {
                                    PacketTempChat.sendChat(player, "§cO Ritual de Ativação falhou: O altar não pode ver a lua");
                                } else if (!ActivationRitual.altarOnEarth(world, x, y, z)) {
                                    PacketTempChat.sendChat(player, "§cO Ritual de Ativação falhou: O altar e o círculo precisam estar sobre grama");
                                } else if (!ActivationRitual.altarInDarkness(world, x, y, z)) {
                                    PacketTempChat.sendChat(player, "§cO Ritual de Ativação falhou: O altar está muito próximo a luz");
                                } else if (!ActivationRitual.naturalEarth(world, x, y, z)) {
                                    PacketTempChat.sendChat(player, "§cO Ritual de Ativação falhou: O altar precisa de mais grama");
                                } else {
                                    i = ActivationRitual.checkTime(world.getWorldTime());
                                    if (i == -1) {
                                        PacketTempChat.sendChat(player, "§cRitual de Ativação falhou: Muito cedo");
                                    } else if (i == 1) {
                                        PacketTempChat.sendChat(player, "§cRitual de Ativação falhou: Muito tarde");
                                    } else {

                                        ActivationRitual.startRitual(world, x, y, z, player);

                                        NetworkHandler.sendSoundEvent(world, 0, (float) x + 0.5F, (float) y + 0.5F, (float) z + 0.5F);

                                        if (ExtraUtils.divisionSigil != null) {
                                            for (j = 0; j < player.inventory.getSizeInventory(); ++j) {
                                                ItemStack item = player.inventory.getStackInSlot(j);
                                                if (item != null && item.getItem() == ExtraUtils.divisionSigil) {
                                                    NBTTagCompound tags;
                                                    if (item.hasTagCompound()) {
                                                        tags = item.getTagCompound();
                                                    } else {
                                                        tags = new NBTTagCompound();
                                                    }

                                                    tags.setInteger("damage", ItemDivisionSigil.maxdamage);
                                                    item.setTagCompound(tags);
                                                }
                                            }
                                        }

                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        ci.cancel();
    }

}