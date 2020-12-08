package fr.milekat.cite_claim.ascenseur.obj;

import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.trait.trait.Equipment;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;

public class Ascenseur {
    private final String name;
    private int npcid;
    private Location floorDownLoc;
    private Location floorUpLoc;
    private Integer yawDown;
    private Integer yawUp;
    private Location boutonDown;
    private Location boutonUp;
    private ArrayList<Block> doorDown;
    private ArrayList<Block> doorUp;
    private Location signDown;
    private Location signUp;
    private Integer floor;
    private boolean work;

    public Ascenseur(String name, int npcid, Location floorDownLoc, Location floorUpLoc, Integer yawDown, Integer yawUp, Location boutonDown, Location boutonUp, ArrayList<Block> doorDown, ArrayList<Block> doorUp, Location signDown, Location signUp) {
        this.name = name;
        this.npcid = npcid;
        this.floorDownLoc = floorDownLoc;
        this.floorUpLoc = floorUpLoc;
        this.yawDown = yawDown;
        this.yawUp = yawUp;
        this.boutonDown = boutonDown;
        this.boutonUp = boutonUp;
        this.doorDown = doorDown;
        this.doorUp = doorUp;
        this.signDown = signDown;
        this.signUp = signUp;
        this.work = false;
        this.floor = 0;
    }

    public boolean isComplete() {
        NPC npc = CitizensAPI.getNPCRegistry().getById(npcid);
        return npc != null && floorDownLoc != null && floorUpLoc != null
                && yawDown != null && yawUp != null && boutonDown != null && boutonUp != null;
    }

    public int getNpcId() {
        return npcid;
    }

    public void setNpcId(int npcid) {
        this.npcid = npcid;
    }

    public Location getFloorDownLoc() {
        return floorDownLoc;
    }

    public void setFloorDownLoc(Location floorDownLoc) {
        this.floorDownLoc = floorDownLoc;
    }

    public Location getFloorUpLoc() {
        return floorUpLoc;
    }

    public void setFloorUpLoc(Location floorUpLoc) {
        this.floorUpLoc = floorUpLoc;
    }

    public void openDoor() {
        ItemStack itemStack = new ItemStack(Material.CONDUIT);
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta!=null) {
            itemMeta.setCustomModelData(2);
            itemStack.setItemMeta(itemMeta);
        }
        setDoor(itemStack);
    }

    public void closeDoor() {
        ItemStack itemStack = new ItemStack(Material.CONDUIT);
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta!=null) {
            itemMeta.setCustomModelData(1);
            itemStack.setItemMeta(itemMeta);
        }
        setDoor(itemStack);
    }

    @SuppressWarnings("deprecation")
    private void setDoor(ItemStack itemStack) {
        NPC npc = CitizensAPI.getNPCRegistry().getById(this.npcid);
        if (npc==null) return;
        Equipment equipment = npc.getTrait(Equipment.class);
        equipment.set(Equipment.EquipmentSlot.HELMET,itemStack);
        Location newLoc = npc.getEntity().getLocation();
        if (floorDownLoc != null && floorUpLoc != null && newLoc.distance(floorDownLoc) < newLoc.distance(floorUpLoc)) {
            newLoc.setYaw(yawDown);
        } else {
            newLoc.setYaw(yawUp);
        }
        npc.teleport(newLoc, PlayerTeleportEvent.TeleportCause.PLUGIN);
    }

    public Integer getYawDown() {
        return yawDown;
    }

    public void setYawDown(Integer yawDown) {
        this.yawDown = yawDown;
    }

    public Integer getYawUp() {
        return yawUp;
    }

    public void setYawUp(Integer yawUp) {
        this.yawUp = yawUp;
    }

    public Location getBoutonDown() {
        return boutonDown;
    }

    public void setBoutonDown(Location boutonDown) {
        this.boutonDown = boutonDown;
    }

    public Location getBoutonUp() {
        return boutonUp;
    }

    public void setBoutonUp(Location boutonUp) {
        this.boutonUp = boutonUp;
    }

    public Location getSignDown() {
        return signDown;
    }

    public void setSignDown(Location signDown) {
        this.signDown = signDown;
    }

    public Location getSignUp() {
        return signUp;
    }

    public void setSignUp(Location signUp) {
        this.signUp = signUp;
    }

    public String getName() {
        return name;
    }

    public Integer getFloor() {
        return floor;
    }

    public void setFloor(Integer floor) {
        this.floor = floor;
    }

    public ArrayList<Block> getDoorDown() {
        return doorDown;
    }

    public void setDoorDown(ArrayList<Block> doorDown) {
        this.doorDown = doorDown;
    }

    public ArrayList<Block> getDoorUp() {
        return doorUp;
    }

    public void setDoorUp(ArrayList<Block> doorUp) {
        this.doorUp = doorUp;
    }

    public boolean isWork() {
        return work;
    }

    public void setWork(boolean work) {
        this.work = work;
    }
}
