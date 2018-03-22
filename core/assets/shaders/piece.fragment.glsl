varying vec2 v_diffuseUV;

uniform sampler2D u_diffuseTexture;
uniform sampler2D u_ambientTexture;

uniform vec4 u_diffuseColor;

vec4 diffuse = texture2D(u_diffuseTexture, v_diffuseUV);

uniform vec3 u_lightPosition;

varying vec4 v_position; 
varying vec4 v_positionLightTrans;

varying vec3 v_normal;

float DecodeFloatRGBA( vec4 rgba ) {
  return dot( rgba, vec4(1.0, 1/255.0, 1/65025.0, 1/16581375.0) );
}

void main() {

	vec4 finalColor = vec4(diffuse.rgb,1);
	
	float shadow = 0.0;
	float texelSize = 1.0 / 500;//1024.0;

	vec3 vpos = vec3(v_position.xyz);	
	vec3 lpos = vec3(u_lightPosition);		
	vec3 lightDir = vpos - lpos;	
	float currentDepth = length(lightDir)/300.0;	
	
//	float currentDepth = length(vec3(v_position.xyz - u_lightPosition))/300.0;	
	float bias = 0.002;
	
	float shade = (1.0 - currentDepth);
	
	vec3 projCoords = (v_positionLightTrans.xyz / v_positionLightTrans.w)*0.5+0.5;

	vec3 N = normalize(v_normal);
	vec3 L = normalize(lightDir);
	float dotl = dot(N,L);
	float bias2 = (texelSize * dotl) + (texelSize * 4 * currentDepth);
	
	for(int x = -1; x <= 1; ++x)
	{
	    for(int y = -1; y <= 1; ++y)
	    {	
	        vec4 vdpth = texture2D(u_ambientTexture, projCoords.xy + vec2(x, y) * texelSize); 	        
	        float pcfDepth = DecodeFloatRGBA(vdpth);	
	        shadow += currentDepth - bias2 > pcfDepth ? 0.8 : 0.0;        
	    }    
	}	
	shadow /= 9.0;

	finalColor.rgb *= (1.0 - shadow) * clamp(pow(shade+0.5, 10), 0,1) * u_diffuseColor;

	gl_FragColor = finalColor;

}
