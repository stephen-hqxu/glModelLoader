/**
 * 
 */
package glModelLoader;

/**
 * <html/>
 * The class Exception and its subclasses are a form of Throwable that indicates conditions that a reasonable
 * application might want to catch.
 * GLModelLoadingException throws when one of the issues appeared: </br>
 * 1. The loading model(or file) is not supported. </br>
 * 2. IOException is thrown. </br>
 * 3. FileNotFoundException is thrown. </br>
 * 4. The obj file is not defined in triangles. </br>
 * 5. The obj file is not a obj file.</br>
 * 6. The mtl file is not a mtl file.
 * </html>
 * @author Haoqian Stephen Xu
 *
 */
public class GLModelLoadingException extends Exception{
	private String TypeofException = "";
	/**
	 * Serial Number
	 */
	private static final long serialVersionUID = 0xaabbccdd;

	/**
	 * Constructor of the exception class
	 */
	GLModelLoadingException(String ErrorMes, Throwable Cause, StackTraceElement[] Trace, String ExceptionType) {
		super(ErrorMes, Cause);
		super.setStackTrace(Trace);
		TypeofException = ExceptionType;
	}
	
	/**
	 * Get the exact type of exception
	 * @return the exact type of exception
	 */
	public String getType() {
		return TypeofException;
	}
	
}

