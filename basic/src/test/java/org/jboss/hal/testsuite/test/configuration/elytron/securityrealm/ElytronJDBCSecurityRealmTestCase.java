package org.jboss.hal.testsuite.test.configuration.elytron.securityrealm;

import org.apache.commons.lang.RandomStringUtils;
import org.jboss.arquillian.graphene.page.Page;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.dmr.ModelNode;
import org.jboss.hal.testsuite.creaper.ResourceVerifier;
import org.jboss.hal.testsuite.dmr.ModelNodeGenerator;
import org.jboss.hal.testsuite.fragment.config.elytron.securityrealm.AddJDBCSecurityRealmWizard;
import org.jboss.hal.testsuite.fragment.config.elytron.securityrealm.AddPrincipalQueryWizard;
import org.jboss.hal.testsuite.page.config.elytron.SecurityRealmPage;
import org.jboss.hal.testsuite.test.configuration.elytron.AbstractElytronTestCase;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.wildfly.extras.creaper.commands.datasources.AddDataSource;
import org.wildfly.extras.creaper.commands.datasources.RemoveDataSource;
import org.wildfly.extras.creaper.core.CommandFailedException;
import org.wildfly.extras.creaper.core.online.operations.Address;
import org.wildfly.extras.creaper.core.online.operations.Values;

import java.io.IOException;

@RunWith(Arquillian.class)
public class ElytronJDBCSecurityRealmTestCase extends AbstractElytronTestCase {

    private static final String
            JDBC_REALM = "jdbc-realm",
            DATASOURCE = "data-source",
            SQL = "sql",
            PRINCIPAL_QUERY = "principal-query";
    @Page
    private SecurityRealmPage page;

    @Test
    public void testAddJDBCSecurityRealm() throws Exception {
        String sqlQueryValue = "SELECT 2",
                datasourceName = null;

        final Address realmAddress = elyOps.getElytronAddress(JDBC_REALM, RandomStringUtils.randomAlphabetic(7));

        page.navigate();
        page.switchToJDBCRealms();

        try {
            datasourceName = createDatasource();
            page.getResourceManager().addResource(AddJDBCSecurityRealmWizard.class)
                    .name(realmAddress.getLastPairValue())
                    .principalQuerySQL(sqlQueryValue)
                    .principalQueryDatasource(datasourceName)
                    .saveWithState()
                    .assertWindowClosed();

            Assert.assertTrue("Resource should be present in table!",
                    page.getResourceManager().isResourcePresent(realmAddress.getLastPairValue()));

            new ResourceVerifier(realmAddress, client)
                    .verifyExists()
                    .verifyAttribute(PRINCIPAL_QUERY, new ModelNodeGenerator.ModelNodeListBuilder()
                            .addNode(new ModelNodeGenerator.ModelNodePropertiesBuilder()
                                    .addProperty(DATASOURCE, datasourceName)
                                    .addProperty(SQL, sqlQueryValue)
                                    .build())
                            .build());
        } finally {
            ops.removeIfExists(realmAddress);
            if (datasourceName != null) {
                client.apply(new RemoveDataSource(datasourceName));
            }
            adminOps.reloadIfRequired();
        }
    }

    @Test
    public void testRemoveJDBCRealm() throws Exception {
        String datasourceName = null;
        Address realmAddress = null;

        try {
            datasourceName = createDatasource();
            realmAddress = createJDBCRealm(datasourceName);
            page.navigate();
            page.switchToJDBCRealms()
                    .getResourceManager()
                    .removeResource(realmAddress.getLastPairValue())
                    .confirmAndDismissReloadRequiredMessage()
                    .assertClosed();

            Assert.assertFalse(page.getResourceManager().isResourcePresent(realmAddress.getLastPairValue()));

            new ResourceVerifier(realmAddress, client).verifyDoesNotExist();
        } finally {
            if (realmAddress != null) {
                ops.removeIfExists(realmAddress);
            }
            if (datasourceName != null) {
                client.apply(new RemoveDataSource(datasourceName));
            }
            adminOps.reloadIfRequired();
        }

    }

