package resourcepoints.Listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import resourcepoints.Util.ResourcePointUtilities;

public class BlockPlaceListener implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockPlace(BlockPlaceEvent event){
        if(ResourcePointUtilities.isInDistanceToAResourcePoint(event.getBlock().getLocation(), 50)){
            event.setCancelled(true);
        }
    }
}
