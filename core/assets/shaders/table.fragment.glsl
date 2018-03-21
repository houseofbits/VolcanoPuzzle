varying vec2 v_diffuseUV;

uniform sampler2D u_diffuseTexture;				//Puzzle image
uniform sampler2D u_reflectionTexture;			//Base naterial
uniform sampler2D u_ambientTexture;				//Shadow depth

uniform vec3 u_lightPosition;

varying vec4 v_position; 
varying vec4 v_positionLightTrans;
varying vec4 v_projectedPos;

vec4 boxBlur (sampler2D source, vec2 uv) {

	vec2 texOffset = vec2(0.004, 0.004);
	
	vec2 tc0 = uv.st + vec2(-texOffset.s, -texOffset.t);
	vec2 tc1 = uv.st + vec2(         0.0, -texOffset.t);
	vec2 tc2 = uv.st + vec2(+texOffset.s, -texOffset.t);
	vec2 tc3 = uv.st + vec2(-texOffset.s,          0.0);
	vec2 tc4 = uv.st + vec2(         0.0,          0.0);
	vec2 tc5 = uv.st + vec2(+texOffset.s,          0.0);
	vec2 tc6 = uv.st + vec2(-texOffset.s, +texOffset.t);
	vec2 tc7 = uv.st + vec2(         0.0, +texOffset.t);
	vec2 tc8 = uv.st + vec2(+texOffset.s, +texOffset.t);
	
	vec4 col0 = texture2D(source, tc0);
	vec4 col1 = texture2D(source, tc1);
	vec4 col2 = texture2D(source, tc2);
	vec4 col3 = texture2D(source, tc3);
	vec4 col4 = texture2D(source, tc4);
	vec4 col5 = texture2D(source, tc5);
	vec4 col6 = texture2D(source, tc6);
	vec4 col7 = texture2D(source, tc7);
	vec4 col8 = texture2D(source, tc8);

	vec4 sum = (1.0 * col0 + 2.0 * col1 + 1.0 * col2 + 
	            2.0 * col3 + 4.0 * col4 + 2.0 * col5 +
	            1.0 * col6 + 2.0 * col7 + 1.0 * col8) / 16.0; 
	            
	return vec4(sum.rgb, 1.0);	            
}

void main() {
	
	//Projection UVs for reflection and refraction
	vec2 ndc = (v_projectedPos.xy / v_projectedPos.w)/2.0 + 0.5;
//	vec2 refractionUV = vec2(ndc.x, ndc.y);
	vec2 projectedUV = vec2(ndc.x, 1.0-ndc.y);	
	
	
	vec4 blend  = boxBlur(u_diffuseTexture, (projectedUV*0.8)+vec2(0.1,0.1));
	vec4 source = texture2D(u_reflectionTexture, projectedUV);
	vec4 blendResult = vec4(1);
	float l = length(blend);
	if(l <= 0.5){
		blendResult = (1 - (1-source) * (1-(blend-0.5)));
	}else{
		blendResult = (source * (blend+0.5));
	}
	
	vec4 finalColor = blendResult;
	
	float shadow = 0.0;
	float texelSize = 1.0 / 1024.0;
	
	float currentDepth = length(vec3(v_position.xyz - u_lightPosition))/200.0;	
	float bias = 0.005;
	
	float shade = (1.0 - currentDepth);
	
	vec3 projCoords = (v_positionLightTrans.xyz / v_positionLightTrans.w)*0.5+0.5;
	
	for(int x = -1; x <= 1; ++x)
	{
	    for(int y = -1; y <= 1; ++y)
	    {	
	        float pcfDepth = texture2D(u_ambientTexture, projCoords.xy + vec2(x, y) * texelSize).r; 
	        shadow += currentDepth - bias > pcfDepth ? 0.6 : 0.0;        
	    }    
	}	
	shadow /= 9.0;

	

	finalColor.rgb *= (1.0 - shadow);// * pow(shade+0.6, 6);	//pow(shade, 0.5);

	gl_FragColor = finalColor;
	
	//gl_FragColor = vec4(vec3(pow(shade+0.5, 4)), 1);
}
