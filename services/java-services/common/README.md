# AOP编程

# 缓存
Caffeine  
基于本地JVM缓存(进程内缓存)  
- 优点:
  - 超快(纳秒级，直接在内存里查找，性能和ConcurrentHashMap接近)
  - 适合热点数据、小对象、短期缓存
- 缺点:  
  - 受限于单机内存，数据量不能太大
  - 不共享，多实例部署时，每台机器各有一份缓存，可能数据不一致
  - 应用重启后数据丢失
  
Redis
- 属于分布式缓存，独立于应用
- 优点:  
  - 多实例共享缓存，保证数据一致性
  - 可以存储更大数据量(几GB~TB级别)
  - 支持丰富的数据结构
  - 有持久化选项，可以保证数据不丢
- 缺点: 
  - 网络IO开销
  - 部署、维护成本更高

## Spring Cache
Spring框架提供了缓存抽象，不需要编写缓存逻辑，需要使用实际的存储来存储数据，提供了两个接口Cache和CacheManager。

1.Cache Interface
```java
package org.springframework.cache;

import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;
import org.springframework.lang.Nullable;

public interface Cache {
    String getName();

    Object getNativeCache();

    ValueWrapper get(Object key);

    <T> T get(Object key, @Nullable Class<T> type);

    <T> T get(Object key, Callable<T> valueLoader);

    default CompletableFuture<?> retrieve(Object key) {
        throw new UnsupportedOperationException(this.getClass().getName() + " does not support CompletableFuture-based retrieval");
    }

    default <T> CompletableFuture<T> retrieve(Object key, Supplier<CompletableFuture<T>> valueLoader) {
        throw new UnsupportedOperationException(this.getClass().getName() + " does not support CompletableFuture-based retrieval");
    }

    void put(Object key, @Nullable Object value);

    default ValueWrapper putIfAbsent(Object key, @Nullable Object value) {
        ValueWrapper existingValue = this.get(key);
        if (existingValue == null) {
            this.put(key, value);
        }

        return existingValue;
    }

    void evict(Object key);

    default boolean evictIfPresent(Object key) {
        this.evict(key);
        return false;
    }

    void clear();

    default boolean invalidate() {
        this.clear();
        return false;
    }

    public static class ValueRetrievalException extends RuntimeException {
        @Nullable
        private final Object key;

        public ValueRetrievalException(@Nullable Object key, Callable<?> loader, @Nullable Throwable ex) {
            super(String.format("Value for key '%s' could not be loaded using '%s'", key, loader), ex);
            this.key = key;
        }

        @Nullable
        public Object getKey() {
            return this.key;
        }
    }

    @FunctionalInterface
    public interface ValueWrapper {
        @Nullable
        Object get();
    }
}

```

2.CacheManage Interface
提供Cache实现Bean的创建，每个应用可以通过cacheName
来对Cache进行隔离，每个cacheName对应一个Cache实现。
```java
public interface CacheManager {
    
    Cache getCache(String name);

    Collection<String> getCacheNames();
}

```