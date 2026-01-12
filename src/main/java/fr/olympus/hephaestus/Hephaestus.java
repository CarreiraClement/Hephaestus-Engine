package fr.olympus.hephaestus;

import fr.olympus.hephaestus.register.AutoRegistrar;
import fr.olympus.hephaestus.register.RegisterType;
import fr.olympus.hephaestus.resources.HephaestusData;

import java.util.concurrent.atomic.AtomicReference;

/**
 * Main class for the Hephaestus framework.
 */
public final class Hephaestus {

    /**
     * Singleton instance of Hephaestus
     */
    private static final AtomicReference<Hephaestus> INSTANCE = new AtomicReference<>();

    /**
     * Data storage for Hephaestus
     */
    private final HephaestusData data;

    private Hephaestus() {
        this.data = new HephaestusData();
    }

    /**
     * Initializes the Hephaestus framework.
     *
     * @return The initialized Hephaestus instance.
     * @throws IllegalStateException if Hephaestus is already initialized.
     */
    public static Hephaestus init() {
        Hephaestus created = new Hephaestus();
        if (!INSTANCE.compareAndSet(null, created)) {
            throw new IllegalStateException("Hephaestus is already initialized.");
        }
        return created;
    }

    /**
     * Automatically registers components based on the specified type and base packages.
     *
     * @param type         The type of components to register. see {@link RegisterType}.
     * @param basePackages The base packages to scan for components.
     */
    public static void autoRegister(RegisterType type, String... basePackages) {
        AutoRegistrar.register(type, basePackages);
    }

    /**
     * Retrieves the singleton instance of Hephaestus.
     *
     * @return The Hephaestus instance.
     * @throws IllegalStateException if Hephaestus is not initialized.
     */
    private static Hephaestus getInstance() {
        Hephaestus inst = INSTANCE.get();
        if (inst == null) {
            throw new IllegalStateException("Hephaestus is not initialized yet.");
        }
        return inst;
    }

    /**
     * Retrieves the Hephaestus data storage.
     *
     * @return The HephaestusData instance.
     */
    public static HephaestusData getData() {
        return getInstance().data;
    }


}
