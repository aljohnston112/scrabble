#ifndef SCRABBLE_LETTERNODE_H
#define SCRABBLE_LETTERNODE_H

#include <array>

class LetterNode {

private:
    static const LetterNode SPACE;

    static const LetterNode E;
    static const LetterNode A;
    static const LetterNode I;
    static const LetterNode O;
    static const LetterNode N;
    static const LetterNode R;
    static const LetterNode T;
    static const LetterNode L;
    static const LetterNode S;
    static const LetterNode U;

    static const LetterNode D;
    static const LetterNode G;

    static const LetterNode B;
    static const LetterNode C;
    static const LetterNode M;
    static const LetterNode P;

    static const LetterNode F;
    static const LetterNode H;
    static const LetterNode V;
    static const LetterNode W;
    static const LetterNode Y;

    static const LetterNode K;

    static const LetterNode J;
    static const LetterNode X;

    static const LetterNode Q;
    static const LetterNode Z;

public:
    const char character;
    const int score;

    constexpr LetterNode(
            const char character,
            const int score
    ) : character(character),
        score(score) {}

    static const std::array<const LetterNode*, 27> letterNodeMap;

};

#endif //SCRABBLE_LETTERNODE_H
