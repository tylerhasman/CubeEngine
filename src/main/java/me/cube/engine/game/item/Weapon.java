package me.cube.engine.game.item;

public class Weapon extends Item {

    public final WeaponType weaponType;

    protected Weapon(String model, WeaponType weaponType) {
        super(model);
        this.weaponType = weaponType;
    }

    public enum WeaponType {
        SWORD,
    }

}
