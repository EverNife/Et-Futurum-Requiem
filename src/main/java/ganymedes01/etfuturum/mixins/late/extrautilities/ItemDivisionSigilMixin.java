package ganymedes01.etfuturum.mixins.late.extrautilities;

import com.rwtema.extrautils.ActivationRitual;
import com.rwtema.extrautils.EventHandlerSiege;
import com.rwtema.extrautils.item.ItemDivisionSigil;
import com.rwtema.extrautils.network.packets.PacketTempChatMultiline;
import ganymedes01.etfuturum.ModBlocks;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityHopper;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = ItemDivisionSigil.class)
public class ItemDivisionSigilMixin {

    @Inject(method = "func_77648_a(Lnet/minecraft/item/ItemStack;Lnet/minecraft/entity/player/EntityPlayer;Lnet/minecraft/world/World;IIIIFFF)Z", at = @At(value = "HEAD"), cancellable = true, remap = false)
    public void onItemUse(ItemStack item, EntityPlayer player, World world, int x, int y, int z, int side, float par8, float par9, float par10, CallbackInfoReturnable<Boolean> cir){

        if (world.isRemote) {

            if ((!item.hasTagCompound() || !item.getTagCompound().hasKey("damage")) && world.getBlock(x, y, z) == ModBlocks.ENCHANTMENT_TABLE.get()) {

                boolean flag = true;

                PacketTempChatMultiline.addChatComponentMessage(new ChatComponentText("§5Ritual de Ativação"));

                if (ActivationRitual.redstoneCirclePresent(world, x, y, z)) {
                    PacketTempChatMultiline.addChatComponentMessage(new ChatComponentText("§a- O Altar tem um círculo de redstone"));

                    if (ActivationRitual.altarOnEarth(world, x, y, z)) {
                        PacketTempChatMultiline.addChatComponentMessage(new ChatComponentText("§a- O Altar e o círculo estão sobre a terra"));
                    } else {
                        flag = false;
                        PacketTempChatMultiline.addChatComponentMessage(new ChatComponentText("§c! O Altar e o círculo não estão sobre a terra"));
                    }

                } else {
                    PacketTempChatMultiline.addChatComponentMessage(new ChatComponentText("§c! O Altar não possui um círculo de redstone"));
                    flag = false;
                }


                if (ActivationRitual.altarCanSeeMoon(world, x, y, z)) {
                    PacketTempChatMultiline.addChatComponentMessage(new ChatComponentText("§a- O Altar pode ver a lua"));
                } else {
                    PacketTempChatMultiline.addChatComponentMessage(new ChatComponentText("§c! O Altar não pode ver a lua"));
                    flag = false;
                }

                if (ActivationRitual.naturalEarth(world, x, y, z)) {
                    PacketTempChatMultiline.addChatComponentMessage(new ChatComponentText("§a- O Altar tem grama suficiente"));
                } else {
                    PacketTempChatMultiline.addChatComponentMessage(new ChatComponentText("§c! O Altar não tem grama suficiente"));
                    flag = false;
                }

                if (ActivationRitual.altarInDarkness_Client(world, x, y, z)) {
                    PacketTempChatMultiline.addChatComponentMessage(new ChatComponentText("§a- O Altar está na escuridão"));
                } else {
                    PacketTempChatMultiline.addChatComponentMessage(new ChatComponentText("§c! O Altar não pode estar perto de luz"));
                }

                int time = ActivationRitual.checkTime(world.getWorldTime());

                if (time == -1) {
                    PacketTempChatMultiline.addChatComponentMessage(new ChatComponentText("§c! Muito cedo, o sacrifício deve ser feito à meia-noite."));
                } else if (time == 1) {
                    PacketTempChatMultiline.addChatComponentMessage(new ChatComponentText("§c! Muito tarde, o sacrifício deve ser feito à meia-noite."));
                } else {
                    PacketTempChatMultiline.addChatComponentMessage(new ChatComponentText("§a- A hora está correta"));
                    if (flag) {
                        PacketTempChatMultiline.addChatComponentMessage(new ChatComponentText("§2Performe o sacrifício"));
                    }
                }

            }

            return;
        }

        if (item.hasTagCompound() && item.getTagCompound().hasKey("damage") && (world.getBlock(x, y, z) == ModBlocks.BEACON.get() || world.getBlock(x, y, z) == Blocks.beacon)) {

            PacketTempChatMultiline.addChatComponentMessage(new ChatComponentText("§5Ritual de Estabilização"));
            PacketTempChatMultiline.addChatComponentMessage(new ChatComponentText(""));

            if (world.provider.dimensionId != 1) {

                if (world.provider.dimensionId == -1) {
                    PacketTempChatMultiline.addChatComponentMessage(new ChatComponentText("§c! Muito quente"));
                } else {
                    PacketTempChatMultiline.addChatComponentMessage(new ChatComponentText("§c! Muita terra natural"));
                }

            } else {

                int water = 0;
                int earth = 0;
                int fire = 0;
                int air = 0;

                if (TileEntityHopper.func_145893_b(world, x, y, z - 5) != null) {
                    PacketTempChatMultiline.addChatComponentMessage(new ChatComponentText("§4- Para o norte, Filho do Fogo: " + (fire = EventHandlerSiege.checkChestFire(TileEntityHopper.func_145893_b(world, x, y, z - 5), false)) + " / 12"));
                } else {
                    PacketTempChatMultiline.addChatComponentMessage(new ChatComponentText("§c Baú do norte não está presente"));
                }

                if (TileEntityHopper.func_145893_b(world, x, y, z + 5) != null) {
                    PacketTempChatMultiline.addChatComponentMessage(new ChatComponentText("§2- Para o sul, Presentes da Terra: " + (earth = EventHandlerSiege.checkChestEarth(TileEntityHopper.func_145893_b(world, x, y, z + 5), false)) + " / 12"));
                } else {
                    PacketTempChatMultiline.addChatComponentMessage(new ChatComponentText("§c! Baú do sul não está presente"));
                }

                if (TileEntityHopper.func_145893_b(world, x + 5, y, z) != null) {
                    PacketTempChatMultiline.addChatComponentMessage(new ChatComponentText("§b- Para o leste, Descendentes da Água: " + (water = EventHandlerSiege.checkChestWater(TileEntityHopper.func_145893_b(world, x + 5, y, z), false)) + " / 12"));
                } else {
                    PacketTempChatMultiline.addChatComponentMessage(new ChatComponentText("§c! Baú do leste não está presente"));
                }

                if (TileEntityHopper.func_145893_b(world, x - 5, y, z) != null) {
                    PacketTempChatMultiline.addChatComponentMessage(new ChatComponentText("§e- Para o oeste, Especiarias do Ar: " + (air = EventHandlerSiege.checkChestAir(TileEntityHopper.func_145893_b(world, x - 5, y, z), false)) + " / 12"));
                } else {
                    PacketTempChatMultiline.addChatComponentMessage(new ChatComponentText("§c! Baú do oeste não está present"));
                }

                PacketTempChatMultiline.addChatComponentMessage(new ChatComponentText(""));

                int[] k = EventHandlerSiege.getStrength(world, x, y, z);

                if (k[1] == 0) {
                    PacketTempChatMultiline.addChatComponentMessage(new ChatComponentText("§dMarcações do Ritual:§c Nenhuma marca foi encontrada"));
                } else if (k[0] == 0) {
                    PacketTempChatMultiline.addChatComponentMessage(new ChatComponentText("§dMarcações do Ritual:§c Somente 1 tipo de marcação for encontrada"));
                } else {
                    StringBuilder t = new StringBuilder(k[0] + "");

                    for(int i = 1; i < k.length; ++i) {
                        t.append(" / ").append(k[i]).append(" / 64");
                    }

                    PacketTempChatMultiline.addChatComponentMessage(new ChatComponentText("§dMarcações do Ritual:§a Força - " + t));
                }

                if (fire >= 12 && earth >= 12 && water >= 12 && air >= 12 && k[0] >= 64) {
                    PacketTempChatMultiline.addChatComponentMessage(new ChatComponentText(""));
                    PacketTempChatMultiline.addChatComponentMessage(new ChatComponentText("§aTudo está preparado."));
                    PacketTempChatMultiline.addChatComponentMessage(new ChatComponentText(""));
                    PacketTempChatMultiline.addChatComponentMessage(new ChatComponentText("§aSacrifique alguém que se sacrificaria."));
                }
            }
        }

        PacketTempChatMultiline.sendCached(player);
        cir.setReturnValue(true);
    }

}
