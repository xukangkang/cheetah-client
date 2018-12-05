package org.kk.cheetah.client.assist;

import java.util.concurrent.TimeUnit;
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

        ProducerRecord blockerProducerRecord = (ProducerRecord) LockSupport.getBlocker(threadPark.getThread());
        if (blockerProducerRecord == null) {
            //等待Thread park
            try {
                TimeUnit.MICROSECONDS.sleep(1);
            } catch (InterruptedException e) {
                if (logger.isDebugEnabled()) {
                    logger.debug("buildReturnData", e);
                }
            }
            buildReturnData(threadPark, serverResponse);
            return;
        }
        ProducerRecord producerRecord = (ProducerRecord) serverResponse;
        if (logger.isDebugEnabled()) {
            logger.debug("buildReturnData ->producerRecord:{}", producerRecord);
        }
        blockerProducerRecord.setDataId(threadPark.getOnlyTag());
    }
}
