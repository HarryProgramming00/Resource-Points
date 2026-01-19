package resourcepoints.Listeners;

import me.angeschossen.lands.api.events.land.claiming.selection.LandClaimSelectionEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import resourcepoints.ResourcePoints;
import resourcepoints.Util.ResourcePointUtilities;

public class LandsClaimEvent implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onLandClaim(LandClaimSelectionEvent event){
        if(ResourcePointUtilities.isInDistanceToAResourcePoint(event.getLandPlayer().getPlayer().getLocation(), ResourcePoints.getInstance().getCapturePointProtectionRange() + 200)){
            event.getLandPlayer().getPlayer().sendMessage("You cannot claim land near an active resource point");
            event.setCancelled(true);
        }
    }
}

