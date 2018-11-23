package org.kk.cheetah.client.handler.selector;

import org.kk.cheetah.client.handler.Handler;
import org.kk.cheetah.common.model.response.ServerResponse;

public interface HandlerSelector {
    public Handler select(ServerResponse serverResponse);
}