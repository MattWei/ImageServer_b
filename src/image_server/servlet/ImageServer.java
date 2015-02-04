package image_server.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mj.imagedownloader.DownloadThread;
import com.mj.imagedownloader.Log;
import com.mj.imagedownloader.SqliteManager;
import com.mj.imagedownloader.TumblrPost;


/**
 * Servlet implementation class ImageServer
 */
@WebServlet("/ImageServer")
public class ImageServer extends HttpServlet {
	private static final String LOG_TAG = "ImageServer";
	
	private static final long serialVersionUID = 1L;
	
	private static boolean mIsDownloadThreadStarted = false;
	
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ImageServer() {
        super();
        // TODO Auto-generated constructor stub
    }
    
    public void init() throws ServletException {
    	  if (!mIsDownloadThreadStarted) {
    		  Log.e(LOG_TAG, "Start download thread\n");
    		  
    		  mIsDownloadThreadStarted = true;
    		  
    		  if (!SqliteManager.getInstance().isCreated()) {
    			  Log.d(LOG_TAG, "Create Database\n");
    			  if (!SqliteManager.getInstance().createDataBase()) {
    				  Log.d(LOG_TAG, "Create Database false\n");
    				  return;
    			  }
    		  }
    		  
    		  DownloadThread dThread = new DownloadThread(SqliteManager.getInstance());
    		  
    		  dThread.start();
    	  }
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) 
			throws ServletException, IOException {
		System.out.print("Do Get V2\n");
		
		SqliteManager sqlManager = SqliteManager.getInstance();
		ArrayList<TumblrPost> posts = (ArrayList<TumblrPost>) sqlManager.select(TumblrPost.PHOTO_TYPE, 0L);
		
        if (posts == null || posts.isEmpty()) {
            Log.e(LOG_TAG, "Error, nothing is download");
        }
        
        for (TumblrPost post : posts) {
            Log.d(LOG_TAG, post.toString());
        }
		
	      // Set response content type
	      response.setContentType("text/html");

	      PrintWriter out = response.getWriter();

	      String title = "JQuery Mobile Test";
	      String docType = "<!doctype html>";
	      String header = docType + "<html> \n" +
	    		  	"<head> \n" +
	    		  	"<title>" + title + "</title>" +
	    		  	"<meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">" +
	    		  	"<link rel=\"stylesheet\" href=\"http://code.jquery.com/mobile/1.2.0/jquery.mobile-1.2.0.min.css\"> " +
	    		  	"<script src=\"http://code.jquery.com/jquery-1.8.2.min.js\"></script>" +
	    		  	"<script src=\"http://code.jquery.com/mobile/1.2.0/jquery.mobile-1.2.0.min.js\"></script>" +
	    		  	"</head> \n";
	      
	    	String body = header + 
	    			"<body> \n" +
	    		  	"<div data-role=\"page\"> \n" + 
	    		  	"<div data-role=\"header\"> " +
	                "<h1>My Title</h1>" +
	                "</div><!-- /header -->" +
	                "<div data-role=\"content\">" +
	                "<p>Hello world</p>" +
	                "</div><!-- /content -->" +
	                "<div data-role=\"footer\">" +
	                "<h4>My Footer</h4>" +
	                "</div><!-- /footer -->" +
	                "</div><!-- /page -->" +
	                "</body>" +
	                "</html>";
	    	
	    	out.println(body);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) 
			throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

}
