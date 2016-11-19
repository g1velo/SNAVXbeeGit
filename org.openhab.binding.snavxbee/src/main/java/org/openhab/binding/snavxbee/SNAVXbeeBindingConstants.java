/**
 * Copyright (c) 2014-2016 by the respective copyright holders.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.snavxbee;

import org.eclipse.smarthome.core.thing.ThingTypeUID;

/**
 * The {@link SNAVXbeeBinding} class defines common constants, which are
 * used across the whole binding.
 *
 * @author Stephan Navarro - Initial contribution
 */
public class SNAVXbeeBindingConstants {

    public static final String BINDING_ID = "snavxbee";

    // List of all Thing Type UIDs
    public final static ThingTypeUID THING_TYPE_SAMPLE = new ThingTypeUID(BINDING_ID, "api");
    // public final static ThingTypeUID THING_TYPE_API = new ThingTypeUID(BINDING_ID, "sample");
    public final static ThingTypeUID THING_TYPE_TOSR02T = new ThingTypeUID(BINDING_ID, "TOSR02T");

    // List of all Channel ids
    public final static String CHANNEL_1 = "channel1";
    public final static String AD0DIO0 = "AD0DIO0";
    public final static String TEMPERATURE = "temperature";

}
