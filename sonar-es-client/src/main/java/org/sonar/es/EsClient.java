package org.sonar.es;

import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class EsClient {

    public TransportClient client() throws UnknownHostException {
        // 设置elasticsearch集群地址 ip和端口
        InetSocketTransportAddress address = new InetSocketTransportAddress(InetAddress.getByName("192.168.32.140"), 9300);

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

    public static void main(String[] args) throws Exception {
        EsClient esClient = new EsClient();
        TransportClient transportClient = esClient.client();
    }

}
