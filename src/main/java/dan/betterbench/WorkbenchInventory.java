package dan.betterbench;

import dan.betterbench.asm.BBHooks;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerWorkbench;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.world.WorldServer;

@SuppressWarnings("unused")
public class WorkbenchInventory extends InventoryCrafting {

    private ContainerWorkbench container;
    private WorkbenchTile mTile;

    public WorkbenchInventory(Container container, int width, int height) {
        super(container, width, height);
        initBetterBench(width * height);
    }

    private void initBetterBench(int capacity) {
        this.container = (this.eventHandler instanceof ContainerWorkbench) ? ((ContainerWorkbench) this.eventHandler) : null;
        if (this.eventHandler == null)
            return;
        this.mTile = BBHooks.getTile(this.container);
        if (this.mTile != null) {
            this.mTile.ensureCraftMatrixCapacity(capacity);
            this.stackList = this.mTile.mCraftMatrix;
        }
    }

    @Override
    public void markDirty() {
        if (this.mTile == null)
            return;
        this.mTile.markDirty();
        if (this.mTile.getWorld().isRemote)
            return;
        WorldServer world = (WorldServer) this.mTile.getWorld();
        world.getPlayerChunkMap().markBlockForUpdate(this.mTile.getPos());
    }

    @Override
    public ItemStack decrStackSize(int index, int count) {
        try {
            return super.decrStackSize(index, count);
        } finally {
            markDirty();
        }
    }
}
