package me.TheFancyGamer.AssasinsPack;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class FileManager
{
  private FileConfiguration config;
  private final AssassinPack plugin;
  private int[] head_alive = new int[2];
  private int[] head_dead = new int[2];
  private int[] body_alive = new int[2];
  private int[] body_dead = new int[2];
  private File file;

  public FileManager(AssassinPack p)
  {
    this.plugin = p;
  }

  public void load()
  {
    String filename = "config.yml";
    this.file = new File(Bukkit.getServer().getPluginManager().getPlugin(this.plugin.getName()).getDataFolder(), filename);

    if (this.file.exists())
      this.config = YamlConfiguration.loadConfiguration(this.file);
    else {
      try {
        Bukkit.getServer().getPluginManager().getPlugin(this.plugin.getName()).getDataFolder().mkdir();
        InputStream jarURL = FileManager.class.getResourceAsStream("/" + filename);
        copyFile(jarURL, this.file);
      }
      catch (Exception localException)
      {
      }
    }

    this.head_alive[0] = 89;
    this.head_alive[1] = 0;
    this.head_dead[0] = 123;
    this.head_dead[1] = 0;

    this.body_alive[0] = 35;
    this.body_alive[1] = 0;
    this.body_dead[0] = 35;
    this.body_dead[1] = 14;

    this.file = new File(this.plugin.getDataFolder(), "config.yml");
    this.config = YamlConfiguration.loadConfiguration(this.file);

    if (!this.config.contains("materials.head_alive")) {
      this.config.set("materials.head_alive", "'100:62'");
    } else {
      String[] data = this.config.getString("materials.head_alive").split(":");
      try {
        this.head_alive[0] = Integer.parseInt(data[0]);
        if (data.length == 2)
          this.head_alive[1] = Integer.parseInt(data[1]);
        else this.head_alive[1] = 0; 
      } catch (NumberFormatException ex) { System.out.println("ERROR! BAD YAML FILE! PLEASE CHECK."); this.head_alive[0] = 20; this.head_alive[1] = 0;
      }
    }

    if (!this.config.contains("materials.body_alive")) {
      this.config.set("materials.body_alive", "'100:62'");
    }
    else {
      String[] data = this.config.getString("materials.body_alive").split(":");
      this.body_alive = new int[2];
      try {
        this.body_alive[0] = Integer.parseInt(data[0]);
        if (data.length == 2)
          this.body_alive[1] = Integer.parseInt(data[1]);
        else this.body_alive[1] = 0; 
      } catch (NumberFormatException ex) { System.out.println("ERROR! BAD YML FILE! PLEASE CHECK."); this.body_alive[0] = 20; this.body_alive[1] = 1;
      }
    }

    if (!this.config.contains("materials.body_dead")) {
      this.config.set("materials.body_dead", "'35:14'");
    }
    else {
      String[] data = this.config.getString("materials.body_dead").split(":");
      this.body_dead = new int[2];
      try {
        this.body_dead[0] = Integer.parseInt(data[0]);
        if (data.length == 2)
          this.body_dead[1] = Integer.parseInt(data[1]);
        else this.body_dead[1] = 0; 
      } catch (NumberFormatException ex) { System.out.println("ERROR! BAD YAML FILE! PLEASE CHECK."); this.body_dead[0] = 35; this.body_dead[1] = 14;
      }
    }

    if (!this.config.contains("materials.head_dead")) {
      this.config.set("materials.head_dead", "'123:0'");
    }
    else {
      String[] data = this.config.getString("materials.head_dead").split(":");
      this.head_dead = new int[2];
      try {
        this.head_dead[0] = Integer.parseInt(data[0]);
        if (data.length == 2)
          this.head_dead[1] = Integer.parseInt(data[1]);
        else this.head_dead[1] = 0; 
      } catch (NumberFormatException ex) { System.out.println("ERROR! BAD YAML FILE! PLEASE CHECK."); this.head_dead[0] = 20; this.head_dead[1] = 0; }
    }
  }

  private static void copyFile(InputStream in, File out) throws Exception
  {
    InputStream fis = in;
    FileOutputStream fos = new FileOutputStream(out);
    try {
      byte[] buf = new byte[1024];
      int i = 0;
      while ((i = fis.read(buf)) != -1)
        fos.write(buf, 0, i);
    }
    catch (Exception e) {
      throw e;
    } finally {
      if (fis != null) {
        fis.close();
      }
      if (fos != null)
        fos.close();
    }
  }
}