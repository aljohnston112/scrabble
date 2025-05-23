#include "LetterNode.h"
#include "WordTree.h"

#include <utility>

void WordTree::addChild(const WordTree& child) {
    children.push_back(child);
}