package js.tinyvm;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Vector;

import js.tinyvm.util.Assertion;

/**
 * This class overrides some specific parts of class <code>Binary</code>.
 * 
 * <p>
 * Should be refactored later with <code>js.tools</code>.
 * </p>
 * 
 * @author <a href="mailto:jochen.hiller@t-online.de">Jochen Hiller</a>
 */
public class PatchedBinary extends Binary {

	private StringWriter stringWriter;
	private PrintWriter signatureWriter;

	public PatchedBinary() {
		super();
		this.stringWriter = new StringWriter();
		this.signatureWriter = new PrintWriter(this.stringWriter);
	}

	public void appendSignature(String msg) {
		this.signatureWriter.println(msg);
	}

	public void flushSignatureFile(File aFile) throws FileNotFoundException {
		PrintWriter pw = new PrintWriter(new FileOutputStream(aFile));
		pw.print(this.stringWriter.toString());
		pw.flush();
		pw.close();
	}

	public static PatchedBinary patchedCreateFromClosureOf(
		Vector aEntryClasses,
		ClassPath aClassPath,
		boolean aAll)
		throws Exception {
		PatchedBinary pBin = new PatchedBinary();
		// From special classes and entry class, store closure
		pBin.processClasses(aEntryClasses, aClassPath);
		// Store special signatures
		pBin.processSpecialSignatures();
		pBin.processConstants();
		pBin.processMethods(aAll);
		pBin.processFields();
		// Copy code as is (first pass)
		pBin.processCode(false);
		pBin.storeComponents();
		pBin.initOffsets();
		// Post-process code after offsets are set (second pass)
		pBin.processCode(true);
		// Do -verbose reporting
		pBin.report();
		return pBin;
	}

	public void report() {
		int pSize = iSignatures.size();
		for (int i = 0; i < pSize; i++) {
			Signature pSig = (Signature) iSignatures.elementAt(i);
			appendSignature("Signature " + i + ": " + pSig.getImage());
		}
		appendSignature(
			"Master record : " + iMasterRecord.getLength() + " bytes.");
		appendSignature(
			"Class records : "
				+ iClassTable.size()
				+ " ("
				+ iClassTable.getLength()
				+ " bytes).");
		appendSignature(
			"Field records : "
				+ getTotalNumInstanceFields()
				+ " ("
				+ iInstanceFieldTables.getLength()
				+ " bytes).");
		appendSignature(
			"Method records: "
				+ getTotalNumMethods()
				+ " ("
				+ iMethodTables.getLength()
				+ " bytes).");
		appendSignature(
			"Code          : "
				+ iCodeSequences.size()
				+ " ("
				+ iCodeSequences.getLength()
				+ " bytes).");

		appendSignature("Class table offset   : " + iClassTable.getOffset());
		appendSignature("Constant table offset: " + iConstantTable.getOffset());
		appendSignature("Method tables offset : " + iMethodTables.getOffset());
		appendSignature(
			"Excep tables offset  : " + iExceptionTables.getOffset());
	}

	public void processClasses(Vector aEntryClasses, ClassPath aClassPath)
		throws Exception {
		Vector pInterfaceMethods = new Vector();
		// Add special classes first
		for (int i = 0; i < CLASSES.length; i++) {
			String pName = CLASSES[i];
			ClassRecord pRec =
				PatchedClassRecord.patchedGetClassRecord(pName, aClassPath, this);
			iClasses.put(pName, pRec);
			iClassTable.add(pRec);
			// pRec.useAllMethods();
		}
		// Now add entry classes
		int pEntrySize = aEntryClasses.size();
		for (int i = 0; i < pEntrySize; i++) {
			String pName = (String) aEntryClasses.elementAt(i);
			ClassRecord pRec =
				PatchedClassRecord.patchedGetClassRecord(pName, aClassPath, this);
			iClasses.put(pName, pRec);
			iClassTable.add(pRec);
			pRec.useAllMethods();
			// Update table of indices to entry classes
			iEntryClassIndices.add(new EntryClassIndex(this, pName));
		}
		Assertion.trace ("Starting with " + iClassTable.size() + " classes.");
		// Now add the closure.
		// Yes, call iClassTable.size() in every pass of the loop.
		for (int pIndex = 0; pIndex < iClassTable.size(); pIndex++) {
			ClassRecord pRec = (ClassRecord) iClassTable.elementAt(pIndex);
			//Assertion.verbose(1, "Class " + pIndex + ": " + pRec.iName);
			appendSignature("Class " + pIndex + ": " + pRec.iName);
			pRec.storeReferredClasses(
				iClasses,
				iClassTable,
				aClassPath,
				pInterfaceMethods);
		}
		// Initialize indices and flags
		int pSize = iClassTable.size();
		for (int pIndex = 0; pIndex < pSize; pIndex++) {
			ClassRecord pRec = (ClassRecord) iClassTable.elementAt(pIndex);
			for (int i = 0; i < pInterfaceMethods.size(); i++)
				pRec.addUsedMethod((String) pInterfaceMethods.elementAt(i));
			pRec.iIndex = pIndex;
			pRec.initFlags();
			pRec.initParent();
		}
	}

	/**
	 * Calls storeMethods on all the classes of the closure previously computed
	 * with processClasses.
	 */
	public void processMethods(boolean iAll) {
		int pSize = iClassTable.size();
		for (int pIndex = 0; pIndex < pSize; pIndex++) {
			PatchedClassRecord pRec =
				(PatchedClassRecord) iClassTable.elementAt(pIndex);
			pRec.storeMethods(
				iMethodTables,
				iExceptionTables,
				iSignatures,
				iAll,
				this.signatureWriter);
		}
	}
}
