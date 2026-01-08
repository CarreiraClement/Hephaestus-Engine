package fr.olympus.hephaestus.processing;

public sealed interface FactoryEvent permits FactoryEvent.Action, FactoryEvent.VoxelPress {

    record Action(String actionId, float amount) implements FactoryEvent {
        public Action {
            if (actionId == null || actionId.isBlank()) throw new IllegalArgumentException("actionId blank");
        }
    }

    record VoxelPress(int x, int y, int z, int button, float strength) implements FactoryEvent {
        public VoxelPress {
            if (strength < 0) throw new IllegalArgumentException("strength < 0");
        }
    }
}
