package resourcepoints.Listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import resourcepoints.ResourcePoints;
import resourcepoints.Util.ResourcePointUtilities;

public class BlockBreakListener implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockBreak(BlockBreakEvent event){
        if(ResourcePointUtilities.isInDistanceToAResourcePoint(event.getBlock().getLocation(), 50)){
            event.setCancelled(true);
        }
    }
}
