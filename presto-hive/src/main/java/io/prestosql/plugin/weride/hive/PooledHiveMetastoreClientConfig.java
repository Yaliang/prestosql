/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.prestosql.plugin.weride.hive;

import io.airlift.configuration.Config;
import io.airlift.configuration.ConfigDescription;

import javax.validation.constraints.Min;

public class PooledHiveMetastoreClientConfig
{
    private int maxTransport = 128;
    private long transportIdleTimeout = 300_000L;
    private long transportEvictInterval = 10_000L;
    private int transportEvictNumTests = 3;

    @Min(0)
    public int getMaxTransport()
    {
        return maxTransport;
    }

    @Config("hive.metastore.max-transport-num")
    @ConfigDescription("Metastore client transport pool size")
    public PooledHiveMetastoreClientConfig setMaxTransport(int maxTransport)
    {
        this.maxTransport = maxTransport;
        return this;
    }

    public long getTransportIdleTimeout()
    {
        return transportIdleTimeout;
    }

    @Config("hive.metastore.transport-idle-timeout")
    @ConfigDescription("Metastore client transport idle timeout threshold")
    public PooledHiveMetastoreClientConfig setTransportIdleTimeout(long transportIdleTimeout)
    {
        this.transportIdleTimeout = transportIdleTimeout;
        return this;
    }

    public long getTransportEvictInterval()
    {
        return transportEvictInterval;
    }

    @Config("hive.metastore.transport-eviction-interval")
    @ConfigDescription("Metastore client transport eviction interval")
    public PooledHiveMetastoreClientConfig setTransportEvictInterval(long transportEvictInterval)
    {
        this.transportEvictInterval = transportEvictInterval;
        return this;
    }

    @Min(0)
    public int getTransportEvictNumTests()
    {
        return transportEvictNumTests;
    }

    @Config("hive.metastore.transport-eviction-num-tests")
    @ConfigDescription("How many metastore client transports are tested in an eviction")
    public PooledHiveMetastoreClientConfig setTransportEvictNumTests(int transportEvictNumTests)
    {
        this.transportEvictNumTests = transportEvictNumTests;
        return this;
    }
}
