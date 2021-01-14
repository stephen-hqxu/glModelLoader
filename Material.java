package glModelLoader;

/**
 * A material is associated with obj file, usually ends with .mtl.
 * Material data can be obtained and returned by GLObjLoader.
 * @author Haoqian Stephen Xu
 *
 */
public final class Material {
	//Variables
	private String Name = "";
	private float specular_exponent = 0.0f;
	private float dissolved  = 0.0f;
	private float Inverted_d = 1.0f;
	private GLVector3 transmission_filter = null;
	private float refraction_index = 1.0f;
	private GLVector3 ambient = null;
	private GLVector3 diffuse = null;
	private GLVector3 specular = null;
	private GLVector3 emissive_coeficient = null;
	private int Illumination_enum = 0;
	//Map, the class Map is read-only so we can pass all variables to the user
	public Map Ambient_Map = null;
	public Map Diffuse_Map = null;
	public Map Specular_Map = null;
	public Map Normal_Map = null;
	public Map Displacement_Map = null;
	public Map Decal_Map = null;
	public Map Reflection_Map = null;
	
	/**
	 * Specify the type of information about the material file
	 * @author Haoqian Stephen Xu
	 *
	 */
	public static enum GLMaterialDataType {
		/**
		 * Defined as Ka
		 */
		Ambient,
		/**
		 * Defined as Kd
		 */
		Diffuse,
		/**
		 * Defined as Ks
		 */
		Specular,
		/**
		 * It does not go with K, Only use in bump map parameter
		 */
		Normal,
		/**
		 * Defined as disp
		 */
		Displacement,
		/**
		 * Defined as decal
		 */
		Decal,
		/**
		 * Defined as Ke
		 */
		Emissive_Coeficient,
		/**
		 * Beta Version
		 */
		Reflection;
	}
	
	/**
	 * Map class stores the location of maps and the setting of them
	 * @author Haoqian Stephen Xu
	 *
	 */
	public final class Map{
		//Constant for channel
		public static final int RED = 54000;
		public static final int GREEN = 54001;
		public static final int BLUE = 54002;
		public static final int MATTE = 54003;
		public static final int LUMINACE = 54004;
		public static final int Z_DEPTH = 54005;
		//Setting
		private GLMaterialDataType Type = null;
		private GLVector3 Offset = new GLVector3(new float[] {0.0f, 0.0f, 0.0f});//-o
		private GLVector3 Scale = new GLVector3(new float[] {1.0f, 1.0f, 1.0f});//-s
		private GLVector3 Turbulence = new GLVector3(new float[] {0.0f, 0.0f, 0.0f});//-t
		private boolean Clamping = false;
		private boolean Color_Correction = false;
		private float Bump_multiplier = 1.0f;//-bm
		private int Channel = LUMINACE;//-imfchan
		private Range Texture_Color_Range = null;
		private int Resolution = 2;
		private Blend blending = null;
		/**
		 * Equals to Path
		 */
		private String Location = null;
		
		/**
		 * A class that specify the range of the texture color, it contains base value and gain value. 
		 * Base controls the brightness of the texture; Gain controls the contrast of the texture.
		 * @author Haoqian Stephen Xu
		 *
		 */
		public final class Range{
			private float base = 0.0f;
			private float gain = 1.0f;
			
			/**
			 * Initialise the class
			 * @param Base Specify the value of base
			 * @param Gain Specify the value of gain
			 * @throws GLModelLoadingException base must be less than gain, or otherwise exception will be thrown
			 */
			Range(float Base, float Gain) throws GLModelLoadingException{
				if(base > gain) {
					Throwable th = new Throwable("GLModelLoadingException");
					throw new GLModelLoadingException("Illegal Argument: base must be less than gain", th, th.getStackTrace(), "GLModelLoadingException");
				}else {
					base = Base;
					gain = Gain;
				}
			}
			
			/**
			 * Retrieve the value of base
			 * @return The value of base
			 */
			public final float getBase() {
				return base;
			}
			
			/**
			 * Retrieve the value of gain
			 * @return The value of gain
			 */
			public final float getGain() {
				return gain;
			}
			
		}
		
