package glModelLoader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

/**
 * 
 * GLModelImporter is a open source obj model importer and it is fully supportive to java opengl libaray.
 * @author Haoqian Stephen Xu
 *
 */
final class GLModelImporter implements GLModelLoader{
	//Variables
	private BufferedReader objreader = null;
	private BufferedReader mtlreader = null;
	private String Copyright = "N/A";
	private String mtl_lib = null;
	/**
	 * <html/>
	 * Definition of each element:</br>
	 * 0: FLIP_UV</br>
	 * 1: TRIANGULATE</br>
	 * 2: INDEX_CORRECTION</br>
	 * 3: INDEX_ELIMINATION
	 * </html>
	 * 
	 */
	private boolean[] post_process_state = new boolean[] {false, false, false, false};
	private boolean MtlLoaded = false;
	private int behaviour_index = 20001;
	
	//constant
	private static final String[] Data_Instrctor = {"v  ", "vt ", "vn ", "f ", "mtllib ", "usemtl ", "v "};
	private static final String[] Mtl_Instrctor = {"newmtl", "Ns", "d", "Tr", "Tf", "Ni", "Ka",
			"Kd", "Ks", "Ke", "map_Ka", "map_Kd", "map_Ks", "illum ", "map_Bump",
			"disp", "decal", "bump", "map_Ns", "map_d"};
	
	/**
	 * Initialisation of the ObjLoader
	 * @param ObjPath Specify the location of the obj file
	 * @throws GLModelLoadingException - If file does not exist or for some reason it cannot be read or the file is not a obj file.
	 */
	GLModelImporter(File ObjPath) throws GLModelLoadingException{
		//Variable
		final String Filename = ObjPath.getName();
		try {
			if(!Filename.endsWith(".obj")) {//It is not a obj file
				Throwable th = new Throwable("GLModelLoadingException");
				throw new GLModelLoadingException("Not A Obj File", th, th.getStackTrace(), "GLModelLoadingException");
			}else {
				objreader = new BufferedReader(new FileReader(ObjPath));
				MtlLoaded = false;
			}
		}catch(FileNotFoundException fne) {
			throw new GLModelLoadingException(fne.getMessage(), fne.getCause(), fne.getStackTrace(), "FileNotFoundException");
		}
	}
	/**
	 * Initialisation of the ObjLoader
	 * @param ObjPath Specify the location of the obj file
	 * @param MtlPath Specify the location of the mtl file
	 * @throws GLModelLoadingException If file does not exist or for some reason it cannot be read or the file is not a obj or mtl file.
	 */
	GLModelImporter(File ObjPath, File MtlPath) throws GLModelLoadingException{
		//Variables
		final String ObjName = ObjPath.getName();
		final String MtlName = MtlPath.getName();
		try {
			if(!ObjName.endsWith(".obj")) {
				Throwable th = new Throwable("GLModelLoadingException");
				throw new GLModelLoadingException("Not A Obj File", th, th.getStackTrace(), "GLModelLoadingException");
			}else if(!MtlName.endsWith(".mtl")) {
				Throwable th = new Throwable("GLModelLoadingException");
				throw new GLModelLoadingException("Not A Mtl File", th, th.getStackTrace(), "GLModelLoadingException");
				
			}else {
				objreader = new BufferedReader(new FileReader(ObjPath));
				mtlreader = new BufferedReader(new FileReader(MtlPath));
				MtlLoaded = true;
			}
		}catch(FileNotFoundException fne) {
			throw new GLModelLoadingException(fne.getMessage(), fne.getCause(), fne.getStackTrace(), "FileNotFoundException");
		}
	}
	
