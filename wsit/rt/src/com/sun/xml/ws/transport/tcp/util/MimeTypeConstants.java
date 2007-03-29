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

package com.sun.xml.ws.transport.tcp.util;

import com.sun.xml.ws.encoding.fastinfoset.FastInfosetMIMETypes;

/**
 *
 * @author Alexey Stashok
 */
public final class MimeTypeConstants {
    public static final String SOAP11 = "text/xml";
    public static final String SOAP12 = "application/soap+xml";
    
    public static final String MTOM = "multipart/related";
    
    public static final String FAST_INFOSET_SOAP11 = FastInfosetMIMETypes.SOAP_11;
    public static final String FAST_INFOSET_SOAP12 = FastInfosetMIMETypes.SOAP_12;
    
    public static final String FAST_INFOSET_STATEFUL_SOAP11 = FastInfosetMIMETypes.STATEFUL_SOAP_11;
    public static final String FAST_INFOSET_STATEFUL_SOAP12 = FastInfosetMIMETypes.STATEFUL_SOAP_12;
}