		/**
		 * A class contains the information of blending
		 * @author Haoqian Stephen Xu
		 *
		 */
		public final class Blend{
			private boolean blendu = false;
			private boolean blendv = false;
			
			/**
			 * Initialise the class
			 * @param U Set the switch of U blending
			 * @param V Set the switch of V blending
			 */
			Blend(boolean U, boolean V){
				blendu = U;
				blendv = V;
			}
			
			/**
			 * Get the switch of U blending
			 * @return the switch of U blending
			 */
			public final boolean getBlendu() {
				return blendu;
			}
			
			/**
			 * Get the switch of V blending
			 * @return the switch of V blending
			 */
			public final boolean getBlendv() {
				return blendv;
			}
			
		}
		
		/**
		 * Initialise the map
		 * @param MapType Type of the map
		 * @param Path The location of the map
		 */
		Map(GLMaterialDataType MapType, String Path){
			Type = MapType;
			Location = Path;
		}
		
		/**
		 * Retrieve the type of the map
		 * @return Type of the map
		 */
		public GLMaterialDataType getMapType() {
			return Type;
		}
		
		/**
		 * Set the value of offset
		 * @param O_U U component of texture coordinate
		 * @param O_V V component of texture coordinate
		 * @param O_W W component of texture coordinate
		 */
		void setO(float O_U, float O_V, float O_W) {
			Offset = new GLVector3(new float[] {O_U, O_V, O_W});
		}
		
		/**
		 * Retrieve the vector contains offset
		 * @return Vector with offset
		 */
		public GLVector3 getO() {
			return Offset;
		}
		
		/**
		 * Set the value of scale
		 * @param S_U U component of scale
		 * @param S_V V component of scale
		 * @param S_W W component of scale
		 */
		void setS(float S_U, float S_V, float S_W) {
			Scale = new GLVector3(new float[] {S_U, S_V, S_W});
		}
		
		/**
		 * Retrieve the vector contains scale
		 * @return Vector with scale
		 */
		public GLVector3 getS() {
			return Scale;
		}
		
		/**
		 * Set the value of turbulence
		 * @param T_U U component of turbulence
		 * @param T_V V component of turbulence
		 * @param T_W W component of turbulence
		 */
		void setT(float T_U, float T_V, float T_W) {
			Turbulence = new GLVector3(new float[] {T_U, T_V, T_W});
		}
		
		/**
		 * Retrieve the vector contains turbulence
		 * @return the vector contains turbulence
		 */
		public GLVector3 getT() {
			return Turbulence;
		}
		
		/**
		 * Set the value of bump multiplier
		 * @param BmValue The value of bump multiplier
		 */
		void setBm(float BmValue) {
			Bump_multiplier = BmValue;
		}
		
		/**
		 * Retrieve the value of bump multiplier
		 * @return The value of bump multiplier
		 */
		public float getBm() {
			return Bump_multiplier;
		}
		
		/**
		 * Set the switch of clamping
		 * @param Switch Control whether the clamp is on or off
		 */
		void setClamp(boolean Switch) {
			Clamping = Switch;
		}
		
		/**
		 * Retrieve the switch of clamping
		 * @return Switch Control whether the clamp is on or off
		 */
		public boolean getClamp() {
			return Clamping;
		}
		
		/**
		 * Set the switch of color correction
		 * @param Switch Switch Control whether the clamp is on or off
		 */
		void setcc(boolean Switch) {
			Color_Correction = Switch;
		}
		
		/**
		 * Retrieve the switch of color correction
		 * @return Switch Control whether the clamp is on or off
		 */
		public boolean getcc() {
			return Color_Correction;
		}
		
		/**
		 * Set the affected channel if the map is used to create bump or scalar
		 * @param channel the affected channel if the map is used to create bump or scalar
		 */
		void setImfchan(int channel) {
			Channel = channel;
		}
		
		/**
		 * Retrieve the affected channel if the map is used to create bump or scalar
		 * @return the affected channel if the map is used to create bump or scalar
		 */
		public int getInfchan() {
			return Channel;
		}
		
