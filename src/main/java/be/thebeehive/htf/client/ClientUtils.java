package be.thebeehive.htf.client;

import be.thebeehive.htf.library.protocol.server.GameRoundServerMessage.Values;

import java.math.BigDecimal;

import static java.math.BigDecimal.ZERO;

public class ClientUtils {

    /**
     * Sums the values of two Values objects, ensuring that the resulting
     * health and crew values do not exceed their respective maximums
     * and do not fall below zero.
     *
     * @param original the original Values object.
     * @param newValues the new Values object to be added to the original.
     * @return a new Values object containing the summed health,
     *         max health, crew, and max crew values.
     */
    public static Values sumValues(Values original, Values newValues) {
        BigDecimal maxHealth = original.getMaxHealth().add(newValues.getMaxHealth());
        BigDecimal maxCrew = original.getMaxCrew().add(newValues.getMaxCrew());

        if (maxHealth.compareTo(ZERO) < 0) {
            maxHealth = ZERO;
        }

        if (maxCrew.compareTo(ZERO) < 0) {
            maxCrew = ZERO;
        }

        BigDecimal health = original.getHealth().add(newValues.getHealth());
        BigDecimal crew = original.getCrew().add(newValues.getCrew());

        if (health.compareTo(maxHealth) > 0) {
            health = maxHealth;
        }

        if (health.compareTo(ZERO) < 0) {
            health = ZERO;
        }

        if (crew.compareTo(maxCrew) > 0) {
            crew = maxCrew;
        }

        if (crew.compareTo(ZERO) < 0) {
            crew = ZERO;
        }

        Values sum = new Values();
        sum.setHealth(health);
        sum.setMaxHealth(maxHealth);
        sum.setCrew(crew);
        sum.setMaxCrew(maxCrew);

        return sum;
    }

    /**
     * Determines if a spaceship is considered dead based on its health and crew values.
     *
     * @param values the Values object containing health and crew metrics.
     * @return true if the health or crew is zero, indicating the spaceship is dead;
     *         false otherwise.
     */
    public static boolean isDead(Values values) {
        return values.getHealth().compareTo(ZERO) == 0 ||
                values.getCrew().compareTo(ZERO) == 0;
    }

    /**
     * Determines if a spaceship is considered alive based on its health and crew values.
     *
     * @param values the Values object containing health and crew metrics.
     * @return true if both the health and crew are greater than zero, indicating the spaceship is alive;
     *         false if either is zero, which means the spaceship is dead.
     */
    public static boolean isAlive(Values values) {
        return !isDead(values);
    }
}
