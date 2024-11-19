package be.thebeehive.htf.library.protocol.server;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

/**
 * The GameRoundServerMessage class represents a server message that contains information about
 * the current game round. This includes details such as round number, unique round identifier,
 * next checkpoint, effects and actions relevant to the current game round, and information
 * about the spaceships (our spaceship and competing spaceships).
 */
public class GameRoundServerMessage extends ServerMessage {

    private long round;
    private UUID roundId;
    private Checkpoint nextCheckpoint;
    private List<Effect> effects;
    private List<Action> actions;
    private Spaceship ourSpaceship;
    private List<Spaceship> competingSpaceships;

    public GameRoundServerMessage() {

    }

    /**
     * Retrieves the current round number of the game.
     *
     * @return the current round number as a long.
     */
    public long getRound() {
        return round;
    }

    /**
     * Sets the current round number of the game.
     *
     * @param round the round number to be set as a long.
     */
    public void setRound(long round) {
        this.round = round;
    }

    /**
     * Retrieves the unique identifier of the current game round.
     * This is must be provided in the SelectActionsClientMessage.
     *
     * @return the unique identifier (UUID) of the current game round.
     */
    public UUID getRoundId() {
        return roundId;
    }

    /**
     * Sets the unique identifier for the current game round.
     *
     * @param roundId the unique identifier (UUID) to be set for the current game round
     */
    public void setRoundId(UUID roundId) {
        this.roundId = roundId;
    }

    /**
     * Retrieves the next checkpoint that will be reached.
     *
     * @return the next {@link Checkpoint}
     */
    public Checkpoint getNextCheckpoint() {
        return nextCheckpoint;
    }

    /**
     * Sets the next checkpoint that the spaceship will reach.
     *
     * @param nextCheckpoint the next {@link Checkpoint} to be reached.
     */
    public void setNextCheckpoint(Checkpoint nextCheckpoint) {
        this.nextCheckpoint = nextCheckpoint;
    }

    /**
     * Retrieves the list of effects that will occur this round.
     *
     * @return a list of {@link Effect} objects.
     */
    public List<Effect> getEffects() {
        return effects;
    }

    /**
     * Sets the list of effects that will occur during this game round.
     *
     * @param effects the list of {@link Effect} objects to be set.
     */
    public void setEffects(List<Effect> effects) {
        this.effects = effects;
    }

    /**
     * Retrieves the list of actions that are available this round.
     *
     * @return a list of {@link Action} objects.
     */
    public List<Action> getActions() {
        return actions;
    }

    /**
     * Sets the list of actions that are available for this game round.
     *
     * @param actions the list of {@link Action} objects to be set
     */
    public void setActions(List<Action> actions) {
        this.actions = actions;
    }

    /**
     * Retrieves our spaceship.
     *
     * @return the current instance of our spaceship.
     */
    public Spaceship getOurSpaceship() {
        return ourSpaceship;
    }

    /**
     * Sets our spaceship for the current game.
     *
     * @param ourSpaceship the instance of our {@link Spaceship} to be set.
     */
    public void setOurSpaceship(Spaceship ourSpaceship) {
        this.ourSpaceship = ourSpaceship;
    }

    /**
     * Retrieves the list of competing spaceships for the current game.
     *
     * @return a list of {@link Spaceship} objects representing the competing spaceships.
     */
    public List<Spaceship> getCompetingSpaceships() {
        return competingSpaceships;
    }

    /**
     * Sets the list of competing spaceships for the current game.
     *
     * @param competingSpaceships the list of {@link Spaceship} objects to be set as competing spaceships.
     */
    public void setCompetingSpaceships(List<Spaceship> competingSpaceships) {
        this.competingSpaceships = competingSpaceships;
    }

    /**
     * Represents a checkpoint within a game round.
     * A checkpoint is characterized by a specific round and the values that will be
     * applied to the spaceship when the checkpoint is reached.
     */
    public static class Checkpoint {

        private long round;
        private Values values;

        public Checkpoint() {

        }

        /**
         * The checkpoint will be reached at the start of this round.
         *
         * @return the round as a long.
         */
        public long getRound() {
            return round;
        }

        /**
         * Sets the round at which the checkpoint will be reached.
         *
         * @param round the round number as a long.
         */
        public void setRound(long round) {
            this.round = round;
        }

        /**
         * The values that will be applied to the spaceship when the checkpoint is reached
         *
         * @return the current Values instance.
         */
        public Values getValues() {
            return values;
        }

        /**
         * Sets the values that will be applied to the spaceship when the checkpoint is reached.
         *
         * @param values the new Values instance to be set.
         */
        public void setValues(Values values) {
            this.values = values;
        }
    }

    /**
     * Represents an effect within a game round.
     * This effect includes an identifier, a step value indicating when the effect
     * will be triggered, and the values that will be applied when the effect is triggered.
     */
    public static class Effect {

        private long id;
        private int step;
        private Values values;

        public Effect() {

        }

        /**
         * Retrieves the ID associated with this effect.
         *
         * @return the ID as a long value.
         */
        public long getId() {
            return id;
        }

        /**
         * Sets the ID associated with this effect.
         *
         * @param id the new ID as a long value.
         */
        public void setId(long id) {
            this.id = id;
        }

