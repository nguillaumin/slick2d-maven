#version 120

//let's declare some variables
float alpha = 1.0;

//this is our RGB color, i.e. pure red
vec3 color = vec3(1.0, 0.0, 0.0);

void main() {
	//returns our colour, as defined above
	gl_FragColor = vec4(color.rgb, alpha);
} 