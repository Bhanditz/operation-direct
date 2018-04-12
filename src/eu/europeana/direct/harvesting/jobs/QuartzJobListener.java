package eu.europeana.direct.harvesting.jobs;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import javax.ejb.EJB;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.apache.commons.lang3.time.DateUtils;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobListener;

import com.mchange.v2.c3p0.impl.NewProxyDatabaseMetaData;

import eu.europeana.direct.backend.repositories.LogRepository;
import eu.europeana.direct.harvesting.logging.model.ImportLog;
import eu.europeana.direct.harvesting.logging.model.ImportLogDetail;
import eu.europeana.direct.harvesting.logging.model.Status;
import eu.europeana.direct.rest.gen.java.io.swagger.model.CulturalHeritageObject;

public class QuartzJobListener implements JobListener {

	public static String name = "Quartz job listener";
	
	//flag for interrupting job
	private boolean interrupted;
	
	
	SimpleDateFormat dateFormat = new SimpleDateFormat("dd-M-yyyy HH:mm:ss");

	@Override
	public String getName() {
		return name; // must return a name
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isInterrupted() {
		return interrupted;
	}

	public void setInterrupted(boolean interrupted) {
		this.interrupted = interrupted;
	}

	@Override
	public void jobExecutionVetoed(JobExecutionContext arg0) {
	}

	@Override
	public void jobToBeExecuted(JobExecutionContext context) {
		LogRepository logRepository = new LogRepository();

		try{			
			ImportLog importLog = new ImportLog();
			ImportLogDetail importLogDetail = new ImportLogDetail();

			TimeZone.setDefault(TimeZone.getTimeZone("CEST"));
			dateFormat.setTimeZone(TimeZone.getTimeZone("CEST"));

			importLog.setStartTime(DateUtils.addHours(new Date(), 2));
			importLog.setJobname(context.getJobDetail().getKey().getName());
			importLog.setWarnings(0);
			importLog.setErrors(0);
			importLog.setSuccesses(0);

			long importLogId = logRepository.saveImportLog(importLog);
			if (importLogId > 0) {
				int id = Math.toIntExact(importLogId);
				importLogDetail.setLogId(id);
				importLogDetail.setStatus("success");
				importLogDetail.setMessage(context.getJobDetail().getKey().getName() + " started");
				importLogDetail.setPayload("" + importLogId);
				logRepository.saveImportLogDetail(importLogDetail);
			}
		} catch (Exception e){
			e.printStackTrace();
		} finally {
			logRepository.close();
		}		
	}

	@Override
	public void jobWasExecuted(JobExecutionContext context, JobExecutionException arg1) {
		LogRepository logRepository = new LogRepository();

		try {
			ImportLog importLog = new ImportLog();
			ImportLogDetail importLogDetail = new ImportLogDetail();

			TimeZone.setDefault(TimeZone.getTimeZone("CEST"));
			dateFormat.setTimeZone(TimeZone.getTimeZone("CEST"));

			importLog.setStartTime(DateUtils.addHours(new Date(), 2));
			importLog.setEndTime(DateUtils.addHours(new Date(), 2));
			importLog.setJobname(context.getJobDetail().getKey().getName());
			if (context.getJobDetail().getJobDataMap().size() > 0) {
				importLog.setWarnings(context.getJobDetail().getJobDataMap().getInt("warnings"));
				importLog.setSuccesses(context.getJobDetail().getJobDataMap().getInt("successes"));
				importLog.setErrors(context.getJobDetail().getJobDataMap().getInt("errors"));
			}

			//logRepository.saveImportLog(importLog);

			if (importLog.getId() > 0) {
				importLogDetail.setLogId(importLog.getId());
				importLogDetail.setStatus("success");
				importLogDetail.setPayload("" + importLog.getId());

				if (interrupted) {
					importLogDetail.setMessage(context.getJobDetail().getKey().getName() + " interrupted");
				} else {
					importLogDetail.setMessage(context.getJobDetail().getKey().getName() + " completed");
				}

				logRepository.saveImportLogDetail(importLogDetail);

				// warning messages occured during harvesting
				if (context.getJobDetail().getJobDataMap().get("warningMessages") != null) {
					List<String> warningMessages = (List<String>) context.getJobDetail().getJobDataMap()
							.get("warningMessages");
					for (String warningMessage : warningMessages) {
						importLogDetail = new ImportLogDetail();
						importLogDetail.setLogId(importLog.getId());
						importLogDetail.setStatus("warning");
						importLogDetail.setMessage(warningMessage);
						importLogDetail.setPayload("" + importLog.getId());
						logRepository.saveImportLogDetail(importLogDetail);
					}
				}

				// error messages occured during harvesting
				if (context.getJobDetail().getJobDataMap().get("errorMessages") != null) {
					List<String> errorMessages = (List<String>) context.getJobDetail().getJobDataMap()
							.get("errorMessages");
					for (String errorMessage : errorMessages) {
						importLogDetail = new ImportLogDetail();
						importLogDetail.setLogId(importLog.getId());
						importLogDetail.setStatus("error");
						importLogDetail.setMessage(errorMessage);
						importLogDetail.setPayload("" + importLog.getId());
						logRepository.saveImportLogDetail(importLogDetail);
					}
				}
			}
			interrupted = false;
		} finally {
			logRepository.close();
		}

	}
}
