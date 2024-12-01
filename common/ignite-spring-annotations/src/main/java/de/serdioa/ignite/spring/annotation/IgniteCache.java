package de.serdioa.ignite.spring.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.apache.ignite.cache.CacheAtomicityMode;
import org.apache.ignite.cache.CacheMode;
import org.apache.ignite.cache.CacheRebalanceMode;
import org.apache.ignite.cache.PartitionLossPolicy;


/**
 * Annotation-based automatic registration of Ignite cache.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface IgniteCache {

    /**
     * Returns the name of an Ignite cache to be created. The default value is an empty string, indicating to use a
     * fully-qualified name of the annotated class as the name of an Ignite cache.
     *
     * @return the cache name.
     */
    String name() default "";


    /**
     * Returns the atomicity mode of an Ignite cache to be created. Defaults to
     * {@link CacheAtomicityMode#TRANSACTIONAL}.
     *
     * @return the atomicity mode.
     */
    CacheAtomicityMode atomicityMode() default CacheAtomicityMode.TRANSACTIONAL;


    /**
     * Returns a number backup nodes for each partition of a cache that uses {@link CacheMode#PARTITIONED} mode.
     * Defaults to no back ups (0).
     *
     * @return a number of backup nodes for each partition.
     */
    int backups() default 0;


    /**
     * Returns the cache mode of an Ignite cache to be created. Defaults to {@link CacheMode#PARTITIONED}.
     *
     * @return the cache mode.
     */
    CacheMode cacheMode() default CacheMode.PARTITIONED;


    /**
     * Returns the name of the Spring bean from which a factory for the Cache Store shall be obtained. The bean shall be
     * an instance of {@code CacheStoreFactoryFactory}. Defaults to an empty string, indicating to use a default
     * factory.
     * <p>
     * This property may be specified only for caches with an external persistency. A cache with an external persistency
     * has either {@link #readThrough()} or {@link #writeThrough()} set to {@code true}, or as a shortcut
     * {@link #externalPersistency()} set to {@code true}.
     *
     * @return the name of the Spring bean from which a factory for the Cache Store shall be obtained.
     */
    String cacheStoreFactoryFactoryBean() default "";


    /**
     * Returns a name of the data region of an Ignite cache to be created. Defaults to an empty string, indicating to
     * use the default data region.
     *
     * @return the data region name.
     */
    String dataRegionName() default "";


    /**
     * A shortcut for configuring an Ignite cache with full external persistency, that is equivalent to setting both
     * {@link #readThrough()} and {@link writeThrough()} to {@code true}.
     * <table>
     * <caption>Effective {@code readThrough} and {@code writeThrough}</caption>
     * <thead>
     * <tr><th>readThrough</th><th>writeThrough</th><th>externalPersistency</th><th>effective
     * readThrough</th><th>effective writeThrough</th></tr>
     * </thead>
     * <tbody>
     * <tr><td>false      </td><td>false       </td><td>false              </td><td>false                </td><td>false                 </td></tr>
     * <tr><td>true       </td><td>false       </td><td>false              </td><td>true                 </td><td>false                 </td></tr>
     * <tr><td>false      </td><td>true        </td><td>false              </td><td>false                </td><td>true                  </td></tr>
     * <tr><td>true       </td><td>true        </td><td>false              </td><td>true                 </td><td>true                  </td></tr>
     * <tr><td>false      </td><td>false       </td><td>true               </td><td>true                 </td><td>true                  </td></tr>
     * <tr><td>true       </td><td>false       </td><td>true               </td><td>true                 </td><td>true                  </td></tr>
     * <tr><td>false      </td><td>true        </td><td>true               </td><td>true                 </td><td>true                  </td></tr>
     * <tr><td>true       </td><td>true        </td><td>true               </td><td>true                 </td><td>true                  </td></tr>
     * </tbody>
     * </table>
     *
     *
     * @return {@code true} if this cache supports full external persistency overwriting {@code readThrough()} and
     * {@code writeThrough()}, {@code false} to use {@code readThrough()} and {@code writeThrough} directly.
     */
    boolean externalPersistency() default false;


    /**
     * Returns a name of the cache group of an Ignite cache to be created. Defaults to an empty string, indicating to
     * not use any cache group.
     *
     * @return the cache group name.
     */
    String groupName() default "";


    /**
     * Returns a mode that determines whether data shall be automatically loaded into this cache during startup.
     *
     * @return the load cache mode.
     */
    LoadCacheMode loadCache() default LoadCacheMode.DEFAULT;


    /**
     * Returns the maximum number of allowed concurrent asynchronous operations. If {@code 0} is returned, the number of
     * concurrent asynchronous operations is unlimited. Defaults to {@code Integer.MIN_VALUE}, indicating to use Ignite
     * default.
     *
     * @return the maximum number of allowed concurrent asynchronous operations.
     */
    int maxConcurrentAsyncOperations() default Integer.MIN_VALUE;


    /**
     * Returns the maximum number of allowed query iterators. Iterators are used to support query pagination, when data
     * is sent to the client node page by page. Defaults to {@code Integer.MIN_VALUE}, indicating to use Ignite default.
     *
     * @return the maximum number of allowed query iterators.
     */
    int maxQueryIteratorsCount() default Integer.MIN_VALUE;


    /**
     * Returns the policy that defines how Ignite behaves when all nodes for some partition leave the cluster. Defaults
     * to {@code PartitionLossPolicy.READ_ONLY_SAFE}.
     *
     * @return the partition loss policy.
     */
    PartitionLossPolicy partitionLossPolicy() default PartitionLossPolicy.READ_ONLY_SAFE;


    /**
     * Returns the query parallelism, that is the number of threads executing each SQL query on each participating
     * Ignite node. By default SQL queries are executed in a single thread, which is optimal for queries returning a
     * small number of results involving index search. On the other hand, queries with table scans and aggregations may
     * benefit from multiple threads.
     * <p>
     * Defaults to {@code Integer.MIN_VALUE}, indicating to use Ignite default.
     *
     * @return the query parallelism.
     */
    int queryParallelism() default Integer.MIN_VALUE;


    /**
     * Returns the rebalance mode, that is a policy that defines how Ignite rebalances values between grid nodes.
     * Defaults to {@code CacheRebalanceMode.ASYNC}.
     *
     * @return the rebalance mode.
     */
    CacheRebalanceMode rebalanceMode() default CacheRebalanceMode.ASYNC;


    /**
     * Returns the rebalance order, that is an order in which Ignite rebalances caches. The rebalance order guarantees
     * that rebalancing for this cache starts only when rebalancing of all caches will smaller rebalance order is
     * completed, that is caches are rebalanced from lowest to the highest rebalance order.
     * <p>
     * Defaults to {@code Integer.MIN_VALUE}, indicating to use Ignite default.
     *
     * @return the rebalance order.
     */
    int rebalanceOrder() default Integer.MIN_VALUE;


    /**
     * Returns a boolean indicating whether the cache supports read-through mode, that is reading data from the
     * underlying persistent storage if it is not available in the cache. Note that load-on-demand works only when
     * requesting data by the key-value API; queries with conditions never search in underlying persistent storage.
     *
     * @return {@code true} if this cache reads data from the underlying persistent storage if it is not available in
     * the cache, {@code false} otherwise.
     *
     * @see #externalPersistency()
     */
    boolean readThrough() default false;


    /**
     * Returns a boolean indicating whether the cache propagates put and remove operations to the underlying persistent
     * storage.
     *
     * @return {@code true} if this cache propagates put and remove operations to the underlying persistent storage,
     * {@code false} otherwise.
     *
     * @see #externalPersistency()
     */
    boolean writeThrough() default false;


    /**
     * Returns a type of the primary key. Usually this property is required only for transient entities. For persistent
     * entities a type of the primary key may be obtained by the JPA annotation {@code @Id}, and this property is not
     * required.
     * <p>
     * If both this property and the JPA annotation {@code @Id} are present, this property takes precedence.
     * <p>
     * The primary key type is required to use SQL-based queries for this entity.
     *
     * @return a type of the primary key, or {@code Void.class} if the primary key is not not defined. In the latter
     * case SQL-based queries cannot be used for this entity.
     */
    Class<?> keyType() default Void.class;
}
