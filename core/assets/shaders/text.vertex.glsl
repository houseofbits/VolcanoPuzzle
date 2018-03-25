attribute vec3 a_position;
uniform mat4 u_projViewTrans;
uniform mat4 u_worldTrans;

attribute vec2 a_texCoord0;
uniform vec4 u_diffuseUVTransform;
varying vec2 v_diffuseUV;

varying vec4 v_position; 
varying vec4 v_positionLightTrans;
varying vec4 v_projectedPos;
uniform mat4 u_lightTrans;

//attribute vec3 a_normal;
//uniform mat3 u_normalMatrix;
//varying vec3 v_normal;

void main() {
	
	v_position = u_worldTrans * vec4(a_position, 1.0);

	v_projectedPos = u_projViewTrans * v_position;
	
	v_positionLightTrans = u_lightTrans * vec4(v_position.xyz, 1.0);

//	v_normal = vec3(0,0,1);	//normalize(u_normalMatrix * a_normal);

	v_diffuseUV = u_diffuseUVTransform.xy + a_texCoord0 * u_diffuseUVTransform.zw;
	vec4 pos = u_worldTrans * vec4(a_position, 1.0);
	gl_Position = u_projViewTrans * pos;
}
