package com.yar.microservices.gateway.ratelimiting;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Bucket4j;
import io.github.bucket4j.BucketConfiguration;
import io.github.bucket4j.grid.GridBucketState;
import io.github.bucket4j.grid.ProxyManager;
import io.github.bucket4j.grid.jcache.JCache;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import javax.cache.CacheManager;
import javax.cache.Caching;
import javax.cache.configuration.CompleteConfiguration;
import javax.cache.configuration.MutableConfiguration;
import javax.cache.spi.CachingProvider;
import java.time.Duration;
import java.util.function.Supplier;

@Slf4j
@Component
public class RateLimitingFilter extends ZuulFilter {


    private final static String GATEWAY_RATE_LIMITING_CACHE_NAME = "gateway-rate-limiting";
    private static final long DURATION_IN_SECOND = 3600L;
    private static final long RATE_LIMIT = 100000L;


    private javax.cache.Cache<String, GridBucketState> cache;

    private ProxyManager<String> buckets;

    public RateLimitingFilter() {

        CachingProvider cachingProvider = Caching.getCachingProvider();
        CacheManager cacheManager = cachingProvider.getCacheManager();
        CompleteConfiguration<String, GridBucketState> config =
                new MutableConfiguration<String, GridBucketState>()
                        .setTypes(String.class, GridBucketState.class);

        this.cache = cacheManager.createCache(GATEWAY_RATE_LIMITING_CACHE_NAME, config);
        this.buckets = Bucket4j.extension(JCache.class).proxyManagerForCache(cache);
    }

    @Override
    public String filterType() {
        return "pre";
    }

    @Override
    public int filterOrder() {
        return 10;
    }

    @Override
    public boolean shouldFilter() {
        // specific APIs can be filtered out using
        // if (RequestContext.getCurrentContext().getRequest().getRequestURI().startsWith("/api")) { ... }
        return true;
    }

    @Override
    public Object run() {
        String bucketId = RequestContext.getCurrentContext().getRequest().getRemoteAddr();
        Bucket bucket = buckets.getProxy(bucketId, getConfigSupplier());
        if (bucket.tryConsume(1)) {
            // the limit is not exceeded
            log.debug("API rate limit OK for {}", bucketId);
        } else {
            // limit is exceeded
            log.info("API rate limit exceeded for {}", bucketId);
            apiLimitExceeded();
        }
        return null;
    }

    private Supplier<BucketConfiguration> getConfigSupplier() {
        return () -> {

            return Bucket4j.configurationBuilder()
                    .addLimit(Bandwidth.simple(RATE_LIMIT,
                            Duration.ofSeconds(DURATION_IN_SECOND)))
                    .buildConfiguration();
        };
    }

    /**
     * Create a Zuul response error when the API limit is exceeded.
     */
    private void apiLimitExceeded() {
        RequestContext ctx = RequestContext.getCurrentContext();
        ctx.setResponseStatusCode(HttpStatus.TOO_MANY_REQUESTS.value());
        if (ctx.getResponseBody() == null) {
            ctx.setResponseBody("API rate limit exceeded");
            ctx.setSendZuulResponse(false);
        }
    }


}