		/**
		 * Set the value of texture resolution. If the texture has the size of power of 2 then this parameter 
		 * will be ignored, or otherwise the texture will be rescaled to the nearest resolution^2
		 * @param resolution The value of resolution
		 */
		void setTexres(int resolution) {
			Resolution = resolution;
		}
		
		/**
		 * Retrieve the value of texture resolution. If the texture has the size of power of 2 then this parameter 
		 * will be ignored, or otherwise the texture will be rescaled to the nearest resolution^2
		 * @return The value of resolution
		 */
		public int getTexres() {
			return Resolution;
		}
		
		/**
		 * Set the value of base and gain. Base controls the brightness of the texture;
		 *  Gain controls the contrast of the texture.
		 * @param base The value of base
		 * @param gain The value of gain
		 * @throws GLModelLoadingException base must be less than gain, or otherwise exception will be thrown
		 */
		void setmm(float base, float gain) throws GLModelLoadingException{
			try {
				Texture_Color_Range = new Range(base, gain);
			}catch(GLModelLoadingException e) {
				throw e;
			}
			
		}
		
		/**
		 * Retrieve the value of base and gain. Base controls the brightness of the texture;
		 *  Gain controls the contrast of the texture.
		 * @return The class range which contains both base and gain value
		 */
		public Range getmm() {
			return Texture_Color_Range;
		}
		
		/**
		 * Set the blending switch in U and V direction
		 * @param U Switch in U direction
		 * @param V Switch in V direction
		 */
		void setBlend(boolean U, boolean V) {
			blending = new Blend(U, V);
		}
		
		/**
		 * Retrieve the blending switch in U and V direction
		 * @return The blending switch
		 */
		public Blend getBlend() {
			return blending;
		}
		
		/**
		 * Retrieve the location of the map
		 * @return The location of the map
		 */
		public String getLocation() {
			return Location;
		}
		
	}
	
	/**
	 * Setting the name of the material
	 * @param name The name of the material
	 */
	void setMtlName(String name) {
		Name = name;
	}
	/**
	 * Get the name of the material
	 * @return The name of the material
	 */
	public String getMtlName() {
		return Name;
	}
	
	/**
	 * Setting the value of the specular exponent which is the weight of specular colour, also known as shininess
	 * @param NsValue specular exponent value
	 */
	void setNs(float NsValue) {
		specular_exponent = NsValue;
	}
	/**
	 * Get the value of the specular exponent which is the weight of specular colour, also known as shininess
	 * @return the value of the specular exponent
	 */
	public float getNs() {
		return specular_exponent;
	}
	
	/**
	 * Setting the value of the dissolved which is the opacity of the texture
	 * @param dValue The value of the dissolved
	 */
	void setd(float dValue) {
		dissolved = dValue;
	}
	/**
	 * Get the value of the dissolved which is the opacity of the texture
	 * @return The value of the dissolved
	 */
	public float getd() {
		return dissolved;
	}
	
	/**
	 * Setting the value of the Inverted dissolved which is 1 - dissolved
	 * @param dValue The value of the Inverted dissolved
	 */
	void setTr(float TrValue) {
		Inverted_d = TrValue;
	}
	/**
	 * Get the value of the Inverted dissolved which is 1 - dissolved
	 * @return The value of the Inverted dissolved
	 */
	public float getTr() {
		return Inverted_d;
	}
	
	/**
	 * Setting the value of the transmission filter. It is defined as: Any light passing through the object is filtered by the transmission 
	 * filter, which only allows the specifiec colors to pass through.  For 
	 * example, Tf 0 1 0 allows all the green to pass through and filters out 
	 * all the red and blue.
	 * @param Tf_Red The weight of red color
	 * @param Tf_Green The weight of green color
	 * @param Tf_Blue The weight of blue color
	 */
	void setTf(float Tf_Red, float Tf_Green, float Tf_Blue) {
		transmission_filter = new GLVector3(new float[] {Tf_Red, Tf_Green, Tf_Blue});
	}
	/**
	 * Get the value of the transmission filter. It is defined as: Any light passing through the object is filtered by the transmission 
	 * filter, which only allows the specifiec colors to pass through.  For 
	 * example, Tf 0 1 0 allows all the green to pass through and filters out 
	 * all the red and blue.
	 * @return The GLVector3 contains RGB weight
	 */
	public GLVector3 getTf() {
		return transmission_filter;
	}
	
