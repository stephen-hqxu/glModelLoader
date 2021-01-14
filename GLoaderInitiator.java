package glModelLoader;

import java.io.File;

/**
 * GLoaderInitiator class, it will be used to initialise the GLModelImporter class and throw out the GLModelLoader to the 
 * user
 * @author Haoqian Stephen Xu
 *
 */
public final class GLoaderInitiator {
	
	/**
	 * All methods are static so user will not be able to use the constructor
	 */
	private GLoaderInitiator(){
		
	}
	
	/**
	 * Initialisation of the ObjLoader
	 * @param ObjPath Specify the location of the obj file
	 * @throws GLModelLoadingException - If file does not exist or for some reason it cannot be read or the file is not a obj file.
	 * @return GLModelLoader The loader that has been loaded with the provided object paths.
	 */
	public static final GLModelLoader initModelLoader(File ObjPath) throws GLModelLoadingException{
		GLModelLoader loader = null;
		try {
			loader = new GLModelImporter(ObjPath);
			return loader;
		}catch(GLModelLoadingException gl) {
			throw gl;
		}finally {
			
		}
		
	}
	
	/**
	 * Initialisation of the ObjLoader
	 * @param ObjPath Specify the location of the obj file
	 * @param MtlPath Specify the location of the mtl file
	 * @throws GLModelLoadingException If file does not exist or for some reason it cannot be read or the file is not a obj or mtl file.
	 * @return GLModelLoader The loader that has been loaded with the provided object paths.
	 */
	public static final GLModelLoader initModelLoader(File ObjPath, File MtlPath) throws GLModelLoadingException{
		GLModelLoader loader = null;
		try {
			loader = new GLModelImporter(ObjPath, MtlPath);
			return loader;
		}catch(GLModelLoadingException gl) {
			throw gl;
		}finally {
			
		}
	}
}
