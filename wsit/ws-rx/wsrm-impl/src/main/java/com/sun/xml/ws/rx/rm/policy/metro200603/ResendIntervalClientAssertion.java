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

package com.sun.xml.ws.rx.rm.policy.metro200603;

import com.sun.xml.ws.policy.AssertionSet;
import com.sun.xml.ws.policy.PolicyAssertion;
import com.sun.xml.ws.policy.SimpleAssertion;
import com.sun.xml.ws.policy.sourcemodel.AssertionData;
import com.sun.xml.ws.rx.policy.AssertionInstantiator;
import com.sun.xml.ws.rx.rm.api.RmAssertionNamespace;
import com.sun.xml.ws.rx.rm.policy.RmConfigurator;
import com.sun.xml.ws.rx.rm.api.ReliableMessagingFeature;
import com.sun.xml.ws.rx.rm.api.ReliableMessagingFeatureBuilder;
import com.sun.xml.ws.rx.rm.api.RmProtocolVersion;
import java.util.Collection;
import javax.xml.namespace.QName;

/**
 * <sunc:ResendInterval Milliseconds="..." />
 */
/**
 * Specifies how long the RM Source will wait after transmitting a message and
 * before retransmitting the message. If omitted, value from
 * {@link ReliableMessagingFeature#DEFAULT_MESSAGE_RETRANSMISSION_INTERVAL}
 * is used. Specified in milliseconds.
 * 
 * @author Marek Potociar (marek.potociar at sun.com)
 */
public class ResendIntervalClientAssertion extends SimpleAssertion implements RmConfigurator {
    public static final QName NAME = RmAssertionNamespace.METRO_CLIENT_200603.getQName("ResendInterval");
    private static final QName MILLISECONDS_ATTRIBUTE_QNAME = new QName("", "Milliseconds");

    private static AssertionInstantiator instantiator = new AssertionInstantiator() {
        public PolicyAssertion newInstance(AssertionData data, Collection<PolicyAssertion> assertionParameters, AssertionSet nestedAlternative){
            return new ResendIntervalClientAssertion(data, assertionParameters);
        }
    };
    
    public static AssertionInstantiator getInstantiator() {
        return instantiator;
    }

    private final long interval;
    
    public ResendIntervalClientAssertion(AssertionData data, Collection<? extends PolicyAssertion> assertionParameters) {
        super(data, assertionParameters);
        
        interval = Long.parseLong(super.getAttributeValue(MILLISECONDS_ATTRIBUTE_QNAME));
    }
   
    public long getInterval() {
        return interval;
    }

    public ReliableMessagingFeatureBuilder update(ReliableMessagingFeatureBuilder builder) {
        return builder.messageRetransmissionInterval(interval);
    }

    public boolean isCompatibleWith(RmProtocolVersion version) {
        return RmProtocolVersion.WSRM200502 == version;
    }
}
