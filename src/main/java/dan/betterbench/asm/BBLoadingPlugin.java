package dan.betterbench.asm;

import net.minecraftforge.common.ForgeVersion;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin.MCVersion;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin.Name;

import java.util.Map;

@Name("dBBLoadingPlugin")
@MCVersion(ForgeVersion.mcVersion)
public class BBLoadingPlugin implements IFMLLoadingPlugin {

    @Override
    public String[] getASMTransformerClass() {
        return new String[] {"dan.betterbench.asm.BBTransformer"};
    }

    @Override
    public String getAccessTransformerClass() {
        return "dan.betterbench.asm.BBAccessTransformer";
    }

    @Override
    public String getModContainerClass() {
        return null;
    }

    @Override
    public String getSetupClass() {
        return null;
    }

    @Override
    public void injectData(Map<String, Object> data) {
    }
}