        /**
         * Retrieves the step when the effect will be triggered.
         *
         * @return the step as an integer.
         */
        public int getStep() {
            return step;
        }

        /**
         * Sets the step when the effect will be triggered.
         *
         * @param step the step value to set.
         */
        public void setStep(int step) {
            this.step = step;
        }

        /**
         * The values that will be applied to the spaceship when the effect is NOT removed
         *
         * @return the current values as a Values object.
         */
        public Values getValues() {
            return values;
        }

        /**
         * Sets the values that will be applied to the spaceship when the effect is not removed.
         *
         * @param values the Values object containing health and crew information.
         */
        public void setValues(Values values) {
            this.values = values;
        }
    }

    /**
     * Represents an action within a game round.
     * An action is characterized by an identifier, an optional associated effect ID,
     * and the values that apply to the spaceship upon action execution.
     */
    public static class Action {

        private long id;
        private long effectId;
        private Values values;

        public Action() {

        }

        /**
         * Retrieves the ID of the action.
         *
         * @return the ID of the action as a long.
         */
        public long getId() {
            return id;
        }

        /**
         * Sets the ID for this action.
         *
         * @param id the new ID for the action
         */
        public void setId(long id) {
            this.id = id;
        }

        /**
         * Retrieves the ID of the effect associated with the action.
         * The effectId can be equal to -1 if the action is not coupled to any effect.
         *
         * @return the effect ID as a long. Or -1.
         */
        public long getEffectId() {
            return effectId;
        }

        /**
         * Sets the ID for the effect associated with this action.
         *
         * @param effectId the new effect ID for the action
         */
        public void setEffectId(long effectId) {
            this.effectId = effectId;
        }

        /**
         * The values that will be applied to the spaceship when the action is executed
         *
         * @return the values as a {@link Values} object.
         */
        public Values getValues() {
            return values;
        }

        /**
         * Sets the values that will be applied to the spaceship when the action is executed
         *
         * @param values the new values as a {@link Values} object
         */
        public void setValues(Values values) {
            this.values = values;
        }
    }

    /**
     * Represents a spaceship within the game context.
     * This class handles various properties of the spaceship such as its team name,
     * the values associated with its health and crew, and its alive status.
     */
    public static class Spaceship {

        private String name;
        private Values values;
        private boolean alive;

        public Spaceship() {

        }

        /**
         * Retrieves the name of the team which manages the spaceship.
         *
         * @return the name of the team.
         */
        public String getName() {
            return name;
        }

        /**
         * Sets the name of the team which manages the spaceship.
         *
         * @param name the name the team.
         */
        public void setName(String name) {
            this.name = name;
        }

        /**
         * Retrieves the current values associated with the spaceship, including health and crew details.
         *
         * @return the current values as a {@link Values} object.
         */
        public Values getValues() {
            return values;
        }

        /**
         * Sets the current values associated with the spaceship, including health and crew details.
         *
         * @param values the new values to be set as a {@link Values} object.
         */
        public void setValues(Values values) {
            this.values = values;
        }

        /**
         * Checks if the spaceship is currently alive.
         *
         * @return true if the spaceship is alive, false otherwise.
         */
        public boolean isAlive() {
            return alive;
        }

        /**
         * Sets the alive status of the spaceship.
         *
         * @param alive true if the spaceship is to be set as alive, false otherwise.
         */
        public void setAlive(boolean alive) {
            this.alive = alive;
        }
    }

    /**
     * Represents the state of various values associated with an entity, such as a spaceship or game effect.
     * This class includes properties related to health and crew, each having their respective maximum values.
     */
    public static class Values {

        private BigDecimal health;
        private BigDecimal maxHealth;
        private BigDecimal crew;
        private BigDecimal maxCrew;

        public Values() {

        }

        /**
         * Retrieves the current health value.
         *
         * @return the current health as a BigDecimal.
         */
        public BigDecimal getHealth() {
            return health;
        }

        /**
         * Sets the current health value.
         *
         * @param health the new health value as a BigDecimal.
         */
        public void setHealth(BigDecimal health) {
            this.health = health;
        }

        /**
         * Retrieves the maximum health value.
         *
         * @return the maximum health as a BigDecimal.
         */
        public BigDecimal getMaxHealth() {
            return maxHealth;
        }

        /**
         * Sets the maximum health value.
         *
         * @param maxHealth the new maximum health value as a BigDecimal.
         */
        public void setMaxHealth(BigDecimal maxHealth) {
            this.maxHealth = maxHealth;
        }

        /**
         * Retrieves the current crew value.
         *
         * @return the current crew as a BigDecimal.
         */
        public BigDecimal getCrew() {
            return crew;
        }

        /**
         * Sets the current crew value.
         *
         * @param crew the new crew value as a BigDecimal.
         */
        public void setCrew(BigDecimal crew) {
            this.crew = crew;
        }

        /**
         * Retrieves the maximum crew value.
         *
         * @return the maximum crew as a BigDecimal.
         */
        public BigDecimal getMaxCrew() {
            return maxCrew;
        }

        /**
         * Sets the maximum crew value.
         *
         * @param maxCrew the new maximum crew value as a BigDecimal.
         */
        public void setMaxCrew(BigDecimal maxCrew) {
            this.maxCrew = maxCrew;
        }
    }
}
