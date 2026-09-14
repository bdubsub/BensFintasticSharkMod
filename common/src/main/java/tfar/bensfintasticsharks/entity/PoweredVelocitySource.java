package tfar.bensfintasticsharks.entity;

import net.minecraft.world.phys.Vec3;

/** Exposes the settings owned powered component to the server diagnostics writer. */
public interface PoweredVelocitySource {

    Vec3 bfsPoweredVelocityForDiagnostics();
}
