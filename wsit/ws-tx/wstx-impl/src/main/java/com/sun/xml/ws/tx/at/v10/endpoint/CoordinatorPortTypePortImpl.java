/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 1997-2017 Oracle and/or its affiliates. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License.  You can
 * obtain a copy of the License at
 * https://oss.oracle.com/licenses/CDDL+GPL-1.1
 * or LICENSE.txt.  See the License for the specific
 * language governing permissions and limitations under the License.
 *
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at LICENSE.txt.
 *
 * GPL Classpath Exception:
 * Oracle designates this particular file as subject to the "Classpath"
 * exception as provided by Oracle in the GPL Version 2 section of the License
 * file that accompanied this code.
 *
 * Modifications:
 * If applicable, add the following below the License Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyright [year] [name of copyright owner]"
 *
 * Contributor(s):
 * If you wish your version of this file to be governed by only the CDDL or
 * only the GPL Version 2, indicate your decision by adding "[Contributor]
 * elects to include this software in this distribution under the [CDDL or GPL
 * Version 2] license."  If you don't indicate a single choice of license, a
 * recipient has the option to distribute your version of this file under
 * either the CDDL, the GPL Version 2 or to extend the choice of license to
 * its licensees as provided above.  However, if you add GPL Version 2 code
 * and therefore, elected the GPL Version 2 license, then the option applies
 * only if the new code is made subject to such option by the copyright
 * holder.
 */

package com.sun.xml.ws.tx.at.v10.endpoint;

import com.sun.xml.ws.developer.MemberSubmissionAddressing;
import com.sun.xml.ws.tx.at.common.WSATVersion;
import com.sun.xml.ws.tx.at.common.endpoint.Coordinator;
import com.sun.xml.ws.tx.at.v10.types.CoordinatorPortType;
import com.sun.xml.ws.tx.at.v10.types.Notification;

import javax.jws.WebService;
import javax.xml.ws.BindingType;
import javax.xml.ws.WebServiceContext;


@WebService(portName = "CoordinatorPortTypePort", serviceName = "WSAT10Service", targetNamespace = "http://schemas.xmlsoap.org/ws/2004/10/wsat", wsdlLocation = "/wsdls/wsat10/wsat.wsdl", endpointInterface = "com.sun.xml.ws.tx.at.v10.types.CoordinatorPortType")
@BindingType("http://schemas.xmlsoap.org/wsdl/soap/http")
@MemberSubmissionAddressing
public class CoordinatorPortTypePortImpl
    implements CoordinatorPortType
{

    @javax.annotation.Resource
    private WebServiceContext m_context;

    public CoordinatorPortTypePortImpl() {
    }

    /**
     * 
     * @param parameters
     */
    public void preparedOperation(Notification parameters) {
        Coordinator<Notification> proxy = getProxy();
        proxy.preparedOperation(parameters);
    }

    /**
     * 
     * @param parameters
     */
    public void abortedOperation(Notification parameters) {
        Coordinator<Notification> proxy = getProxy();
        proxy.abortedOperation(parameters);
    }

    /**
     * 
     * @param parameters
     */
    public void readOnlyOperation(Notification parameters) {
        Coordinator<Notification> proxy = getProxy();
        proxy.readOnlyOperation(parameters);
    }

    /**
     * 
     * @param parameters
     */
    public void committedOperation(Notification parameters) {
        Coordinator<Notification> proxy = getProxy();
        proxy.committedOperation(parameters);
    }

    /**
     * 
     * @param parameters
     */
    public void replayOperation(Notification parameters) {
        Coordinator<Notification> proxy = getProxy();
        proxy.replayOperation(parameters);
    }

    protected Coordinator<Notification> getProxy() {
        Coordinator<Notification> proxy = new Coordinator<Notification>(m_context, WSATVersion.v10);
        return proxy;
    }


}
