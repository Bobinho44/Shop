 package de.elias177.shop.manager;

 import de.elias177.shop.Shop;
 import java.io.File;
 import java.util.HashMap;
 import org.bukkit.Location;


 public class ShopManager
 {
   public static HashMap<String, NPCShop> shopsDisplayname = new HashMap<>();
   public static HashMap<String, NPCShop> shopsName = new HashMap<>();
   public static HashMap<Location, NPCShop> shopsLocation = new HashMap<>();

   private static File getFolder() {
     return new File(Shop.getInstance().getDataFolder().getPath());
   }

   public static void loadShops() {
     File[] files = getFolder().listFiles();

     shopsDisplayname.clear();
     shopsName.clear();
     if (getFolder().exists())
       for (int i = 0; i < files.length; i++) {
         if (files[i].exists() && 
           files[i].getName().endsWith(".yml")) {
           String name = files[i].getName().replace(".yml", "");
           NPCShop npcShop = new NPCShop(name);
           shopsDisplayname.put(npcShop.getStrDisplayName(), npcShop);
           shopsName.put(name, npcShop);
           shopsLocation.put(npcShop.getLocLocation(), npcShop);
         }
       }
   }
 }
