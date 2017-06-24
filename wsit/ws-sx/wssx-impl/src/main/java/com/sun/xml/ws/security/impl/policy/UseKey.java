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

/*
 * UseKey.java 
 *
 * Created on February 22, 2006, 6:37 PM 
 * 
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.sun.xml.ws.security.impl.policy;
import com.sun.xml.ws.policy.AssertionSet;
import com.sun.xml.ws.policy.PolicyAssertion;
import com.sun.xml.ws.policy.sourcemodel.AssertionData;
import com.sun.xml.ws.security.policy.SecurityAssertionValidator;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.logging.Level;
import javax.xml.namespace.QName;
import static com.sun.xml.ws.security.impl.policy.Constants.*;

/** 
 * 
 * @author Abhijit Das
 */

public class UseKey extends PolicyAssertion implements com.sun.xml.ws.security.policy.UseKey, SecurityAssertionValidator {    
    
    private static QName sig = new QName("sig"); 
    private URI signatureID;  
    private boolean populated = false;    
    private AssertionFitness fitness = AssertionFitness.IS_VALID;
    
    /** Creates a new instance of UseKeyIMpl */ 
    
    public UseKey(AssertionData name,Collection<PolicyAssertion> nestedAssertions, AssertionSet nestedAlternative) { 
        super(name,nestedAssertions,nestedAlternative);    
    }  
    
    public AssertionFitness validate(boolean isServer) {    
        return populate(isServer);  
    }     
   
    
    private synchronized AssertionFitness populate(boolean isServer) { 
        if(!populated){   
            try {        
                this.signatureID = new URI(this.getAttributeValue(sig));       
            } catch (URISyntaxException ex) { 
                logger.log(Level.SEVERE,LogStringsMessages.SP_0102_INVALID_URI_VALUE(this.getAttributeValue(sig)),ex);
                fitness = AssertionFitness.HAS_INVALID_VALUE;        
            }   
            populated = true;   
        }      
        return fitness;  
    }
}
