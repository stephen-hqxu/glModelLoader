/**
 * 
 */
package glModelLoader;

/**
 * Vector3 class can store information about vertice, normals, etc. from the model file.
 * It can be obtained and returned by Mesh class.
 * @author Haoqian Stephen Xu
 *
 */
public final class GLVector3 {
	//Variables
	private float X, Y, Z = 0.0f;
	/**
	 * Vector3 class can store information about vertice, normals, etc. from the model file.
	 * 
	 * @param Component Provide the {X, Y, Z} component of the vector. Once the datas are specified, it cannot be changed
	 */
	GLVector3(float[] Component){
		X = Component[0];
		Y = Component[1];
		Z = Component[2];
	}
	
	/**
	 * 
	 * @return X component of the vector
	 */
	public final float getComponentX() {
		return X;
	}
	/**
	 * 
	 * @return Y component of the vector
	 */
	public final float getComponentY() {
		return Y;
	}/**
	 * 
	 * @return Z component of the vector
	 */
	public final float getComponentZ() {
		return Z;
	}
	/**
	 * Convert GLVector3 class to a float array
	 * @return Float array
	 */
	public final float[] toFloatArray() {
		return new float[] {X, Y, Z};
	}
	
}
