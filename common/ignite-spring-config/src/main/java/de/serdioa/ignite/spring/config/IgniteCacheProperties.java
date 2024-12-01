package de.serdioa.ignite.spring.config;

import de.serdioa.ignite.spring.annotation.LoadCacheMode;
import lombok.Data;
import org.apache.ignite.cache.CacheAtomicityMode;
import org.apache.ignite.cache.CacheMode;
import org.apache.ignite.cache.CacheRebalanceMode;
import org.apache.ignite.cache.PartitionLossPolicy;
import org.apache.ignite.configuration.CacheConfiguration;


/**
 * Configuration of an Ignite cache set from Spring properties. This class allows to selectively replace some properties
 * of an Ignite cache defined by a different mechanism, for example by annotations, or by providing cache configurations
 * in the Spring context.
 * <p>
 * The meaning of all properties is the same as in the class {@link CacheConfiguration}.
 */
@Data
public class IgniteCacheProperties {

    private CacheAtomicityMode atomicityMode;

    private Integer backups;

    private CacheMode cacheMode;

    private String cacheStoreFactoryFactoryBean;

    private String dataRegionName;

    private Boolean externalPersistency;

    private String groupName;

    private LoadCacheMode loadCache;

    private Integer maxConcurrentAsyncOperations;

    private Integer maxQueryIteratorsCount;

    private PartitionLossPolicy partitionLossPolicy;

    private Integer queryParallelism;

    private CacheRebalanceMode rebalanceMode;

    private Integer rebalanceOrder;

    private Boolean readThrough;

    private Boolean writeThrough;

    private Class<?> keyType;

    private Class<?> valueType;
}
