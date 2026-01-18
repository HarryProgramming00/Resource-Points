package resourcepoints.Managers;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import resourcepoints.ResourcePoints;

import java.util.*;

public class WaypointBeamManager {

    // Each beam has a location, color, expiry, and per-player visibility map
    private final List<BeamData> beams = new ArrayList<>();
    private static WaypointBeamManager waypointBeamManager;

   private WaypointBeamManager(){}

    public static WaypointBeamManager getWaypointBeamManager() {
       if(waypointBeamManager == null){
           waypointBeamManager = new WaypointBeamManager();
       }
        return waypointBeamManager;
    }

    private static class BeamData {
        final Location loc;
        final Material color;
        final long expireAt;
        final Map<UUID, Boolean> visibleTo = new HashMap<>();
        BeamData(Location loc, Material color, long expireAt) {
            this.loc = loc; this.color = color;
            this.expireAt = expireAt; }
    }

    public void createBeam(Location loc, Material color, long durationTicks) {
        BeamData data = new BeamData(
                loc.clone(),
                color,
                System.currentTimeMillis() + (durationTicks * 50)
        );

        beams.add(data);
        startTask();
    }


    private void sendBeacon(Player player, Location loc, Material glassColor) {
        player.sendBlockChange(loc, Bukkit.createBlockData(Material.BEACON));
        player.sendBlockChange(loc.clone().add(0, 1, 0), Bukkit.createBlockData(glassColor));
    }

    private void clearBeacon(Player player, Location loc) {
        player.sendBlockChange(loc, loc.getBlock().getBlockData());
        player.sendBlockChange(loc.clone().add(0, 1, 0),
                loc.clone().add(0, 1, 0).getBlock().getBlockData());
    }



    private boolean running = false;

    private void startTask() {
        if (running) return;
        running = true;

        Bukkit.getScheduler().runTaskTimer(ResourcePoints.getInstance(), () -> {

            long now = System.currentTimeMillis();
            Iterator<BeamData> it = beams.iterator();

            while (it.hasNext()) {
                BeamData beam = it.next();

                // Auto-expire
                if (now >= beam.expireAt) {
                    // Clear for all players who still see it
                    for (Player p : Bukkit.getOnlinePlayers()) {
                        if (beam.visibleTo.getOrDefault(p.getUniqueId(), false)) {
                            clearBeacon(p, beam.loc);
                        }
                    }
                    it.remove();
                    continue;
                }

                // Distance-based visibility
                for (Player p : Bukkit.getOnlinePlayers()) {

                    double distSq = p.getLocation().distanceSquared(beam.loc);
                    boolean shouldSee = distSq <= 200 * 200; // 200-block radius

                    boolean currentlyVisible =
                            beam.visibleTo.getOrDefault(p.getUniqueId(), false);

                    if (shouldSee && !currentlyVisible) {
                        sendBeacon(p, beam.loc, beam.color);
                        beam.visibleTo.put(p.getUniqueId(), true);
                    }

                    if (!shouldSee && currentlyVisible) {
                        clearBeacon(p, beam.loc);
                        beam.visibleTo.put(p.getUniqueId(), false);
                    }
                }
            }

            if (beams.isEmpty()) running = false;

        }, 10, 10);
    }



}
