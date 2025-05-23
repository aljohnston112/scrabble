#include "LetterNode.h"

#include <array>

constexpr LetterNode LetterNode::SPACE{' ', 0};
constexpr LetterNode LetterNode::E{'E', 1};
constexpr LetterNode LetterNode::A{'A', 1};
constexpr LetterNode LetterNode::I{'I', 1};
constexpr LetterNode LetterNode::O{'O', 1};
constexpr LetterNode LetterNode::N{'N', 1};
constexpr LetterNode LetterNode::R{'R', 1};
constexpr LetterNode LetterNode::T{'T', 1};
constexpr LetterNode LetterNode::L{'L', 1};
constexpr LetterNode LetterNode::S{'S', 1};
constexpr LetterNode LetterNode::U{'U', 1};

constexpr LetterNode LetterNode::D{'D', 2};
constexpr LetterNode LetterNode::G{'G', 2};

constexpr LetterNode LetterNode::B{'B', 3};
constexpr LetterNode LetterNode::C{'C', 3};
constexpr LetterNode LetterNode::M{'M', 3};
constexpr LetterNode LetterNode::P{'P', 3};

constexpr LetterNode LetterNode::F{'F', 4};
constexpr LetterNode LetterNode::H{'H', 4};
constexpr LetterNode LetterNode::V{'V', 4};
constexpr LetterNode LetterNode::W{'W', 4};
constexpr LetterNode LetterNode::Y{'Y', 4};

constexpr LetterNode LetterNode::K{'K', 5};

constexpr LetterNode LetterNode::J{'J', 8};
constexpr LetterNode LetterNode::X{'X', 8};

constexpr LetterNode LetterNode::Q{'Q', 10};
constexpr LetterNode LetterNode::Z{'Z', 10};

constexpr std::array<const LetterNode*, 27> LetterNode::letterNodeMap = []() {
    std::array<const LetterNode*, 27> map = {};

    map[0] = &LetterNode::SPACE; // @ - 64 gives 0
    map['A' - 64] = &LetterNode::A;
    map['B' - 64] = &LetterNode::B;
    map['C' - 64] = &LetterNode::C;
    map['D' - 64] = &LetterNode::D;
    map['E' - 64] = &LetterNode::E;
    map['F' - 64] = &LetterNode::F;
    map['G' - 64] = &LetterNode::G;
    map['H' - 64] = &LetterNode::H;
    map['I' - 64] = &LetterNode::I;
    map['J' - 64] = &LetterNode::J;
    map['K' - 64] = &LetterNode::K;
    map['L' - 64] = &LetterNode::L;
    map['M' - 64] = &LetterNode::M;
    map['N' - 64] = &LetterNode::N;
    map['O' - 64] = &LetterNode::O;
    map['P' - 64] = &LetterNode::P;
    map['Q' - 64] = &LetterNode::Q;
    map['R' - 64] = &LetterNode::R;
    map['S' - 64] = &LetterNode::S;
    map['T' - 64] = &LetterNode::T;
    map['U' - 64] = &LetterNode::U;
    map['V' - 64] = &LetterNode::V;
    map['W' - 64] = &LetterNode::W;
    map['X' - 64] = &LetterNode::X;
    map['Y' - 64] = &LetterNode::Y;
    map['Z' - 64] = &LetterNode::Z;

    return map;
}();