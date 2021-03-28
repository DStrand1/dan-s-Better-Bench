package dan.betterbench.asm;

import dan.betterbench.WorkbenchRenderer;
import dan.betterbench.WorkbenchTile;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.inventory.ContainerWorkbench;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.FMLLaunchHandler;
import net.minecraftforge.fml.relauncher.Side;

import java.util.List;

public class BBHooks {

    public static WorkbenchTile createNewTileEntity() {
        return new WorkbenchTile();
    }

    public static void breakBlock(World world, BlockPos pos) {
        TileEntity te = world.getTileEntity(pos);
        if (te instanceof WorkbenchTile) {
            List<ItemStack> items = ((WorkbenchTile) te).getCraftMatrixItems();
            for (ItemStack item : items) {
                if (item != ItemStack.EMPTY)
                    dropBlockAsItem(world, pos.getX(), pos.getY(), pos.getZ(), item);
            }
        }
    }

    protected static void dropBlockAsItem(World world, int x, int y, int z, ItemStack item) {
        if (!world.isRemote && world.getGameRules().getBoolean("doTileDrops") && !world.restoringBlockSnapshots) {
            float f = 0.7f;
            double d0 = (world.rand.nextFloat() * f) + (1.0f - f) * 0.5D;
            double d1 = (world.rand.nextFloat() * f) + (1.0f - f) * 0.5D;
            double d2 = (world.rand.nextFloat() * f) + (1.0f - f) * 0.5D;
            EntityItem entityItem = new EntityItem(world, x + d0, y + d1, z + d2, item);
            entityItem.setDefaultPickupDelay();
            world.spawnEntity(entityItem);
        }
    }

    public static void staticInit() {
        GameRegistry.registerTileEntity(WorkbenchTile.class, "betterbench:Workbench");
        if (FMLLaunchHandler.side() == Side.CLIENT)
            Client.init();
    }

    public static native WorkbenchTile getTile(ContainerWorkbench paramContainerWorkbench);

    static class Client {
        static void init() {
            ClientRegistry.bindTileEntitySpecialRenderer(WorkbenchTile.class, new WorkbenchRenderer());
        }
    }

    public static WorkbenchTile getTile(World world, BlockPos pos) {
        TileEntity te = world.getTileEntity(pos);
        return te instanceof WorkbenchTile ? (WorkbenchTile) te : null;
    }
}
