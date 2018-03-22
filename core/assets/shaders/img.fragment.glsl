varying vec2 v_diffuseUV;

uniform sampler2D u_diffuseTexture;
uniform sampler2D u_ambientTexture;

//vec4 diffuse = texture2D(u_diffuseTexture, v_diffuseUV);

uniform vec3 u_lightPosition;

varying vec4 v_position; 
varying vec4 v_positionLightTrans;

varying vec3 v_normal;

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

float DecodeFloatRGBA( vec4 rgba ) {
  return dot( rgba, vec4(1.0, 1/255.0, 1/65025.0, 1/16581375.0) );
}

void main() {
	
	vec4 img  = boxBlur(u_diffuseTexture, v_diffuseUV, 0.008);
	img  += boxBlur(u_diffuseTexture, v_diffuseUV, 0.005);
	img = img * 0.5;
	
	
	float gray = (img.r + img.g + img.b) / 3.0;
	
	img = vec4(vec3(gray * 2), 1);
	
	vec4 finalColor = mix(img, vec4(0.3,0.3,0.3,1), 0.7);
	
	float shadow = 0.0;
	float texelSize = 1.0 / 500;//1024.0;
	
	vec3 vpos = vec3(v_position.xyz);	
	vec3 lpos = vec3(u_lightPosition);		
	vec3 lightDir = vpos - lpos;	
	float currentDepth = length(lightDir)/300.0;		
	
//	float currentDepth = length(vec3(v_position.xyz - u_lightPosition))/300.0;	
//	float bias = 0.002;
	
	vec3 projCoords = (v_positionLightTrans.xyz / v_positionLightTrans.w)*0.5+0.5;
	
	//float bias2 = max(0.01 * currentDepth, 0.001);  
	
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
	        shadow += currentDepth - bias2 > pcfDepth ? 0.6 : 0.0;       
	    }    
	}	
	shadow /= 9.0;

	finalColor.rgb *= (1.0 - shadow);

	gl_FragColor = finalColor;
}
