package dan.betterbench;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemBlockSpecial;
import net.minecraft.item.ItemStack;

import java.util.Map;
import java.util.WeakHashMap;

@SuppressWarnings("unused")
public class WorkbenchRenderer extends TileEntitySpecialRenderer<WorkbenchTile> {

    Map<WorkbenchTile, RenderingState> states = new WeakHashMap<>();

    private static final int ANIMATION_DURATION = 1000;

    static class RenderingState {
        byte sector;
        float currentAngle;
        boolean animating;
        float animationAngleStart;
        float animationAngleEnd;
        long startTime;
    }

    @Override
    public void render(WorkbenchTile te, double xOffset, double yOffset, double zOffset, float partialTicks, int destroyState, float alpha) {
        super.render(te, xOffset, yOffset, zOffset, partialTicks, destroyState, alpha);
        for (int i = 0; i < te.mCraftMatrix.size(); i++) {
            ItemStack stack = te.mCraftMatrix.get(i);
            if (stack != ItemStack.EMPTY) {
                Block block;
                RenderingState state = this.states.computeIfAbsent(te, k -> new RenderingState());
                double playerAngle = (Math.atan2(xOffset + 0.5D, zOffset + 0.5D) + 3.9269908169872414D) % 6.283185307179586D;
                byte sector = (byte)(int)(playerAngle * 2.0D / Math.PI);
                long time = System.currentTimeMillis();
                if (state.sector != sector) {
                    state.animating = true;
                    state.animationAngleStart = state.currentAngle;
                    float delta1 = sector * 90.0F - state.currentAngle, abs1 = Math.abs(delta1);
                    float delta2 = delta1 + 360.0F, abs2 = Math.abs(delta2);
                    float delta3 = delta1 - 360.0F, abs3 = Math.abs(delta3);
                    if (abs3 < abs1 && abs3 < abs2) {
                        state.animationAngleEnd = delta3 + state.currentAngle;
                    } else if (abs2 < abs1 && abs2 < abs3) {
                        state.animationAngleEnd = delta2 + state.currentAngle;
                    } else {
                        state.animationAngleEnd = delta1 + state.currentAngle;
                    }
                    state.startTime = time;
                    state.sector = sector;
                }
                if (state.animating) {
                    if (time >= state.startTime + 1000L) {
                        state.animating = false;
                        state.currentAngle = (state.animationAngleEnd + 360.0F) % 360.0F;
                    } else {
                        state.currentAngle = (easeOutQuad(time - state.startTime, state.animationAngleStart, state.animationAngleEnd - state.animationAngleStart, 1000) + 360.0F) % 360.0F;
                    }
                }

                Item item = stack.getItem();
                if (item instanceof ItemBlock) {
                    block = ((ItemBlock) item).getBlock();
                } else if (item instanceof ItemBlockSpecial) {
                    block = ((ItemBlockSpecial) item).block;
                } else {
                    block = null;
                }

                boolean normalBlock = (block != null && block.getDefaultState().getMaterial().isSolid());
                float shift = (float)Math.abs((time + (i * 1000)) % 5000L - 2500L) / 200000.0f;

                GlStateManager.pushMatrix();
                GlStateManager.translate(xOffset + 0.5D, yOffset + shift, zOffset + 0.5D);
                GlStateManager.rotate(state.currentAngle, 0.0f, 1.0f, 0.0f);
                GlStateManager.translate((i % 3) * 3.0D / 16.0D + 0.3125D - 0.5D, 1.09375D, (i / 3) * 3.0D / 16.0D + 0.3125D - 0.5D);
                if (!normalBlock)
                    GlStateManager.rotate(-this.rendererDispatcher.entityPitch, 1.0f, 0.0f, 0.0f);
                GlStateManager.scale(0.125f, 0.125f, 0.125f);
                Minecraft.getMinecraft().getRenderItem().renderItem(stack, ItemCameraTransforms.TransformType.NONE);
                GlStateManager.popMatrix();
            }
        }
    }

    private static float easeOutQuad(long t, float b, float c, int d) {
        float z = (float) t / d;
        return -c * z * (z - 2.0f) + b;
    }
}
