#include <array>
#include <exception>
#include <format>

#include "Board.h"

Board Board::afterAddingTile(
        std::shared_ptr<const Tile> tile,
        const int row,
        const int col
) const {
    if (board[row][col]) {
        throw std::invalid_argument(
                std::format(
                        "Error: There is already a tile at the given location (row: {}, col: {}).",
                        row,
                        col
                )
        );
    }

    std::array<std::array<std::shared_ptr<const Tile>, BOARD_WIDTH_AND_HEIGHT>, BOARD_WIDTH_AND_HEIGHT> tempBoard{};
    for (size_t i = 0; i < BOARD_WIDTH_AND_HEIGHT; ++i) {
        std::array<std::shared_ptr<const Tile>, BOARD_WIDTH_AND_HEIGHT> tempRow;
        for (size_t j = 0; j < BOARD_WIDTH_AND_HEIGHT; ++j) {
            if (row == i && col == j) {
                tempRow[j] = tile;
            } else {
                tempRow[j] = board[i][j];
            }
        }
        tempBoard[i] = std::move(tempRow);
    }

    return {
            tempBoard,
            wordLocations
    };
}

const std::array<
        std::array<
                std::shared_ptr<const WordLocation>,
                Board::BOARD_WIDTH_AND_HEIGHT
        >,
        Board::BOARD_WIDTH_AND_HEIGHT
> Board::getWordsAfterLetterAdded(
        const std::array<
                std::array<
                        std::shared_ptr<const Tile>,
                        BOARD_WIDTH_AND_HEIGHT
                >,
                BOARD_WIDTH_AND_HEIGHT
        > &board,
        int row,
        int col
) {


    return std::array<std::array<std::shared_ptr<const WordLocation>, BOARD_WIDTH_AND_HEIGHT>, BOARD_WIDTH_AND_HEIGHT>();
};
