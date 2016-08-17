package org.iop.client.version_1.channels.processors;

import com.bitdubai.fermat_api.layer.all_definition.events.EventSource;
import com.bitdubai.fermat_api.layer.all_definition.exceptions.InvalidParameterException;
import com.bitdubai.fermat_api.layer.all_definition.network_service.enums.NetworkServiceType;
import com.bitdubai.fermat_p2p_api.layer.all_definition.communication.commons.clients.events.NetworkClientIsActorOnlineEvent;
import com.bitdubai.fermat_p2p_api.layer.all_definition.communication.commons.data.Package;
import com.bitdubai.fermat_p2p_api.layer.all_definition.communication.commons.data.client.respond.IsActorOnlineMsgRespond;
import com.bitdubai.fermat_p2p_api.layer.all_definition.communication.enums.P2pEventType;
import com.bitdubai.fermat_p2p_api.layer.all_definition.communication.enums.PackageType;

import org.iop.client.version_1.channels.endpoints.NetworkClientCommunicationChannel;

import javax.websocket.Session;

/**
 * Created by Manuel Perez (darkpriestrelative@gmail.com) on 17/08/16.
 */
public class IsActorOnlineRespondProcessor extends PackageProcessor {
    /**
     * Constructor with parameter
     *
     * @param channel
     */
    public IsActorOnlineRespondProcessor(NetworkClientCommunicationChannel channel) {
        super(channel, PackageType.IS_ACTOR_ONLINE);
    }

    @Override
    public void processingPackage(Session session, Package packageReceived) {
        System.out.println("Processing new package received, packageType: " + packageReceived.getPackageType());
        //Parse package received
        IsActorOnlineMsgRespond isActorOnlineMsgRespond = IsActorOnlineMsgRespond.parseContent(
                packageReceived.getContent());
        //Create an event to raise
        NetworkClientIsActorOnlineEvent networkClientIsActorOnlineEvent = getEventManager()
                .getNewEventMati(
                        P2pEventType.NETWORK_CLIENT_IS_ACTOR_ONLINE,
                        NetworkClientIsActorOnlineEvent.class);
        networkClientIsActorOnlineEvent.setSource(EventSource.NETWORK_CLIENT);
        networkClientIsActorOnlineEvent.setActorProfile(
                isActorOnlineMsgRespond.getRequestedProfile());
        networkClientIsActorOnlineEvent.setProfileStatus(
                isActorOnlineMsgRespond.getProfileStatus());
        try {
            networkClientIsActorOnlineEvent.setNetworkServiceType(
                    NetworkServiceType.getByCode(
                            isActorOnlineMsgRespond.getNetworkServiceType()));
        } catch (InvalidParameterException e) {
            e.printStackTrace();
        }
        networkClientIsActorOnlineEvent.setQueryID(isActorOnlineMsgRespond.getQueryId());
        if(isActorOnlineMsgRespond.getStatus() == IsActorOnlineMsgRespond.STATUS.SUCCESS){
            networkClientIsActorOnlineEvent.setStatus(NetworkClientIsActorOnlineEvent.STATUS.SUCCESS);
        }else{
            networkClientIsActorOnlineEvent.setStatus(NetworkClientIsActorOnlineEvent.STATUS.FAILED);
        }
        networkClientIsActorOnlineEvent.setPackageId(packageReceived.getPackageId());
        //Raise the event
        System.out.println("ActorListRespondProcessor - Raised a event = P2pEventType.NETWORK_CLIENT_IS_ACTOR_ONLINE");
        getEventManager().raiseEvent(networkClientIsActorOnlineEvent);
    }
}
