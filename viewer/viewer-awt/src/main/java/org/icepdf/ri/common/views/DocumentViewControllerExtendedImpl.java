/**
 * 
 */
package org.icepdf.ri.common.views;

import org.icepdf.ri.common.SwingController;

import com.consultec.esigns.strokes.api.IStrokeSignature;

// TODO: Auto-generated Javadoc
/**
 * The Class DocumentViewControllerExtendedImpl.
 *
 * @author hrodriguez
 */
public class DocumentViewControllerExtendedImpl extends DocumentViewControllerImpl {

	/** The signature vendor. */
	private IStrokeSignature signatureVendor;
	
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

	
}
