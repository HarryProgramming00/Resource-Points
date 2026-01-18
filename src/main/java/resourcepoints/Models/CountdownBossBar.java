package resourcepoints.Models;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import resourcepoints.ResourcePoints;

import java.util.concurrent.atomic.AtomicInteger;

public class CountdownBossBar {

    private BossBar bar;
    private final Location locationOfBeacon;
    private final int durationSeconds;
    private int total;
    private AtomicInteger timeLeft;


    public CountdownBossBar(Location locationOfBeacon, int durationSeconds) {
        this.locationOfBeacon = locationOfBeacon;
        this.durationSeconds = durationSeconds;
    }

    public void startCountdownBossBar() {

        BossBar bar = Bukkit.createBossBar(
                "Time left: " + durationSeconds + "s",
                BarColor.BLUE,
                BarStyle.SOLID
        );

        this.bar = bar;

        bar.setProgress(1.0); // full bar

        this.total = durationSeconds;
        this.timeLeft = new AtomicInteger(durationSeconds);
    }

    public boolean updateBeam() {

            int t = timeLeft.getAndDecrement();

            // Timer finished
            if (t <= 0) {
                bar.setTitle("Time left: 0s");
                bar.setProgress(0.0);
                bar.removeAll();
               return true;
            }

            // Update title
            bar.setTitle("Time left: " + t + "s");

            // Update progress (0.0 → 1.0)
            double progress = (double) t / total;
            bar.setProgress(progress);

            applyBarToPlayersInRange();

            return false;
    }


    private void applyBarToPlayersInRange(){

        for (Player player : Bukkit.getOnlinePlayers()) {

            if (player.getLocation().distance(locationOfBeacon) < 200) {

                this.bar.addPlayer(player);

            }
        }
    }

}
