//define our sampler2D object, i.e. texture
uniform sampler2D tex0;

void main() {
	//get the unaltered color...
	vec4 color = texture2D(tex0, gl_TexCoord[0].st);
	//invert the color, leaving alpha intact
	gl_FragColor = vec4(1.0 - color.rgb, color.a);
} 