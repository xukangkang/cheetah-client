package org.kk.cheetah.client.handler;

import org.kk.cheetah.common.model.response.ServerResponse;

public interface Handler {
    void handle(ServerResponse serverResponse);

    boolean support(ServerResponse serverResponse);
}
