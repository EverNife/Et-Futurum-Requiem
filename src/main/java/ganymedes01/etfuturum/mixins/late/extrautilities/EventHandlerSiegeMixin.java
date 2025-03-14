package ganymedes01.etfuturum.mixins.late.extrautilities;

import com.rwtema.extrautils.EventHandlerSiege;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.tileentity.TileEntityBeacon;
import net.minecraft.tileentity.TileEntityHopper;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Iterator;
import java.util.List;

@Mixin(value = EventHandlerSiege.class, remap = false)
public abstract class EventHandlerSiegeMixin {
    @Shadow
    public static boolean hasSigil(EntityPlayer player) {
        return false;
    }

    @Shadow public abstract double sq(double x, double y, double z);

    @Shadow
    public static int[] getStrength(World world, int x, int y, int z) {
        return null;
    }

    @Shadow
    public static int checkChestFire(IInventory chest, boolean destroy) {
        return 0;
    }

    @Shadow
    public static int checkChestEarth(IInventory chest, boolean destroy) {
        return 0;
    }

    @Shadow
    public static int checkChestAir(IInventory chest, boolean destroy) {
        return 0;
    }

    @Shadow
    public static int checkChestWater(IInventory chest, boolean destroy) {
        return 0;
    }

    @Shadow @Final public static int[] ddx;
    @Shadow @Final public static int[] ddz;

    @Shadow
    public static void beginSiege(World world) {
    }

    /**
     * @author EverNife
     * @reason I need to replace the Beacon used
     */
    @Overwrite
    @SubscribeEvent
    public void golemDeath(LivingDeathEvent event) {
        if (!event.entity.worldObj.isRemote && event.entity.worldObj.provider.dimensionId == 1 && event.entity instanceof EntityIronGolem && event.source.getSourceOfDamage() instanceof EntityPlayer) {

            EntityPlayer player = (EntityPlayer) event.source.getSourceOfDamage();
            if (!hasSigil(player)) {
                return;
            }

            List t = event.entity.worldObj.loadedTileEntityList;
            Iterator i$ = t.iterator();

            while (i$.hasNext()) {
                Object aT = i$.next();
                if (aT instanceof TileEntityBeacon) {
                    TileEntityBeacon beacon = (TileEntityBeacon) aT;
                    int x = beacon.xCoord;
                    int y = beacon.yCoord;
                    int z = beacon.zCoord;
                    if (this.sq(event.entity.posX - (double) x - 0.5, event.entity.posY - (double) y - 0.5, event.entity.posZ - (double) z - 0.5) < 300.0) {
                        int[] s = getStrength(event.entity.worldObj, x, y, z);
                        World world = beacon.getWorldObj();
                        if (s[0] == 64) {
                            int debug = 1;
                            boolean flag = true;
                            if (checkChestFire(TileEntityHopper.func_145893_b(world, (double) x, (double) y, (double) (z - 5)), false) < debug) {
                                flag = false;
                            }

                            if (flag && checkChestEarth(TileEntityHopper.func_145893_b(world, (double) x, (double) y, (double) (z + 5)), false) < debug) {
                                flag = false;
                            }

                            if (flag && checkChestAir(TileEntityHopper.func_145893_b(world, (double) (x - 5), (double) y, (double) z), false) < debug) {
                                flag = false;
                            }

                            if (flag && checkChestWater(TileEntityHopper.func_145893_b(world, (double) (x + 5), (double) y, (double) z), false) < debug) {
                                flag = false;
                            }

                            if (flag) {
                                world.func_147480_a(x, y, z, false);

                                int j;
                                for (j = 0; j < 4; ++j) {
                                    world.func_147480_a(x + ddx[j] * 5, y, z + ddz[j] * 5, false);
                                }

                                world.func_147480_a(x, y, z, false);
                                world.createExplosion((Entity) null, (double) x, (double) y, (double) z, 6.0F, true);

                                for (j = 0; j < 4; ++j) {
                                    world.createExplosion((Entity) null, (double) (x + ddx[j] * 5), (double) y, (double) (z + ddz[j] * 5), 3.0F, true);
                                }

                                beginSiege(world);
                                return;
                            }
                        }
                    }
                }
            }
        }

    }

}