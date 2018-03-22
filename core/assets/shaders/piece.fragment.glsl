varying vec2 v_diffuseUV;

uniform sampler2D u_diffuseTexture;
uniform sampler2D u_ambientTexture;

uniform vec4 u_diffuseColor;

vec4 diffuse = texture2D(u_diffuseTexture, v_diffuseUV);

uniform vec3 u_lightPosition;

varying vec4 v_position; 
varying vec4 v_positionLightTrans;

float DecodeFloatRGBA( vec4 rgba ) {
  return dot( rgba, vec4(1.0, 1/255.0, 1/65025.0, 1/16581375.0) );
}

void main() {

	vec4 finalColor = vec4(diffuse.rgb,1);
	
	float shadow = 0.0;
	float texelSize = 1.0 / 500;//1024.0;
	
	float currentDepth = length(vec3(v_position.xyz - u_lightPosition))/300.0;	
	float bias = 0.002;
	
	float shade = (1.0 - currentDepth);
	
	vec3 projCoords = (v_positionLightTrans.xyz / v_positionLightTrans.w)*0.5+0.5;
	
	float bias2 = max(0.015 * currentDepth, 0.001);  
	
	for(int x = -1; x <= 1; ++x)
	{
	    for(int y = -1; y <= 1; ++y)
	    {	
	        vec4 vdpth = texture2D(u_ambientTexture, projCoords.xy + vec2(x, y) * texelSize); 	        
	        float pcfDepth = DecodeFloatRGBA(vdpth);	
	        shadow += currentDepth - bias > pcfDepth ? 0.8 : 0.0;        
	    }    
	}	
	shadow /= 9.0;

	finalColor.rgb *= (1.0 - shadow) * clamp(pow(shade+0.8, 10), 0,1) * u_diffuseColor;

	gl_FragColor = finalColor;
    	
}