	@Override
	public Mesh[] ImportWaveFrontObj() throws GLModelLoadingException{
		//Variables
		String line = "";
		ArrayList<Mesh> object = new ArrayList<Mesh>(0);
		String[] vector = new String[3];
		String[] raw_face = null;
		float[][] indices = new float[3][3];//{{X_ver, X_tex, X_nor},{Y_ver,....},{}}
		GLVector3[] faces = new GLVector3[3];
		Mesh[] returnValue = null;
		int[] size = {0,0,0};
		
		int CurrentMesh = -1;//as a index for mesh[]
		try {
			line = objreader.readLine();
			Copyright = line.substring(2);//the first line always contains copyright info
			while(line != null) {//It means there are unread lines
				if(line.isEmpty()) {
					
				}else if(line.startsWith(Data_Instrctor[4])) {//material library
					mtl_lib = line.substring(7);
					
				}else if(line.startsWith("# object ")||line.startsWith("o ")) {//a new MESH found!!!
					object.add(new Mesh());
					CurrentMesh++;
					//Different exporter behaviours
					if(line.startsWith("# object ")) {
						object.get(CurrentMesh).setMeshName(line.substring(9));
					}else if(line.startsWith("o ")) {
						object.get(CurrentMesh).setMeshName(line.substring(2));
					}
					object.get(CurrentMesh).Index_Eliminated = post_process_state[3];
				}else if(line.startsWith(Data_Instrctor[0])||line.startsWith(Data_Instrctor[6])) {//vertex data
					if(line.startsWith(Data_Instrctor[0])){
						vector = getVector3(line, 3);
						object.get(CurrentMesh).newVertex(Float.parseFloat(vector[0]), Float.parseFloat(vector[1]), Float.parseFloat(vector[2]));
					}else {
						vector = getVector3(line, 2);
						object.get(CurrentMesh).newVertex(Float.parseFloat(vector[0]), Float.parseFloat(vector[1]), Float.parseFloat(vector[2]));
					}
					
				}else if(line.startsWith(Data_Instrctor[1])) {//texture coordinate
					vector = getVector3(line, 3);
					if(post_process_state[0]) {//flip UV
						if(vector.length == 3) {//if it has uvw coordinate
							object.get(CurrentMesh).newTextureCoordinate(Float.parseFloat(vector[1]), Float.parseFloat(vector[0]), Float.parseFloat(vector[2]));
						}else {
							object.get(CurrentMesh).newTextureCoordinate(Float.parseFloat(vector[1]), Float.parseFloat(vector[0]), 0.0f);
						}
					}else {
						if(vector.length == 3) {
							object.get(CurrentMesh).newTextureCoordinate(Float.parseFloat(vector[0]), Float.parseFloat(vector[1]), Float.parseFloat(vector[2]));
						}else {
							object.get(CurrentMesh).newTextureCoordinate(Float.parseFloat(vector[0]), Float.parseFloat(vector[1]), 0.0f);
						}
					}
					
				}else if(line.startsWith(Data_Instrctor[2])) {//normal
					vector = getVector3(line, 3);
					object.get(CurrentMesh).newNormal(Float.parseFloat(vector[0]), Float.parseFloat(vector[1]), Float.parseFloat(vector[2]));
					
				}else if(line.startsWith(Data_Instrctor[3])) {//face
					vector = getVector3(line, 2);
					for(int i = 0; i <= vector.length - 1; i++) {
						raw_face = vector[i].split("/");
						indices[i][0] = Float.parseFloat(raw_face[0]);
						if(raw_face.length == 2) {//only contains vertex and texture coordinate index
							indices[i][1] = Float.parseFloat(raw_face[1]);
							indices[i][2] = 0.0f;
						}else if(raw_face.length == 3) {
							if(raw_face[1].isEmpty()) {//means there is no texture coordinate
								indices[i][1] = 0.0f;
							}else {
								indices[i][1] = Float.parseFloat(raw_face[1]);
							}
							indices[i][2] = Float.parseFloat(raw_face[2]);
						}
					}
					
					if(post_process_state[2]) {//index correction
						//Different Behaviours of exporter
						if(behaviour_index == START_FROM_NEGATIVE_NUM_OF_ELEMENT) {
							size[0] = object.get(CurrentMesh).getVertexSize();
							size[1] = object.get(CurrentMesh).getTextureCoordinateSize();
							size[2] = object.get(CurrentMesh).getNormalSize();
							
							faces[0] = new GLVector3(new float[] {indices[0][0] + size[0], indices[1][0] + size[0], indices[2][0] + size[0]});//vertex
							faces[1] = new GLVector3(new float[] {indices[0][1] + size[1], indices[1][1] + size[1], indices[2][1] + size[1]});//texture coordinate
							faces[2] = new GLVector3(new float[] {indices[0][2] + size[2], indices[1][2] + size[2], indices[2][2] + size[2]});//normal
						}else if(behaviour_index == START_FROM_ONE) {
							faces[0] = new GLVector3(new float[] {indices[0][0] - 1, indices[1][0] - 1, indices[2][0] - 1});//vertex
							faces[1] = new GLVector3(new float[] {indices[0][1] - 1, indices[1][1] - 1, indices[2][1] - 1});//texture coordinate
							faces[2] = new GLVector3(new float[] {indices[0][2] - 1, indices[1][2] - 1, indices[2][2] - 1});//normal
						}
					}else if(post_process_state[2] == false||behaviour_index == START_FROM_ZERO){
						faces[0] = new GLVector3(new float[] {indices[0][0], indices[1][0], indices[2][0]});//vertex
						faces[1] = new GLVector3(new float[] {indices[0][1], indices[1][1], indices[2][1]});//texture coordinate
						faces[2] = new GLVector3(new float[] {indices[0][2], indices[1][2], indices[2][2]});//normal
					}
					object.get(CurrentMesh).newFace(new Face(faces[0], faces[1], faces[2]));
					
				}else if(line.startsWith(Data_Instrctor[5])) {//using material
					object.get(CurrentMesh).setMtl(line.substring(7));
				}
				line = objreader.readLine();
			}
			
			returnValue = PostProcess_EliminateIndex(post_process_state[3], object);
			return returnValue;
			
		}catch(IOException ioe) {
			throw new GLModelLoadingException(ioe.getMessage(), ioe.getCause(), ioe.getStackTrace(), "IOException");
		}catch(NumberFormatException ne) {//if this exception throw, that probably means the file is not a obj file or it is not triangulated.
			throw new GLModelLoadingException("File Cannot Be Input(Non-Standard Obj File)", ne.getCause(), ne.getStackTrace(), "GLModelLoadingException");
		}catch(ArrayIndexOutOfBoundsException iobe) {
			throw new GLModelLoadingException("File Cannot Be Input(Non-Standard Obj File)", iobe.getCause(), iobe.getStackTrace(), "GLModelLoadingException");
		}
	}
	
