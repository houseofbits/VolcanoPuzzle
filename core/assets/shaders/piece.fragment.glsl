varying vec2 v_diffuseUV;
uniform sampler2D u_diffuseTexture;
uniform sampler2D u_ambientTexture;
uniform vec4 u_diffuseColor;
vec4 diffuse = texture2D(u_diffuseTexture, v_diffuseUV);
uniform vec3 u_lightPosition;
varying vec4 v_position; 
varying vec4 v_positionLightTrans;
//varying vec3 v_normal;

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
	        shadow += currentDepth - bias > pcfDepth ? 0.6 : 0.0;        
	    }    
	}	
	shadow /= 9.0;
	return shadow;
}

void main() {

	vec4 finalColor = vec4(diffuse.rgb,1);

	vec3 vpos = vec3(v_position.xyz);	
	vec3 lpos = vec3(u_lightPosition);		
	vec3 lightDir = vpos - lpos;	
	float currentDepth = length(lightDir)/300.0;	
	
	float shade = (1.0 - currentDepth);

	float shadow = 1.0 - shadowComponent(u_ambientTexture, u_lightPosition, v_position.xyz, v_positionLightTrans, vec3(0,0,1));

	finalColor.rgb *= shadow * clamp(pow(shade+0.5, 10), 0,1) * u_diffuseColor;

	gl_FragColor = finalColor;

}
