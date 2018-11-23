package org.kk.cheetah.client.assist;

import org.kk.cheetah.common.model.response.ServerResponse;

public interface ThreadParkCoordinator {
    public void unpark(ServerResponse serverResponse);

    public void addProducerThreadParkList(ThreadPark threadPark);
}
