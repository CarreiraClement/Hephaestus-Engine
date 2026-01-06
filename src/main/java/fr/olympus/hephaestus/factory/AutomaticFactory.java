package fr.olympus.hephaestus.factory;

public interface AutomaticFactory {

    float[] getProcessingTimeBounds();
    float getCurrentProcessingTime();

    void setCurrentProcessingTime(float time);

    default void updateAutomaticFactory(float dt) {
        setCurrentProcessingTime(getCurrentProcessingTime() + dt);
    }

}
