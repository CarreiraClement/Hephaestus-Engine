package fr.olympus.hephaestus.processing;

import fr.olympus.hephaestus.register.RecipeSelector;

import java.util.List;

public interface ProcessRecipe {

    // ---- planning ----
    String id();
    RecipeSelector selector();
    boolean ordered();
    List<MaterialMatcher> inputs();
    List<MaterialMatcher> outputs();
    int cost();

    // ---- runtime selection ----
    int priority();
    int specificityScore();
    int inputCount();

    TimeWindow timeWindowOrNull();

    boolean canStart(ProcessContext ctx, fr.olympus.hephaestus.resources.HephaestusData data);

    default void onTick(ProcessContext ctx, fr.olympus.hephaestus.resources.HephaestusData data, float elapsedSeconds, ProcessingPhase phase) {}
    default void onEvent(ProcessContext ctx, fr.olympus.hephaestus.resources.HephaestusData data, FactoryEvent event, float elapsedSeconds, ProcessingPhase phase) {}

    boolean tryComplete(ProcessContext ctx, fr.olympus.hephaestus.resources.HephaestusData data, float elapsedSeconds, ProcessingPhase phase);

    default void onOverProcessed(ProcessContext ctx, fr.olympus.hephaestus.resources.HephaestusData data, float elapsedSeconds) {}
}
