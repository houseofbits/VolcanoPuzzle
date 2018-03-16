varying vec2 v_diffuseUV;

uniform sampler2D u_diffuseTexture;
uniform sampler2D u_ambientTexture;

uniform vec4 u_diffuseColor;

vec4 diffuse = texture2D(u_diffuseTexture, v_diffuseUV);

uniform vec3 u_lightPosition;

varying vec4 v_position; 
varying vec4 v_positionLightTrans;

void main() {
	vec4 finalColor = vec4(diffuse.rgb,1);
	
	float shadow = 0.0;
	vec2 texelSize = 1.0 / 1024.0;
	
	float currentDepth = length(vec3(v_position.xyz - u_lightPosition))/200.0;	
	float bias = 0.005;
	
	float shade = (1.0 - currentDepth);
	
	vec3 projCoords = (v_positionLightTrans.xyz / v_positionLightTrans.w)*0.5+0.5;
	
	for(int x = -1; x <= 1; ++x)
	{
	    for(int y = -1; y <= 1; ++y)
	    {	
	        float pcfDepth = texture2D(u_ambientTexture, projCoords.xy + vec2(x, y) * texelSize).r; 
	        shadow += currentDepth - bias > pcfDepth ? 0.8 : 0.0;        
	    }    
	}	
	shadow /= 9.0;

	finalColor.rgb *= (1.0 - shadow) * clamp(pow(shade+0.8, 10), 0,1) * u_diffuseColor;

	gl_FragColor = finalColor;
    	
}
