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
package com.sun.xml.ws.rx.rm.runtime;

import com.sun.xml.ws.api.message.Packet;
import com.sun.xml.ws.rx.util.Communicator;
import com.sun.xml.ws.rx.RxConfiguration;
import com.sun.xml.ws.api.addressing.WSEndpointReference;
import com.sun.xml.ws.api.pipe.Fiber;
import com.sun.xml.ws.commons.Logger;
import com.sun.xml.ws.rx.RxRuntimeException;
import com.sun.xml.ws.rx.rm.RmVersion;
import com.sun.xml.ws.rx.rm.localization.LocalizationMessages;
import com.sun.xml.ws.rx.rm.runtime.sequence.Sequence;
import com.sun.xml.ws.rx.rm.protocol.wsrm200702.AcceptType;
import com.sun.xml.ws.rx.rm.protocol.wsrm200702.CloseSequenceElement;
import com.sun.xml.ws.rx.rm.protocol.wsrm200702.CloseSequenceResponseElement;
import com.sun.xml.ws.rx.rm.protocol.wsrm200702.CreateSequenceElement;
import com.sun.xml.ws.rx.rm.protocol.wsrm200702.CreateSequenceResponseElement;
import com.sun.xml.ws.rx.rm.protocol.wsrm200702.Identifier;
import com.sun.xml.ws.rx.rm.protocol.wsrm200702.OfferType;
import com.sun.xml.ws.rx.rm.protocol.wsrm200702.TerminateSequenceElement;
import com.sun.xml.ws.rx.rm.protocol.wsrm200702.TerminateSequenceResponseElement;
import com.sun.xml.ws.rx.rm.protocol.wsrm200702.UsesSequenceSTR;
import com.sun.xml.ws.security.secext10.SecurityTokenReferenceType;
import java.util.Calendar;

/**
 *
 * @author Marek Potociar (marek.potociar at sun.com)
 */
final class Rm11ClientSession extends ClientSession {

    private static final Logger LOGGER = Logger.getLogger(Rm11ClientSession.class);

    Rm11ClientSession(RxConfiguration configuration, WSEndpointReference rmsEndpointReference, Communicator communicator) {
        super(configuration, rmsEndpointReference, communicator);
    }

    @Override
    void openRmSession(String offerInboundSequenceId, SecurityTokenReferenceType strType) throws RxRuntimeException {
        CreateSequenceElement csElement = new CreateSequenceElement();
        csElement.setAcksTo(super.rmSourceReference.toSpec());

        if (offerInboundSequenceId != null) {
            Identifier offerIdentifier = new Identifier();
            offerIdentifier.setValue(offerInboundSequenceId);

            OfferType offer = new OfferType();
            offer.setIdentifier(offerIdentifier);
            // Microsoft does not accept CreateSequence messages if AcksTo and Offer/Endpoint are not the same
            offer.setEndpoint(csElement.getAcksTo());
            csElement.setOffer(offer);
        }
        if (strType != null) {
            csElement.setSecurityTokenReference(strType);
        }


        PacketAdapter requestAdapter = PacketAdapter.getInstance(configuration, communicator.createEmptyRequestPacket(true));
        requestAdapter.setRequestMessage(csElement, RmVersion.WSRM200702.createSequenceAction);

        if (strType != null) {
            UsesSequenceSTR usesSequenceSTR = new UsesSequenceSTR();
            usesSequenceSTR.getOtherAttributes().put(communicator.soapMustUnderstandAttributeName, "true");
            requestAdapter.appendHeader(usesSequenceSTR);
        }

        PacketAdapter responseAdapter = PacketAdapter.getInstance(configuration, communicator.send(requestAdapter.getPacket()));
        if (!responseAdapter.containsMessage()) {
            throw LOGGER.logSevereException(new RxRuntimeException(LocalizationMessages.WSRM_1114_NULL_RESPONSE_ON_PROTOCOL_MESSAGE_REQUEST("CreateSequence")));
        }
        if (responseAdapter.isFault()) {
            // FIXME pass fault data into exception
            throw LOGGER.logSevereException(new RxRuntimeException(LocalizationMessages.WSRM_1115_PROTOCOL_MESSAGE_REQUEST_REFUSED("CreateSequence")));
        }

        CreateSequenceResponseElement csrElement = responseAdapter.unmarshallMessage();
        responseAdapter.getPacket();
        outboundSequenceId = csrElement.getIdentifier().getValue();

        long expirationTime = Sequence.NO_EXPIRATION;
        if (csrElement.getExpires() != null && !"PT0S".equals(csrElement.getExpires().getValue().toString())) {
            expirationTime = csrElement.getExpires().getValue().getTimeInMillis(Calendar.getInstance()) + System.currentTimeMillis();
        }
        sequenceManager.createOutboundSequence(outboundSequenceId, (strType != null) ? strType.getId() : null, expirationTime);

        if (offerInboundSequenceId != null) {
            AcceptType accept = csrElement.getAccept();
            if (accept == null || accept.getAcksTo() == null) {
                throw new RxRuntimeException(LocalizationMessages.WSRM_1116_ACKS_TO_NOT_EQUAL_TO_ENDPOINT_DESTINATION(null, communicator.getDestinationAddress()));
            } else if (!communicator.getDestinationAddress().getURI().toString().equals(new WSEndpointReference(accept.getAcksTo()).getAddress())) {
                throw new RxRuntimeException(LocalizationMessages.WSRM_1116_ACKS_TO_NOT_EQUAL_TO_ENDPOINT_DESTINATION(accept.getAcksTo().toString(), communicator.getDestinationAddress()));
            }
            inboundSequenceId = offerInboundSequenceId;
            sequenceManager.createInboundSequence(inboundSequenceId, (strType != null) ? strType.getId() : null, Sequence.NO_EXPIRATION);
        }
    }

