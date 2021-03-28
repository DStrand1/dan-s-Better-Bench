package dan.betterbench;

import com.google.common.collect.ImmutableList;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;

public class WorkbenchTile extends TileEntity {

    protected NonNullList<ItemStack> mCraftMatrix = NonNullList.withSize(9, ItemStack.EMPTY);

    public List<ItemStack> getCraftMatrixItems() {
        ImmutableList.Builder<ItemStack> builder = ImmutableList.builder();
        for (ItemStack stack : this.mCraftMatrix) {
            if (stack != null)
                builder.add(stack);
        }
        return builder.build(); // TODO List<ItemStack> cast
    }

    private void writeSlots(NBTTagCompound tag) {
        tag.setInteger("Capacity", this.mCraftMatrix.size());
        for (int i = 0; i < 9; i++) {
            if (this.mCraftMatrix.get(i) == ItemStack.EMPTY) {
                tag.removeTag("Slot" + i);
            } else {
                NBTTagCompound slot = new NBTTagCompound();
                this.mCraftMatrix.get(i).writeToNBT(slot); // TODO ItemStack cast
                tag.setTag("Slot" + i, slot); // TODO NBTBase cast
            }
        }
    }

    private void readSlots(NBTTagCompound tag) {
        int capacity = 9;
        if (tag.hasKey("Capacity", 3))
            capacity = tag.getInteger("Capacity");
        ensureCraftMatrixCapacity(capacity);
        for (int i = 0; i < 9; i++) {
            if (!tag.hasKey("Slot" + i, 10))
                this.mCraftMatrix.set(i, ItemStack.EMPTY);
            else
                this.mCraftMatrix.set(i, new ItemStack(tag.getCompoundTag("Slot" + i)));
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        readSlots(tag);
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);
        writeSlots(tag);
        return tag;
    }

    @Override
    public NBTTagCompound getUpdateTag() {
        NBTTagCompound tag = super.getUpdateTag();
        writeSlots(tag);
        return tag;
    }

    @Override
    public void handleUpdateTag(NBTTagCompound tag) {
        super.handleUpdateTag(tag);
        readSlots(tag);
    }

    @Override
    @Nullable
    public SPacketUpdateTileEntity getUpdatePacket() {
        return new SPacketUpdateTileEntity(getPos(), 0, getUpdateTag());
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity packet) {
        handleUpdateTag(packet.getNbtCompound());
    }

    public void ensureCraftMatrixCapacity(int capacity) {
        if (this.mCraftMatrix.size() != capacity)
            this.mCraftMatrix = NonNullList.withSize(capacity, ItemStack.EMPTY);
    }
}
