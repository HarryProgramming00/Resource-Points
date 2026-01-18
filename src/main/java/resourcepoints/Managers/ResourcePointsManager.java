package resourcepoints.Managers;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.scheduler.BukkitRunnable;
import resourcepoints.ResourcePoints;
import resourcepoints.Models.*;


import java.util.ArrayList;

public class ResourcePointsManager {

    public static ResourcePointsManager resourcePointsManager;
    private ArrayList<ResourcePoint> resourcePoints = new ArrayList<>();

    private ResourcePointsManager() {
    }

    public static ResourcePointsManager getResourcePointsManager() {
        if(resourcePointsManager == null){
            resourcePointsManager = new ResourcePointsManager();
        }
        return resourcePointsManager;
    }

    public ArrayList<ResourcePoint> getResourcePoints() {
        return resourcePoints;
    }

    private void createResourcePoint(){

        Location resourcePointLocation = ResourcePoint.generateLocation(ResourcePoints.getInstance().getWorldToSpawnResourcePoints().getWorldBorder());

        Material resourcePointMaterial = ResourcePoint.chooseResourceType();

        ResourcePoint resourcePoint = new ResourcePoint(resourcePointMaterial, resourcePointLocation);

        this.resourcePoints.add(resourcePoint);

        resourcePoint.createResourcePoint();
    }


    public void startResourcePointCreationTimer() {
        new BukkitRunnable() {
            @Override
            public void run() {
                // async work

                Bukkit.getScheduler().runTask(ResourcePoints.getInstance(), () -> {
                    // safe sync world interaction here
                    createResourcePoint();
                });

            }
        }.runTaskTimerAsynchronously(ResourcePoints.getInstance(), 0L, 20L * 90); // every 2 seconds
    }
}
