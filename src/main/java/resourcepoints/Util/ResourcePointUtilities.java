package resourcepoints.Util;

import org.bukkit.Location;
import resourcepoints.Managers.ResourcePointsManager;
import resourcepoints.Models.ResourcePoint;

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

}
