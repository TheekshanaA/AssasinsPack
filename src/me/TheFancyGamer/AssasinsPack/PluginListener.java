package me.TheFancyGamer.AssasinsPack;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

public class PluginListener
  implements Listener
{
  private AssassinPack plugin;
  boolean x = false;
  int HeadshotDamage;
  String WN;
  public Map<String, Long> cooldown = new HashMap();
  public int COOLDOWN_TIME = 5;

  public PluginListener(AssassinPack plugin) {
    this.plugin = plugin;
    this.HeadshotDamage = plugin.getConfig().getInt("HeadshotDamage");
    this.WN = plugin.getConfig().getString("WorldNames");
  }

  @EventHandler(priority=EventPriority.HIGH, ignoreCancelled=true)
  public void onPlayerIneract(PlayerInteractEvent event) {
    Player p = event.getPlayer();
    if (((event.getAction() == Action.LEFT_CLICK_AIR) || (event.getAction() == Action.LEFT_CLICK_BLOCK)) && 
      (event.getPlayer().getItemInHand().getType() == Material.FEATHER) && (
      (p.hasPermission("assassin.longjump")) || ((p.isOp()) && (!event.getPlayer().getServer().getPluginManager().isPluginEnabled("SoliCore"))))) {
      if (p.getItemInHand().getAmount() == 1) {
        p.setItemInHand(new ItemStack(Material.AIR, 1));
      }
      else if (p.getItemInHand().getAmount() > 1) {
        p.getItemInHand().setAmount(p.getItemInHand().getAmount() - 1);
      }

      launch(event.getPlayer());
    }
  }

  @EventHandler(priority=EventPriority.HIGHEST, ignoreCancelled=true)
  public void onEntityDamage(EntityDamageEvent event)
  {
    if ((event.getEntity().getLocation().getBlock().getRelative(BlockFace.DOWN).getType() == Material.SPONGE) || (this.x)) {
      if (!(event.getEntity() instanceof Player)) return;
      if (event.getCause().equals(EntityDamageEvent.DamageCause.FALL)) {
        event.setCancelled(true);
        this.x = false;
      }
    }
    EntityType e = event.getEntity().getType();
    if ((e == EntityType.PLAYER) && 
      ((((Player)event.getEntity()).hasPermission("assassin.roll")) || (((Player)event.getEntity()).isOp())) && 
      (((Player)event.getEntity()).getFoodLevel() > 3)) {
      if (!(event.getEntity() instanceof Player)) return;
      if ((((Player)event.getEntity()).isSneaking()) && 
        (event.getCause().equals(EntityDamageEvent.DamageCause.FALL)))
        if (event.getEntity().getFallDistance() < 15.0F) {
          event.setCancelled(true);
          event.getEntity().setFallDistance(4.0F);
          ((Player)event.getEntity()).addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 2, 40));
          int test = ((Player)event.getEntity()).getFoodLevel();
          test -= 4;
          ((Player)event.getEntity()).setFoodLevel(test);
        }
        else {
          ((Player)event.getEntity()).sendMessage(ChatColor.RED + "~You have fallen too far~");
        }
    }
  }

  @EventHandler(priority=EventPriority.HIGHEST, ignoreCancelled=true)
  public void onShot(EntityDamageByEntityEvent event)
  {
    if (event.getCause() != EntityDamageEvent.DamageCause.PROJECTILE) {
      return;
    }
    Projectile proj = (Projectile)event.getDamager();

    if (!(proj.getShooter() instanceof Player))
      return;
    if (proj.getType() != EntityType.ARROW) return;
    Entity victim = event.getEntity();
    EntityType victimType = victim.getType();

    double projY = proj.getLocation().getY() + proj.getVelocity().getY() / 2.0D;
    double victimY = victim.getLocation().getY();
    boolean headshot = projY - victimY > getBodyHeight(victimType);

    if ((((Player)proj.getShooter()).hasPermission("assassin.headshot")) && (!event.getEntity().getServer().getPluginManager().isPluginEnabled("CombatCrits")) && 
      (headshot)) { event.getEntity().getType(); if (victimType == EntityType.PLAYER)
      {
        String color = "BLUE";
        ((Player)event.getEntity()).sendMessage(ChatColor.valueOf(color) + "[Headshot BY " + ((Player)proj.getShooter()).getName() + "]");
        ((Player)proj.getShooter()).sendMessage(ChatColor.DARK_RED + "[Headshot ON " + ((Player)event.getEntity()).getName() + "]");
        ((Player)proj.getShooter()).playSound(((Entity) proj.getShooter()).getLocation(), Sound.ORB_PICKUP, 1000.0F, 1000.0F);
        ((LivingEntity)victim).damage(event.getDamage() + 2);
        event.getEntity().getWorld().playEffect(event.getEntity().getLocation(), Effect.STEP_SOUND, 55);
        proj.remove();
        return;
      }
    }
  }

  @EventHandler(priority=EventPriority.HIGH, ignoreCancelled=true)
  public void onArrow(ProjectileHitEvent event)
  {
    Projectile proj = event.getEntity();
    double loc2 = proj.getLocation().getX();
    double loc4 = proj.getLocation().getZ();

    double loc1 = ((Entity) event.getEntity().getShooter()).getLocation().getX();
    double loc3 = ((Entity) event.getEntity().getShooter()).getLocation().getZ();
    double locn1 = loc3 - loc4;
    double locn = loc1 - loc2;
    if (proj.getType() != EntityType.ARROW) return;
    event.getEntity().getType(); if ((((Entity) event.getEntity().getShooter()).getType() == EntityType.PLAYER) && 
      ((((Player)proj.getShooter()).isOp()) || (((Player)proj.getShooter()).hasPermission("assassin.arrowspecials"))) && 
      (((Player)event.getEntity().getShooter()).isSneaking())) {
      if ((((Player)event.getEntity().getShooter()).getItemInHand().getType() == Material.SULPHUR) && (((Player)event.getEntity().getShooter()).getGameMode().getValue() == 0)) {
        if (locn1 < 0.0D) {
          locn1 = loc4 - loc3;
        }

        if (locn < 0.0D) {
          locn = loc2 - loc1;
        }
        if (((HumanEntity)event.getEntity().getShooter()).getItemInHand().getAmount() < 20) {
          ((Player)event.getEntity().getShooter()).sendMessage(ChatColor.GRAY + "You do not have the correct amount of resources to do that!");
          return;
        }

        if (((locn > 50.0D) && (locn < 60.0D)) || ((locn1 > 50.0D) && (locn1 < 60.0D))) {
          event.getEntity().getWorld().playEffect(((Entity) event.getEntity().getShooter()).getLocation(), Effect.SMOKE, 900);
          event.getEntity().getWorld().createExplosion(event.getEntity().getLocation(), 0.0F);
          ((HumanEntity)event.getEntity().getShooter()).getItemInHand().setAmount(((HumanEntity)event.getEntity().getShooter()).getItemInHand().getAmount() - 20);
        }

        if (((locn > 40.0D) && (locn < 50.0D)) || ((locn1 > 40.0D) && (locn1 < 50.0D))) {
          event.getEntity().getWorld().playEffect(((Entity) event.getEntity().getShooter()).getLocation(), Effect.SMOKE, 900);
          event.getEntity().getWorld().createExplosion(event.getEntity().getLocation(), 0.0F);
          ((HumanEntity)event.getEntity().getShooter()).getItemInHand().setAmount(((HumanEntity)event.getEntity().getShooter()).getItemInHand().getAmount() - 15);
        }

        if (((locn > 30.0D) && (locn < 40.0D)) || ((locn1 > 30.0D) && (locn1 < 40.0D))) {
          event.getEntity().getWorld().playEffect(((Entity) event.getEntity().getShooter()).getLocation(), Effect.SMOKE, 900);
          event.getEntity().getWorld().createExplosion(event.getEntity().getLocation(), 0.0F);
          ((HumanEntity)event.getEntity().getShooter()).getItemInHand().setAmount(((HumanEntity)event.getEntity().getShooter()).getItemInHand().getAmount() - 10);
        }

        if (((locn > 20.0D) && (locn < 30.0D)) || ((locn1 > 20.0D) && (locn1 < 30.0D))) {
          event.getEntity().getWorld().playEffect(((Entity) event.getEntity().getShooter()).getLocation(), Effect.SMOKE, 900);
          event.getEntity().getWorld().createExplosion(event.getEntity().getLocation(), 0.0F);
          ((HumanEntity)event.getEntity().getShooter()).getItemInHand().setAmount(((HumanEntity)event.getEntity().getShooter()).getItemInHand().getAmount() - 5);
        }

        if ((locn < 10.0D) || (locn1 < 10.0D)) {
          event.getEntity().getWorld().playEffect(((Entity) event.getEntity().getShooter()).getLocation(), Effect.SMOKE, 900);
          event.getEntity().getWorld().createExplosion(event.getEntity().getLocation(), 0.0F);
          ((HumanEntity)event.getEntity().getShooter()).getItemInHand().setAmount(((HumanEntity)event.getEntity().getShooter()).getItemInHand().getAmount() - 5);
        }
      }

      if ((((Player)event.getEntity().getShooter()).getItemInHand().getType() == Material.STRING) && (((Player)event.getEntity().getShooter()).getGameMode().getValue() == 0)) {
        if (this.cooldown.containsKey(((Player)event.getEntity().getShooter()).getName()))
        {
          long diff = (System.currentTimeMillis() - ((Long)this.cooldown.get(((HumanEntity)event.getEntity().getShooter()).getName())).longValue()) / 1000L;
          if (diff < this.COOLDOWN_TIME)
          {
            long remaining = this.COOLDOWN_TIME - diff;
            ((Player)event.getEntity().getShooter()).sendMessage(ChatColor.BLUE + "You recently underwent a teleport! Please wait " + ChatColor.AQUA + remaining + ChatColor.BLUE + " seconds, or be incinerated!");
            return;
          }
        }
        if (locn1 < 0.0D) {
          locn1 = loc4 - loc3;
        }

        if (locn < 0.0D) {
          locn = loc2 - loc1;
        }
        if (((HumanEntity)event.getEntity().getShooter()).getItemInHand().getAmount() < 25) {
          ((Player)event.getEntity().getShooter()).sendMessage(ChatColor.GRAY + "You do not have the correct amount of resources to grapple!");
          return;
        }

        if (((locn > 50.0D) && (locn < 60.0D)) || ((locn1 > 50.0D) && (locn1 < 60.0D))) {
          ((Entity) event.getEntity().getShooter()).teleport(proj);
          ((Damageable) event.getEntity().getShooter()).damage(0);
          event.getEntity().getWorld().playEffect(((Entity) event.getEntity().getShooter()).getLocation(), Effect.SMOKE, 900);

          int test = ((Player)event.getEntity().getShooter()).getFoodLevel();
          test--;
          ((Player)event.getEntity().getShooter()).setFoodLevel(test);
          ((HumanEntity)event.getEntity().getShooter()).getItemInHand().setAmount(((HumanEntity)event.getEntity().getShooter()).getItemInHand().getAmount() - 25);
          this.cooldown.put(((Player)event.getEntity().getShooter()).getName(), Long.valueOf(System.currentTimeMillis()));
        }

        if (((locn > 40.0D) && (locn < 50.0D)) || ((locn1 > 40.0D) && (locn1 < 50.0D))) {
          ((Damageable) event.getEntity().getShooter()).damage(0);
          ((Entity) event.getEntity().getShooter()).teleport(proj);
          event.getEntity().getWorld().playEffect(((Entity) event.getEntity().getShooter()).getLocation(), Effect.SMOKE, 900);

          int test = ((Player)event.getEntity().getShooter()).getFoodLevel();
          test--;
          ((Player)event.getEntity().getShooter()).setFoodLevel(test);
          ((HumanEntity)event.getEntity().getShooter()).getItemInHand().setAmount(((HumanEntity)event.getEntity().getShooter()).getItemInHand().getAmount() - 20);
          this.cooldown.put(((Player)event.getEntity().getShooter()).getName(), Long.valueOf(System.currentTimeMillis()));
        }

        if (((locn > 30.0D) && (locn < 40.0D)) || ((locn1 > 30.0D) && (locn1 < 40.0D))) {
          ((Damageable) event.getEntity().getShooter()).damage(0);
          ((Entity) event.getEntity().getShooter()).teleport(proj);
          event.getEntity().getWorld().playEffect(((Entity) event.getEntity().getShooter()).getLocation(), Effect.SMOKE, 900);

          int test = ((Player)event.getEntity().getShooter()).getFoodLevel();
          test--;
          ((Player)event.getEntity().getShooter()).setFoodLevel(test);
          ((HumanEntity)event.getEntity().getShooter()).getItemInHand().setAmount(((HumanEntity)event.getEntity().getShooter()).getItemInHand().getAmount() - 15);
          this.cooldown.put(((Player)event.getEntity().getShooter()).getName(), Long.valueOf(System.currentTimeMillis()));
        }

        if (((locn > 20.0D) && (locn < 30.0D)) || ((locn1 > 20.0D) && (locn1 < 30.0D))) {
          ((Damageable) event.getEntity().getShooter()).damage(0);
          ((Entity) event.getEntity().getShooter()).teleport(proj);
          event.getEntity().getWorld().playEffect(((Entity) event.getEntity().getShooter()).getLocation(), Effect.SMOKE, 900);

          int test = ((Player)event.getEntity().getShooter()).getFoodLevel();
          test--;
          ((Player)event.getEntity().getShooter()).setFoodLevel(test);
          ((HumanEntity)event.getEntity().getShooter()).getItemInHand().setAmount(((HumanEntity)event.getEntity().getShooter()).getItemInHand().getAmount() - 10);
          this.cooldown.put(((Player)event.getEntity().getShooter()).getName(), Long.valueOf(System.currentTimeMillis()));
        }

        if ((locn < 10.0D) || (locn1 < 10.0D)) {
          ((Damageable) event.getEntity().getShooter()).damage(0);
          ((Entity) event.getEntity().getShooter()).teleport(proj);
          event.getEntity().getWorld().playEffect(((Entity) event.getEntity().getShooter()).getLocation(), Effect.SMOKE, 900);
          int test = ((Player)event.getEntity().getShooter()).getFoodLevel();
          test--;
          ((Player)event.getEntity().getShooter()).setFoodLevel(test);
          ((HumanEntity)event.getEntity().getShooter()).getItemInHand().setAmount(((HumanEntity)event.getEntity().getShooter()).getItemInHand().getAmount() - 5);
          this.cooldown.put(((Player)event.getEntity().getShooter()).getName(), Long.valueOf(System.currentTimeMillis()));
        }
      }
      if ((!this.plugin.isRegioned(event.getEntity().getLocation())) && 
        (((Player)proj.getShooter()).hasPermission("assassin.torcharrow")) && 
        (((Player)event.getEntity().getShooter()).getItemInHand().getType() == Material.TORCH) && (((Player)event.getEntity().getShooter()).getGameMode().getValue() == 0)) {
        if (((HumanEntity)event.getEntity().getShooter()).getItemInHand().getAmount() < 1) {
          ((Player)event.getEntity().getShooter()).sendMessage(ChatColor.GRAY + "You do not have the correct amount of resources to do that!");
          return;
        }
        Byte blockData = Byte.valueOf((byte)0);
        event.getEntity().getWorld().spawnFallingBlock(event.getEntity().getLocation(), Material.TORCH, blockData.byteValue());
        ((HumanEntity)event.getEntity().getShooter()).getItemInHand().setAmount(((HumanEntity)event.getEntity().getShooter()).getItemInHand().getAmount() - 1);
      }

      if ((((Player)event.getEntity().getShooter()).getGameMode().getValue() == 1) && (((Player)event.getEntity().getShooter()).isOp())) {
        if (locn1 < 0.0D) {
          locn1 = loc4 - loc3;
        }

        if (locn < 0.0D) {
          locn = loc2 - loc1;
        }
        if (((locn > 50.0D) && (locn < 60.0D)) || ((locn1 > 50.0D) && (locn1 < 60.0D))) {
          ((Entity) event.getEntity().getShooter()).teleport(proj);
          event.getEntity().getWorld().playEffect(((Entity) event.getEntity().getShooter()).getLocation(), Effect.SMOKE, 900);
        }
        if (((locn > 40.0D) && (locn < 50.0D)) || ((locn1 > 40.0D) && (locn1 < 50.0D))) {
          ((Entity) event.getEntity().getShooter()).teleport(proj);
          event.getEntity().getWorld().playEffect(((Entity) event.getEntity().getShooter()).getLocation(), Effect.SMOKE, 900);
        }
        if (((locn > 30.0D) && (locn < 40.0D)) || ((locn1 > 30.0D) && (locn1 < 40.0D))) {
          ((Entity) event.getEntity().getShooter()).teleport(proj);
          event.getEntity().getWorld().playEffect(((Entity) event.getEntity().getShooter()).getLocation(), Effect.SMOKE, 900);
        }
        if (((locn > 20.0D) && (locn < 30.0D)) || ((locn1 > 20.0D) && (locn1 < 30.0D))) {
          ((Entity) event.getEntity().getShooter()).teleport(proj);
          event.getEntity().getWorld().playEffect(((Entity) event.getEntity().getShooter()).getLocation(), Effect.SMOKE, 900);
        }
        if ((locn < 10.0D) || (locn1 < 10.0D)) {
          ((Entity) event.getEntity().getShooter()).teleport(proj);
          event.getEntity().getWorld().playEffect(((Entity) event.getEntity().getShooter()).getLocation(), Effect.SMOKE, 900);
        }
      }
    }
  }

  private double getBodyHeight(EntityType type)
  {
    switch (type)
    {
    case WITHER_SKULL:
      return 1.4D;
    }

    return (1.0D / 0.0D);
  }

  public void launch(Player p)
  {
    p.setVelocity(new Vector(p.getVelocity().getX(), 0.7D, 0.0D));
  }
}