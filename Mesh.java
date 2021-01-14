package glModelLoader;

import java.util.ArrayList;

/**
 * A mesh data from the the obj file. A mesh data is defined by the vertices, normals, texture coordinates, etc. which 
 * are enclosed by the line before vertex data:"#Object (Mesh name)". A obj file may contains several meshed data, 
 * so it can be a array.
 * Mesh data can be obtained and returned by GLObjLoader.
 * @author Haoqian Stephen Xu
 *
 */
public final class Mesh{
	//Variables
	private ArrayList<GLVector3> Vertices = new ArrayList<GLVector3>(0);
	private ArrayList<GLVector3> TextureCoordinates = new ArrayList<GLVector3>(0);
	private ArrayList<GLVector3> Normals = new ArrayList<GLVector3>(0);
	private ArrayList<Face> Indices = new ArrayList<Face>(0);
	private ArrayList<GLVector3> Tangents = new ArrayList<GLVector3>(0);//Generate after the import process
	private String Name = "N/A";
	private String Mtl = null;
	boolean Index_Eliminated = false;
	/**
	 * Output the error message when calling {@link #calculateTangents(boolean, float)}
	 */
	private String Tangent_error_Mes = "No Error Found";
	
	/**
	 * Specify the type of information about the model file
	 * @author Haoqian Stephen Xu
	 *
	 */
	public static enum GLModelDataType{
		Vertices,
		Normals,
		TextureCoordinates;
	}
	
	/**
	 * A simple vector class which is similar to jogl VectorUtil
	 * @author Haoqian Xu
	 *
	 */
	private static class SimpleMaths{
		protected static int errors = 0;
		/**
		 * Subtract two vectors
		 * @param VecA vector A 
		 * @param VecB vector B
		 * @return VecA - VecB
		 */
		static GLVector3 subVector3(final GLVector3 VecA, final GLVector3 VecB){
			float[] subvec = {
					VecA.getComponentX() - VecB.getComponentX(),
					VecA.getComponentY() - VecB.getComponentY(),
					VecA.getComponentZ() - VecB.getComponentZ()
			};
			return new GLVector3(subvec);
		}
		
		/**
		 * Normalise the vector
		 * @param VecInput the vector that needed to be normalised
		 * @return The normalised vector
		 */
		static GLVector3 normaliseVector3(final GLVector3 VecInput) {
			float modulus = VecInput.getComponentX()*VecInput.getComponentX() + VecInput.getComponentY()*VecInput.getComponentY() + VecInput.getComponentZ()*VecInput.getComponentZ();
			modulus = (float)Math.pow(modulus, 0.5);
			modulus = 1.0f / modulus;
			return new GLVector3(new float[] {
					VecInput.getComponentX() * modulus, 
					VecInput.getComponentY() * modulus, 
					VecInput.getComponentZ() * modulus
			});
		}
		
		/**
		 * Inverting a 2x2 matrix. Note that is the determinate of the matrix equals to zero then the matrix is singular
		 * @param Mat2x2 The 2x2 matrix that need to be inverted
		 * @return The inverted matrix or null if the matrix is singular
		 */
		static float[] Inverse_Mat2x2(final float[] Mat2x2) {
			//2x2 matrix is defined as: {a,	b
			//							 c,	d}
			//Determinant of a 2x2 matrix is ad-bc
			//co-factor of a 2x2 matrix is: {d,	-b
			//								-c,	a}
			//Then, inverse of a 2x2 matrix is given as: (1/det) * co-factor
			float inverse_determinant = 1.0f * Mat2x2[0] * Mat2x2[3] - Mat2x2[1] * Mat2x2[2];//in case of integer matrix
			//det has not yet inverted
			final float[] co_factor = {
					Mat2x2[3], -Mat2x2[1],
					-Mat2x2[2], Mat2x2[0]
			};
			if(inverse_determinant == 0) {
				errors++;
				return null;
			}else {
				inverse_determinant = 1.0f / inverse_determinant;
				return new float[] {
						inverse_determinant * co_factor[0], inverse_determinant * co_factor[1],
						inverse_determinant * co_factor[2], inverse_determinant * co_factor[3]
				};
			}
		}
		
