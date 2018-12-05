package org.kk.cheetah.client.assist;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

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
public abstract class AbstractThreadParkCoordinator implements ThreadParkCoordinator {
    private final static Logger logger = LoggerFactory.getLogger(AbstractThreadParkCoordinator.class);
    private final List<ThreadPark> threadParkList = new CopyOnWriteArrayList<ThreadPark>();

    public void addProducerThreadParkList(ThreadPark roducerThreadPark) {
        threadParkList.add(roducerThreadPark);
    }

    public void unpark(ServerResponse serverResponse) {
        if (logger.isDebugEnabled()) {
            logger.debug("unpark -> serverResponse:{}", serverResponse);
        }
        Iterator<ThreadPark> iterator = threadParkList.iterator();

        while (iterator.hasNext()) {
            ThreadPark threadPark = iterator.next();
            if (logger.isDebugEnabled()) {
                logger.debug("unpark -> try unpark , onlyTag:{}", serverResponse.getOnlyTag());
            }
            if (threadPark.getOnlyTag().equals(serverResponse.getOnlyTag())) {
                if (serverResponse.getErrorMsg() != null) {
                    buildErrorData(threadPark, serverResponse);
                } else {
                    buildReturnData(threadPark, serverResponse);
                }
                LockSupport.unpark(threadPark.getThread());
                threadParkList.remove(threadPark);
                if (logger.isDebugEnabled()) {
                    logger.debug("unpark -> unpark success , onlyTag:{}", serverResponse.getOnlyTag());
                }
                return;
            }
        }
        try {
            if (logger.isDebugEnabled()) {
                logger.debug("unpark -> unpark fail,try unpark again, onlyTag:{}", serverResponse.getOnlyTag());
            }
            TimeUnit.MICROSECONDS.sleep(1);
            unpark(serverResponse);
        } catch (InterruptedException e) {
            logger.error("unpark", e);
        }
    }

    private void buildErrorData(ThreadPark threadPark, ServerResponse serverResponse) {
        Thread thread = threadPark.getThread();
        ServerResponse blockerServerResponse = (ServerResponse) LockSupport.getBlocker(thread);
        if (blockerServerResponse == null) {
            //等待Thread park
            try {
                TimeUnit.MICROSECONDS.sleep(1);
            } catch (InterruptedException e) {
                if (logger.isDebugEnabled()) {
                    logger.debug("buildReturnData", e);
                }
            }
            buildErrorData(threadPark, serverResponse);
            return;
        }
        blockerServerResponse.setErrorMsg(serverResponse.getErrorMsg());
    }

    protected abstract void buildReturnData(ThreadPark threadPark, ServerResponse serverResponse);
}
