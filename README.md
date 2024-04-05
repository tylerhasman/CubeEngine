# Cube Engine

Voxel Engine written in Java with OpenGL. Partially rewritten in C++ [here](https://github.com/tylerhasman/Voxel-Game-CPP)

## Features

- Procedurally generated terrain using Voronoi diagrams and Perlin Noise
  - Voronoi diagrams are used to separate biomes
  - Perlin Noise is used to generate random noise for biome hills
- SSAO using Deferred Rendering
- Basic physics
- Basic particle engine
- Supports different materials for different objects

## Compiling + Running

Import the code into your IDE of choice that supports Gradle.

Run Start.java to start a very basic game where you can run around.

or

Run StartEditor.java to start a very basic editor where you can fly around.

## Contributing
Pull requests are welcome. For major changes, please open an issue first to discuss what you would like to change.

## Screenshots

![alt text](https://github.com/tylerhasman/CubeEngine/blob/master/screenshots/in-game.png)


Biomes are generated using Voronoi diagrams like the one below. Gray areas represent mountain biomes, green areas represent plains and blue areas represent lakes.
The biomes are blurred based on distance to edges of other biomes. 

The amount of influence a biome has on an area is controlled by the strength of its color in an area. 
This allows areas between connected biomes to be blended together.

![alt text](https://github.com/tylerhasman/CubeEngine/blob/master/screenshots/biomeTest.png)

## License
[MIT](https://choosealicense.com/licenses/mit/)
