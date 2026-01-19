package resourcepoints.Models;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.WorldBorder;
import org.bukkit.block.Beacon;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import resourcepoints.Managers.WaypointBeamManager;
import resourcepoints.ResourcePoints;
import resourcepoints.Util.ResourcePointUtilities;

import java.util.concurrent.ThreadLocalRandom;

public class ResourcePoint {

    private final Material material;
    private final Location chestLocation;
    private CountdownBossBar countdownBossBar;

    public ResourcePoint(Material material, Location chestLocation) {
        this.material = material;
        this.chestLocation = chestLocation;
    }

    public void createResourcePoint(){

        resourcePointRepeatTimer();
        announceResourcePointLocation();;
        //Create loop for giving items to chest and despawning once done
    }

    public Location getChestLocation() {
        return chestLocation;
    }

    public void showFakeBeacon(Material glassColor, Location locationOfBeacon) {

            for (Player player : Bukkit.getOnlinePlayers()) {

                if(player.getLocation().distance(locationOfBeacon) < 200) {


                    // Fake beacon block
                    player.sendBlockChange(
                            locationOfBeacon,
                            Bukkit.createBlockData(Material.BEACON)
                    );

                    // Fake stained glass above it (beam color)
                    player.sendBlockChange(
                            locationOfBeacon.clone().add(0, 1, 0),
                            Bukkit.createBlockData(glassColor)
                    );

                    // Fake a 3x3 iron block base under it
                    for (int x = -1; x <= 1; x++) {
                        for (int z = -1; z <= 1; z++) {
                            Location base = locationOfBeacon.clone().add(x, -1, z);
                            player.sendBlockChange(base, Bukkit.createBlockData(Material.IRON_BLOCK));
                        }
                    }
                }
            }
        }


    public void hideFakeBeacon(Location locationOfBeacon) {

        for (Player player : Bukkit.getOnlinePlayers()) {

            // Restore beacon + glass
            player.sendBlockChange(locationOfBeacon, locationOfBeacon.getBlock().getBlockData());
            player.sendBlockChange(
                    locationOfBeacon.clone().add(0, 1, 0),
                    locationOfBeacon.clone().add(0, 1, 0).getBlock().getBlockData()
            );

            // Restore the 3x3 base
            for (int x = -1; x <= 1; x++) {
                for (int z = -1; z <= 1; z++) {
                    Location base = locationOfBeacon.clone().add(x, -1, z);
                    player.sendBlockChange(base, base.getBlock().getBlockData());
                }
            }
        }
    }




    private void buildResourcePoint(){
        Block block = this.chestLocation.getBlock();
        block.setType(Material.CHEST);
        Chest chest = (org.bukkit.block.Chest) block.getState();
        chest.setCustomName("Resource Point");
        chest.update();
    }


    public void announceResourcePointLocation(){
        Bukkit.broadcastMessage("test2");
        Bukkit.broadcastMessage("A resource point has spawned at " + this.chestLocation.getBlockX() +  " " +
                this.chestLocation.getBlockY() +  " " + this.chestLocation.getBlockZ());
    }

    public static void getResourcePointLocation(){

    }

    public static Location generateLocation(WorldBorder worldBorder){

        int xCenter = worldBorder.getCenter().getBlockX();
        int zCenter = worldBorder.getCenter().getBlockZ();
        int maxXCoordinate = (int) (xCenter + ((worldBorder.getSize() / 2) * 0.9));
        int minXCoordinate = (int) (xCenter - ((worldBorder.getSize() / 2) * 0.9));
        int maxZCoordinate = (int) (zCenter + ((worldBorder.getSize() / 2) * 0.9));
        int minZCoordinate = (int) (zCenter - ((worldBorder.getSize() / 2) * 0.9));


        Location chosenLocation = null;

        int highestYBlock;
        boolean onTopOfClaim;

        do {

            int randomX = ThreadLocalRandom.current().nextInt(minXCoordinate, maxXCoordinate);
            int randomZ = ThreadLocalRandom.current().nextInt(minZCoordinate, maxZCoordinate);

            highestYBlock = (worldBorder.getWorld().getHighestBlockYAt(randomX, randomZ) + 1);

            chosenLocation = new Location(worldBorder.getWorld(), randomX, highestYBlock, randomZ);

            onTopOfClaim = false;


           if(ResourcePoints.getInstance().isLandsEnabled()) {
               if (ResourcePointUtilities.isLocationInALandClaim(chosenLocation)) {
                   onTopOfClaim = true;
               }
           }

           if(ResourcePoints.getInstance().isWorldGuardEnabled()){
              if(ResourcePointUtilities.isLocationInAWorldGuardRegion(chosenLocation)) {
                  onTopOfClaim = true;
               }
           }

        }while ((highestYBlock > (worldBorder.getWorld().getMaxHeight() - 40)) || onTopOfClaim);

        return chosenLocation;
    }

    public static Material chooseResourceType(){

        int index = ThreadLocalRandom.current().nextInt(ResourcePoints.getInstance().getMaterials().size());

        return ResourcePoints.getInstance().getMaterials().get(index);
    }

    public void spawnChest() {

        Location locationOfBeacon = this.chestLocation.clone();
        locationOfBeacon.subtract(0, 2, 0);
        hideFakeBeacon(locationOfBeacon);
        buildResourcePoint();
    }

    public void resourcePointRepeatTimer() {

        Location locationOfBeacon = this.chestLocation.clone();
        locationOfBeacon.subtract(0, 2, 0);

        countdownBossBar = new CountdownBossBar(this.chestLocation, ResourcePoints.getInstance().getCaptureTime());
        countdownBossBar.startCountdownBossBar();

        Bukkit.getScheduler().runTaskTimer(ResourcePoints.getInstance(), task -> {
            showFakeBeacon(Material.BLUE_STAINED_GLASS, locationOfBeacon);
            if(countdownBossBar.updateBeam()){
                spawnChest();
                startChestDespawnTime();
                task.cancel();
            }

        }, 0L, 20L); // update every second (20 ticks)
    }

    public void startChestDespawnTime(){

        Bukkit.getScheduler().runTaskLater(ResourcePoints.getInstance(), this::chestDespawn, 20L * ResourcePoints.getInstance().getChestDespawnTime());

    }

    public void chestDespawn(){

        Block block = this.chestLocation.getBlock();
        block.setType(Material.AIR);
    }
}
