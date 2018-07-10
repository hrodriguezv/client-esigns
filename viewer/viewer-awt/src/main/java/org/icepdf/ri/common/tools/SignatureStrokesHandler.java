/**
 * 
 */
package org.icepdf.ri.common.tools;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.text.SimpleDateFormat;

import org.icepdf.core.pobjects.Destination;
import org.icepdf.ri.common.SwingController;
import org.icepdf.ri.common.views.AbstractPageViewComponent;
import org.icepdf.ri.common.views.DocumentViewController;
import org.icepdf.ri.common.views.DocumentViewControllerExtendedImpl;

import com.consultec.esigns.core.io.FileSystemManager;
import com.consultec.esigns.core.util.PropertiesManager;
import com.consultec.esigns.strokes.api.IStrokeSignature;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.layout.element.Image;
import com.twelvemonkeys.io.FileUtil;

/**
 * The Class SignatureStrokesHandler.
 *
 * @author hrodriguez
 */
public class SignatureStrokesHandler extends CommonToolHandler implements ToolHandler {

	/** The Constant IMAGE_SRC_NAME. */
	final static String STROKE_SRC_NAME = PropertiesManager.getInstance().getPreferences()
			.get(PropertiesManager.PROPERTY_USER_STROKE_FILENAME, null);

	/** The Constant IMAGE_SRC_EXT. */
	final static String IMAGE_SRC_EXT = PropertiesManager.getInstance().getPreferences()
			.get(PropertiesManager.PROPERTY_USER_STROKE_IMGEXT, null);

	/** The Constant TEXT_SRC_EXT. */
	final static String TEXT_SRC_EXT = PropertiesManager.getInstance().getPreferences()
			.get(PropertiesManager.PROPERTY_USER_STROKE_TEXTEXT, null);

	/** The sign. */
	private IStrokeSignature signProvider;

