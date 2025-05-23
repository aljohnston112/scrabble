#ifndef SCRABBLE_WORDLOCATION_H
#define SCRABBLE_WORDLOCATION_H

enum Direction {

    DOWN,
    RIGHT,
    BOTH

};

class WordLocation {

    const Direction direction;
    const bool isValid;

    WordLocation(
            const Direction direction,
            const bool isValid
    ) : direction(direction),
        isValid(isValid) {}

};


#endif //SCRABBLE_WORDLOCATION_H
