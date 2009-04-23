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

package com.sun.xml.ws.dump;

import com.sun.xml.ws.api.message.Packet;
import com.sun.xml.ws.api.pipe.Fiber;
import com.sun.xml.ws.api.pipe.NextAction;
import com.sun.xml.ws.api.pipe.Tube;
import com.sun.xml.ws.api.pipe.TubeCloner;
import com.sun.xml.ws.api.pipe.helper.AbstractFilterTubeImpl;
import com.sun.xml.ws.dump.MessageDumper.ProcessingState;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Marek Potociar <marek.potociar at sun.com>
 */
public class LoggingDumpTube extends AbstractFilterTubeImpl {
    public static enum Position {
        Before(MessageDumper.ProcessingState.Received, MessageDumper.ProcessingState.Processed),
        After(MessageDumper.ProcessingState.Processed, MessageDumper.ProcessingState.Received);

        private final MessageDumper.ProcessingState requestState;
        private final MessageDumper.ProcessingState responseState;

        private Position(ProcessingState requestState, ProcessingState responseState) {
            this.requestState = requestState;
            this.responseState = responseState;
        }
    }

    private static final AtomicInteger ID_GENERATOR = new AtomicInteger(0);
    //
    private MessageDumper messageDumper;
    private final Level loggingLevel;
    private final Position position;
    private final int tubeId;

    public LoggingDumpTube(Level loggingLevel, Position position, Tube tubelineHead) {
        super(tubelineHead);

        this.position = position;
        this.loggingLevel = loggingLevel;
        
        this.tubeId = ID_GENERATOR.incrementAndGet();
    }

    public void setLoggedTubeName(String loggedTubeName) {
        assert messageDumper == null; // must not set a new message dumper once already set
        this.messageDumper = new MessageDumper(loggedTubeName, Logger.getLogger(loggedTubeName), loggingLevel);
    }

    /**
     * Copy constructor.
     */
    private LoggingDumpTube(LoggingDumpTube original, TubeCloner cloner) {
        super(original, cloner);

        this.messageDumper = original.messageDumper;
        this.loggingLevel = original.loggingLevel;
        this.position = original.position;

        this.tubeId = ID_GENERATOR.incrementAndGet();
    }

    public LoggingDumpTube copy(TubeCloner cloner) {
        return new LoggingDumpTube(this, cloner);
    }


    @Override
    public NextAction processRequest(Packet request) {
        if (messageDumper.isLoggable()) {
            messageDumper.dump(MessageDumper.MessageType.Request, position.requestState, messageDumper.convertToString(request), tubeId, Fiber.current().owner.id);
        }

        return super.processRequest(request);
    }

    @Override
    public NextAction processResponse(Packet response) {
        if (messageDumper.isLoggable()) {
            messageDumper.dump(MessageDumper.MessageType.Response, position.responseState, messageDumper.convertToString(response), tubeId, Fiber.current().owner.id);
        }

        return super.processResponse(response);
    }

    @Override
    public NextAction processException(Throwable t) {
        if (messageDumper.isLoggable()) {
            messageDumper.dump(MessageDumper.MessageType.Exception, position.responseState, messageDumper.convertToString(t), tubeId, Fiber.current().owner.id);
        }

        return super.processException(t);
    }

    @Override
    public void preDestroy() {
        super.preDestroy();
    }
}
