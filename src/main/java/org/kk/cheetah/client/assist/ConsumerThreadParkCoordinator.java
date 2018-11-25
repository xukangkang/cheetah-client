package org.kk.cheetah.client.assist;

import java.util.concurrent.TimeUnit;
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
    	if(logger.isDebugEnabled()){
    		logger.debug("buildReturnData -> onlyTag:{}",serverResponse.getOnlyTag());
    	}
        ConsumerRecords blockerConsumerRecords = (ConsumerRecords) LockSupport.getBlocker(threadPark.getThread());
        if(blockerConsumerRecords == null){
        	//等待Thread park
        	try {
				TimeUnit.MICROSECONDS.sleep(1);
			} catch (InterruptedException e) {
		    	if(logger.isDebugEnabled()){
		    		logger.debug("buildReturnData",e);
		    	}
			}
        	buildReturnData(threadPark,serverResponse);
        	return;
        }
        ConsumerRecords consumerRecords = (ConsumerRecords) serverResponse;
    	if(logger.isDebugEnabled()){
    		logger.debug("buildReturnData ->consumerRecords:{}",consumerRecords);
    	}
    	blockerConsumerRecords.setConsumberRecords(consumerRecords.getConsumberRecords());
    }
}
