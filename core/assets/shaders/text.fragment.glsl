varying vec2 v_diffuseUV;

uniform sampler2D u_diffuseTexture;				//Puzzle image
uniform sampler2D u_ambientTexture;				//Shadow depth
uniform sampler2D u_reflectionTexture;				//Color projection

uniform vec3 u_lightPosition;

varying vec4 v_position; 
varying vec4 v_positionLightTrans;
varying vec4 v_projectedPos;

float decodeFloatRGBA( vec4 rgba ) {
  return dot( rgba, vec4(1.0, 1/255.0, 1/65025.0, 1/16581375.0) );
}
float shadowComponent(sampler2D depthMap, vec3 lightPos, vec3 vPos, vec4 vLightSpace, vec3 normal){
	float shadow = 0.0;
	float texelSize = 1.0 / 500;	//1024.0;
	vec3 lightDir = vPos - lightPos;
	float currentDepth = length(lightDir)/300.0;		
	vec3 projCoords = (vLightSpace.xyz / vLightSpace.w)*0.5+0.5;
	float dotl = dot(normal, normalize(lightDir));
	float bias = (texelSize * dotl) + (texelSize * 4 * currentDepth);  
	for(int x = -1; x <= 1; ++x){
	    for(int y = -1; y <= 1; ++y){	
	        vec4 vdpth = texture2D(depthMap, projCoords.xy + vec2(x, y) * texelSize);
	        float pcfDepth = decodeFloatRGBA(vdpth);	        
	        shadow += currentDepth - bias > pcfDepth ? 0.7 : 0.0;        
	    }    
	}	
	shadow /= 9.0;
	return shadow;
}

void main() {
	vec2 ndc = (v_projectedPos.xy / v_projectedPos.w)/2.0 + 0.5;
	vec2 projectedUV = vec2(ndc.x, 1.0-ndc.y);	
	
	vec4 colorProj = texture2D(u_reflectionTexture, (projectedUV*0.8)+vec2(0.1,0.1));

	float gray = (colorProj.r + colorProj.g + colorProj.b) / 3.0;
	
	colorProj = vec4(vec3(gray + 0.7), 1);
	
	vec4 diffuse = texture2D(u_diffuseTexture, v_diffuseUV) * colorProj;
	vec4 finalColor = diffuse;
	
	float shadow = 1.0 - shadowComponent(u_ambientTexture, u_lightPosition, v_position.xyz, v_positionLightTrans, vec3(0,0,1));

	finalColor.rgb *= shadow;

	gl_FragColor = vec4(finalColor.rgb, diffuse.a);
}
