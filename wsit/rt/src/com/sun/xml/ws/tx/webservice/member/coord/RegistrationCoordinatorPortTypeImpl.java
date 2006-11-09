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
package com.sun.xml.ws.tx.webservice.member.coord;

import com.sun.xml.ws.developer.MemberSubmissionAddressing;
import com.sun.xml.ws.developer.Stateful;
import com.sun.xml.ws.developer.StatefulWebServiceManager;
import com.sun.xml.ws.tx.common.TxLogger;
import com.sun.xml.ws.tx.coordinator.RegistrationManager;

import javax.annotation.Resource;
import javax.jws.WebService;
import javax.xml.ws.WebServiceContext;
import java.util.logging.Level;

/**
 * This class handles the register web service method
 *
 * @author Ryan.Shoemaker@Sun.COM
 * @version $Revision: 1.1 $
 * @since 1.0
 */
@MemberSubmissionAddressing
@Stateful
@WebService(serviceName = RegistrationCoordinatorPortTypeImpl.serviceName,
        portName = RegistrationCoordinatorPortTypeImpl.portName,
        endpointInterface = "com.sun.xml.ws.tx.webservice.member.coord.RegistrationCoordinatorPortType",
        targetNamespace = "http://schemas.xmlsoap.org/ws/2004/10/wscoor",
        wsdlLocation = "WEB-INF/wsdl/wscoor.wsdl")
public class RegistrationCoordinatorPortTypeImpl implements RegistrationCoordinatorPortType {

    public static final String serviceName = "Coordinator";
    public static final String portName = "RegistrationCoordinator";

    private static final TxLogger logger = TxLogger.getLogger(RegistrationCoordinatorPortTypeImpl.class);

    @Resource
    private WebServiceContext wsContext;

    /* stateful fields */
    public static StatefulWebServiceManager<RegistrationCoordinatorPortTypeImpl> manager;
    private String activityId;

    public RegistrationCoordinatorPortTypeImpl() {
    }

    /**
     * Constructor for maintaining state
     *
     * @param activityId
     */
    public RegistrationCoordinatorPortTypeImpl(String activityId) {
        this.activityId = activityId;
    }

    /**
     * Handle ws:coor &lt;Register> messages.
     * <p/>
     * <pre>
     * &lt;Register ...>
     *   &lt;ProtocolIdentifier> ... &lt;/ProtocolIdentifier>
     *   &lt;ParticipantProtocolService> ... &lt;/ParticipantProtocolService>
     *   ...
     * &lt;/Register>
     * </pre>
     *
     * @param parameters contains the &lt;register> message
     */
    public void registerOperation(RegisterType parameters) {
        if (logger.isLogging(Level.FINER)) {
            logger.entering("wscoor:register", parameters);
        }
        RegistrationManager.getInstance().register(wsContext, activityId, parameters);
        if (logger.isLogging(Level.FINER)) {
            logger.exiting("wscoor:register");
        }
    }
}
