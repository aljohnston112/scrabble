#ifndef SCRABBLE_TILE_H
#define SCRABBLE_TILE_H


class Tile {

public:
    const char character;
    const int points;

    Tile(
            const char character,
            const int points
    ) : character(character),
        points(points) {}

};


#endif //SCRABBLE_TILE_H
