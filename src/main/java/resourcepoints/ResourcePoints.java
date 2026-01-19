package resourcepoints;

import com.sk89q.worldguard.WorldGuard;
import me.angeschossen.lands.api.LandsIntegration;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import resourcepoints.Listeners.BlockBreakListener;
import resourcepoints.Listeners.BlockPlaceListener;
import resourcepoints.Managers.ResourcePointsManager;

import java.util.ArrayList;
import java.util.List;

public class ResourcePoints extends JavaPlugin {

    private static ResourcePoints instance;
    private World worldToSpawnResourcePoints;
    private ArrayList<Material> materials;
    private LandsIntegration landsAPI = LandsIntegration.of(this);
    private boolean worldGuardEnabled;
    private boolean landsEnabled;
    private int spawnCycleTime;
    private int captureTime;
    private int chestDespawnTime;
    private int capturePointProtectionRange;

    @Override
    public void onEnable() {
        instance = this;

        this.worldGuardEnabled = getServer().getPluginManager().getPlugin("WorldGuard") != null;
        this.landsEnabled = getServer().getPluginManager().getPlugin("Lands") != null;

        //Register Commands

        this.saveDefaultConfig();

        //Register Listener for broken blocks.
        getServer().getPluginManager().registerEvents(new BlockBreakListener(), this);
        getServer().getPluginManager().registerEvents(new BlockPlaceListener(), this);

        // Create the block broken check for each dedicated block type.
        loadConfigFile();
        ResourcePointsManager.getResourcePointsManager().startResourcePointCreationTimer();
    }

    private void loadConfigFile(){

        FileConfiguration config = this.getConfig();

        this.worldToSpawnResourcePoints = Bukkit.getWorld(config.getString("world"));

        if(this.worldToSpawnResourcePoints == null){
            Bukkit.getLogger().severe("No world with name " + config.getString("world") + " found");
        }

        this.materials = getMaterialsFromStrings((List<String>) config.getList("materials"));

        this.spawnCycleTime = config.getInt("spawn-cycle-time");
        this.captureTime = config.getInt("capture-time");
        this.chestDespawnTime = config.getInt("chest-despawn-time");
        this.capturePointProtectionRange = config.getInt("capture-point-protection-range");
    }

    private ArrayList<Material> getMaterialsFromStrings(List<String> materialsStrings){

        ArrayList<Material> materials = new ArrayList<>();

        for(String string : materialsStrings){
            materials.add(Material.getMaterial(string));
        }
        return materials;
    }


    public static ResourcePoints getInstance() {
        return instance;
    }

    public ArrayList<Material> getMaterials() {
        return materials;
    }

    public World getWorldToSpawnResourcePoints() {
        return worldToSpawnResourcePoints;
    }

    public LandsIntegration getLandsAPI() {
        return landsAPI;
    }

    public boolean isWorldGuardEnabled() {
        return worldGuardEnabled;
    }

    public boolean isLandsEnabled() {
        return landsEnabled;
    }

    public int getSpawnCycleTime() {
        return spawnCycleTime;
    }

    public int getCaptureTime() {
        return captureTime;
    }

    public int getChestDespawnTime() {
        return chestDespawnTime;
    }

    public int getCapturePointProtectionRange() {
        return capturePointProtectionRange;
    }
}
