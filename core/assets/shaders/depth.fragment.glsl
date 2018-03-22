
varying vec4 v_position;
uniform vec4 u_cameraPosition;

vec4 EncodeFloatRGBA( float v ) {
  vec4 enc = vec4(1.0, 255.0, 65025.0, 16581375.0) * v;
  enc = fract(enc);
  enc -= enc.yzww * vec4(1.0/255.0,1.0/255.0,1.0/255.0,0.0);
  return enc;
}

void main() {
	vec3 lv = u_cameraPosition.xyz - v_position.xyz;
	float l = length(lv) / 300.0;
	//gl_FragColor = vec4(vec3(l), 1.0);
	
	gl_FragColor = EncodeFloatRGBA(l);
}