    @Test
    public void testAddPrincipalQuery() throws Exception {
        String datasourceName = null;
        Address realmAddress = null;

        try {
            datasourceName = createDatasource();
            realmAddress = createJDBCRealm(datasourceName);
            page.navigate();
            page.switchToJDBCRealms()
                    .getResourceManager()
                    .selectByName(realmAddress.getLastPairValue());

            page.getConfig()
                    .getResourceManager()
                    .addResource(AddPrincipalQueryWizard.class)
                    .sql(RandomStringUtils.randomAlphanumeric(7))
                    .dataSource(datasourceName)
                    .saveAndDismissReloadRequiredWindowWithState()
                    .assertWindowClosed();

            Assert.assertTrue(page.getConfigFragment().getResourceManager().isResourcePresent(realmAddress.getLastPairValue()));

            new ResourceVerifier(realmAddress, client).verifyExists();
        } finally {
            if (realmAddress != null) {
                ops.removeIfExists(realmAddress);
            }
            if (datasourceName != null) {
                client.apply(new RemoveDataSource(datasourceName));
            }
            adminOps.reloadIfRequired();
        }
    }

    @Test
    public void testRemovePrincipalQuery() throws Exception {
        String datasourceName = null;
        Address realmAddress = null;

        try {
            final String principalQueryToBeRemoved = RandomStringUtils.randomAlphabetic(7);
            datasourceName = createDatasource();
            JDBCRealm jdbcRealm = new JDBCRealm.Builder(RandomStringUtils.randomAlphabetic(7), datasourceName)
                    .addPrincipalQuery(principalQueryToBeRemoved, datasourceName)
                    .build();

            realmAddress = jdbcRealm.getAddress();

            page.navigate();
            page.switchToJDBCRealms()
                    .getResourceManager()
                    .selectByName(realmAddress.getLastPairValue());

            page.getConfig()
                    .getResourceManager()
                    .removeResource(principalQueryToBeRemoved)
                    .confirmAndDismissReloadRequiredMessage()
                    .assertClosed();

            Assert.assertFalse("Resource shouldn't be present in resource table! Probably failed because of" +
                            "https://issues.jboss.org/browse/HAL-1348",
                    page.getConfig().getResourceManager().isResourcePresent(principalQueryToBeRemoved));

            new ResourceVerifier(realmAddress, client).verifyAttribute(PRINCIPAL_QUERY,
                    new ModelNode().add(jdbcRealm.getPrincipalQueries()
                            .asList().stream()
                            .filter(modelNode -> !modelNode.hasDefined(principalQueryToBeRemoved))
                            .findFirst().get()));
        } finally {
            if (realmAddress != null) {
                ops.removeIfExists(realmAddress);
            }
            if (datasourceName != null) {
                client.apply(new RemoveDataSource(datasourceName));
            }
            adminOps.reloadIfRequired();
        }
    }

    private String createDatasource() throws CommandFailedException {
        final String datasourceName = RandomStringUtils.randomAlphanumeric(7);
        client.apply(new AddDataSource.Builder(datasourceName)
                .jndiName("java:/" + datasourceName)
                .driverName("h2")
                .connectionUrl("jdbc:h2:mem:test2;DB_CLOSE_DELAY=-1")
                .enableAfterCreate()
                .build());
        return datasourceName;
    }

    private static final class JDBCRealm {

        private final ModelNodeGenerator.ModelNodeListBuilder listBuilder;
        private final Address address;

        private JDBCRealm(Builder builder) {
            this.listBuilder = builder.listBuilder;
            this.address = builder.address;
        }

        public ModelNode getPrincipalQueries() {
            return listBuilder.build();
        }

        public Address getAddress() {
            return address;
        }

        public static final class Builder {

            private final ModelNodeGenerator.ModelNodeListBuilder listBuilder = new ModelNodeGenerator.ModelNodeListBuilder();
            private final Address address = elyOps.getElytronAddress(JDBC_REALM, RandomStringUtils.randomAlphanumeric(7));

            public Builder(String sqlQuery, String datasourceName) {
                addPrincipalQuery(sqlQuery, datasourceName);
            }

            public Builder addPrincipalQuery(String sqlQuery, String datasourceName) {
                listBuilder.addNode(new ModelNodeGenerator.ModelNodePropertiesBuilder()
                        .addProperty(SQL, sqlQuery)
                        .addProperty(DATASOURCE, datasourceName)
                        .build());
                return this;
            }

            public JDBCRealm build() throws IOException {
                ops.add(address, Values.of(PRINCIPAL_QUERY, listBuilder.build())).assertSuccess();
                return new JDBCRealm(this);
            }
        }


    }

    private Address createJDBCRealm(String datasourceName) throws IOException {
        return new JDBCRealm.Builder(RandomStringUtils.randomAlphabetic(7), datasourceName).build().getAddress();
    }
}
