package glModelLoader;

/**
 * GLModerloader is a open source obj model importer and it is fully supportive to java opengl libaray.
 * @author Haoqian Stephen Xu
 *
 */
public interface GLModelLoader {
	//Constant of mode
	//Index Preference
	/**
	 * Index start from 0 (opengl default form)
	 */
	public final static int START_FROM_ZERO = 20001;
	/**
	 * Index start from 1
	 */
	public final static int START_FROM_ONE = 20101;
	/**
	 * Index start from -(number of element). For example, if there are 20 vertices, vertex index starts from -20.
	 */
	public final static int START_FROM_NEGATIVE_NUM_OF_ELEMENT = 20201;
	
	/**
	 * Import obj model file from local computer.
	 * @return The mesh in the obj file. Note that one obj file may contain several meshes
	 * @throws GLModelLoadingException - Only IOException will be thrown in this method, when the file is non-accessible.
	 */
	public abstract Mesh[] ImportWaveFrontObj() throws GLModelLoadingException;
	
	/**
	 * Import mtl model file from local computer.
	 * @return The material in the mtl file. Note that one mtl file may contain several material configurations
	 * @throws GLModelLoadingException - Only IOException will be thrown in this method, when the file is non-accessible.
	 */
	public abstract Material[] ImportWaveFrontMtl() throws GLModelLoadingException;
	
	/**
	 * Given the thing that need to be adjusted when importing model. 
	 * The post process will be completed BEFORE importing obj file.
	 * @param Type Provide the type of post processing
	 * @param Enabled To set the Activity state of the function. The initial states of all functions are false.
	 */
	public abstract void setObjPostProcessing(GLPostProcessingType Type, boolean Enabled);
	
	/**
	 * Set the index behaviour of the obj file.
	 * The setting only affect when INDEX_CORRECTION has been turned on BEFORE importing obj.
	 * @param IndexMode The mode of index
	 * @throws GLModelLoadingException If a wrong enum is provided
	 */
	public abstract void setIndexBehaviour(int IndexMode)  throws GLModelLoadingException;
	
	/**
	 * Get copyright info of the imported obj file.
	 * @return The copyright info of the imported obj file. If there is no copyright info, null is returned.
	 */
	public String getCopyrightInfo();
	
	/**
	 * Get the name of the material library
	 * The name of the material library only available after importing obj file
	 * @return the name of the material library. If there is no material library, it will return null.
	 */
	public String getMtl();
	
	/**
	 * Close the BufferedReader stream created by the GLModelLoader
	 * @throws GLModelLoadingException -  If an I/O error occurs (only IOException will be thrown in this method).
	 */
	public void dump() throws GLModelLoadingException;
	
}