	@Override
	public Material[] ImportWaveFrontMtl() throws GLModelLoadingException{
		//Variables
		String line = "";
		String[] part = null;
		ArrayList<Material> object = new ArrayList<Material>(0);
		float[] vector = new float[3];
		
		int CurrentMtl = -1;
		try {
			line = mtlreader.readLine();
			//no need to read the copyright info again
			while(line != null) {
				part = line.trim().split("\\s+");//mtl library contains empty space
				Material.Map map = null;
				if(part[0].equals(Mtl_Instrctor[0])) {//A new MATERIAL FOUND!!!
					object.add(new Material());
					CurrentMtl++;
					
					object.get(CurrentMtl).setMtlName(part[1]);
				}else if(part[0].equals(Mtl_Instrctor[1])) {//specular exponent(shininess)
					object.get(CurrentMtl).setNs(Float.parseFloat(part[1]));
					
				}else if(part[0].equals(Mtl_Instrctor[2])) {//dissolved
					object.get(CurrentMtl).setd(Float.parseFloat(part[1]));
					
				}else if(part[0].equals(Mtl_Instrctor[3])) {//Inverted dissolved
					object.get(CurrentMtl).setTr(Float.parseFloat(part[1]));
					
				}else if(part[0].equals(Mtl_Instrctor[4])) {//transmission filter
					vector = getColor3(part, 1);
					object.get(CurrentMtl).setTf(vector[0], vector[1], vector[2]);
					
				}else if(part[0].equals(Mtl_Instrctor[5])) {//refractive index
					object.get(CurrentMtl).setNi(Float.parseFloat(part[1]));
					
				}else if(part[0].equals(Mtl_Instrctor[6])) {//ambient color
					object.get(CurrentMtl).setK_Para(Material.GLMaterialDataType.Ambient, getColor3(part, 1));
					
				}else if(part[0].equals(Mtl_Instrctor[7])) {//diffuse color
					object.get(CurrentMtl).setK_Para(Material.GLMaterialDataType.Diffuse, getColor3(part, 1));
					
				}else if(part[0].equals(Mtl_Instrctor[8])) {//specular color
					object.get(CurrentMtl).setK_Para(Material.GLMaterialDataType.Specular, getColor3(part, 1));
					
				}else if(part[0].equals(Mtl_Instrctor[9])) {//Emissive coeficient
					object.get(CurrentMtl).setK_Para(Material.GLMaterialDataType.Emissive_Coeficient, getColor3(part, 1));
					
				}else if(part[0].equals(Mtl_Instrctor[10])) {//ambient map
					map = object.get(CurrentMtl).new Map(Material.GLMaterialDataType.Ambient, part[part.length - 1]);
					uploadMap(map, part);
					object.get(CurrentMtl).Ambient_Map = map;
					 
				}else if(part[0].equals(Mtl_Instrctor[11])) {//diffuse map
					map = object.get(CurrentMtl).new Map(Material.GLMaterialDataType.Diffuse, part[part.length - 1]);
					uploadMap(map, part);
					object.get(CurrentMtl).Diffuse_Map = map;
					
				}else if(part[0].equals(Mtl_Instrctor[12])) {//specular map
					map = object.get(CurrentMtl).new Map(Material.GLMaterialDataType.Specular, part[part.length - 1]);
					uploadMap(map, part);
					object.get(CurrentMtl).Specular_Map = map;
					
				}else if(part[0].equals(Mtl_Instrctor[13])) {//illumination enum
					object.get(CurrentMtl).setIllum(Integer.parseInt(part[1]));
					
				}else if(part[0].equals(Mtl_Instrctor[14]) || part[0].equals(Mtl_Instrctor[17])) {//normal map/bump map
					map = object.get(CurrentMtl).new Map(Material.GLMaterialDataType.Normal, part[part.length - 1]);
					uploadMap(map, part);
					object.get(CurrentMtl).Normal_Map = map;
					
				}else if(part[0].equals(Mtl_Instrctor[15])) {//displacement
					map = object.get(CurrentMtl).new Map(Material.GLMaterialDataType.Displacement, part[part.length - 1]);
					uploadMap(map, part);
					object.get(CurrentMtl).Displacement_Map = map;
					
				}else if(part[0].equals(Mtl_Instrctor[16])) {//decal
					map = object.get(CurrentMtl).new Map(Material.GLMaterialDataType.Decal, part[part.length - 1]);
					uploadMap(map, part);
					object.get(CurrentMtl).Decal_Map = map;
					
				}
				line = mtlreader.readLine();
			}
		}catch(IOException ioe) {
			throw new GLModelLoadingException(ioe.getMessage(), ioe.getCause(), ioe.getStackTrace(), "IOException");
		}catch(NumberFormatException ne) {
			throw new GLModelLoadingException("File Cannot Be Input(Non-Standard Mtl File)", ne.getCause(), ne.getStackTrace(), "GLModelLoadingException");
		}catch(ArrayIndexOutOfBoundsException iobe) {
			throw new GLModelLoadingException("File Cannot Be Input(Non-Standard Mtl File)", iobe.getCause(), iobe.getStackTrace(), "GLModelLoadingException");
		}catch(IndexOutOfBoundsException iob) {
			throw new GLModelLoadingException("File Cannot Be Input(Non-Standard Mtl File)", iob.getCause(), iob.getStackTrace(), "GLModelLoadingException");
		}
		
		return object.toArray(new Material[0]);
	}
	
