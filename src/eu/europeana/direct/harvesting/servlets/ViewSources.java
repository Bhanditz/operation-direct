package eu.europeana.direct.harvesting.servlets;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import eu.europeana.direct.backend.model.HarvestSource;
import eu.europeana.direct.backend.repositories.HarvestRepository;
import eu.europeana.direct.backend.repositories.LogRepository;
import eu.europeana.direct.harvesting.mapper.EdmMapper;
import eu.europeana.direct.harvesting.mapper.IMapper;
import eu.europeana.direct.harvesting.models.view.HarvestSourceView;
import eu.europeana.direct.harvesting.source.edm.ISourceReader;
import eu.europeana.direct.harvesting.source.edm.SourceReader;
import eu.europeana.direct.harvesting.source.edm.model.EdmOaiSource;
import eu.europeana.direct.logic.CulturalHeritageObjectLogic;
import eu.europeana.direct.rest.gen.java.io.swagger.model.CulturalHeritageObject;

/**
 * Servlet implementation class ViewSources
 */
@WebServlet("/ViewSources")
public class ViewSources extends HttpServlet {
	private static final long serialVersionUID = 1L;	
	private HarvestSourceView harvestSourceViewModel;
	private HarvestRepository harvestRepository = null;	
	private SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
	
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ViewSources() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
					
		try{		
			harvestRepository = new HarvestRepository();
			List<HarvestSource> harvestSource = harvestRepository.getAll();
			List<HarvestSourceView> harvestViewList = new ArrayList<>();				
			
			for (int i = 0; i < harvestSource.size(); i++) {
				harvestSourceViewModel = new HarvestSourceView();
				
				harvestSourceViewModel.setType(harvestSource.get(i).getType());
				harvestSourceViewModel.setName(harvestSource.get(i).getName());
				harvestSourceViewModel.setConfiguration(harvestSource.get(i).getConfiguration());								
				harvestSourceViewModel.setCreated(sdf.format(harvestSource.get(i).getCreated()));
				harvestViewList.add(harvestSourceViewModel);
			}
			request.setAttribute("sources", harvestViewList);
			
		} catch (Exception e){
			
		} finally {
			harvestRepository.close();
		}		
		
		RequestDispatcher rd = request.getRequestDispatcher("index.jsp");
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
