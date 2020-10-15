package fr.abes.hello.chunk;

import fr.abes.hello.ProxyRetry;
import lombok.extern.slf4j.Slf4j;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.Namespace;
import org.jdom2.filter.Filters;
import org.jdom2.xpath.XPath;
import org.jdom2.xpath.XPathExpression;
import org.jdom2.xpath.XPathFactory;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;

import java.net.URL;
import java.util.List;

@Slf4j
public class LineProcessor implements ItemProcessor<String, String>, StepExecutionListener {

    @Autowired
    ProxyRetry proxyRetry;

    @Override
    public void beforeStep(StepExecution stepExecution) {
        log.info("Line Processor initialized.");
    }

    @Override
    public String process(String line) throws Exception {

        String infos = "";
        try {
            Document doc = proxyRetry.getDocumentXMLFromBNF(new URL(line));
            log.info("AAA=" + doc.getRootElement());

            XPathFactory xFactory = XPathFactory.instance();

            String expression = String.format("/srw:searchRetrieveResponse/srw:records[1]/srw:record[1]/srw:recordData[1]/*[namespace-uri()='info:lc/xmlns/marcxchange-v2' and local-name()='record'][1]/*[namespace-uri()='info:lc/xmlns/marcxchange-v2' and local-name()='controlfield'][1]");
            XPathExpression<Element> xPathExpression = xFactory.compile(expression, Filters.element(), null,
                    Namespace.getNamespace("srw", "http://www.loc.gov/zing/srw/"));
            Element controlField = xPathExpression.evaluateFirst(doc);
            infos = "controlField = " + controlField.getText() + ", 608$a = ";

            expression = String.format("/srw:searchRetrieveResponse/srw:records/srw:record/srw:recordData/*[namespace-uri()='info:lc/xmlns/marcxchange-v2' and local-name()='record']/*[namespace-uri()='info:lc/xmlns/marcxchange-v2' and local-name()='datafield'][@tag='608']/*[namespace-uri()='info:lc/xmlns/marcxchange-v2' and local-name()='subfield'][@code='a']");
            xPathExpression = xFactory.compile(expression, Filters.element(), null,
                    Namespace.getNamespace("srw", "http://www.loc.gov/zing/srw/"));
            List<Element> zone608 = xPathExpression.evaluate(doc);
            for (int i = 0; i < zone608.size() ; i++) {
                infos += ((Element) zone608.get(i)).getText() + ";";
            }
        }
        catch (Exception e) {
            log.error(e.toString());
        }
        log.info(infos);
        return infos;
    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        log.info("Line Processor ended.");
        return ExitStatus.COMPLETED;
    }
}