	/**
	 * Instantiates a new signature strokes handler.
	 *
	 * @param documentViewController
	 *            the document view controller
	 * @param pageViewComponent
	 *            the page view component
	 */
	public SignatureStrokesHandler(DocumentViewController documentViewController,
			AbstractPageViewComponent pageViewComponent) {
		super(documentViewController, pageViewComponent);
		if (documentViewController instanceof DocumentViewControllerExtendedImpl) {
			this.signProvider = ((DocumentViewControllerExtendedImpl) documentViewController).getSignatureVendor();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.icepdf.ri.common.tools.CommonToolHandler#checkAndApplyPreferences()
	 */
	@Override
	protected void checkAndApplyPreferences() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseClicked(MouseEvent arg0) {
		if (pageViewComponent != null) {
			pageViewComponent.requestFocus();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.MouseListener#mouseEntered(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseEntered(MouseEvent arg0) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.MouseListener#mouseExited(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseExited(MouseEvent arg0) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
	 */
	@Override
	public void mousePressed(MouseEvent arg0) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseReleased(MouseEvent arg0) {
		
		if (signProvider == null) return;
		
		File pdfDocument = FileSystemManager.getInstance().getPdfDocument();
		File strokedDocument = FileSystemManager.getInstance().getPdfStrokedDoc();

		if (strokedDocument.exists()) {
			pdfDocument = strokedDocument;
		}

		String pathFile = pdfDocument.getAbsolutePath();
		String pathOutputFile = strokedDocument.getAbsolutePath();
		String dateFmask = PropertiesManager.getInstance().getPreferences()
				.get(PropertiesManager.DEFAULT_FORMATTER_MASK, null);

		AffineTransform pageInverseTransform = getPageTransform();
		Dimension scaledSize = new Dimension((int) Math.abs(2 * pageInverseTransform.getScaleX()),
				(int) Math.abs(2 * pageInverseTransform.getScaleY()));

		// convert bbox and start and end line points.
		Rectangle bBox = new Rectangle(arg0.getX(), arg0.getY(), scaledSize.width, scaledSize.height);
		Rectangle tBbox = convertToPageSpace(bBox).getBounds();

		try {
			SwingController controller = (SwingController) documentViewController.getParentController();
			String timeStamp = new SimpleDateFormat(dateFmask).format(new java.util.Date());
			File finalStrokeText = new File(FileSystemManager.getInstance().getBaseHome().getAbsolutePath(),
					STROKE_SRC_NAME + timeStamp + TEXT_SRC_EXT);
			File finalImgName = new File(FileSystemManager.getInstance().getBaseHome().getAbsolutePath(),
					STROKE_SRC_NAME + timeStamp + IMAGE_SRC_EXT);

			int signal = signProvider.sign();

			if (signal == 0) {
				signProvider.writeImageFile(finalImgName.getAbsolutePath());
				FileUtil.write(finalStrokeText.getAbsolutePath(), signProvider.getEncodedSign().getBytes());

				FileSystemManager.getInstance().addImgStrokeFile(finalImgName);
				FileSystemManager.getInstance().addTextStrokeFile(finalStrokeText);

				manipulatePdf(pathFile, pathOutputFile, finalImgName.getAbsolutePath(), tBbox);

				int currentPage = documentViewController.getCurrentPageDisplayValue();
				controller.openDocument(pathOutputFile);
				controller.showPage(currentPage);
				// navigate to the location
				Rectangle2D.Float bounds = new Rectangle2D.Float((float) tBbox.getX(), (float) tBbox.getY(),
						(float) tBbox.getWidth(), (float) tBbox.getHeight()); // word.getBounds();
				controller.getDocumentViewController().setDestinationTarget(
						new Destination(controller.getDocument().getPageTree().getPage(--currentPage), (int) bounds.x,
								(int) (bounds.y + bounds.height + 100)));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * java.awt.event.MouseMotionListener#mouseDragged(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseDragged(MouseEvent arg0) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.MouseMotionListener#mouseMoved(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseMoved(MouseEvent arg0) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.icepdf.ri.common.tools.ToolHandler#paintTool(java.awt.Graphics)
	 */
	@Override
	public void paintTool(Graphics g) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.icepdf.ri.common.tools.ToolHandler#installTool()
	 */
	@Override
	public void installTool() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.icepdf.ri.common.tools.ToolHandler#uninstallTool()
	 */
	@Override
	public void uninstallTool() {
	}

	/**
	 * Manipulate pdf.
	 *
	 * @param src
	 *            the src
	 * @param dest
	 *            the dest
	 * @param imgSrc
	 *            the img src
	 * @param r
	 *            the r
	 * @throws Exception
	 *             the exception
	 */
	protected void manipulatePdf(String src, String dest, String imgSrc, Rectangle r) throws Exception {
		PdfReader reader = new PdfReader(src);
		PdfDocument pdfDoc = new PdfDocument(reader, new PdfWriter(dest + 1));
		int currentPage = documentViewController.getCurrentPageDisplayValue();

		ImageData image = ImageDataFactory.create(imgSrc);
		Image imageModel = new Image(image);
		int x, y = 0;

		x = (int) r.getX();
		y = (int) r.getY();

		if (x < 35)
			x = 35;
		if (x > 547)
			x = 400;
		if (y > 580)
			y = 580;
		if (y < 35)
			y = 35;

		AffineTransform at = AffineTransform.getTranslateInstance(x, y);
		at.concatenate(
				AffineTransform.getScaleInstance(imageModel.getImageScaledWidth(), imageModel.getImageScaledHeight()));
		PdfCanvas canvas = new PdfCanvas(pdfDoc.getPage(currentPage));
		double[] matrix = new double[6];
		at.getMatrix(matrix);
		canvas.addImage(image, (float) matrix[0], (float) matrix[1], (float) matrix[2], (float) matrix[3],
				(float) matrix[4], (float) matrix[5]);
		pdfDoc.close();
		FileUtil.delete(dest);
		FileUtil.rename(dest + 1, dest);
	}
}
