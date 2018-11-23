package org.kk.cheetah.client.handler;

import org.kk.cheetah.client.assist.ThreadParkCoordinator;
import org.kk.cheetah.common.model.response.ProducerRecord;
import org.kk.cheetah.common.model.response.ServerResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProducerRecordResponseHandler extends AbstractHandler {

    private Logger logger = LoggerFactory.getLogger(this.getClass());
    private ThreadParkCoordinator threadParkCoordinator;

    public ProducerRecordResponseHandler(ThreadParkCoordinator threadParkCoordinator) {
        this.threadParkCoordinator = threadParkCoordinator;
    }

    public boolean support(ServerResponse serverResponse) {
        return serverResponse instanceof ProducerRecord;
    }

    public void handle(ServerResponse serverResponse) {
        if (logger.isDebugEnabled()) {
            logger.debug("handle", serverResponse);
        }
        ProducerRecord producerRecord = (ProducerRecord) serverResponse;
        threadParkCoordinator.unpark(producerRecord);
    }

}
