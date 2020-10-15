package fr.abes.hello;

import lombok.extern.slf4j.Slf4j;
import org.jdom2.Document;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URL;

@Component
@Slf4j
public class ProxyRetry {

	@Retryable(maxAttempts = 2)
	public Document getDocumentXMLFromBNF(URL url) throws JDOMException, IOException {
		log.info("dans ProxyRetry pour récuperer l'url = " + url.getPath());
		SAXBuilder sxb = new SAXBuilder();
		Document document = sxb.build(url);

		return document;
	}

}
