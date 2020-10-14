package fr.milekat.cite_claim.ascenseur.obj;

import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.trait.trait.Equipment;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.data.type.Sign;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class Ascenseur {
    private final String name;
    private NPC npc;
    private Location floorDownLoc;
    private Location floorUpLoc;
    private Integer yawDown;
    private Integer yawUp;
    private Location boutonDown;
    private Location boutonUp;
    private Sign signDown;
    private Sign signUp;

    public Ascenseur(String name, NPC npc, Location floorDownLoc, Location floorUpLoc, Integer yawDown, Integer yawUp, Location boutonDown, Location boutonUp, Sign signDown, Sign signUp) {
        this.name = name;
        this.npc = npc;
        this.floorDownLoc = floorDownLoc;
        this.floorUpLoc = floorUpLoc;
        this.yawDown = yawDown;
        this.yawUp = yawUp;
        this.boutonDown = boutonDown;
        this.boutonUp = boutonUp;
        this.signDown = signDown;
        this.signUp = signUp;
    }

    public boolean isComplete() {
        return npc != null && floorDownLoc != null && floorUpLoc != null && yawDown != null && yawUp != null;
    }

    public NPC getNpc() {
        return npc;
    }

    public void setNpc(NPC npc) {
        this.npc = npc;
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

    private void setDoor(ItemStack itemStack) {
        Equipment equipment = this.npc.getTrait(Equipment.class);
        equipment.set(Equipment.EquipmentSlot.HELMET,itemStack);
        Location newLoc = npc.getEntity().getLocation();
        if (newLoc.distance(floorDownLoc) < newLoc.distance(floorUpLoc)) {
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

    public Sign getSignDown() {
        return signDown;
    }

    public void setSignDown(Sign signDown) {
        this.signDown = signDown;
    }

    public Sign getSignUp() {
        return signUp;
    }

    public void setSignUp(Sign signUp) {
        this.signUp = signUp;
    }

    public String getName() {
        return name;
    }
}
