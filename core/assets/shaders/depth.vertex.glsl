attribute vec3 a_position;
uniform mat4 u_worldTrans;
uniform mat4 u_projViewWorldTrans;
varying vec4 v_position;
void main() {
	v_position = u_worldTrans * vec4(a_position, 1.0);
	gl_Position = u_projViewWorldTrans * vec4(a_position, 1.0);
}
