package resourcepoints.Util;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import org.bukkit.Location;
import resourcepoints.Managers.ResourcePointsManager;
import resourcepoints.Models.ResourcePoint;
import resourcepoints.ResourcePoints;

public class ResourcePointUtilities {

// worldGuard, Lands, Towny and Grief prevention check

    //check if location is within distance to any active Resource
    public static boolean isInDistanceToAResourcePoint(Location location, int distance){
        for(ResourcePoint resourcePoint : ResourcePointsManager.getResourcePointsManager().getResourcePoints()){

            if(resourcePoint.getChestLocation().distance(location) < distance){
                return true;
            }

        }
        return false;
    }

    public static boolean isLocationInALandClaim(Location location){

        return ResourcePoints.getInstance().getLandsAPI().getUnloadedArea(location) != null;
    }

    public static boolean isLocationInAWorldGuardRegion(Location location){

        if (location == null || location.getWorld() == null) {
            return false;
        }

        // Get WorldGuard's RegionContainer
        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        RegionManager regions = container.get(BukkitAdapter.adapt(location.getWorld()));

        if (regions == null) {
            return false; // No regions in this world
        }

        // Convert Bukkit Location to WorldEdit Location
        com.sk89q.worldedit.util.Location weLoc = BukkitAdapter.adapt(location);

        // Get all regions that apply to this location
        ApplicableRegionSet set = regions.getApplicableRegions(weLoc.toVector().toBlockPoint());

        // If set is not empty, location is claimed
        return !set.getRegions().isEmpty();
    }

}
