package com.ald.ebei.util;

import java.lang.ref.SoftReference;
import java.util.HashMap;

/**
 * 版权：XXX公司 版权所有
 * 作者：Jacky Yu
 * 版本：1.0
 * 创建日期：2017/1/3
 * 描述：软引用map
 * 修订历史：
 */
public class EbeiSoftMap<K, V> {
    private SoftReference<HashMap<K, V>> innerMap = new SoftReference<>(null);

    /**
     * 向map中存入Key-Value
     */
    public void put(K k, V v) {
        HashMap<K, V> map = innerMap.get();
        if (map == null) {
            map = new HashMap<>();
            map.put(k, v);
            innerMap = new SoftReference<>(map);
        } else {
            map.put(k, v);
        }
    }

    /**
     * 从map中获取Key对应的Value
     */
    public V get(K k) {
        final HashMap<K, V> map = innerMap.get();
        if (map != null) {
            return map.get(k);
        }
        return null;
    }

    /**
     * 从map中删除该Key对应的value
     */
    public void remove(K k) {
        final HashMap<K, V> map = innerMap.get();
        if (map != null && map.containsKey(k)) {
            map.remove(k);
        }
    }

    /**
     * map中是否有此键值对
     */
    public boolean containsKey(K k) {
        HashMap<K, V> map = innerMap.get();
        return map != null && map.containsKey(k);
    }
}