	@Override
	public void setObjPostProcessing(GLPostProcessingType Type, boolean Enabled) {
		switch(Type) {
		case FLIP_UV : post_process_state[0] = Enabled;
		break;
		case TRIANGULATE : post_process_state[1] = Enabled;
		break;
		case INDEX_CORRECTION : post_process_state[2] = Enabled;
		break;
		case INDEX_ELIMINATION : post_process_state[3] = Enabled;
		break;
		default:
			break;
		}
	}
	
	@Override
	public void setIndexBehaviour(int IndexMode) throws GLModelLoadingException{
		switch(IndexMode) {
		case START_FROM_ZERO : behaviour_index = START_FROM_ZERO;
		break;
		case START_FROM_ONE : behaviour_index = START_FROM_ONE;
		break;
		case START_FROM_NEGATIVE_NUM_OF_ELEMENT : behaviour_index = START_FROM_NEGATIVE_NUM_OF_ELEMENT;
		break;
		default:
			StackTraceElement ste = new StackTraceElement("GLObjLoader", "GLObjLoader", null, -1);
			Throwable th = new Throwable();
			throw new GLModelLoadingException("Wrong Enum Is Given", th, new StackTraceElement[] {ste}, "GLModelLoadingException");
			
		}
	}
	
	/**
	 *  Convert a line which contains vector data to a String array
	 * @param Line The line contains vector data
	 */
	private final String[] getVector3(String Line, int offset) {
		return Line.substring(offset).split(" ", 3);//To eliminate the beginning of the line such as "v  "
	}
	
	/**
	 * Convert a array which contains RGB value info to a float array
	 * @param ValuePart a lie contains RGB value
	 * @param offset Index to start with
	 * @return
	 */
	private final float[] getColor3(String[] ValuePart, int offset) {
		return new float[] {Float.parseFloat(ValuePart[offset]), Float.parseFloat(ValuePart[1 + offset]), Float.parseFloat(ValuePart[2 + offset])};
	}
	