		/**
		 * Multiply a 2x2 matrix with a 3x2 matrix. Note that the order of multiplication will be: Mat2x2 * Mat3x2
		 * @param Mat2x2 Import of 2x2 matrix
		 * @param Mat3x2 Import of 3x2 matrix
		 * @return The resultant matrix, which is a 3x2 matrix
		 */
		static float[] Mat2x2multiMat3x2(final float[] Mat2x2, final float[] Mat3x2) {
			//Consider matrix 2x2: {a,	b
			//						c,	d}
			//and matrix 3x2: {e,	f,	g
			//				   h,	i,	j}
			return new float[] {
					1.0f * Mat2x2[0] * Mat3x2[0] + Mat2x2[1] * Mat3x2[3], 1.0f * Mat2x2[0] * Mat3x2[1] + Mat2x2[1] * Mat3x2[4], 1.0f * Mat2x2[0] * Mat3x2[2] + Mat2x2[1] * Mat3x2[5],
					1.0f * Mat2x2[2] * Mat3x2[0] + Mat2x2[3] * Mat3x2[3], 1.0f * Mat2x2[2] * Mat3x2[1] + Mat2x2[3] * Mat3x2[4], 1.0f * Mat2x2[2] * Mat3x2[2] + Mat2x2[3] * Mat3x2[5]
			};
		}
	}
	
	/**
	 *  Set up a new vertex data in the mesh, new data will be placed at the end of the array.
	 * @param x X component of a vector
	 * @param y Y component of a vector
	 * @param z Z component of a vector
	 */
	void newVertex(float x, float y, float z) {
		Vertices.add(new GLVector3(new float[] {x, y, z}));
	}
	/**
	 *  To get the vertex data
	 * @param Index The position of the vertex in the mesh
	 * @return return the vector contains vertex data
	 */
	public GLVector3 getVertex(int Index) {
		return Vertices.get(Index);
	}
	/**
	 * The number of elements in this vertex data.
	 * @return Returns the number of elements in this vertex data.
	 */
	public int getVertexSize() {
		return Vertices.size();
	}
	
	/**
	 * Set up a new texture coordinate data in the mesh, new data will be placed at the end of the array.
	 * @param x X component of a vector
	 * @param y Y component of a vector
	 * @param z Z component of a vector
	 */
	
	void newTextureCoordinate(float x, float y, float z) {
		TextureCoordinates.add(new GLVector3(new float[] {x, y, z}));
	}
	/**
	 * To get the texture coordinate data
	 * @param Index The position of the texture coordinate in the mesh
	 * @return return the vector contains texture coordinate data
	 */
	public GLVector3 getTextureCoordinate(int Index) {
		return TextureCoordinates.get(Index);
	}
	/**
	 * The number of elements in this texture coordinate data.
	 * @return Returns the number of elements in this texture coordinate data.
	 */
	public int getTextureCoordinateSize() {
		return TextureCoordinates.size();
	}
	
	/**
	 * Set up a new normal data in the mesh, new data will be placed at the end of the array.
	 * @param x X component of a vector
	 * @param y Y component of a vector
	 * @param z Z component of a vector
	 */
	void newNormal(float x, float y, float z) {
		Normals.add(new GLVector3(new float[] {x, y, z}));
	}
	/**
	 * To get the normal data
	 * @param Index The position of the normal in the mesh
	 * @return return the vector contains normal data
	 */
	public GLVector3 getNormal(int Index) {
		return Normals.get(Index);
	}
	/**
	 * The number of elements in this normal data.
	 * @return Returns the number of elements in this normal data.
	 */
	public int getNormalSize() {
		return Normals.size();
	}
	
	/**
	 * Set up a new index data in the mesh, new data will be placed at the end of the array.
	 * @param Index The new added face
	 */
	void newFace(Face IndexData) {
		Indices.add(IndexData);
	}
	/**
	 * To get the face data
	 * @param Index The position of the index in the mesh
	 * @return The vector contains face data
	 */
	public Face getFace(int Index) {
		return Indices.get(Index);
	}
	/**
	 * To Get the number of elements in this face data.
	 * @return Returns the number of elements in this face data.
	 */
	public int getFaceSize() {
		return Indices.size();
	}
	
	/**
	 * Setting the name of the mesh
	 * @param name The name of the mesh
	 */
	void setMeshName(String name) {
		Name = name;
	}
	/**
	 * Get the name of this mesh
	 * @return the name of this mesh
	 */
	public String getMeshName() {
		return Name;
	}
	
