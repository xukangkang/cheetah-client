package org.kk.cheetah.client.producer;

import java.util.concurrent.Future;

import org.kk.cheetah.common.model.request.ProducerRecordRequest;
import org.kk.cheetah.common.model.response.ProducerRecord;

public interface Producer<K, V> {
    public Future<ProducerRecord> send(ProducerRecordRequest<K, V> producerRecord);
}
