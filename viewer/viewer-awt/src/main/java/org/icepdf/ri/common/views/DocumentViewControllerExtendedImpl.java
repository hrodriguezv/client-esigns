/**
 * 
 */
package org.icepdf.ri.common.views;

import org.icepdf.ri.common.SwingController;

import com.consultec.esigns.strokes.api.IStrokeSignature;

/**
 * The Class DocumentViewControllerExtendedImpl.
 *
 * @author hrodriguez
 */
public class DocumentViewControllerExtendedImpl extends DocumentViewControllerImpl {

	/** The signature vendor. */
	private IStrokeSignature signatureVendor;
	
	/** The page view component. */
	private AbstractPageViewComponent pageViewComponent;
	
	/**
	 * Instantiates a new document view controller extended impl.
	 *
	 * @param viewerController the viewer controller
	 */
	public DocumentViewControllerExtendedImpl(SwingController viewerController) {
		super(viewerController);
	}

	/**
	 * Gets the signature vendor.
	 *
	 * @return the signature vendor
	 */
	public IStrokeSignature getSignatureVendor() {
		return signatureVendor;
	}

	/**
	 * Sets the signature vendor.
	 *
	 * @param signatureVendor the new signature vendor
	 */
	public void setSignatureVendor(IStrokeSignature signatureVendor) {
		this.signatureVendor = signatureVendor;
	}

	/**
	 * Sets the page view component.
	 *
	 * @param pageView the new page view component
	 */
	public void setPageViewComponent(
		AbstractPageViewComponent pageView) {
		this.pageViewComponent = pageView;
	}

	/**
	 * Gets the page view component.
	 *
	 * @return the page view component
	 */
	public AbstractPageViewComponent getPageViewComponent() {
		return pageViewComponent;
	}

	
}
