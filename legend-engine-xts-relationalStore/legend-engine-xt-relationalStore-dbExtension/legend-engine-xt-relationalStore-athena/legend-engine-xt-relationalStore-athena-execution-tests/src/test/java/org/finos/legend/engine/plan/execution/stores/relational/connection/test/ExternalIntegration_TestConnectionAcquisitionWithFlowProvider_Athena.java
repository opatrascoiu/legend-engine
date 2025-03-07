// Copyright 2021 Goldman Sachs
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package org.finos.legend.engine.plan.execution.stores.relational.connection.test;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.finos.legend.engine.authentication.AthenaTestDatabaseAuthenticationFlowProvider;
import org.finos.legend.engine.authentication.AthenaTestDatabaseAuthenticationFlowProviderConfiguration;
import org.finos.legend.engine.plan.execution.stores.relational.config.TemporaryTestDbConfiguration;
import org.finos.legend.engine.plan.execution.stores.relational.connection.manager.ConnectionManagerSelector;
import org.finos.legend.engine.protocol.pure.v1.model.packageableElement.store.relational.connection.RelationalDatabaseConnection;
import org.finos.legend.engine.shared.core.identity.Identity;
import org.finos.legend.engine.shared.core.identity.factory.*;
import org.finos.legend.engine.shared.core.vault.EnvironmentVaultImplementation;
import org.finos.legend.engine.shared.core.vault.Vault;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.sql.Connection;
import java.util.Collections;
import java.util.Optional;

public class ExternalIntegration_TestConnectionAcquisitionWithFlowProvider_Athena extends RelationalConnectionTest
{
    private ConnectionManagerSelector connectionManagerSelector;

    @BeforeClass
    public static void setupTest()
    {
        Vault.INSTANCE.registerImplementation(new EnvironmentVaultImplementation());
    }
    
    @Before
    public void setup() throws IOException
    {
        AthenaTestDatabaseAuthenticationFlowProviderConfiguration flowProviderConfiguration = new AthenaTestDatabaseAuthenticationFlowProviderConfiguration();
        AthenaTestDatabaseAuthenticationFlowProvider flowProvider = new AthenaTestDatabaseAuthenticationFlowProvider();
        flowProvider.configure(flowProviderConfiguration);
        this.connectionManagerSelector = new ConnectionManagerSelector(new TemporaryTestDbConfiguration(-1), Collections.emptyList(), Optional.of(flowProvider));
    }
    
    @Test
    public void testConnectivity() throws Exception
    {
        RelationalDatabaseConnection systemUnderTest = getTestConnection();
        Connection connection = this.connectionManagerSelector.getDatabaseConnection(IdentityFactoryProvider.getInstance().getAnonymousIdentity(), systemUnderTest);
        testConnection(connection, 1, "select 1");
    }

    private RelationalDatabaseConnection getTestConnection() throws JsonProcessingException
    {
        return getRelationalConnectionByElement(readRelationalConnections(getResourceAsString("/org/finos/legend/engine/server/test/athenaRelationalDatabaseConnections.json")), "firstConn");
    }
}
