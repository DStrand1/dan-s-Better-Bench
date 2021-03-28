package dan.betterbench.asm;

import net.minecraft.launchwrapper.IClassTransformer;
import net.minecraftforge.fml.relauncher.FMLLaunchHandler;
import net.minecraftforge.fml.relauncher.Side;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.*;

import java.util.Iterator;

public class BBTransformer implements IClassTransformer {

    public static final Logger logger = LogManager.getLogger("dBBTransformer");

    public static void info(String format, Object... args) {
        logger.info(String.format(format, args));
    }

    public static void error(String format, Object... args) {
        logger.error(format, args);
    }

    public byte[] transform(String name, String dev, byte[] bytes) {
        boolean client = (FMLLaunchHandler.side() == Side.CLIENT);
        if ("net.minecraft.block.BlockWorkbench".equals(dev)) {
            boolean obf = !name.equals(dev);
            String _isOpaqueCube = obf ? "func_149662_c" : "isOpaqueCube";
            String _createNewTileEntity = obf ? "func_149915_a" : "createNewTileEntity";
            String _breakBlock = obf ? "func_180663_b" : "breakBlock";
            info("Injecting into %s (%s)", name, dev);
            ClassReader reader = new ClassReader(bytes);
            ClassWriter writer = new ClassWriter(3);
            ClassNode node = new ClassNode();
            reader.accept(node, 8);
            info(" - ITileEntityProvider");
            node.interfaces.add("net/minecraft/block/ITileEntityProvider");
            info(" - createNewTileEntity");
            MethodNode createNewTileEntity = new MethodNode(327680, 1, _createNewTileEntity, "(Lnet/minecraft/world/World;I)Lnet/minecraft/tileentity/TileEntity;", null, null);
            InsnList insn = createNewTileEntity.instructions;
            insn.add(new MethodInsnNode(184, "dan/betterbench/asm/BBHooks", "createNewTileEntity", "()Ldan/betterbench/WorkbenchTile;", false));
            insn.add(new InsnNode(176));
            node.methods.add(createNewTileEntity);
            info(" - breakBlock");
            MethodNode breakBlock = new MethodNode(327680, 1, _breakBlock, "(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/state/IBlockState;)V", null, null);
            insn = breakBlock.instructions;
            insn.add(new VarInsnNode(25, 1));
            insn.add(new VarInsnNode(25, 2));
            insn.add(new MethodInsnNode(184, "dan/betterbench/asm/BBHooks", "breakBlock", "(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;)V", false));
            insn.add(new VarInsnNode(25, 0));
            insn.add(new VarInsnNode(25, 1));
            insn.add(new VarInsnNode(25, 2));
            insn.add(new VarInsnNode(25, 3));
            insn.add(new MethodInsnNode(183, "net/minecraft/block/Block", _breakBlock, "(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/state/IBlockState;)V", false));
            insn.add(new InsnNode(177));
            node.methods.add(breakBlock);
            info(" - <clinit>");
            MethodNode clinit = new MethodNode(327680, 9, "<clinit>", "()V", null, null);
            insn = clinit.instructions;
            insn.add((AbstractInsnNode)new MethodInsnNode(184, "dan/betterbench/asm/BBHooks", "staticInit", "()V", false));
            insn.add(new InsnNode(177));
            node.methods.add(clinit);
            if (client) {
                info(" - isOpaqueCube");
                MethodNode isOpaqueCube = new MethodNode(327680, 1, _isOpaqueCube, "(Lnet/minecraft/block/state/IBlockState;)Z", null, null);
                insn = isOpaqueCube.instructions;
                insn.add(new InsnNode(3));
                insn.add(new InsnNode(172));
                node.methods.add(isOpaqueCube);
            }
            node.accept(writer);
            info(" - Done");
            return writer.toByteArray();
        }
        if ("net.minecraft.inventory.ContainerWorkbench".equals(dev)) {
            boolean obf = !name.equals(dev);
            String _onContainerClosed = obf ? "func_75134_a" : "onContainerClosed";
            String _onContainerClosed_notch = obf ? "b" : "onContainerClosed";
            String _onContainerClosed_notch_desc = obf ? "(Laed;)V" : "(Lnet/minecraft/entity/player/EntityPlayer;)V";
            String _inventoryCrafting_notch = obf ? "afy" : "net/minecraft/inventory/InventoryCrafting";
            String _container_notch = obf ? "afr" : "net/minecraft/inventory/Container";
            String _craftMatrix = obf ? "field_75162_e" : "craftMatrix";
            String _closeInventory = obf ? "func_174886_c" : "closeInventory";
            String _containerWorkbench_world = obf ? "field_75161_g" : "world";
            String _containerWorkbench_player = obf ? "field_192390_i" : "player";
            String _containerWorkbench_result = obf ? "field_75160_f" : "craftResult";
            String _containerWorkbench_slotChanged = obf ? "func_192389_a" : "slotChangedCraftingGrid";
            info("Injecting into %s (%s)", name, dev);
            ClassReader reader = new ClassReader(bytes);
            ClassWriter writer = new ClassWriter(3);
            ClassNode node = new ClassNode();
            reader.accept(node, 0);
            info(" - mBetterBenchTile");
            node.fields.add(new FieldNode(327680, 1, "mBetterBenchTile", "Ldan/betterbench/WorkbenchTile;", null, null));
            int hooks = 0;
            for (MethodNode method : node.methods) {
                if ("<init>".equals(method.name)) {
                    info(" - <init>");
                    hooks++;
                    InsnList insn = method.instructions;
                    Iterator<AbstractInsnNode> n = insn.iterator();
                    while (n.hasNext()) {
                        AbstractInsnNode n1 = n.next();
                        if (n1.getOpcode() == 187) {
                            TypeInsnNode n2 = (TypeInsnNode)n1;
                            if (_inventoryCrafting_notch.equals(n2.desc)) {
                                info(" - WorkbenchInventory[NEW]");
                                hooks++;
                                n2.desc = "dan/betterbench/WorkbenchInventory";
                            }
                            continue;
                        }
                        if (n1.getOpcode() == 183) {
                            MethodInsnNode n2 = (MethodInsnNode)n1;
                            if (_inventoryCrafting_notch.equals(n2.owner) && "<init>".equals(n2.name)) {
                                info(" - WorkbenchInventory[INVOKESPECIAL]");
                                hooks++;
                                n2.owner = "dan/betterbench/WorkbenchInventory";
                                continue;
                            }
                            if (_container_notch.equals(n2.owner) && "<init>".equals(n2.name)) {
                                info(" - WorkbenchTile[PUTFIELD]");
                                hooks++;
                                InsnList n3 = new InsnList();
                                n3.add(new VarInsnNode(25, 0));
                                n3.add(new VarInsnNode(25, 2));
                                n3.add(new VarInsnNode(25, 3));
                                n3.add(new MethodInsnNode(184, "dan/betterbench/asm/BBHooks", "getTile", "(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;)Ldan/betterbench/WorkbenchTile;", false));
                                n3.add(new FieldInsnNode(181, "net/minecraft/inventory/ContainerWorkbench", "mBetterBenchTile", "Ldan/betterbench/WorkbenchTile;"));
                                insn.insert(n2, n3);
                            }
                            continue;
                        }
                        if (n1.getOpcode() == 177) {
                            info(" - ContainerWorkbench[REFRESH_MATRIX]");
                            hooks++;
                            InsnList n3 = new InsnList();
                            n3.add(new VarInsnNode(25, 0));
                            n3.add(new InsnNode(89));
                            n3.add(new FieldInsnNode(180, "net/minecraft/inventory/ContainerWorkbench", _containerWorkbench_world, "Lnet/minecraft/world/World;"));
                            n3.add(new VarInsnNode(25, 0));
                            n3.add(new FieldInsnNode(180, "net/minecraft/inventory/ContainerWorkbench", _containerWorkbench_player, "Lnet/minecraft/entity/player/EntityPlayer;"));
                            n3.add(new VarInsnNode(25, 0));
                            n3.add(new FieldInsnNode(180, "net/minecraft/inventory/ContainerWorkbench", _craftMatrix, "Lnet/minecraft/inventory/InventoryCrafting;"));
                            n3.add(new VarInsnNode(25, 0));
                            n3.add(new FieldInsnNode(180, "net/minecraft/inventory/ContainerWorkbench", _containerWorkbench_result, "Lnet/minecraft/inventory/InventoryCraftResult;"));
                            n3.add(new MethodInsnNode(182, "net/minecraft/inventory/ContainerWorkbench", _containerWorkbench_slotChanged, "(Lnet/minecraft/world/World;Lnet/minecraft/entity/player/EntityPlayer;Lnet/minecraft/inventory/InventoryCrafting;Lnet/minecraft/inventory/InventoryCraftResult;)V", false));
                            insn.insertBefore(n1, n3);
                        }
                    }
                    continue;
                }
                if (_onContainerClosed_notch.equals(method.name) && _onContainerClosed_notch_desc.equals(method.desc)) {
                    info(" - onContainerClosed");
                    hooks++;
                    InsnList insn = method.instructions;
                    insn.clear();
                    insn.add(new VarInsnNode(25, 0));
                    insn.add(new VarInsnNode(25, 1));
                    insn.add(new MethodInsnNode(183, "net/minecraft/inventory/Container", _onContainerClosed, "(Lnet/minecraft/entity/player/EntityPlayer;)V", false));
                    insn.add(new VarInsnNode(25, 0));
                    insn.add(new FieldInsnNode(180, "net/minecraft/inventory/ContainerWorkbench", _craftMatrix, "Lnet/minecraft/inventory/InventoryCrafting;"));
                    insn.add(new VarInsnNode(25, 1));
                    insn.add(new MethodInsnNode(185, "net/minecraft/inventory/IInventory", _closeInventory, "(Lnet/minecraft/entity/player/EntityPlayer;)V", true));
                    insn.add(new InsnNode(177));
                }
            }
            requireHooks(dev, hooks, 6);
            node.accept(writer);
            info(" - Done");
            return writer.toByteArray();
        }
        if ("dan.betterbench.asm.BBHooks".equals(dev)) {
            info("Injecting into %s (%s)", name, dev);
            ClassReader reader = new ClassReader(bytes);
            ClassWriter writer = new ClassWriter(3);
            ClassNode node = new ClassNode();
            reader.accept(node, 8);
            int hooks = 0;
            for (MethodNode method : node.methods) {
                if ("getTile".equals(method.name)) {
                    info(" - getTile");
                    hooks++;
                    method.access &= 0xFFFFFEFF;
                    InsnList insn = method.instructions;
                    insn.clear();
                    insn.add(new VarInsnNode(25, 0));
                    insn.add(new FieldInsnNode(180, "net/minecraft/inventory/ContainerWorkbench", "mBetterBenchTile", "Ldan/betterbench/WorkbenchTile;"));
                    insn.add(new InsnNode(176));
                    break;
                }
            }
            requireHooks(dev, hooks, 1);
            node.accept(writer);
            info(" - Done");
            return writer.toByteArray();
        }
        if ("dan.betterbench.WorkbenchInventory".equals(dev)) {
            info("Injecting into %s (%s)", name, dev);
            ClassReader reader = new ClassReader(bytes);
            ClassWriter writer = new ClassWriter(3);
            ClassNode node = new ClassNode();
            reader.accept(node, 8);
            info(" - <init>");
            MethodNode init = new MethodNode(327680, 1, "<init>", "(Lnet/minecraft/inventory/Container;IILnet/minecraft/entity/player/EntityPlayer;)V", null, null);
            InsnList insn = init.instructions;
            insn.add(new VarInsnNode(25, 0));
            insn.add(new VarInsnNode(25, 1));
            insn.add(new VarInsnNode(21, 2));
            insn.add(new VarInsnNode(21, 3));
            insn.add(new VarInsnNode(25, 4));
            insn.add(new MethodInsnNode(183, "net/minecraft/inventory/InventoryCrafting", "<init>", "(Lnet/minecraft/inventory/Container;IILnet/minecraft/entity/player/EntityPlayer;)V", false));
            insn.add(new VarInsnNode(25, 0));
            insn.add(new MethodInsnNode(182, "dan/betterbench/WorkbenchInventory", "initRealBench", "()V", false));
            insn.add(new InsnNode(177));
            node.methods.add(init);
            node.accept(writer);
            info(" - Done");
            return writer.toByteArray();
        }
        return bytes;
    }

    private static void requireHooks(String clazz, int hooks, int requiredHooks) {
        if (hooks != requiredHooks)
            error("Mossing hooks for class %s: should be %d, but actually is %d", clazz, requiredHooks, hooks);
    }
}