	/**
	 * Specifies the optical density for the surface.  This is also known as 
	 * index of refraction.
	 * "optical_density" is the value for the optical density.  The values can 
	 * range from 0.001 to 10.  A value of 1.0 means that light does not bend 
	 * as it passes through an object.  Increasing the optical_density 
	 * increases the amount of bending.  Glass has an index of refraction of 
	 * about 1.5.  Values of less than 1.0 produce bizarre results and are not 
	 * recommended.
	 * @param NiValue The value of refraction index
	 */
	void setNi(float NiValue) {
		refraction_index = NiValue;
	}
	/**
	 * Specifies the optical density for the surface.  This is also known as 
	 * index of refraction.
	 * "optical_density" is the value for the optical density.  The values can 
	 * range from 0.001 to 10.  A value of 1.0 means that light does not bend 
	 * as it passes through an object.  Increasing the optical_density 
	 * increases the amount of bending.  Glass has an index of refraction of 
	 * about 1.5.  Values of less than 1.0 produce bizarre results and are not 
	 * recommended.
	 * @return The value of refraction index
	 */
	public float getNi() {
		return refraction_index;
	}
	
	/**
	 * Setting the value for Ka, Kd, Ks and Ke. Ke is rarely seen and it us defined as following:
	 * It goes together with ambient, diffuse and specular and represents the amount of light emitted by the material.
	 * @param Type The type of the basic K material data
	 * @param Value The setting value
	 */
	void setK_Para(GLMaterialDataType Type, float[] Value) {
		switch(Type) {
		case Ambient : ambient = new GLVector3(Value);
		break;
		case Diffuse : diffuse = new GLVector3(Value);
		break;
		case Specular : specular = new GLVector3(Value);
		break;
		case Emissive_Coeficient : emissive_coeficient = new GLVector3(Value);
		break;
		default:
			break;
		}
	}
	/**
	 * Returning the value for Ka, Kd, Ks and Ke. Ke(Emissive Coeficient) is rarely seen and it us defined as following:
	 * It goes together with ambient, diffuse and specular and represents the amount of light emitted by the material.
	 * @param Type The type of the basic K material data
	 * @return The requested value
	 */
	public GLVector3 getK_Para(GLMaterialDataType Type) {
		GLVector3 returnValue = null;
		switch(Type) {
		case Ambient : returnValue = ambient;
		break;
		case Diffuse : returnValue = diffuse;
		break;
		case Specular : returnValue = specular;
		break;
		case Emissive_Coeficient : returnValue = emissive_coeficient;
		break;
		default:
			break;
		}
		return returnValue;
	}
	
