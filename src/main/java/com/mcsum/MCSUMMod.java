/* Package. */
package com.mcsum;

/* Imports. */
import com.mcsum.net.OpenDialogPayload;
import com.mcsum.rk800.EntityInit;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MCSUMMod implements ModInitializer {
    public static final String MOD_ID = "mcsum";

    //Logger for printing messages to console/log file.
    public static final Logger LOGGER = LoggerFactory.getLogger("MCSUM");

    @Override
    public void onInitialize() {
        /* Entity registration. */
        EntityInit.register();

        /* Networking. */
        PayloadTypeRegistry.playS2C().register(OpenDialogPayload.ID, OpenDialogPayload.CODEC);

        /* Log. */
        LOGGER.info("Project: MCSUM loaded (Minecraft 1.21.8 / Fabric 0.17.2)");
    }
}