    @Override
    void closeOutboundSequence() {
        PacketAdapter requestAdapter = PacketAdapter.getInstance(configuration, communicator.createEmptyRequestPacket(true));

        requestAdapter.setRequestMessage(
                new CloseSequenceElement(outboundSequenceId, sequenceManager.getSequence(outboundSequenceId).getLastMessageId()),
                RmVersion.WSRM200702.closeSequenceAction);
        if (inboundSequenceId != null) {
            requestAdapter.appendSequenceAcknowledgementHeader(sequenceManager.getSequence(inboundSequenceId));
        }

        communicator.sendAsync(requestAdapter.getPacket(), new Fiber.CompletionCallback() {

            public void onCompletion(Packet responsePacket) {
                PacketAdapter responseAdapter = PacketAdapter.getInstance(configuration, responsePacket);
                if (!responseAdapter.containsMessage()) {
                    LOGGER.warning(LocalizationMessages.WSRM_1114_NULL_RESPONSE_ON_PROTOCOL_MESSAGE_REQUEST("CloseSequence"));
                }

                processInboundMessageHeaders(responseAdapter, false);

                if (responseAdapter.isFault()) {
                    // FIXME need to find a way how to pass fault information into the exception
                    LOGGER.warning(LocalizationMessages.WSRM_1115_PROTOCOL_MESSAGE_REQUEST_REFUSED("CloseSequence"));
                }

                CloseSequenceResponseElement csrElement = responseAdapter.unmarshallMessage(); // consuming message here
                responseAdapter.getPacket();

                if (!outboundSequenceId.equals(csrElement.getIdentifier().getValue())) {
                    LOGGER.warning(LocalizationMessages.WSRM_1119_UNEXPECTED_SEQUENCE_ID_IN_CLOSE_SR(csrElement.getIdentifier().getValue(), outboundSequenceId));
                }
            }

            public void onCompletion(Throwable error) {
                // TODO L10N
                LOGGER.warning("Unexpected exception while trying to close sequence", error);
            }
        });
    }

    @Override
    void terminateOutboundSequence() {
        PacketAdapter requestAdapter = PacketAdapter.getInstance(configuration, communicator.createEmptyRequestPacket(true));
        requestAdapter.setRequestMessage(
                new TerminateSequenceElement(outboundSequenceId, sequenceManager.getSequence(outboundSequenceId).getLastMessageId()),
                RmVersion.WSRM200702.terminateSequenceAction);

        communicator.sendAsync(requestAdapter.getPacket(), new Fiber.CompletionCallback() {

            public void onCompletion(Packet responsePacket) {
                PacketAdapter responseAdapter = null;
                try {
                    responseAdapter = PacketAdapter.getInstance(configuration, responsePacket);
                    if (!responseAdapter.containsMessage()) {
                        if (inboundSequenceId != null) {
                            // we should get the TerminateSequence response back
                            LOGGER.warning(LocalizationMessages.WSRM_1114_NULL_RESPONSE_ON_PROTOCOL_MESSAGE_REQUEST("TerminateSequence"));
                        } else {
                            return;
                        }
                    }

                    processInboundMessageHeaders(responseAdapter, false);

                    if (responseAdapter.isFault()) {
                        LOGGER.warning(LocalizationMessages.WSRM_1115_PROTOCOL_MESSAGE_REQUEST_REFUSED("TerminateSequence"));
                    }

                    String responseAction = responseAdapter.getWsaAction();
                    if (RmVersion.WSRM200702.terminateSequenceAction.equals(responseAction)) {
                        TerminateSequenceElement tsElement = responseAdapter.unmarshallMessage();

                        sequenceManager.terminateSequence(tsElement.getIdentifier().getValue());
                    } else if (RmVersion.WSRM200702.terminateSequenceResponseAction.equals(responseAction)) {
                        TerminateSequenceResponseElement tsrElement = responseAdapter.unmarshallMessage();

                        if (!outboundSequenceId.equals(tsrElement.getIdentifier().getValue())) {
                            LOGGER.warning(LocalizationMessages.WSRM_1117_UNEXPECTED_SEQUENCE_ID_IN_TERMINATE_SR(tsrElement.getIdentifier().getValue(), outboundSequenceId));
                        }
                    }
                } finally {
                    if (responseAdapter != null) {
                        responseAdapter.consume();
                    }
                }
            }

            public void onCompletion(Throwable error) {
                // TODO L10N
                LOGGER.warning("Unexpected exception while trying to terminate sequence", error);
            }
        });
    }
}