	/**
	 * <html/>
	 * The "illum" statement specifies the illumination model to use in the 
	 * material.  Illumination models are mathematical equations that represent 
	 * various material lighting and shading effects.</br>
	 * </br>
	 * Illumination  //	  Properties that are turned on in the </br>
 	 * model      //	  Property Editor</br>
 	 *
 	 * 0		Color on and Ambient off</br>
 	 * 1		Color on and Ambient on</br>
 	 * 2		Highlight on</br>
 	 * 3		Reflection on and Ray trace on</br>
 	 * 4		Transparency: Glass on
 	 *	Reflection: Ray trace on</br>
 	 * 5		Reflection: Fresnel on and Ray trace on</br>
 	 * 6		Transparency: Refraction on
 	 * 	Reflection: Fresnel off and Ray trace on</br>
 	 * 7		Transparency: Refraction on
 	 * 	Reflection: Fresnel on and Ray trace on</br>
 	 * 8		Reflection on and Ray trace off</br>
 	 * 9		Transparency: Glass on
 	 * 	Reflection: Ray trace off</br>
 	 * 10		Casts shadows onto invisible surfaces
 	 * </html>
	 * 
	 * @param Value Set the value of illumination enum
	 */
	void setIllum(int Value) {
		Illumination_enum = Value;
	}
	/**
	 * <html/>
	 * The "illum" statement specifies the illumination model to use in the 
	 * material.  Illumination models are mathematical equations that represent 
	 * various material lighting and shading effects.</br>
	 * </br>
	 * Illumination  //	  Properties that are turned on in the </br>
 	 * model      //	  Property Editor</br>
 	 *
 	 * 0		Color on and Ambient off</br>
 	 * 1		Color on and Ambient on</br>
 	 * 2		Highlight on</br>
 	 * 3		Reflection on and Ray trace on</br>
 	 * 4		Transparency: Glass on
 	 *	Reflection: Ray trace on</br>
 	 * 5		Reflection: Fresnel on and Ray trace on</br>
 	 * 6		Transparency: Refraction on
 	 * 	Reflection: Fresnel off and Ray trace on</br>
 	 * 7		Transparency: Refraction on
 	 * 	Reflection: Fresnel on and Ray trace on</br>
 	 * 8		Reflection on and Ray trace off</br>
 	 * 9		Transparency: Glass on
 	 * 	Reflection: Ray trace off</br>
 	 * 10		Casts shadows onto invisible surfaces
 	 * </html>
	 * @return The value of illumination enum
	 */
	public int getIllum() {
		return Illumination_enum;
	}
	
	/**
	 * Convert switch on to true, off or otherwise to false
	 * @param input The input data
	 * @return Boolean switch
	 */
	protected static final boolean toSwitch(String input) {
		if(input == "on") {
			return true;
		}else {
			return false;
		}
	}
	
	/**
	 * Get the channel enum from the input
	 * @param input The input value contains the channel information
	 * @return The channel enum
	 */
	protected static final int toChannel(String input) {
		int re = -1;
		switch(input) {
		case "r": re = Material.Map.RED;
		break;
		case "g": re = Material.Map.GREEN;
		break;
		case "b": re = Material.Map.BLUE;
		break;
		case "m": re = Material.Map.MATTE;
		break;
		case "l": re = Material.Map.LUMINACE;
		break;
		case "z": re = Material.Map.Z_DEPTH;
		break;
		default:
			re = -1;
			break;
		}
		return re;
	}
	
	/**
	 * In the GLObjLoader package, Mesh classes record the name of the material classes. But the Material classes only
	 * record the index of the Material classes by order. By this method, users can get the correspond Material array indices
	 * that are used by each Mesh array elements.
	 * @param checkedMesh The Mesh array that need to be got the indices
	 * @param CorrespondedMtl The Material array that is used by the Mesh
	 * @return The integer array that contains the indices of the material array, corresponded to the Mesh array.
	 * @throws GLModelLoadingException Thrown if the given Material array is not corresponded to the given Mesh array
	 */
	public static int[] GetMeshMatrialIndex(Mesh[] checkedMesh, Material[] CorrespondedMtl) throws GLModelLoadingException{
		final int length_mesh = checkedMesh.length;
		final int length_mtl = CorrespondedMtl.length;
		int[] Result = new int[length_mesh];
		//Finding material
		int mtl = -1;
		String currentCheckedMeshMtl = "null";
		//find the first occurrence
		for(int mesh = 0; mesh < length_mesh; mesh++) {
			mtl = -1;//reset
			currentCheckedMeshMtl = checkedMesh[mesh].getMtl();
			do {
				mtl++;
				if(mtl > length_mtl - 1) {//array WILL out of index, cannot find correspond material
					StackTraceElement[] sel = {new StackTraceElement("Material", "GetMeshMatrialIndex", null, 303)};
					Throwable th = new Throwable();
					th.setStackTrace(sel);
					throw new GLModelLoadingException("Material Cannot Be Found", th, sel, "MaterialNotFoundException");
					//break;
				}
			}while(!currentCheckedMeshMtl.equals(CorrespondedMtl[mtl].getMtlName()));
			Result[mesh] = mtl;
		}
		return Result;
	}
	
}
