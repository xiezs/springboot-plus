package com.ibeetl.admin.core.util.cache;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;

public class CacheUtil {
  private static final Cache CACHE = Caffeine.newBuilder().maximumSize(1024).recordStats().build();

  public static void put(Object key, Object value) {
    CACHE.put(key, value);
  }

  public static Object get(Object key) {
    return CACHE.getIfPresent(key);
  }
}
