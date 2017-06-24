/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2010-2017 Oracle and/or its affiliates. All rights reserved.
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
 * $Id: CancelTargetImpl.java,v 1.2 2010-10-21 15:36:54 snajper Exp $
 */

package com.sun.xml.ws.security.trust.impl.elements;

import com.sun.xml.ws.security.trust.elements.str.SecurityTokenReference;
import com.sun.xml.ws.security.trust.impl.elements.str.SecurityTokenReferenceImpl;
import com.sun.xml.ws.security.secext10.SecurityTokenReferenceType;
import com.sun.xml.ws.security.Token;
import com.sun.xml.ws.security.trust.elements.CancelTarget;
import com.sun.xml.ws.security.trust.impl.bindings.CancelTargetType;
import javax.xml.bind.JAXBElement;

/**
 * Defines Binding for requesting security tokens to be cancelled.
 *
 * @author Manveen Kaur
 */
public class CancelTargetImpl extends CancelTargetType implements CancelTarget {
    
    private String targetType = null;
    
    // either STR will be present or the token will be
    // carried directly. This will typically be a BST.
    private SecurityTokenReference str = null;    
    private Token token = null;
        
    public CancelTargetImpl(SecurityTokenReference str) {
        setSecurityTokenReference(str);
        setTargetType(CancelTarget.STR_TARGET_TYPE);
    }
    
    public CancelTargetImpl(Token token) {
        setToken(token);
        setTargetType(CancelTarget.CUSTOM_TARGET_TYPE);
    }
    
    public CancelTargetImpl (CancelTargetType ctType){
        final JAXBElement obj = (JAXBElement)ctType.getAny();
        final String local = obj.getName().getLocalPart();
        if ("SecurityTokenReference".equals(local)) {
            final SecurityTokenReference str = 
                        new SecurityTokenReferenceImpl((SecurityTokenReferenceType)obj.getValue());
            setSecurityTokenReference(str);
            setTargetType(CancelTarget.STR_TARGET_TYPE);
        } else {
            //ToDo
        } 
    }
    
    public String getTargetType() {
        return targetType;
    }
    
    public final void setTargetType(final String ttype) {
        targetType = ttype;
    }
    
    public final void setSecurityTokenReference(final SecurityTokenReference ref) {
        if (ref != null) {
            str = ref;
            final JAXBElement<SecurityTokenReferenceType> strElement=
                    (new com.sun.xml.ws.security.secext10.ObjectFactory()).createSecurityTokenReference((SecurityTokenReferenceType)ref);
            setAny(strElement);
        }
        setTargetType(CancelTarget.STR_TARGET_TYPE);
        token = null;        
    }
    
    public SecurityTokenReference getSecurityTokenReference() {
        return str;
    }
    
    public final void setToken(final Token token) {
        if (token != null) {
            this.token = token;
            setAny(token);
        }
        setTargetType(CancelTarget.CUSTOM_TARGET_TYPE);                
        str = null;
    }
    
    public Token getToken() {
        return token;
    }
    
}
