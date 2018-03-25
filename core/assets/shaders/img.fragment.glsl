varying vec2 v_diffuseUV;

uniform sampler2D u_diffuseTexture;
uniform sampler2D u_ambientTexture;

//vec4 diffuse = texture2D(u_diffuseTexture, v_diffuseUV);

uniform vec3 u_lightPosition;

varying vec4 v_position; 
varying vec4 v_positionLightTrans;
//varying vec3 v_normal;

vec4 boxBlur (sampler2D source, vec2 uv, float offset) {

	vec2 texOffset = vec2(offset, offset);
	
	float edgeOffset = 0.1;
	vec2 edgeDistMult = vec2(1);
	if(uv.s <= edgeOffset)texOffset.s *= (uv.s / edgeOffset);	
	if(uv.t <= edgeOffset)texOffset.t *= (uv.t / edgeOffset);	
	if(uv.s >= (1.0-edgeOffset))texOffset.s *= ((1.0-uv.s) / edgeOffset);	
	if(uv.t >= (1.0-edgeOffset))texOffset.t *= ((1.0-uv.t) / edgeOffset);	
	
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

float decodeFloatRGBA( vec4 rgba ) {
  return dot( rgba, vec4(1.0, 1/255.0, 1/65025.0, 1/16581375.0) );
}
float shadowComponent(sampler2D depthMap, vec3 lightPos, vec3 vPos, vec4 vLightSpace, vec3 normal){
	float shadow = 0.0;
	float texelSize = 1.0 / 500;	//1024.0;
	vec3 lightDir = vPos - lightPos;
	float currentDepth = length(lightDir)/300.0;			
	vec3 projCoords = (vLightSpace.xyz / vLightSpace.w)*0.5+0.5;
	if(projCoords.y < 0)return 0;	
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
	
	vec4 img  = boxBlur(u_diffuseTexture, v_diffuseUV, 0.008);
	img  += boxBlur(u_diffuseTexture, v_diffuseUV, 0.005);
	img = img * 0.5;
	
	float gray = (img.r + img.g + img.b) / 3.0;
	
	img = vec4(vec3(gray * 2), 1);
	
	vec4 finalColor = mix(img, vec4(0.5,0.5,0.5,1), 0.7);
	
	float shadow = 1.0 - shadowComponent(u_ambientTexture, u_lightPosition, v_position.xyz, v_positionLightTrans, vec3(0,0,1));

	finalColor.rgb *= shadow;

	gl_FragColor = finalColor;
}
