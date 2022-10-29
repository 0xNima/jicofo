package org.jitsi.jicofo.rest;

import org.jetbrains.annotations.*;
import org.jitsi.jicofo.*;
import org.jitsi.utils.logging.Logger;
import org.jxmpp.jid.*;
import org.jxmpp.jid.impl.*;
import org.jxmpp.jid.parts.Domainpart;
import org.jxmpp.jid.parts.Localpart;
import org.jxmpp.stringprep.XmppStringprepException;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;

import java.util.*;



@Path("/api/check_room/")
public class RestRooms {
    private final String DOMAIN = "conference.conf.turkgram.org";

    @NotNull
    private final JicofoServices jicofoServices
            = Objects.requireNonNull(JicofoServices.getJicofoServicesSingleton(), "jicofoServices");

    private static final Logger logger = Logger.getLogger(RestRooms.class);

    private Domainpart domain = null;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getRoom(@QueryParam("room") String room_name) {
        logger.error("/check_room/ request-QP: " + room_name);

        try {
            FocusManager focusManager = jicofoServices.getFocusManager();
            EntityBareJid roomJid;

            if (room_name == null) {
                logger.error("/check_room/ status: 400");
                return Response.status(400).build();
            }
            if (!room_name.isEmpty()) {
                try {
                    domain = Domainpart.from(DOMAIN);
                    Localpart localpart = Localpart.from(room_name);
                    roomJid = JidCreate.entityBareFrom(localpart, domain);

                    logger.error("/check_room/ room jid: " + roomJid.toString());

                    if(focusManager.getConference(roomJid) != null){
                        return Response.status(200).build();
                    }

                    logger.error("/check_room/ status: 404");

                    return Response.status(404).build();
                } catch (XmppStringprepException e) {
                    logger.error("/check_room/ bad room name: 400");
                    return Response.status(400).build();
                }
            }

            logger.error("/check_room/ null room name-status: 400");

            return Response.status(400).build();

        } catch (Exception ex) {
            logger.error("check room of Jicofo failed!", ex);
            return Response.serverError().build();
        }
    }
}
