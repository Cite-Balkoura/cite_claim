package fr.milekat.cite_claim.obj;

import fr.milekat.cite_core.core.obj.Team;
import org.bukkit.block.Sign;

public class Region {
    private final int id;
    private final String name;
    private final int type;
    private final Sign sign;
    private int prix;
    private Team team;

    public Region(int id, String name, int type, Sign sign, int prix, Team team) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.sign = sign;
        this.prix = prix;
        this.team = team;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getType() {
        return type;
    }

    public Sign getSign() {
        return sign;
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
}
