package tfar.bensfintasticsharks.entity;

import net.minecraft.network.protocol.game.ClientboundSetPassengersPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;

import java.util.List;

/**
 * Implemented by shark species that can grab a victim and thrash it.
 * The grab timer is a synced
 * entity-data value, so clients can read it too — the 0.18 client-side grab
 * effects (camera lock + red tint) key off the local player's vehicle
 * implementing this interface.
 */
public interface SharkGrabber {

    /** Ticks remaining in the current grab/thrash. 0 = not grabbing. */
    int getGrabTimer();

    /**
     * Releases every passenger and resends the authoritative empty passenger list.
     * Implementations also clear their synchronized timer before calling this helper.
     */
    default void releaseGrabPassengers() {
        Entity holder = (Entity) this;
        List<Entity> passengers = List.copyOf(holder.getPassengers());
        holder.ejectPassengers();
        for (Entity passenger : passengers) {
            if (passenger instanceof ServerPlayer serverPlayer) {
                serverPlayer.connection.send(new ClientboundSetPassengersPacket(holder));
            }
        }
    }
}
