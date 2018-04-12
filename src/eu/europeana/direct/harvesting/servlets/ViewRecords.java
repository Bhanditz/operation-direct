package eu.europeana.direct.harvesting.servlets;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

import javax.ejb.EJB;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.swing.plaf.synth.SynthSpinnerUI;

import org.quartz.SchedulerException;

import eu.europeana.direct.backend.repositories.LogRepository;
import eu.europeana.direct.harvesting.logging.model.ImportLog;
import eu.europeana.direct.harvesting.logging.model.ImportLogDetail;
import eu.europeana.direct.harvesting.mapper.IMapper;
import eu.europeana.direct.harvesting.models.view.RecordsView;
import eu.europeana.direct.harvesting.source.edm.SourceReader;
import eu.europeana.direct.harvesting.source.edm.ISourceReader;
import eu.europeana.direct.harvesting.source.edm.model.EdmOaiSource;
import eu.europeana.direct.rest.gen.java.io.swagger.model.CulturalHeritageObject;
import eu.europeana.direct.rest.gen.java.io.swagger.model.CulturalHeritageObjectLanguageAware;
import se.kb.oai.pmh.OaiPmhServer;

/**
 * Servlet implementation class ViewRecords
 */
@WebServlet("/ViewRecords")
public class ViewRecords extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private RecordsView recordsViewModel = new RecordsView();
		
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ViewRecords() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		LogRepository logRepository = new LogRepository();
		
		try{			
			List<ImportLog> importLogList = logRepository.getAllImportLog();
			List<ImportLogDetail> importLogDetailList = logRepository.getAllImportLogDetail();
			List<RecordsView> recordsViewModelList = new ArrayList<RecordsView>();

			SimpleDateFormat sdf = new SimpleDateFormat("dd-M-yyyy HH:mm:ss");
			TimeZone.setDefault(TimeZone.getTimeZone("CEST"));

			for (int i = 0; i < importLogList.size(); i++) {

				recordsViewModel.setStart(sdf.format(importLogList.get(i).getStartTime()));
				recordsViewModel.setErrors(importLogList.get(i).getErrors());
				recordsViewModel.setWarnings(importLogList.get(i).getWarnings());
				recordsViewModel.setSuccesses(importLogList.get(i).getSuccesses());
				recordsViewModel.setTask(importLogList.get(i).getJobname());

				for (int j = 0; j < importLogDetailList.size(); j++) {
					if (importLogDetailList.get(j).getLogId() == importLogList.get(i).getId()) {
						recordsViewModel.setStatus(importLogDetailList.get(j).getStatus());
						recordsViewModel.setMessage(importLogDetailList.get(j).getMessage());
						break;
					}
				}
				recordsViewModelList.add(recordsViewModel);
				recordsViewModel = new RecordsView();
			}
			request.setAttribute("records", recordsViewModelList);

		} catch (Exception e){
			e.printStackTrace();
		} finally {
			logRepository.close();
		}				

		RequestDispatcher rd = request.getRequestDispatcher("records.jsp");
		rd.forward(request, response);		
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}
}
