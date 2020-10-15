package fr.abes.hello.chunk;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemReader;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public class LineReader implements ItemReader<String>, StepExecutionListener {

	List<String> adresses;

	AtomicInteger i = new AtomicInteger();

	@Override
    public void beforeStep(StepExecution stepExecution) {
		ExecutionContext executionContext = stepExecution
				.getJobExecution()
				.getExecutionContext();
		this.adresses = (List<String>) executionContext.get("adresses");
    }

    @Override
    public String read() throws Exception {

		String adresse = null;
		if (i.intValue() < this.adresses.size()) {
			adresse = adresses.get(i.getAndIncrement());
		}
		return adresse;

    }
    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        log.info("Line Reader ended.");
        return ExitStatus.COMPLETED;
    }
}