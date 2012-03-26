/**
 * 
 */
package HTTPClient;

import java.io.IOException;
import java.io.InputStream;

import edu.ucdenver.ccp.common.file.FileArchiveUtil;

/**
 * The HTTPClient.UncompressInputStream class is unfortunately package-private. This wrapper class
 * simply allows it to be exposed to other packages. It's used by the {@link FileArchiveUtil} class.
 * 
 * @author Colorado Computational Pharmacology, UC Denver; ccpsupport@ucdenver.edu
 * 
 */
public class UncompressInputStreamWrapper extends UncompressInputStream {

	/**
	 * @param is
	 * @throws IOException
	 */
	public UncompressInputStreamWrapper(InputStream is) throws IOException {
		super(is);
	}

}
