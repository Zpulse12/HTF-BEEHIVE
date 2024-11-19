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
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
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

        // Log round header and ship status
        System.out.println("\n==================================");
        System.out.printf("========== ROUND %d =============%n", msg.getRound());
        System.out.println("==================================");
        System.out.printf("Health: %s/%s%n",
                ourShip.getValues().getHealth(),
                ourShip.getValues().getMaxHealth());
        System.out.printf("Crew: %s/%s%n",
                ourShip.getValues().getCrew(),
                ourShip.getValues().getMaxCrew());

        // Log all incoming effects
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

        // Log all available actions
        System.out.println("\n=== Available Actions ===");
        if (availableActions.isEmpty()) {
            System.out.println("No actions available");
        } else {
            for (GameRoundServerMessage.Action action : availableActions) {
                System.out.printf("Action ID %d (counters Effect ID %d)%n",
                        action.getId(), action.getEffectId());
            }
        }

        if (!ourShip.isAlive()) {
            System.out.println("\n!!! Ship is dead !!!");
            client.send(new SelectActionsClientMessage(msg.getRoundId(), new ArrayList<>()));
            return;
        }

        List<Long> selectedActions = new ArrayList<>();
        
        // First priority: Handle deadly effects
        System.out.println("\n=== Checking for Deadly Effects ===");
        for (GameRoundServerMessage.Effect effect : effects) {
            Values futureValues = ClientUtils.sumValues(ourShip.getValues(), effect.getValues());
            System.out.printf("Checking Effect ID %d:%n", effect.getId());
            System.out.printf("  Future Health: %s%n", futureValues.getHealth());
            System.out.printf("  Future Crew: %s%n", futureValues.getCrew());

            if (futureValues.getHealth().compareTo(BigDecimal.ZERO) <= 0 ||
                    futureValues.getCrew().compareTo(BigDecimal.ZERO) <= 0) {
                System.out.println("  WARNING: Deadly effect detected!");
                handleDeadlyEffect(effect, availableActions, selectedActions);
            }
        }

        // Second priority: Counter negative effects
        System.out.println("\n=== Checking for Negative Effects ===");
        for (GameRoundServerMessage.Effect effect : effects) {
            Values effectValues = effect.getValues();
            if (effectValues.getHealth().compareTo(BigDecimal.ZERO) < 0 ||  // Negative health
                effectValues.getCrew().compareTo(BigDecimal.ZERO) < 0) {    // Negative crew
                
                System.out.printf("Found negative effect ID %d%n", effect.getId());
                handleNegativeEffect(effect, availableActions, selectedActions);
            }
        }

        // Third priority: Take beneficial actions
        System.out.println("\n=== Checking for Beneficial Actions ===");
        Values currentValues = ourShip.getValues();
        
        // If health is not full, look for healing opportunities
        if (currentValues.getHealth().compareTo(currentValues.getMaxHealth()) < 0) {
            System.out.println("Health not full - looking for healing actions");
            for (GameRoundServerMessage.Action action : new ArrayList<>(availableActions)) {
                Values actionEffect = getActionEffect(action, effects);
                if (actionEffect != null && actionEffect.getHealth().compareTo(BigDecimal.ZERO) > 0) {
                    BigDecimal futureCrewCount = currentValues.getCrew().add(actionEffect.getCrew());
                    if (futureCrewCount.compareTo(new BigDecimal("100")) > 0) {
                        selectedActions.add(action.getId());
                        availableActions.remove(action);
                        System.out.printf("Selected healing action ID %d%n", action.getId());
                    }
                }
            }
        }

        // If we have excess crew, consider beneficial trades
        if (currentValues.getCrew().compareTo(new BigDecimal("200")) > 0) {
            System.out.println("Have excess crew - looking for beneficial trades");
            for (GameRoundServerMessage.Action action : new ArrayList<>(availableActions)) {
                Values actionEffect = getActionEffect(action, effects);
                if (actionEffect != null && isBeneficialTrade(currentValues, actionEffect)) {
                    selectedActions.add(action.getId());
                    availableActions.remove(action);
                    System.out.printf("Selected beneficial trade action ID %d%n", action.getId());
                }
            }
        }

        // Log final decision
        System.out.println("\n=== Final Decision ===");
        if (selectedActions.isEmpty()) {
            System.out.println("No actions selected this round");
        } else {
            System.out.println("Selected Actions:");
            for (Long actionId : selectedActions) {
                System.out.printf("- Action ID: %d%n", actionId);
            }
        }
        System.out.println("==================================\n");

        client.send(new SelectActionsClientMessage(msg.getRoundId(), selectedActions));
    }

    private Values getActionEffect(GameRoundServerMessage.Action action, List<GameRoundServerMessage.Effect> effects) {
        for (GameRoundServerMessage.Effect effect : effects) {
            if (effect.getId() == action.getEffectId()) {
                return effect.getValues();
            }
        }
        return null;
    }

    private void handleDeadlyEffect(GameRoundServerMessage.Effect effect, 
                                      List<GameRoundServerMessage.Action> availableActions,
                                      List<Long> selectedActions) {
        for (GameRoundServerMessage.Action action : availableActions) {
            if (action.getEffectId() == effect.getId()) {
                selectedActions.add(action.getId());
                availableActions.remove(action);
                System.out.printf("Selected Action ID %d to counter deadly effect%n", action.getId());
                break;
            }
        }
    }

    private void handleNegativeEffect(GameRoundServerMessage.Effect effect,
                                        List<GameRoundServerMessage.Action> availableActions,
                                        List<Long> selectedActions) {
        for (GameRoundServerMessage.Action action : availableActions) {
            if (action.getEffectId() == effect.getId()) {
                selectedActions.add(action.getId());
                availableActions.remove(action);
                System.out.printf("Selected Action ID %d to counter negative effect%n", action.getId());
                break;
            }
        }
    }

    private boolean isBeneficialTrade(Values currentValues, Values actionEffect) {
        // Consider an action beneficial if:
        // 1. It gives more health than crew lost (weighted)
        // 2. Or it gives more maxHealth/maxCrew
        BigDecimal healthGain = actionEffect.getHealth();
        BigDecimal crewLoss = actionEffect.getCrew().negate();
        
        boolean isHealthTrade = healthGain.compareTo(BigDecimal.ZERO) > 0 && 
                               crewLoss.compareTo(BigDecimal.ZERO) > 0 &&
                               healthGain.compareTo(crewLoss.divide(new BigDecimal("2"), 2, RoundingMode.HALF_UP)) > 0;
        
        boolean isMaxStatTrade = actionEffect.getMaxHealth().compareTo(BigDecimal.ZERO) > 0 ||
                                actionEffect.getMaxCrew().compareTo(BigDecimal.ZERO) > 0;
        
        return isHealthTrade || isMaxStatTrade;
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
