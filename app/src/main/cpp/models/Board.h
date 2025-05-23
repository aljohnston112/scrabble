#ifndef SCRABBLE_BOARD_H
#define SCRABBLE_BOARD_H

#include <array>
#include <memory>

#include "Tile.h"
#include "WordLocation.h"

class Board {

public:
    const static unsigned int BOARD_WIDTH_AND_HEIGHT = 15;

    const std::array<
            std::array<
                    std::shared_ptr<const Tile>,
                    BOARD_WIDTH_AND_HEIGHT
            >, BOARD_WIDTH_AND_HEIGHT
    > board;

    const std::array<
            std::array<
                    std::shared_ptr<const WordLocation>,
                    BOARD_WIDTH_AND_HEIGHT
            >,
            BOARD_WIDTH_AND_HEIGHT
    > wordLocations;

    Board(
            const std::array<
                    std::array<
                            std::shared_ptr<const Tile>,
                            BOARD_WIDTH_AND_HEIGHT
                    >, BOARD_WIDTH_AND_HEIGHT
            > &board,
            const std::array<
                    std::array<
                            std::shared_ptr<const WordLocation>,
                            BOARD_WIDTH_AND_HEIGHT
                    >,
                    BOARD_WIDTH_AND_HEIGHT
            > &wordLocations
    ) : board(board),
        wordLocations(wordLocations) {}

    [[nodiscard]] Board afterAddingTile(
            std::shared_ptr<const Tile> tile,
            int row,
            int col
    ) const;

    [[nodiscard]] const std::array<
            std::array<
                    std::shared_ptr<const WordLocation>,
                    BOARD_WIDTH_AND_HEIGHT
            >,
            BOARD_WIDTH_AND_HEIGHT
    > getWordsAfterLetterAdded(
            const std::array<
                    std::array<
                            std::shared_ptr<const Tile>,
                            BOARD_WIDTH_AND_HEIGHT
                    >, BOARD_WIDTH_AND_HEIGHT
            > &board,
            int row,
            int col
    );

};


#endif //SCRABBLE_BOARD_H