	/**
	 * The material which is used by this mesh. It only reads the name of the material class but not actually loading it.
	 * @param MtlName The given name of one material class
	 */
	void setMtl(String MtlName) {
		Mtl = MtlName;
	}
	/**
	 * The material which is used by this mesh. It only reads the name of the material class but not actually loading it.
	 * @return The given name of one material class
	 */
	public String getMtl() {
		return Mtl;
	}
	
	/**
	 * Calculate the tangent lines in each triangle using UV coordinates and vertices. Be aware that tangents will
	 * only be calculated when indices are eliminated when importing the mesh.
	 * @param Recalculate If the tangents have been calculated before and the value is true, all tangent lines
	 * will be cleared. If the tangents have not been calculated then the parameter is ignored.
	 * @param bias The calculation involves inverting a 2x2 matrix. If the matrix is singular then the U-coordinate will
	 *  be shift by the magnitude of the bias value. Bias should not be zero but very close to zero.
	 * @return True if tangents are calculated. If the face data has not been eliminated then will return false;
	 */
	public boolean calculateTangents(boolean Recalculate, float bias) {
		if((Tangents.size() == 0 || Tangents.size() != 0 && Recalculate) && Index_Eliminated && bias != 0.0f) {
			//Reset
			Tangents.clear();
			Tangent_error_Mes = "No Error Found";
			SimpleMaths.errors = 0;
			
			for(int i = 0; i < Indices.size(); i++) {
				//Calculate edges
				GLVector3 edge1 = SimpleMaths.subVector3(Vertices.get(3 * i + 1), Vertices.get(3 * i));
				GLVector3 edge2 = SimpleMaths.subVector3(Vertices.get(3 * i + 2), Vertices.get(3 * i));
				float[] Edge_mat = {
						edge1.getComponentX(), edge1.getComponentY(), edge1.getComponentZ(),
						edge2.getComponentX(), edge2.getComponentY(), edge2.getComponentZ()
				};
				//calculate delta UVs
				float[] UV_mat;
				float bias_accumulate = 0.0f;
				do {
					GLVector3 deltaUV1 = SimpleMaths.subVector3(TextureCoordinates.get(3 * i + 1), TextureCoordinates.get(3 * i));
					GLVector3 deltaUV2 = SimpleMaths.subVector3(TextureCoordinates.get(3 * i + 2), TextureCoordinates.get(3 * i));
					UV_mat = new float[]{
							deltaUV1.getComponentX() + bias_accumulate, deltaUV1.getComponentY() + bias_accumulate,
							deltaUV2.getComponentX() + bias_accumulate, deltaUV2.getComponentY() + bias_accumulate,
					};
					
					UV_mat = SimpleMaths.Inverse_Mat2x2(UV_mat);
					bias_accumulate += bias;
				}while(UV_mat == null);//singular
				//Final calculation
				float[] Tangent_Bitangent = SimpleMaths.Mat2x2multiMat3x2(UV_mat, Edge_mat);
				GLVector3 Tangent = new GLVector3(new float[]{Tangent_Bitangent[0], Tangent_Bitangent[1], Tangent_Bitangent[2]});
				Tangent = SimpleMaths.normaliseVector3(Tangent);
				//add to tangent
				for(int j = 0; j < 3; j++)
					Tangents.add(Tangent);//one face shares the same tangent lines
			}
			if(SimpleMaths.errors != 0) {
				Tangent_error_Mes = Integer.toString(SimpleMaths.errors) + " Singular Matrices Found";
			}
			return true;
		}else {
			return false;
		}
	}
	
	/**
	 * Get the error message after calling {@link #calculateTangents(boolean, float)} during post-processing
	 * @return Error message
	 */
	public String getTangentError() {
		return Tangent_error_Mes;
	}
	
	/**
	 * To get the tangent data. Tangents are calculated after the import. User must call method {@link #calculateTangents(boolean,float)}
	 * before calling this method.
	 * @param Index  The position of the tangent in the mesh
	 * @return The vector of the tangent
	 * @throws GLModelLoadingException Thrown if user did not call the specific method before retrieving tangents
	 */
	public GLVector3 getTangent(int Index) throws GLModelLoadingException{
		if(Tangents.size() == 0) {
			Throwable th = new Throwable("NullPointerException");
			throw new GLModelLoadingException("No Tangent Data Exist", th, th.getStackTrace(), "NullPointerException");
		}else {
			return Tangents.get(Index);
		}
	}
	
	/**
	 * Return the number of tangent lines
	 * @return The number of tangents
	 */
	public int getTangentSize() {
		return Tangents.size();
	}
	
}
