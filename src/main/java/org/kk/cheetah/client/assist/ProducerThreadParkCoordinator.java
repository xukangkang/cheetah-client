package org.kk.cheetah.client.assist;

import java.util.concurrent.locks.LockSupport;

import org.kk.cheetah.common.model.response.ProducerRecord;
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
public class ProducerThreadParkCoordinator extends AbstractThreadParkCoordinator {
    private final static Logger logger = LoggerFactory.getLogger(ProducerThreadParkCoordinator.class);

    @Override
    protected void buildReturnData(ThreadPark threadPark, ServerResponse serverResponse) {
        ProducerRecord producerRecord = (ProducerRecord) LockSupport.getBlocker(threadPark.getThread());
        producerRecord.setDataId(threadPark.getOnlyTag());
    }
}
