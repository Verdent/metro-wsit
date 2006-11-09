/*
 * The contents of this file are subject to the terms
 * of the Common Development and Distribution License
 * (the License).  You may not use this file except in
 * compliance with the License.
 * 
 * You can obtain a copy of the license at
 * https://glassfish.dev.java.net/public/CDDLv1.0.html.
 * See the License for the specific language governing
 * permissions and limitations under the License.
 * 
 * When distributing Covered Code, include this CDDL
 * Header Notice in each file and include the License file
 * at https://glassfish.dev.java.net/public/CDDLv1.0.html.
 * If applicable, add the following below the CDDL Header,
 * with the fields enclosed by brackets [] replaced by
 * you own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 * 
 * Copyright 2006 Sun Microsystems Inc. All Rights Reserved
 */
package com.sun.xml.ws.tx.coordinator;

import com.sun.xml.ws.tx.common.ActivityIdentifier;
import static com.sun.xml.ws.tx.common.Constants.WSAT_2004_PROTOCOL;
import static com.sun.xml.ws.tx.common.Constants.WSAT_OASIS_NSURI;
import com.sun.xml.ws.tx.common.TxLogger;
import com.sun.xml.ws.tx.webservice.member.coord.CreateCoordinationContextType;

import java.util.logging.Level;

/**
 * This class is an abstraction of the two kinds of CoordinationContexts defined
 * in WS-Coordination 2004/10 member submission and 2006/03 OASIS.
 *
 * @author Ryan.Shoemaker@Sun.COM
 * @version $Revision: 1.1 $
 * @since 1.0
 */
public class ContextFactory {

    private static long ACTIVITY_ID;

    static private TxLogger logger = TxLogger.getCoordLogger(ContextFactory.class);


    /**
     * Create a new {@link CoordinationContextInterface} of the appropriate type based on
     * the coordination type namespace uri.
     * <p/>
     *
     * @param coordType the nsuri of the coordination type, either {@link com.sun.xml.ws.tx.common.Constants#WSAT_2004_PROTOCOL}
     *                  or {@link com.sun.xml.ws.tx.common.Constants#WSAT_OASIS_NSURI}
     * @return the {@link CoordinationContextInterface}
     */
    public static CoordinationContextInterface createContext(String coordType, long expires) {

        if (logger.isLogging(Level.FINER)) {
            logger.entering("ContextFactory.createContext: coordType=" + coordType + " expires=" + expires);
        }
        CoordinationContextInterface context;

        if (WSAT_2004_PROTOCOL.equals(coordType)) {
            context = new CoordinationContext200410();
            context.setCoordinationType(coordType);

            context.setExpires(expires);

            ACTIVITY_ID += 1;
            context.setIdentifier("uuid:WSCOOR-SUN-" + ACTIVITY_ID);

            // bake the activity id as <wsa:ReferenceParameters> into the registration service EPR, so we can
            // identify the activity when the <register> requests come in.
            ActivityIdentifier activityId = new ActivityIdentifier(context.getIdentifier());
            context.setRegistrationService(RegistrationManager.newRegistrationEPR(activityId));
        } else if (WSAT_OASIS_NSURI.equals(coordType)) {
            throw new UnsupportedOperationException(
                    LocalizationMessages.OASIS_UNSUPPORTED()
            );
        } else {
            throw new UnsupportedOperationException(
                    LocalizationMessages.UNRECOGNIZED_COORDINATION_TYPE(coordType)
            );
        }

        if (logger.isLogging(Level.FINER)) {
            logger.exiting("ContextFactory.createContext: created context for activity id: " + context.getIdentifier());
        }
        return context;
    }

    public static CoordinationContextInterface createContext(CreateCoordinationContextType contextRequest) {
        // TODO: send fault ws:coor S4.2 InvalidProtocol if createContext fails
        return createContext(contextRequest.getCoordinationType(), contextRequest.getExpires().getValue());
    }


}
