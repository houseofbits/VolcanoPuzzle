
varying vec4 v_position;
uniform vec4 u_cameraPosition;

void main() {
	vec3 lv = u_cameraPosition.xyz - v_position.xyz;
	float l = length(lv) / 200.0;
	gl_FragColor = vec4(vec3(l), 1.0);
}
