package fr.olympus.hephaestus;

public class Hephaestus {

    private static Hephaestus instance;

    private Hephaestus() {
        // Private constructor to prevent instantiation
    }

    public static Hephaestus init(){
        if (instance != null) {
            throw new IllegalStateException("Hephaestus is already initialized.");
        }
        instance = new Hephaestus();
        return instance;
    }

    public static Hephaestus getInstance(){
        if (instance == null) {
            throw new IllegalStateException("Hephaestus is not initialized yet.");
        }
        return instance;
    }

}
