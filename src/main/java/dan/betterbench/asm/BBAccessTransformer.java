package dan.betterbench.asm;

import net.minecraftforge.fml.common.asm.transformers.AccessTransformer;

import java.io.IOException;

public class BBAccessTransformer extends AccessTransformer {

    public BBAccessTransformer() throws IOException {
        super("betterbench_at.cfg");
    }
}
