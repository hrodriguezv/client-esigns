

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.security.PrivateKey;
import java.security.cert.Certificate;

import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.DERObjectIdentifier;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.esf.OtherHashAlgAndValue;
import org.bouncycastle.asn1.esf.SignaturePolicyId;
import org.bouncycastle.asn1.esf.SignaturePolicyIdentifier;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import com.consultec.esigns.core.security.SecurityProvider;
import com.consultec.esigns.core.util.InetUtility;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.signatures.BouncyCastleDigest;
import com.itextpdf.signatures.DigestAlgorithms;
import com.itextpdf.signatures.IExternalDigest;
import com.itextpdf.signatures.IExternalSignature;
import com.itextpdf.signatures.ITSAClient;
import com.itextpdf.signatures.PdfPKCS7;
import com.itextpdf.signatures.PdfSignatureAppearance;
import com.itextpdf.signatures.PdfSigner;
import com.itextpdf.signatures.PrivateKeySignature;
import com.itextpdf.signatures.SignatureUtil;
import com.itextpdf.signatures.TSAClientBouncyCastle;

public class TestSign {

	protected static final String BASEPATH = "C:\\Users\\hrodriguez\\Documents\\Consultec\\dev\\Firmas digitales\\recursos\\openssl\\test-realsec\\";
	protected static final String KEYSTORE = BASEPATH + "keystore.p12";
	protected static final char[] PASSWORD = "123456".toCharArray();
	protected static final String PDFIN = BASEPATH + "Holamundo-variaspaginas.pdf";
	protected static final String PDFOUT = BASEPATH + "HM-vp_signed.pdf";
	protected static final String BACK_IMG = BASEPATH + "sig.png";

	public static void main(String[] args) throws IOException, GeneralSecurityException {
		padesEpesProfileTest01();
	}

	@SuppressWarnings("unused")
	private static void signCMS() throws IOException, GeneralSecurityException {
		SecurityProvider provider = new SecurityProvider(KEYSTORE, PASSWORD);

		PdfReader reader = new PdfReader(PDFIN);
		PdfSigner signer = new PdfSigner(reader, new FileOutputStream(PDFOUT), false);
		com.itextpdf.kernel.geom.Rectangle rect = new com.itextpdf.kernel.geom.Rectangle(100.0f, 300.0f, 250.0f,
				250.0f);

		// Creating the appearance
		PdfSignatureAppearance appearance = signer.getSignatureAppearance().setReason("Reason").setLocation("location")
				.setReuseAppearance(false)
				.setRenderingMode(PdfSignatureAppearance.RenderingMode.GRAPHIC_AND_DESCRIPTION).setPageRect(rect)
				.setSignatureGraphic(ImageDataFactory.create(BACK_IMG));
		// appearance.setImageScale(1);
		// Creating the signature
		IExternalSignature pks = new PrivateKeySignature(provider.getPrivateKey(), DigestAlgorithms.SHA512,
				provider.getProviderName());
		IExternalDigest digest = new BouncyCastleDigest();
		ITSAClient tsaClient = new TSAClientBouncyCastle("http://time.certum.pl/");
		signer.signDetached(digest, pks, provider.getTrustedChain(), null, null, tsaClient, 0,
				PdfSigner.CryptoStandard.CMS);
	}

	public static void padesEpesProfileTest01() throws IOException, GeneralSecurityException {
		String alg = DigestAlgorithms.getAllowedDigest("SHA1");
		SignaturePolicyIdentifier sigPolicyIdentifier = SecurityProvider.getPadesEpesProfile(alg);
		signApproval(KEYSTORE, PDFOUT, sigPolicyIdentifier);
		basicCheckSignedDoc(PDFOUT, "Signature1");
	}

	private static void signApproval(String signCertFileName, String outFileName,
			SignaturePolicyIdentifier sigPolicyInfo) throws IOException, GeneralSecurityException {
		SecurityProvider provider = new SecurityProvider(KEYSTORE, PASSWORD);

		Certificate[] signChain = provider.getTrustedChain();
		PrivateKey signPrivateKey = provider.getPrivateKey();
		IExternalSignature pks = new PrivateKeySignature(signPrivateKey, DigestAlgorithms.SHA256,
				BouncyCastleProvider.PROVIDER_NAME);

		PdfReader reader = new PdfReader(PDFIN);
		PdfSigner signer = new PdfSigner(reader, new FileOutputStream(PDFOUT), false);
		signer.setFieldName("Signature1");
		signer.getSignatureAppearance()
			  .setPageRect(new Rectangle(150, 650, 200, 100))
			  .setReason("Test")
			  .setLocation("TestCity")
			  .setLayer2Text("Approval test signature.")
			  .setCertificate(provider.getCertificate());

		
		//http://213.37.154.21:12080/CryptosecOpenKey/tsa_service
		//http://as-demo.bit4id.org/smartengine/tsa
		//http://time.certum.pl/
		
		String serverUrl =  "http://time.certum.pl/";
		ITSAClient tsaClient = null;
		
		if (InetUtility.isReachable(serverUrl)) {
			tsaClient = new TSAClientBouncyCastle(serverUrl);
		}
		
		if (sigPolicyInfo == null) {
			signer.signDetached(new BouncyCastleDigest(), pks, signChain, null, null, tsaClient, 0,
					PdfSigner.CryptoStandard.CADES);
		} else {
			signer.signDetached(new BouncyCastleDigest(), pks, signChain, null, null, tsaClient, 0,
					PdfSigner.CryptoStandard.CADES, sigPolicyInfo);
		}
	}

	
	
	static void basicCheckSignedDoc(String filePath, String signatureName)
			throws GeneralSecurityException, IOException {
		PdfDocument outDocument = new PdfDocument(new PdfReader(filePath));

		SignatureUtil sigUtil = new SignatureUtil(outDocument);
		PdfPKCS7 pdfPKCS7 = sigUtil.verifySignature(signatureName);
		System.err.println(pdfPKCS7.verify());
		outDocument.close();
	}

}
