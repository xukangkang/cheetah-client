package org.kk.cheetah.client.assist;

import java.util.concurrent.locks.LockSupport;

import org.kk.cheetah.common.model.response.ConsumerRecords;
import org.kk.cheetah.common.model.response.ServerResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
* @ClassName: DataSendTaskCoordinator
* @Description: 数据生产任务协调器
* @author xukangkang
* @date 2018年11月20日  
*
 */
public class ConsumerThreadParkCoordinator extends AbstractThreadParkCoordinator {
    private final static Logger logger = LoggerFactory.getLogger(ConsumerThreadParkCoordinator.class);

    @Override
    protected void buildReturnData(ThreadPark threadPark, ServerResponse serverResponse) {
        ConsumerRecords blockerConsumerRecords = (ConsumerRecords) LockSupport.getBlocker(threadPark.getThread());
        ConsumerRecords consumerRecords = (ConsumerRecords) serverResponse;
        blockerConsumerRecords.setConsumberRecords(consumerRecords.getConsumberRecords());
    }
}
