attribute vec3 a_position;
attribute vec3 a_normal;
uniform mat4 u_projViewTrans;
uniform mat4 u_worldTrans;
uniform float u_outlineWidth; // Grosor de la línea

void main() {
    vec4 pos = u_worldTrans * vec4(a_position, 1.0);
    vec3 norm = normalize(mat3(u_worldTrans) * a_normal);
    // Expandir el vértice en la dirección de su normal
    pos.xyz += norm * u_outlineWidth;
    gl_Position = u_projViewTrans * pos;
}