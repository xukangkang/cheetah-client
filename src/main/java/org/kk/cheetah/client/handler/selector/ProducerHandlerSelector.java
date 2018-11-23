package org.kk.cheetah.client.handler.selector;

import java.util.ArrayList;
import java.util.List;

import org.kk.cheetah.client.assist.ThreadParkCoordinator;
import org.kk.cheetah.client.handler.Handler;
import org.kk.cheetah.client.handler.ProducerRecordResponseHandler;
import org.kk.cheetah.common.model.response.ServerResponse;

public class ProducerHandlerSelector implements HandlerSelector {
    private final List<Handler> handlers = new ArrayList<Handler>();
    private ThreadParkCoordinator threadParkCoordinator;

    public ProducerHandlerSelector(ThreadParkCoordinator threadParkCoordinator) {
        handlers.add(new ProducerRecordResponseHandler(threadParkCoordinator));
    }

    public Handler select(ServerResponse serverResponse) {
        for (Handler handler : handlers) {
            if (handler.support(serverResponse)) {
                return handler;
            }
        }
        //TODO 自定义异常
        throw new RuntimeException("没有找到匹配的处理器");
    }
}
