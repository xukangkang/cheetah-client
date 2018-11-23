package org.kk.cheetah.client.consumer;

import org.kk.cheetah.common.model.response.ConsumerRecords;

public interface Consumer<K, V> {
    public ConsumerRecords<K, V> poll();
}
