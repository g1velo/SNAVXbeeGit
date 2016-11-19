/**
 * Copyright (c) 2014-2016 by the respective copyright holders.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.snavxbee.handler;

import static org.openhab.binding.snavxbee.SNAVXbeeBindingConstants.*;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Random;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.eclipse.smarthome.config.core.status.ConfigStatusMessage;
import org.eclipse.smarthome.core.library.types.DecimalType;
import org.eclipse.smarthome.core.thing.ChannelUID;
import org.eclipse.smarthome.core.thing.Thing;
import org.eclipse.smarthome.core.thing.ThingStatus;
import org.eclipse.smarthome.core.thing.binding.ConfigStatusThingHandler;
import org.eclipse.smarthome.core.types.Command;
import org.eclipse.smarthome.core.types.State;
import org.eclipse.smarthome.core.types.UnDefType;
import org.openhab.binding.snavxbee.util.MyDataReceiveListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.digi.xbee.api.RemoteXBeeDevice;
import com.digi.xbee.api.XBeeDevice;
import com.digi.xbee.api.XBeeNetwork;
import com.digi.xbee.api.exceptions.XBeeException;

/**
 * The {@link SNAVXbeeHandler} is responsible for handling commands, which are
 * sent to one of the channels.
 *
 * @author Stephan Navarro - Initial contribution
 */

// public class SNAVXbeeHandler extends BaseThingHandler {

public class SNAVXbeeHandler extends ConfigStatusThingHandler {

    ScheduledFuture<?> refreshJob;
    private BigDecimal refresh;

    private Logger logger = LoggerFactory.getLogger(SNAVXbeeHandler.class);
    // TODO Replace with the serial port where your receiver module is connected.
    private static final String PORT = "COM9";
    // TODO Replace with the baud rate of you receiver module.
    private static final int BAUD_RATE = 9600;
    private static String REMOTE_NODE_IDENTIFIER = "ss";
    // private static List<String> commandList = Arrays.asList("n", "d", "b", "[", "a");
    private String xbeecmd = new String();
    private static XBeeDevice myDevice = new XBeeDevice(PORT, BAUD_RATE);

    public SNAVXbeeHandler(Thing thing) {
        super(thing);
    }

    @Override
    public void handleCommand(ChannelUID channelUID, Command command) {

        logger.debug("ChannelUID : " + channelUID);

        if (channelUID.getId().equals(CHANNEL_1)) {
            // TODO: handle command
            // Note: if communication with thing fails for some reason,
            // indicate that by setting the status with detail information
            // updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.COMMUNICATION_ERROR,
            // "Could not control device at IP address x.x.x.x");
            logger.debug("We are on Channel1 and running command : " + command);

            switch (command.toString()) {
                case "ON":
                    logger.debug("setting : " + channelUID.getId() + " to " + command);
                    this.xbeecmd = "e";
                    break;
                case "OFF":
                    logger.debug("setting : " + channelUID.getId() + " to " + command);
                    this.xbeecmd = "o";
                    break;
                default:
                    logger.info("Don't know what to do with command : " + command);
                    this.xbeecmd = null;
                    break;

            }
            if (this.xbeecmd != null) {
                runCommand(xbeecmd);

            }
        }

        if (channelUID.getId().equals(TEMPERATURE)) {
            logger.debug("We are on temperature and running command : " + command);
            logger.debug("We are on temperature and running command : " + command);

        }

    }

    @Override
    public void initialize() {
        // TODO: Initialize the thing. If done set status to ONLINE to indicate proper working.
        // Long running initialization should be done asynchronously in background.
        updateStatus(ThingStatus.ONLINE);

        // Note: When initialization can NOT be done set the status with more details for further
        // analysis. See also class ThingStatusDetail for all available status details.
        // Add a description to give user information to understand why thing does not work
        // as expected. E.g.
        // updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.CONFIGURATION_ERROR,
        // "Can not access device as username and/or password are invalid");

        logger.debug("Working on Thing : " + thing.getUID());

        String node[] = thing.getUID().getAsString().split(":");
        logger.debug("Thing Xbee Address is : " + node[2]);
        REMOTE_NODE_IDENTIFIER = node[2];

        try {
            myDevice.open();
            myDevice.addDataListener(new MyDataReceiveListener());
            // myDevice.liste

            XBeeNetwork xbeeNetwork = myDevice.getNetwork();

            RemoteXBeeDevice remoteDevice = xbeeNetwork.discoverDevice(REMOTE_NODE_IDENTIFIER);

            Thread.sleep(2000);

            if (remoteDevice == null) {
                logger.debug(
                        "Couldn't find the remote XBee device with '" + REMOTE_NODE_IDENTIFIER + "' Node Identifier.");
                // System.exit(1);
            }

        } catch (XBeeException e) {
            e.printStackTrace();
            // System.exit(1);
        } catch (Exception e) {
            e.printStackTrace();
            // System.exit(1);
        }

        startAutomaticRefresh();
    }

    public State getTemperature() {

        Random randomGenerator = new Random();
        int randomInt = randomGenerator.nextInt(400);

        float float_temp = 8 + (randomInt / 100);

        // String tempa = "10.2";
        String tempa = String.valueOf(float_temp);

        logger.debug("in getTemperature : " + tempa + " " + float_temp + " " + randomInt + " " + randomInt / 100);

        try {
            logger.info("should ne running command to get temperature");
            this.xbeecmd = "b";
            runCommand(this.xbeecmd);
        } catch (Exception e) {
            e.printStackTrace();

        }
        if (true) {
            if (tempa != null) {
                return new DecimalType(tempa);
            }
        }
        return UnDefType.UNDEF;

    }

    @Override
    public Collection<ConfigStatusMessage> getConfigStatus() {
        // TODO Auto-generated method stub
        return null;
    }

    private boolean runCommand(String cmd) {
        logger.debug("in runCmd");
        boolean success = false;

        try {

            XBeeNetwork xbeeNetwork = myDevice.getNetwork();

            RemoteXBeeDevice remoteDevice = xbeeNetwork.discoverDevice(REMOTE_NODE_IDENTIFIER);

            if (remoteDevice == null) {
                logger.debug(
                        "Couldn't find the remote XBee device with '" + REMOTE_NODE_IDENTIFIER + "' Node Identifier.");
                // System.exit(1);
            }

            myDevice.sendData(remoteDevice, xbeecmd.getBytes());

            success = true;

        } catch (com.digi.xbee.api.exceptions.InterfaceNotOpenException e) {
            e.printStackTrace();
            this.open();
        } catch (XBeeException e) {
            e.printStackTrace();

        } catch (Exception e) {
            e.printStackTrace();
            // System.exit(1);
        }

        return success;
    }

    private void startAutomaticRefresh() {

        logger.debug("startAutomaticRefresh()");

        Runnable runnable = new Runnable() {
            @Override
            public void run() {

                updateState(new ChannelUID(getThing().getUID(), TEMPERATURE), getTemperature());

            }
        };

        refreshJob = scheduler.scheduleAtFixedRate(runnable, 15, 15, TimeUnit.SECONDS);

    }

    @Override
    public void dispose() {
        // TODO Auto-generated method stub
        super.dispose();
        logger.debug(this.getClass() + " dispose was called ");
        myDevice.close();

    }

    public void open() {
        try {
            myDevice.open();
        } catch (XBeeException e) {
            e.printStackTrace();
        }
    }

}
