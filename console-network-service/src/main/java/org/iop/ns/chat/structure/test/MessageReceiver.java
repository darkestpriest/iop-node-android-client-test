package org.iop.ns.chat.structure.test;

import com.bitdubai.fermat_p2p_api.layer.all_definition.communication.commons.profiles.ActorProfile;
import org.iop.ns.chat.structure.ChatMetadataRecord;

import java.util.List;
import java.util.UUID;

/**
 * Created by mati on 14/08/16.
 */
public interface MessageReceiver {

    void onMessageReceived(String sender,ChatMetadataRecord chatMetadataRecord);

    void onActorListReceived(List<ActorProfile> list);

    void onActorRegistered(ActorProfile actorProfile);

    void onMessageFail(UUID messageId);

    void onActorOffline(String remotePkGoOffline);
}