	/**
	 * Part of post processing operation
	 * @param Enabled
	 * @param Raw_data The mesh array which needs to be eliminated index
	 * @return Mesh array which no longer contains index and all elements have been repeated according to indices
	 * @throws IndexOutOfBoundsException
	 */
	private Mesh[] PostProcess_EliminateIndex(boolean Enabled, ArrayList<Mesh> Raw_data) throws IndexOutOfBoundsException{
		try {
		if(Enabled) {
			//Variables
			ArrayList<Mesh> returning = new ArrayList<Mesh>(0);
			int[] face = null;
			GLVector3[] vector = new GLVector3[3];
			
			for(int i = 0; i < Raw_data.size(); i++) {//operate mesh[]
				returning.add(new Mesh());
				//misc
				returning.get(i).setMeshName(Raw_data.get(i).getMeshName());
				returning.get(i).setMtl(Raw_data.get(i).getMtl());
				returning.get(i).Index_Eliminated = true;
				for(int j = 0; j < Raw_data.get(i).getFaceSize(); j++) {//operate face[]
					//vertex
					face = Raw_data.get(i).getFace(j).getIndex(Mesh.GLModelDataType.Vertices);
					returning.get(i).newVertex(Raw_data.get(i).getVertex(face[0]).getComponentX(), Raw_data.get(i).getVertex(face[0]).getComponentY(), Raw_data.get(i).getVertex(face[0]).getComponentZ());
					returning.get(i).newVertex(Raw_data.get(i).getVertex(face[1]).getComponentX(), Raw_data.get(i).getVertex(face[1]).getComponentY(), Raw_data.get(i).getVertex(face[1]).getComponentZ());
					returning.get(i).newVertex(Raw_data.get(i).getVertex(face[2]).getComponentX(), Raw_data.get(i).getVertex(face[2]).getComponentY(), Raw_data.get(i).getVertex(face[2]).getComponentZ());
					//texture
					if(Raw_data.get(i).getTextureCoordinateSize() != 0) {//There are Texture Coordinates in this model
						face = Raw_data.get(i).getFace(j).getIndex(Mesh.GLModelDataType.TextureCoordinates);
						returning.get(i).newTextureCoordinate(Raw_data.get(i).getTextureCoordinate(face[0]).getComponentX(), Raw_data.get(i).getTextureCoordinate(face[0]).getComponentY(), Raw_data.get(i).getTextureCoordinate(face[0]).getComponentZ());
						returning.get(i).newTextureCoordinate(Raw_data.get(i).getTextureCoordinate(face[1]).getComponentX(), Raw_data.get(i).getTextureCoordinate(face[1]).getComponentY(), Raw_data.get(i).getTextureCoordinate(face[1]).getComponentZ());
						returning.get(i).newTextureCoordinate(Raw_data.get(i).getTextureCoordinate(face[2]).getComponentX(), Raw_data.get(i).getTextureCoordinate(face[2]).getComponentY(), Raw_data.get(i).getTextureCoordinate(face[2]).getComponentZ());
					}
					//normal
					if(Raw_data.get(i).getNormalSize() != 0) {//There are normals in this model
						face = Raw_data.get(i).getFace(j).getIndex(Mesh.GLModelDataType.Normals);
						returning.get(i).newNormal(Raw_data.get(i).getNormal(face[0]).getComponentX(), Raw_data.get(i).getNormal(face[0]).getComponentY(), Raw_data.get(i).getNormal(face[0]).getComponentZ());
						returning.get(i).newNormal(Raw_data.get(i).getNormal(face[1]).getComponentX(), Raw_data.get(i).getNormal(face[1]).getComponentY(), Raw_data.get(i).getNormal(face[1]).getComponentZ());
						returning.get(i).newNormal(Raw_data.get(i).getNormal(face[2]).getComponentX(), Raw_data.get(i).getNormal(face[2]).getComponentY(), Raw_data.get(i).getNormal(face[2]).getComponentZ());
					}
					//index
					vector[0] = new GLVector3(new float[] {3 * j, 3 * j + 1, 3 * j + 2});
					if(Raw_data.get(i).getTextureCoordinateSize() == 0) {
						vector[1] = new GLVector3(new float[] {0.0f, 0.0f, 0.0f});
					}else {
						vector[1] = new GLVector3(new float[] {3 * j, 3 * j + 1, 3 * j + 2});
					}
					if(Raw_data.get(i).getNormalSize()== 0) {
						vector[2] = new GLVector3(new float[] {0.0f, 0.0f, 0.0f});
					}else {
						vector[2] = new GLVector3(new float[] {3 * j, 3 * j + 1, 3 * j + 2});
					}
					
					returning.get(i).newFace(new Face(vector[0], vector[1], vector[2]));
				}
			}
			
			return returning.toArray(new Mesh[0]);
		}else {
			//Do not change anything
			return Raw_data.toArray(new Mesh[0]);
		}
	}catch(IndexOutOfBoundsException e) {
		throw e;
	}
	}
	
