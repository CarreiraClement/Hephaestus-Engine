package fr.olympus.hephaestus.processing;

/**
 * Events that can occur in a factory.
 */
public sealed interface FactoryEvent permits FactoryEvent.Action, FactoryEvent.VoxelPress {

    /**
     * An action event with an action ID and an amount.
     *
     * @param actionId The unique identifier for the action.
     * @param amount   The amount associated with the action.
     */
    record Action(String actionId, float amount) implements FactoryEvent {
        // Constructor validation

        /**
         * Constructs an Action event.
         *
         * @param actionId The unique identifier for the action.
         * @param amount   The amount associated with the action.
         * @throws IllegalArgumentException if actionId is null or blank.
         */
        public Action {
            if (actionId == null || actionId.isBlank()) throw new IllegalArgumentException("actionId blank");
        }
    }

    /**
     * A voxel press event at specific coordinates with a button and strength.
     *
     * @param x        The x-coordinate of the voxel.
     * @param y        The y-coordinate of the voxel.
     * @param z        The z-coordinate of the voxel.
     * @param button   The button pressed.
     * @param strength The strength of the press.
     */
    record VoxelPress(int x, int y, int z, int button, float strength) implements FactoryEvent {
        /**
         * Constructs a VoxelPress event.
         *
         * @param x        The x-coordinate of the voxel.
         * @param y        The y-coordinate of the voxel.
         * @param z        The z-coordinate of the voxel.
         * @param button   The button pressed.
         * @param strength The strength of the press.
         * @throws IllegalArgumentException if strength is negative.
         */
        public VoxelPress {
            if (strength < 0) throw new IllegalArgumentException("strength < 0");
        }
    }
}
