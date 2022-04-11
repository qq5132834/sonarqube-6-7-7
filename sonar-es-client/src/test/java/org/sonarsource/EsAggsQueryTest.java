package org.sonarsource;

import org.elasticsearch.client.Client;
import org.junit.Before;
import org.sonar.es.MyEsClient;

import java.net.UnknownHostException;

public class EsAggsQueryTest {
    private Client client;

    public static String index = "person";
    public static String type = "man";

    @Before
    public void createClient() throws UnknownHostException {
        this.client = MyEsClient.getTransportClient();
    }


}
