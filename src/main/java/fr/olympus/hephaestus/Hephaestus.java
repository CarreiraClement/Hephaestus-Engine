package fr.olympus.hephaestus;

import fr.olympus.hephaestus.register.AutoRegistrar;
import fr.olympus.hephaestus.register.RegisterType;
import fr.olympus.hephaestus.resources.HephaestusData;

import java.util.concurrent.atomic.AtomicReference;

public final class Hephaestus {

    private static final AtomicReference<Hephaestus> INSTANCE = new AtomicReference<>();

    private final HephaestusData data;

    private Hephaestus() {
        this.data = new HephaestusData();
    }

    public static Hephaestus init() {
        Hephaestus created = new Hephaestus();
        if (!INSTANCE.compareAndSet(null, created)) {
            throw new IllegalStateException("Hephaestus is already initialized.");
        }
        return created;
    }

    public static void autoRegister(RegisterType type, String... basePackages) {
        AutoRegistrar.register(type, basePackages);
    }

    private static Hephaestus getInstance() {
        Hephaestus inst = INSTANCE.get();
        if (inst == null) {
            throw new IllegalStateException("Hephaestus is not initialized yet.");
        }
        return inst;
    }

    public static HephaestusData getData() {
        return getInstance().data;
    }
}
