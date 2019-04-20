/**
 * 
 */
package org.icepdf.ri.common.tools;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;

import org.icepdf.core.pobjects.Destination;
import org.icepdf.ri.common.SwingExtendedController;
import org.icepdf.ri.common.views.AbstractPageViewComponent;
import org.icepdf.ri.common.views.DocumentViewController;

import com.consultec.esigns.core.io.FileSystemManager;
import com.consultec.esigns.core.util.PropertiesManager;
import com.consultec.esigns.strokes.api.IStrokeSignature;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.twelvemonkeys.io.FileUtil;

/**
 * The Class SignStrokesUserExperience.
 *
 * @author hrodriguez
 */
public class SignStrokesUserExperience extends CommonToolHandler {

	/** The Constant IMAGE_SRC_NAME. */
	final static String STROKE_SRC_NAME =
		PropertiesManager.getInstance().getValue(
			PropertiesManager.PROPERTY_USER_STROKE_FILENAME);

	/** The Constant IMAGE_SRC_EXT. */
	final static String IMAGE_SRC_EXT =
		PropertiesManager.getInstance().getValue(
			PropertiesManager.PROPERTY_USER_STROKE_IMGEXT);

	/** The Constant TEXT_SRC_EXT. */
	final static String TEXT_SRC_EXT = PropertiesManager.getInstance().getValue(
		PropertiesManager.PROPERTY_USER_STROKE_TEXTEXT);


	/** The sign provider. */
	private IStrokeSignature signProvider;

	/**
	 * Instantiates a new sign strokes user experience.
	 *
	 * @param documentViewController the document view controller
	 * @param pageViewComponent the page view component
	 * @param provider the provider
	 */
	public SignStrokesUserExperience(
		DocumentViewController documentViewController,
		AbstractPageViewComponent pageViewComponent, IStrokeSignature provider) {
		super(documentViewController, pageViewComponent);
		signProvider = provider;
	}

	/* (non-Javadoc)
	 * @see org.icepdf.ri.common.tools.CommonToolHandler#checkAndApplyPreferences()
	 */
	@Override
	protected void checkAndApplyPreferences() {
	}

	/**
	 * Gets the inverse coordinates.
	 *
	 * @param x the x
	 * @param y the y
	 * @return the inverse coordinates
	 */
	public Rectangle getInverseCoordinates(int x, int y) {

		AffineTransform pageInverseTransform = getPageTransform();
		Dimension scaledSize = new Dimension(
			(int) Math.abs(2 * pageInverseTransform.getScaleX()),
			(int) Math.abs(2 * pageInverseTransform.getScaleY()));

		// convert bbox and start and end line points.
		Rectangle bBox =
			new Rectangle(x, y, scaledSize.width, scaledSize.height);
		Rectangle tBbox = convertToPageSpace(bBox).getBounds();
		return tBbox;
	}

	/**
	 * Creates the stroke files.
	 *
	 * @return the file
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public File createStrokeFiles()
		throws IOException {

		PropertiesManager props = PropertiesManager.getInstance();
		FileSystemManager fsManager = FileSystemManager.getInstance();

		String dateFmask =
			props.getValue(PropertiesManager.DEFAULT_FORMATTER_MASK);
		String baseHomePath = fsManager.getBaseHome().getAbsolutePath();
		String timeStamp =
			new SimpleDateFormat(dateFmask).format(new java.util.Date());

		File finalStrokeText =
			new File(baseHomePath, STROKE_SRC_NAME + timeStamp + TEXT_SRC_EXT);
		File finalImgName =
			new File(baseHomePath, STROKE_SRC_NAME + timeStamp + IMAGE_SRC_EXT);

		signProvider.writeImageFile(finalImgName.getAbsolutePath());
		FileUtil.write(
			finalStrokeText.getAbsolutePath(),
			signProvider.getEncodedSign().getBytes());

		fsManager.addImgStrokeFile(finalImgName);
		fsManager.addTextStrokeFile(finalStrokeText);
		return finalImgName;
	}

	/**
	 * Adds the strokes to PDF.
	 *
	 * @param imgSrc the img src
	 * @param coordinates the coordinates
	 * @throws Exception the exception
	 */
	public void addStrokesToPDF(String imgSrc, Rectangle coordinates)
		throws Exception {

		FileSystemManager fsManager = FileSystemManager.getInstance();
		int currentPage = documentViewController.getCurrentPageDisplayValue();

		File pdfDocument = fsManager.getPdfDocument();
		File strokedDocument = fsManager.getPdfStrokedDoc();

		if (strokedDocument.exists()) {
			pdfDocument = strokedDocument;
		}

		String pathOutputFile = strokedDocument.getAbsolutePath();

		PdfReader reader = new PdfReader(pdfDocument.getAbsolutePath());
		PdfDocument pdfDoc =
			new PdfDocument(reader, new PdfWriter(pathOutputFile + 1));

		ImageData image = ImageDataFactory.create(imgSrc);

		int x, y = 0;
		x = (int) coordinates.getX();
		y = (int) coordinates.getY();

        AffineTransform at = AffineTransform.getTranslateInstance(x - 85, y - 65);
		at.concatenate(AffineTransform.getScaleInstance(200, 130));
		PdfCanvas canvas = new PdfCanvas(pdfDoc.getPage(currentPage));
		double[] matrix = new double[6];
		at.getMatrix(matrix);
		canvas.addImage(
			image, (float) matrix[0], (float) matrix[1], (float) matrix[2],
			(float) matrix[3], (float) matrix[4], (float) matrix[5]);
		pdfDoc.close();
		FileUtil.delete(pathOutputFile);
		FileUtil.rename(pathOutputFile + 1, pathOutputFile);
	}

	/**
	 * Display changes.
	 *
	 * @param coordinates the coordinates
	 */
	public void displayChanges(Rectangle coordinates) {

		int currentPage = documentViewController.getCurrentPageDisplayValue();
		SwingExtendedController controller =
			(SwingExtendedController) documentViewController.getParentController();

		controller.openDocument(
			FileSystemManager.getInstance().getPdfStrokedDoc().getAbsolutePath());
		// navigate to the location
		Rectangle2D.Float bounds = new Rectangle2D.Float(
			(float) coordinates.getX(), (float) coordinates.getY(),
			(float) coordinates.getWidth(), (float) coordinates.getHeight());

		controller.getDocumentViewController().setDestinationTarget(
			new Destination(
				controller.getDocument().getPageTree().getPage(--currentPage),
				(int) bounds.x, (int) (bounds.y + bounds.height + 100)));
	}
}
