package elasicsearch.elasicsearch;

import java.io.File;

import javax.net.ssl.SSLContext;

import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.ssl.SSLContexts;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestClientBuilder.HttpClientConfigCallback;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.indices.CreateIndexRequest;
import co.elastic.clients.elasticsearch.indices.CreateIndexResponse;
import co.elastic.clients.elasticsearch.indices.ElasticsearchIndicesClient;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;

public class CreateEsIndexUsingHttps {
	
	public static void main(String[] args) throws Exception {

		ElasticsearchClient client = getElasticsearchClient();

		CreateIndexRequest.Builder createIndexBuilder = new CreateIndexRequest.Builder();
		createIndexBuilder.index("techognite");
		CreateIndexRequest createIndexRequest = createIndexBuilder.build();

		ElasticsearchIndicesClient indices = client.indices();
		CreateIndexResponse createIndexResponse = indices.create(createIndexRequest);

//		Function<CreateIndexRequest.Builder, ObjectBuilder<CreateIndexRequest>> createIndexFunction 
//				= createIndexBuilder -> createIndexBuilder.index("techognite");
//		client.indices().create(createIndexFunction);

		System.out.println("Index Created Successfully");
	}

	private static ElasticsearchClient getElasticsearchClient() {

		// Create the low-level client
		RestClientBuilder builder = RestClient.builder(new HttpHost("localhost", 9200, "https"));

		HttpClientConfigCallback httpClientConfigCallback = new HttpClientConfigCallbackImpl();
		builder.setHttpClientConfigCallback(httpClientConfigCallback);
		RestClient restClient = builder.build();

		// Create the transport with a Jackson mapper
		ElasticsearchTransport transport = new RestClientTransport(restClient, new JacksonJsonpMapper());

		return new ElasticsearchClient(transport);
	}

}

class HttpClientConfigCallbackImpl implements HttpClientConfigCallback {

	@Override
	public HttpAsyncClientBuilder customizeHttpClient(HttpAsyncClientBuilder httpClientBuilder) {
		try {
			final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
			UsernamePasswordCredentials usernamePasswordCredentials = new UsernamePasswordCredentials("elastic",
					"aJYdX=hoT7bPxH3AQKD_");
			credentialsProvider.setCredentials(AuthScope.ANY, usernamePasswordCredentials);
			httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider);

			// keytool -import -file http_ca.crt -keystore truststore.p12 -storepass
			// password -noprompt -storetype pkcs12

			String trustStoreLocation = "D:\\esdemo\\elasticsearch-8.4.1-windows-x86_64\\elasticsearch-8.4.1\\config\\certs\\truststore.p12";
			File trustStoreLocationFile = new File(trustStoreLocation);
			SSLContextBuilder sslContextBuilder = SSLContexts.custom().loadTrustMaterial(trustStoreLocationFile,
					"password".toCharArray());
			SSLContext sslContext = sslContextBuilder.build();
			httpClientBuilder.setSSLContext(sslContext);

		} catch (Exception e) {
			e.printStackTrace();
		}

		return httpClientBuilder;
	}

}