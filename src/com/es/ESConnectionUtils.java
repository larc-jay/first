package com.es;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;

public class ESConnectionUtils {
	public static Client getConnection(ESSetting setting) throws UnknownHostException {
		Settings settings = Settings.settingsBuilder()
				.put("cluster.name", setting.name)
				.put("client.transport.sniff", setting.isSniff)
				.put("client.log", "trace").build();
		TransportClient tClient = TransportClient.builder().settings(settings).build();
		for (String s: setting.addresses) {
			tClient.addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName(s), 9300));
		}
		return tClient;
	}
	
	public static void close(Client client) {
		if (client != null) {
			client.close();
		}
}
}
