package me.TheFancyGamer.AssasinsPack;

import java.io.File;
import java.util.logging.Logger;

import org.apache.logging.log4j.core.appender.FileManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.flags.DefaultFlag;
import com.sk89q.worldguard.protection.managers.RegionManager;

public class AssassinPack extends JavaPlugin
{
  public final Logger log = Logger.getLogger("Minecraft");
  public File configFile;
  private FileManager fm;
  public String value;
  public WorldEditPlugin we = null;
  public WorldGuardPlugin wg = null;

  public void onEnable()
  {
    loadConfiguration();
    getConfig().options().copyDefaults(true);
    saveConfig();
    getServer().getPluginManager().registerEvents(new PluginListener(this), this);
    PluginDescriptionFile pdfFile = getDescription();
    this.log.info(pdfFile.getName() + " " + " v" + pdfFile.getVersion() + " : Has been enabled");

    Plugin plTemp = Bukkit.getServer().getPluginManager()
      .getPlugin("WorldGuard");
    if ((plTemp == null) || (!(plTemp instanceof WorldGuardPlugin)))
      this.wg = null;
    else {
      this.wg = ((WorldGuardPlugin)plTemp);
    }

    plTemp = Bukkit.getServer().getPluginManager().getPlugin("WorldEdit");
    if ((plTemp == null) || (!(plTemp instanceof WorldEditPlugin)))
      this.we = null;
    else
      this.we = ((WorldEditPlugin)plTemp);
  }

  public boolean isRegioned(Location testLoc)
  {
    boolean ret = false;
    if (this.wg != null)
    {
      RegionManager rm = this.wg.getRegionManager(testLoc.getWorld());
      if (rm != null)
      {
        if (!rm.getApplicableRegions(testLoc).allows(
          DefaultFlag.MOB_SPAWNING)) {
          ret = true;
        }
      }
    }
    return ret;
  }

  private void loadConfiguration() {
    getConfig().addDefault("HeadshotDamage", Integer.valueOf(5));
    String WorldName = "world";
    getConfig().addDefault("WorldNames", WorldName);
  }

  private WorldGuardPlugin getWorldGuard()
  {
    Plugin plugin = Bukkit.getServer().getPluginManager()
      .getPlugin("WorldGuard");
    if ((plugin == null) || (!(plugin instanceof WorldGuardPlugin))) {
      return null;
    }
    return (WorldGuardPlugin)plugin;
  }
}