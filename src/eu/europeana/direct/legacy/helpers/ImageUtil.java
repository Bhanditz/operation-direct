package eu.europeana.direct.legacy.helpers;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import javax.imageio.ImageIO;

import org.apache.commons.codec.binary.Base64;

import com.sun.xml.internal.ws.api.server.EndpointData;

/**
 * 
 * ImageUtil is utility class for CHO images
 *
 */
public class ImageUtil {

	// scales the image and maintains aspect ratio
	/**
	 * Method scales the image and maintains the aspect ratio of the image
	 * @param image BufferedImage object
	 * @param imageType Image type
	 * @param newWidth New width of image
	 * @param newHeight New height of image
	 * @return BufferedImage scaled image with new dimension's
	 */
	public BufferedImage scaleImage(BufferedImage image, int imageType, int newWidth, int newHeight) {
		// Make sure the aspect ratio is maintained, so the image is not
		// distorted
		double thumbRatio = (double) newWidth / (double) newHeight;
		int imageWidth = image.getWidth(null);
		int imageHeight = image.getHeight(null);
		double aspectRatio = (double) imageWidth / (double) imageHeight;

		if (thumbRatio < aspectRatio) {
			newHeight = (int) (newWidth / aspectRatio);
		} else {
			newWidth = (int) (newHeight * aspectRatio);
		}

		// Draw the scaled image
		BufferedImage newImage = new BufferedImage(newWidth, newHeight, imageType);
		Graphics2D graphics2D = newImage.createGraphics();
		graphics2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		graphics2D.drawImage(image, 0, 0, newWidth, newHeight, null);

		return newImage;
	}

	public void saveImage(InputStream streamImage, String destinationFile, String imageRandomName) throws IOException {

		Path path = Paths.get(destinationFile);		
		Files.createDirectories(path);
		
		File file = new File(destinationFile+imageRandomName);		
		InputStream input = new BufferedInputStream(streamImage);
		OutputStream output = new FileOutputStream(file);
		
		byte[] b = new byte[2048];
		int length;

		while ((length = input.read(b)) != -1) {
			output.write(b, 0, length);
		}

		output.flush();
		output.close();
		input.close();
	}
	
	public ByteArrayInputStream generateImage(String url) {
		ByteArrayInputStream bais = null;		

		
		// url to sha256hex
		String urlSha256Hex = org.apache.commons.codec.digest.DigestUtils.sha256Hex(url)+".jpg";   
		//check if exists
		File sourceimage = new File("/var/jhn-images/"+urlSha256Hex);				
		if(sourceimage.exists() && !sourceimage.isDirectory()) { 			
		} else {
			// save img from url
			try{							
				String decodedURL = null; 
				if(Base64.isArrayByteBase64(url.getBytes())){
					byte[] decoded = Base64.decodeBase64(url);						
					decodedURL = URLDecoder.decode(new String(decoded), "UTF-8");
				} else {
					decodedURL = URLDecoder.decode(url, "UTF-8");
				}
				
				//replace whitespaces if any
				decodedURL = decodedURL.replaceAll("\\s+","%20");
				// save decoded URL to URL object
				URL imgUrl = new URL(decodedURL);	        					
				// url object to URI
				URI uriImg = imgUrl.toURI();
				// uri object to new URL object with decoded characters
				URL newURL = new URL(uriImg.toASCIIString());									
				
				saveImage(newURL.openStream(), "/var/jhn-images/",urlSha256Hex);												        			
							
			}catch(Exception e){
				e.printStackTrace();
			}	
				
		}	
		
		try{			
			BufferedImage image = ImageIO.read(sourceimage);			
	        if(image != null){	        		        	
	        	int type = image.getType() == 0 ? BufferedImage.TYPE_INT_ARGB : image.getType();	        	
				BufferedImage resizedImage;
				//scales image to 400 width, maintains aspect ratio				
				resizedImage = scaleImage(image, type, 400, image.getHeight());								
				ByteArrayOutputStream baos = new ByteArrayOutputStream();				
				ImageIO.write(resizedImage, "png", baos);
				byte[] imageData = baos.toByteArray();
				bais = new ByteArrayInputStream(imageData);				
			}	
		}catch (Exception e){
			e.printStackTrace();
		}		
		
		return bais;
	}	
}
