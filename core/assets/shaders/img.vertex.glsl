attribute vec3 a_position;
uniform mat4 u_projViewTrans;
uniform mat4 u_worldTrans;

attribute vec2 a_texCoord0;
uniform vec4 u_diffuseUVTransform;
varying vec2 v_diffuseUV;

varying vec4 v_position; 
varying vec4 v_positionLightTrans;

uniform mat4 u_lightTrans;

void main() {
	
	v_position = u_worldTrans * vec4(a_position, 1.0);
	
	v_positionLightTrans = u_lightTrans * v_position;

	v_diffuseUV = u_diffuseUVTransform.xy + a_texCoord0 * u_diffuseUVTransform.zw;
	vec4 pos = u_worldTrans * vec4(a_position, 1.0);
	gl_Position = u_projViewTrans * pos;
}
