package be.thebeehive.htf.client;

import be.thebeehive.htf.library.HtfClient;
import be.thebeehive.htf.library.HtfClientListener;
import be.thebeehive.htf.library.protocol.client.SelectActionsClientMessage;
import be.thebeehive.htf.library.protocol.server.ErrorServerMessage;
import be.thebeehive.htf.library.protocol.server.GameEndedServerMessage;
import be.thebeehive.htf.library.protocol.server.GameRoundServerMessage;
import be.thebeehive.htf.library.protocol.server.WarningServerMessage;

import java.util.ArrayList;
import java.util.Collections;

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

    }

    /**
     * A new round has started.
     * You must reply within 1 second!
     */
    @Override
    public void onGameRoundServerMessage(HtfClient client, GameRoundServerMessage msg) throws Exception {
        GameRoundServerMessage.Spaceship ourShip = msg.getOurSpaceship();
        if (!ourShip.isAlive()) {
            client.send(new SelectActionsClientMessage(msg.getRoundId(), new ArrayList<>()));
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
