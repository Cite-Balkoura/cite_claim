package fr.milekat.cite_claim.obj;

import fr.milekat.cite_core.core.obj.Team;
import org.bukkit.Location;
import org.bukkit.block.Sign;

import java.util.ArrayList;

public class Region {
    private final int id;
    private final String name;
    private String quartier;
    private Sign sign;
    private int prix;
    private Team team;
    private final ArrayList<Location> blocks;

    public Region(int id, String name, String quartier, Sign sign, int prix, Team team, ArrayList<Location> blocks) {
        this.id = id;
        this.name = name;
        this.quartier = quartier;
        this.sign = sign;
        this.prix = prix;
        this.team = team;
        this.blocks = blocks;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getQuartier() {
        return quartier;
    }

    public void setQuartier(String quartier) {
        this.quartier = quartier;
    }

    public Sign getSign() {
        return sign;
    }

    public void setSign(Sign sign) {
        this.sign = sign;
    }

    public int getPrix() {
        return prix;
    }

    public void setPrix(int prix) {
        this.prix = prix;
    }

    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        this.team = team;
    }

    public ArrayList<Location> getBlocks() {
        return blocks;
    }
}
