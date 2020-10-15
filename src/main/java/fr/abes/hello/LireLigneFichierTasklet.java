package fr.abes.hello;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class LireLigneFichierTasklet implements Tasklet, StepExecutionListener  {

	List<String> adresses = new ArrayList<String>();
    @Override
    public void beforeStep(StepExecution stepExecution) {

    }

    @Override
    public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) {
		
		log.info("DANS LA TASKLET");
	
		try
		{
			BufferedReader in = new BufferedReader(new FileReader("C:\\dev\\extraction608\\req.txt"));
			String str;
			while ((str=in.readLine()) != null) {
				this.adresses.add(str);
			}
			in.close();

		}
		catch (Exception e)
		{
			log.error("EXCEPTION DU PROXYRETRY");
		}
	
        return RepeatStatus.FINISHED;
    }


    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
		ExecutionContext executionContext = stepExecution
				.getJobExecution()
				.getExecutionContext();
		executionContext.put("adresses", this.adresses);
		return stepExecution.getExitStatus();
    }
}
