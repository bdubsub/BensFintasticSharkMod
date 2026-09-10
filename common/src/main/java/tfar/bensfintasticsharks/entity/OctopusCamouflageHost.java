package tfar.bensfintasticsharks.entity;

import net.minecraft.world.entity.Entity;

/** Server selected and client interpolated appearance state for an octopus. */
public interface OctopusCamouflageHost {
    Entity camouflageEntity();

    int camouflageTargetColor();

    float camouflageTargetWeight();

    void setCamouflageTarget(int color, float weight);

    int camouflageColor();

    float camouflageWeight();

    void setCamouflageCurrent(int color, float weight);
}
