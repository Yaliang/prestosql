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

import com.google.common.net.HostAndPort;
import io.prestosql.plugin.hive.metastore.thrift.MetastoreLocator;
import io.prestosql.plugin.hive.metastore.thrift.StaticMetastoreConfig;
import io.prestosql.plugin.hive.metastore.thrift.StaticMetastoreLocator;
import io.prestosql.plugin.hive.metastore.thrift.ThriftMetastoreClient;
import org.apache.thrift.TException;

import javax.inject.Inject;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Strings.isNullOrEmpty;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toList;

public class PooledStaticMetastoreLocator
        implements MetastoreLocator
{
    private final List<HostAndPort> addresses;
    private final PooledThriftMetastoreClientFactory clientFactory;
    private final String metastoreUsername;

    @Inject
    public PooledStaticMetastoreLocator(StaticMetastoreConfig config, PooledThriftMetastoreClientFactory clientFactory)
    {
        this(config.getMetastoreUris(), config.getMetastoreUsername(), clientFactory);
    }

    public PooledStaticMetastoreLocator(List<URI> metastoreUris, String metastoreUsername, PooledThriftMetastoreClientFactory clientFactory)
    {
        requireNonNull(metastoreUris, "metastoreUris is null");
        checkArgument(!metastoreUris.isEmpty(), "metastoreUris must specify at least one URI");
        this.addresses = metastoreUris.stream()
                .map(StaticMetastoreLocator::checkMetastoreUri)
                .map(uri -> HostAndPort.fromParts(uri.getHost(), uri.getPort()))
                .collect(toList());
        this.metastoreUsername = metastoreUsername;
        this.clientFactory = requireNonNull(clientFactory, "clientFactory is null");
    }

    /**
     * Create a metastore client connected to the Hive metastore.
     * <p>
     * As per Hive HA metastore behavior, return the first metastore in the list
     * list of available metastores (i.e. the default metastore) if a connection
     * can be made, else try another of the metastores at random, until either a
     * connection succeeds or there are no more fallback metastores.
     */
    @Override
    public ThriftMetastoreClient createMetastoreClient()
            throws TException
    {
        List<HostAndPort> metastores = new ArrayList<>(addresses);
        Collections.shuffle(metastores.subList(1, metastores.size()));

        TException lastException = null;
        for (HostAndPort metastore : metastores) {
            try {
                ThriftMetastoreClient client = clientFactory.create(metastore.getHost(), metastore.getPort());
                if (!isNullOrEmpty(metastoreUsername)) {
                    client.setUGI(metastoreUsername);
                }
                return client;
            }
            catch (TException e) {
                lastException = e;
            }
        }

        throw new TException("Failed connecting to Hive metastore: " + addresses, lastException);
    }
}
