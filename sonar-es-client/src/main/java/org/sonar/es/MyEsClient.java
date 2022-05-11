package org.sonar.es;

import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.sonar.api.utils.System2;
import org.sonar.server.component.index.ComponentIndex;
import org.sonar.server.component.index.ComponentQuery;
import org.sonar.server.es.EsClient;
import org.sonar.server.es.SearchIdResult;
import org.sonar.server.es.SearchOptions;
import org.sonar.server.permission.index.AuthorizationTypeSupport;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class MyEsClient {

    public static TransportClient getTransportClient() throws UnknownHostException {
        // 设置elasticsearch集群地址 ip和端口
        InetSocketTransportAddress address = new InetSocketTransportAddress(InetAddress.getByName("192.168.32.151"), 9300);

        // 设置elasticsearch集群名称
        Settings settings = Settings.builder()
                .put("cluster.name", "my-application")
                //如果开启嗅探功能，即自动检测集群内其他的节点和新加入的节点，
                //不需要全部都是用addTransportAddress添加
                .put("client.transport.sniff", true)
                .build();

        TransportClient client = new PreBuiltTransportClient(settings);
        client.addTransportAddress(address);
        return client;
    }


    public static EsClient getEsClient(TransportClient transportClient){
        return new org.sonar.server.es.EsClient(transportClient);
    }

    public static void main(String[] args) throws UnknownHostException {
        EsClient esClient = getEsClient(getTransportClient());
    }

}
