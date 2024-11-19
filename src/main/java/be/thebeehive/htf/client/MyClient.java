package be.thebeehive.htf.client;

import be.thebeehive.htf.library.HtfClient;
import be.thebeehive.htf.library.HtfClientListener;
import be.thebeehive.htf.library.protocol.client.SelectActionsClientMessage;
import be.thebeehive.htf.library.protocol.server.ErrorServerMessage;
import be.thebeehive.htf.library.protocol.server.GameEndedServerMessage;
import be.thebeehive.htf.library.protocol.server.GameRoundServerMessage;
import be.thebeehive.htf.library.protocol.server.GameRoundServerMessage.Values;
import be.thebeehive.htf.library.protocol.server.WarningServerMessage;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class MyClient implements HtfClientListener {

    /**
     * You tried to perform an action that is not allowed.
     * An error occurred, and we are unable to recover from this.
     * You will also be disconnected.
     */

    @Override
    public void onErrorServerMessage(HtfClient client, ErrorServerMessage msg) throws Exception {
        System.err.println("CRITICAL ERROR: " + msg.getMsg());
        System.err.println("Connection will be terminated.");
    }

    /**
     * The game finished. Did you win?
     */
    @Override
    public void onGameEndedServerMessage(HtfClient client, GameEndedServerMessage msg) throws Exception {
        System.err.println("\n====== Game Over ======");
        System.err.println("Final Round: " + msg.getRound());
        System.err.println("\nFinal Standings:");

        List<GameEndedServerMessage.LeaderboardTeam> leaderboard = msg.getLeaderboard();
        for (GameEndedServerMessage.LeaderboardTeam team : leaderboard) {
            System.err.printf("%s - Points: %s (Survived until round %d)%n",
                    team.getName(), team.getPoints(), team.getLastRound());
        }
    }

    /**
     * A new round has started.
     * You must reply within 1 second!
     */
    @Override
    public void onGameRoundServerMessage(HtfClient client, GameRoundServerMessage msg) throws Exception {
        GameRoundServerMessage.Spaceship ourShip = msg.getOurSpaceship();
        List<GameRoundServerMessage.Effect> effects = msg.getEffects();
        List<GameRoundServerMessage.Action> availableActions = new ArrayList<>(msg.getActions());

        if (!ourShip.isAlive()) {
            client.send(new SelectActionsClientMessage(msg.getRoundId(), new ArrayList<>()));
            return;
        }

        List<Long> selectedActions = new ArrayList<>();

        System.out.println("\n==================================");
        System.out.printf("========== ROUND %d =============%n", msg.getRound());
        System.out.println("==================================");
        System.out.printf("Health: %s/%s%n",
                ourShip.getValues().getHealth(),
                ourShip.getValues().getMaxHealth());
        System.out.printf("Crew: %s/%s%n",
                ourShip.getValues().getCrew(),
                ourShip.getValues().getMaxCrew());

        System.out.println("\n=== Incoming Effects ===");
        if (effects.isEmpty()) {
            System.out.println("No effects this round");
        } else {
            for (GameRoundServerMessage.Effect effect : effects) {
                Values effectValues = effect.getValues();
                System.out.printf("Effect ID %d:%n", effect.getId());
                System.out.printf("  Health: %s%n", effectValues.getHealth());
                System.out.printf("  MaxHealth: %s%n", effectValues.getMaxHealth());
                System.out.printf("  Crew: %s%n", effectValues.getCrew());
                System.out.printf("  MaxCrew: %s%n", effectValues.getMaxCrew());
            }
        }

        System.out.println("\n=== Available Actions ===");
        if (availableActions.isEmpty()) {
            System.out.println("No actions available");
        } else {
            for (GameRoundServerMessage.Action action : availableActions) {
                Values actionValues = action.getValues();
                System.out.printf("Action ID %d (counters Effect %d):%n",
                        action.getId(), action.getEffectId());
                System.out.printf("  Health: %s%n", actionValues.getHealth());
                System.out.printf("  MaxHealth: %s%n", actionValues.getMaxHealth());
                System.out.printf("  Crew: %s%n", actionValues.getCrew());
                System.out.printf("  MaxCrew: %s%n", actionValues.getMaxCrew());
            }
        }

        for (GameRoundServerMessage.Effect effect : effects) {
            Values futureValues = ClientUtils.sumValues(ourShip.getValues(), effect.getValues());
            if (futureValues.getHealth().compareTo(BigDecimal.ZERO) <= 0 ||
                    futureValues.getCrew().compareTo(BigDecimal.ZERO) <= 0) {
                handleEffect(effect, availableActions, selectedActions, "deadly");
            }
        }

        for (GameRoundServerMessage.Effect effect : effects) {
            Values effectValues = effect.getValues();
            if (effectValues.getHealth().compareTo(BigDecimal.ZERO) < 0 ||
                    effectValues.getCrew().compareTo(BigDecimal.ZERO) < 0) {
                handleEffect(effect, availableActions, selectedActions, "negative");
            }
        }

        client.send(new SelectActionsClientMessage(msg.getRoundId(), selectedActions));
    }

    private void handleEffect(
            GameRoundServerMessage.Effect effect,
            List<GameRoundServerMessage.Action> availableActions,
            List<Long> selectedActions,
            String effectDescription) {
        for (GameRoundServerMessage.Action action : availableActions) {
            if (action.getEffectId() == effect.getId()) {
                selectedActions.add(action.getId());
                availableActions.remove(action);
                System.out.printf("Selected Action ID %d to counter %s effect%n",
                        action.getId(), effectDescription);
                break;
            }
        }
    }

    /**
     * You tried to perform an action that is not allowed.
     * An error occurred but you can still play along.
     * You will NOT be disconnected.
     */
    @Override
    public void onWarningServerMessage(HtfClient client, WarningServerMessage msg) throws Exception {
        System.out.println("WARNING: " + msg.getMsg());
    }
}
