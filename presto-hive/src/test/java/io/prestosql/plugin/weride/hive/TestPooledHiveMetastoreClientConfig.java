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

import com.google.common.collect.ImmutableMap;
import org.testng.annotations.Test;

import java.util.Map;

import static io.airlift.configuration.testing.ConfigAssertions.assertFullMapping;
import static io.airlift.configuration.testing.ConfigAssertions.assertRecordedDefaults;
import static io.airlift.configuration.testing.ConfigAssertions.recordDefaults;

public class TestPooledHiveMetastoreClientConfig
{
    @Test
    public void testDefaults()
    {
        assertRecordedDefaults(recordDefaults(PooledHiveMetastoreClientConfig.class)
                .setMaxTransport(128)
                .setTransportIdleTimeout(300_000L)
                .setTransportEvictInterval(10_000L)
                .setTransportEvictNumTests(3));
    }

    @Test
    public void testExplicitPropertyMappingsSingleMetastore()
    {
        Map<String, String> properties = new ImmutableMap.Builder<String, String>()
                .put("hive.metastore.max-transport-num", "64")
                .put("hive.metastore.transport-idle-timeout", "100000")
                .put("hive.metastore.transport-eviction-interval", "1000")
                .put("hive.metastore.transport-eviction-num-tests", "10")
                .build();

        PooledHiveMetastoreClientConfig expected = new PooledHiveMetastoreClientConfig()
                .setMaxTransport(64)
                .setTransportIdleTimeout(100_000L)
                .setTransportEvictInterval(1_000L)
                .setTransportEvictNumTests(10);

        assertFullMapping(properties, expected);
    }
}
