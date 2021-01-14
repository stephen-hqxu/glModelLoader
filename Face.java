/**
 * 
 */
package glModelLoader;

/**
 * Face contains indices of vertices.
 * @author Haoqian Stephen Xu
 *
 */
public final class Face{
	//Variables
	private float[] VertexIndex = {0.0f, 0.0f, 0.0f};
	private float[] TextureCoordinateIndex = {0.0f, 0.0f, 0.0f};
	private float[] NormalIndex = {0.0f, 0.0f, 0.0f};
	
	/**
	 * Face contains indices of vertices.
	 * Once the datas are specified, it cannot be changed.
	 * Parameters are given in GLVector3 which is defined in float, and the class will convert
	 * float into integer when outputting data.
	 * @param VertexIndex Provide index of vertex
	 * @param TextureCoordinateIndex Provide index of texture coordinate
	 * @param NormalIndex Provide index of normal
	 */
	Face(GLVector3 vertexIndex, GLVector3 textureCoordinateIndex, GLVector3 normalIndex){
		VertexIndex[0] = vertexIndex.getComponentX();
		VertexIndex[1] = vertexIndex.getComponentY();
		VertexIndex[2] = vertexIndex.getComponentZ();
		
		TextureCoordinateIndex[0] = textureCoordinateIndex.getComponentX();
		TextureCoordinateIndex[1] = textureCoordinateIndex.getComponentY();
		TextureCoordinateIndex[2] = textureCoordinateIndex.getComponentZ();
		
		NormalIndex[0] = normalIndex.getComponentX();
		NormalIndex[1] = normalIndex.getComponentY();
		NormalIndex[2] = normalIndex.getComponentZ();
	}
	
	/**
	 * Return the indices stored in the class
	 * @param IndexType Define the type of returning index.
	 * @return The integer array contains index, usually it has size of three.
	 */
	public int[] getIndex(Mesh.GLModelDataType IndexType) {
		int[] returnValue = new int[3];
		
		switch(IndexType) {
		case Vertices : returnValue[0] = (int)VertexIndex[0];
		returnValue[1] = (int)VertexIndex[1];
		returnValue[2] = (int)VertexIndex[2];
		break;
		case TextureCoordinates : returnValue[0] = (int)TextureCoordinateIndex[0];
		returnValue[1] = (int)TextureCoordinateIndex[1];
		returnValue[2] = (int)TextureCoordinateIndex[2];
		break;
		case Normals : returnValue[0] = (int)NormalIndex[0];
		returnValue[1] = (int)NormalIndex[1];
		returnValue[2] = (int)NormalIndex[2];
		break;
		default:
			break;
		}
		return returnValue;
	}
	
	
}