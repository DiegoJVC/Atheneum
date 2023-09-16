package com.cobelpvp.atheneum.util;

import com.google.common.collect.ImmutableSet;
import net.minecraft.server.v1_7_R4.*;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_7_R4.CraftWorld;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.Set;

public final class BlockUtils {
    private static final Set<Material> INTERACTABLE;

    static {
        INTERACTABLE = (Set) ImmutableSet.of(Material.FENCE_GATE, Material.FURNACE, Material.BURNING_FURNACE, Material.BREWING_STAND, Material.CHEST, Material.HOPPER, (Object[]) new Material[]{Material.DISPENSER, Material.WOODEN_DOOR, Material.STONE_BUTTON, Material.WOOD_BUTTON, Material.TRAPPED_CHEST, Material.TRAP_DOOR, Material.LEVER, Material.DROPPER, Material.ENCHANTMENT_TABLE, Material.BED_BLOCK, Material.ANVIL, Material.BEACON});
    }

    public static boolean isInteractable(final Block block) {
        return isInteractable(block.getType());
    }

    public static boolean isInteractable(final Material material) {
        return BlockUtils.INTERACTABLE.contains(material);
    }

    public static boolean setBlockFast(final World world, final int x, final int y, final int z, final int blockId, final byte data) {
        final net.minecraft.server.v1_7_R4.World w = ((CraftWorld) world).getHandle();
        final Chunk chunk = w.getChunkAt(x >> 4, z >> 4);
        return a(chunk, x & 0xF, y, z & 0xF, net.minecraft.server.v1_7_R4.Block.getById(blockId), data);
    }

    private static void queueChunkForUpdate(final Player player, final int cx, final int cz) {
        ((CraftPlayer) player).getHandle().chunkCoordIntPairQueue.add(new ChunkCoordIntPair(cx, cz));
    }

    private static boolean a(final Chunk that, final int i, final int j, final int k, final net.minecraft.server.v1_7_R4.Block block, final int l) {
        final int i2 = k << 4 | i;
        if (j >= that.b[i2] - 1) {
            that.b[i2] = -999;
        }
        final int j2 = that.heightMap[i2];
        final net.minecraft.server.v1_7_R4.Block block2 = that.getType(i, j, k);
        final int k2 = that.getData(i, j, k);
        if (block2 == block && k2 == l) {
            return false;
        }
        boolean flag = false;
        ChunkSection chunksection = that.getSections()[j >> 4];
        if (chunksection == null) {
            if (block == Blocks.AIR) {
                return false;
            }
            final ChunkSection[] sections = that.getSections();
            final int n = j >> 4;
            final ChunkSection chunkSection = new ChunkSection(j >> 4 << 4, !that.world.worldProvider.g);
            sections[n] = chunkSection;
            chunksection = chunkSection;
            flag = (j >= j2);
        }
        final int l2 = that.locX * 16 + i;
        final int i3 = that.locZ * 16 + k;
        if (!that.world.isStatic) {
            block2.f(that.world, l2, j, i3, k2);
        }
        if (!(block2 instanceof IContainer)) {
            chunksection.setTypeId(i, j & 0xF, k, block);
        }
        if (!that.world.isStatic) {
            block2.remove(that.world, l2, j, i3, block2, k2);
        } else if (block2 instanceof IContainer && block2 != block) {
            that.world.p(l2, j, i3);
        }
        if (block2 instanceof IContainer) {
            chunksection.setTypeId(i, j & 0xF, k, block);
        }
        if (chunksection.getTypeId(i, j & 0xF, k) != block) {
            return false;
        }
        chunksection.setData(i, j & 0xF, k, l);
        if (flag) {
            that.initLighting();
        }
        if (block2 instanceof IContainer) {
            final TileEntity tileentity = that.e(i, j, k);
            if (tileentity != null) {
                tileentity.u();
            }
        }
        if (!that.world.isStatic && (!that.world.captureBlockStates || block instanceof BlockContainer)) {
            block.onPlace(that.world, l2, j, i3);
        }
        if (block instanceof IContainer) {
            if (that.getType(i, j, k) != block) {
                return false;
            }
            TileEntity tileentity = that.e(i, j, k);
            if (tileentity == null) {
                tileentity = ((IContainer) block).a(that.world, l);
                that.world.setTileEntity(l2, j, i3, tileentity);
            }
            if (tileentity != null) {
                tileentity.u();
            }
        }
        return that.n = true;
    }
}
