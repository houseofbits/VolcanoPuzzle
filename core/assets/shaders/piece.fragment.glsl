varying vec2 v_diffuseUV;

uniform sampler2D u_diffuseTexture;
uniform sampler2D u_ambientTexture;

vec4 diffuse = texture2D(u_diffuseTexture, v_diffuseUV);
vec4 lightDepth = texture2D(u_ambientTexture, v_diffuseUV);

void main() {
	gl_FragColor = diffuse;	// * vec4(1.0, 1.0, 0.0, 1.0);
	//vec4(vec3(lightDepth.a), 1);		//
}