	/**
	 * Find the map parameter from the line which contains the data we want
	 * @param Line_data The line contains map parameter
	 * @param Ins_index The instructor we want to be used to locate
	 * @return the index which the instructor first appears within the line data. If the 
	 * Instructor does not exist, -1 will be returned
	 */
	private int getMapsetting(String[] Line_data, int Ins_index) {
		//Different types of setting
		final String[] Setting_Instructor = {"-o", "-s", "-t", "-bm", "-mm",
				"-clamp", "-blendu", "-blendv", "-imfchan", "-texres", "-cc"};
		final int length_line = Line_data.length;
		//search for corresponded instructor
		//keeping finding until we get the instructor we want
		int position = -1;
		do {
			position ++;
			if(position > length_line - 1) {
				position = -1;//Instructor not found
				break;//index out of bound
			}
		}while(!Line_data[position].equals(Setting_Instructor[Ins_index]));
		
		return position;
	}
	
	private void uploadMap(Material.Map map, String[] Line) throws GLModelLoadingException{
		int index = 0;
		float[] vector = new float[3];
		boolean[] blend = {false, false};
		
		try {
			for(int i = 0; i < 10; i++) {
				index = getMapsetting(Line, i);
				if(index == -1) {
					continue;
				}
				switch(i) {
				case 0: //-o
				vector[0] = Float.parseFloat(Line[index + 1]);
				vector[1] = Float.parseFloat(Line[index + 2]);
				vector[2] = Float.parseFloat(Line[index + 3]);
				map.setO(vector[0], vector[1], vector[2]);
				break;
				case 1: //-s
				vector[0] = Float.parseFloat(Line[index + 1]);
				vector[1] = Float.parseFloat(Line[index + 2]);
				vector[2] = Float.parseFloat(Line[index + 3]);
				map.setS(vector[0], vector[1], vector[2]);
				break;
				case 2: //-t
				vector[0] = Float.parseFloat(Line[index + 1]);
				vector[1] = Float.parseFloat(Line[index + 2]);
				vector[2] = Float.parseFloat(Line[index + 3]);
				map.setT(vector[0], vector[1], vector[2]);
				break;
				case 3: //-bm
				map.setBm(Float.parseFloat(Line[index + 1]));
				break;
				case 4: //-mm
				map.setmm(Float.parseFloat(Line[index + 1]), Float.parseFloat(Line[index + 2]));
				break;
				case 5: //-clamp
				map.setClamp(Material.toSwitch(Line[index + 1]));
				break;
				case 6: //-blendu
				blend[0] = Material.toSwitch(Line[index + 1]);
				break;
				case 7: //-blendv
				blend[1] = Material.toSwitch(Line[index + 1]);
				break;
				case 8: //-imfchan
				map.setImfchan(Material.toChannel(Line[index + 1]));
				break;
				case 9: //-texres
				map.setTexres(Integer.parseInt(Line[index + 1]));
				break;
				case 10: //-cc
				map.setcc(Material.toSwitch(Line[index + 1]));
				break;
				default:
					break;
				}
			}
			map.setBlend(blend[0], blend[1]);
		}catch(GLModelLoadingException e) {
			throw e;
		}catch(NumberFormatException ne) {
			throw new GLModelLoadingException("Illegal Mtl Format", ne.getCause(), ne.getStackTrace(), "NumberFormatException");
		}
	}
	
	@Override
	public String getCopyrightInfo(){
		return Copyright;
	}
	
	@Override
	public String getMtl() {
		return mtl_lib;
	}
	
	@Override
	public void dump() throws GLModelLoadingException{
		try {
			objreader.close();
			if(MtlLoaded) {
				mtlreader.close();
			}
		}catch(IOException ioe) {
			throw new GLModelLoadingException(ioe.getMessage(), ioe.getCause(), ioe.getStackTrace(), "IOException");
		}catch(Throwable th) {
			throw new GLModelLoadingException(th.getMessage(), th.getCause(), th.getStackTrace(), "Throwable");
		}
	}
}