/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 * 
 * Copyright 1997-2008 Sun Microsystems, Inc. All rights reserved.
 * 
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License. You can obtain
 * a copy of the License at https://glassfish.dev.java.net/public/CDDL+GPL.html
 * or glassfish/bootstrap/legal/LICENSE.txt.  See the License for the specific
 * language governing permissions and limitations under the License.
 * 
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at glassfish/bootstrap/legal/LICENSE.txt.
 * Sun designates this particular file as subject to the "Classpath" exception
 * as provided by Sun in the GPL Version 2 section of the License file that
 * accompanied this code.  If applicable, add the following below the License
 * Header, with the fields enclosed by brackets [] replaced by your own
 * identifying information: "Portions Copyrighted [year]
 * [name of copyright owner]"
 * 
 * Contributor(s):
 * 
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

package com.sun.xml.ws.assembler;

import com.sun.xml.ws.api.BindingID;
import com.sun.xml.ws.api.EndpointAddress;
import com.sun.xml.ws.api.WSBinding;
import com.sun.xml.ws.api.WSService;
import com.sun.xml.ws.api.client.WSPortInfo;
import com.sun.xml.ws.api.model.wsdl.WSDLModel;
import com.sun.xml.ws.api.model.wsdl.WSDLPort;
import com.sun.xml.ws.api.pipe.ClientPipeAssemblerContext;
import com.sun.xml.ws.api.pipe.Pipe;
import com.sun.xml.ws.api.pipe.PipelineAssembler;
import com.sun.xml.ws.api.server.Container;
import com.sun.xml.ws.client.WSServiceDelegate;
import com.sun.xml.ws.policy.PolicyException;
import com.sun.xml.ws.policy.PolicyMap;
import com.sun.xml.ws.policy.jaxws.PolicyConfigParser;
import com.sun.xml.ws.policy.jaxws.WSDLPolicyMapWrapper;
import com.sun.xml.ws.policy.jaxws.client.PolicyFeature;
import com.sun.xml.ws.policy.privateutil.PolicyUtils;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import junit.framework.TestCase;


/**
 * @author Fabian Ritzmann
 */
public class PipelineAssemblerFactoryImplTest extends TestCase {
    
    private static final String NAMESPACE = "http://service1.test.ws.xml.sun.com/";
    private static final URI ADDRESS_URL;
    static {
        try {
            ADDRESS_URL = new URI("http://localhost:8080/dispatch/Service1Service");
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException("Failed to initialize address URI", e);
        }
    }
    
    public PipelineAssemblerFactoryImplTest(String testName) {
        super(testName);
    }

    public void testCreateClientNull() {
        try {
            final BindingID bindingId = BindingID.SOAP11_HTTP;
            final PipelineAssemblerFactoryImpl factory = new PipelineAssemblerFactoryImpl();
            final PipelineAssembler assembler = factory.doCreate(bindingId);
            final Pipe pipe = assembler.createClient(null);
            fail("Expected NullPointerException");
        } catch (NullPointerException e) {
        }
    }
    
    public void testCreateServerNull() {
        try {
            final BindingID bindingId = BindingID.SOAP11_HTTP;
            final PipelineAssemblerFactoryImpl factory = new PipelineAssemblerFactoryImpl();
            final PipelineAssembler assembler = factory.doCreate(bindingId);
            final Pipe pipe = assembler.createServer(null);
            fail("Expected NullPointerException");
        } catch (NullPointerException e) {
        }
    }

    /**
     * Test client creation with parameters that correspond to a dispatch client
     * with no wsit-client.xml.
     */
    public void testCreateDispatchClientNoConfig() throws Exception {
        final BindingID bindingId = BindingID.SOAP11_HTTP;
        final WSBinding binding = bindingId.createBinding();
        final EndpointAddress address = new EndpointAddress(ADDRESS_URL);
        final WSDLPort port = null;
        final WSService service = null;
        final Container container = Container.NONE;
        final ClientPipeAssemblerContext context = new ClientPipeAssemblerContext(
                address, port, service, binding, container);
        final PipelineAssemblerFactoryImpl factory = new PipelineAssemblerFactoryImpl();
        final PipelineAssembler assembler = factory.doCreate(bindingId);
        final Pipe pipe = assembler.createClient(context);
        assertNotNull(pipe);
    }
    
    /**
     * Test client creation with parameters that correspond to a dispatch client
     * with wsit-client.xml.
     */
    public void testCreateDispatchClientNoPoliciesConfig() throws PolicyException {
        Pipe pipe = testDispatch("nopolicies.xml");
        assertNotNull(pipe);
    }

    /**
     * Test client creation with parameters that correspond to a dispatch client
     * with wsit-client.xml.
     */
    public void testCreateDispatchClientAllFeaturesConfig() throws PolicyException {
        Pipe pipe = testDispatch("allfeatures.xml");
        assertNotNull(pipe);
    }

    /**
     * Test client creation with parameters that correspond to a dispatch client
     * with wsit-client.xml.
     */
    public void testCreateDispatchClientNoServiceMatchConfig() throws PolicyException {
        Pipe pipe = testDispatch("noservicematch.xml");
        assertNotNull(pipe);
    }

    /**
     * Execute a sequence that corresponds to:
     * <pre>
     Service.createService(null, serviceName);
     Service.addPort(portName, bindingId, address);</pre>
     */
    private Pipe testDispatch(String configFileName) throws PolicyException {
        final URL wsdlLocation = null;
        final QName serviceName = new QName(NAMESPACE, "Service1Service");
        // Corresponds to Service.createService(wsdlLocation, serviceName)
        final WSServiceDelegate serviceDelegate = new WSServiceDelegate(wsdlLocation, serviceName, Service.class);
        
        final QName portName = new QName(NAMESPACE, "Service1Port");
        final BindingID bindingId = BindingID.SOAP11_HTTP;
        // Corresponds to Service.addPort(portName, bindingId, address)
        serviceDelegate.addPort(portName, bindingId.toString(), ADDRESS_URL.toString());

        final EndpointAddress address = new EndpointAddress(ADDRESS_URL);
        final WSDLPort port = null;
        final WSDLModel clientModel = parseConfigFile(configFileName);
        final WSDLPolicyMapWrapper mapWrapper = clientModel.getExtension(WSDLPolicyMapWrapper.class);
        final PolicyMap map = mapWrapper.getPolicyMap();
        final WSPortInfo portInfo  = serviceDelegate.safeGetPort(portName);
        final PolicyFeature feature = new PolicyFeature(map, clientModel, portInfo);
        final WSBinding binding = bindingId.createBinding(feature);
        final Container container = Container.NONE;
        final ClientPipeAssemblerContext context = new ClientPipeAssemblerContext(
                address, port, serviceDelegate, binding, container);

        final PipelineAssemblerFactoryImpl factory = new PipelineAssemblerFactoryImpl();
        final PipelineAssembler assembler = factory.doCreate(bindingId);
        return assembler.createClient(context);
    }
    
    private WSDLModel parseConfigFile(String configFile) throws PolicyException {
        URL url = PolicyUtils.ConfigFile.loadFromClasspath("assembler/" + configFile);
        return PolicyConfigParser.parseModel(url, true);
    }
    
}