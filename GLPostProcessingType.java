/**
 * 
 */
package glModelLoader;

/**
 * Provide the type of post processing when importing obj models
 * @author Haoqian Stephen Xu
 *
 */
public enum GLPostProcessingType {
	/**
	 * To flip the UV texture coordinates. Note that some exporters have already fliped UV when exporting.
	 */
	FLIP_UV,
	/**
	 * To re-defined all faces/indices to make each face to be triangle.
	 * Opengl cannot draw non-triangular shapes.
	 */
	TRIANGULATE,
	/**
	 * For most exporter, the recorded indices 
	 * have been subtracted by the number of vertex/texture coordinate/normal. So the post process will 
	 * add each index by the number of each elements to make the starting index 0.
	 */
	INDEX_CORRECTION,
	/**
	 * To align vertex/texture coordinate/normal using index data
	 * Opengl and most modern graphic APIs do not support using multiple indices with vertex/texture coordinate/normal.
	 * After the elimination process, faces data will be REMOVEN and become UNAVAILABLE.
	 */
	INDEX_ELIMINATION,
	
}
