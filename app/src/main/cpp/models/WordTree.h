#ifndef SCRABBLE_WORDTREE_H
#define SCRABBLE_WORDTREE_H

#include "LetterNode.h"

#include <string>
#include <vector>

class WordTree {
private:
    const LetterNode* letterNode;
    std::vector<WordTree> children{};

    void addChild(const WordTree& child);

public:
    constexpr explicit WordTree(const LetterNode* letterNode) :
            letterNode(letterNode) {}

    constexpr void addWord(std::string_view word){
        WordTree* currentNode = this;
        for(const char w : word){
            const LetterNode* temporaryLetterNode = LetterNode::letterNodeMap[w];

            WordTree* existingNode = nullptr;
            for(auto &childLetterNode : children){
                if(childLetterNode.letterNode->character == w){
                    existingNode = &childLetterNode;
                    break;
                }
            }
            if(existingNode != nullptr){
                currentNode = existingNode;
            } else{
                auto newLetterNode = LetterNode::letterNodeMap[temporaryLetterNode->character];
                currentNode->addChild(WordTree(newLetterNode));
                currentNode = &children.back();
            }
        }
    }

    static constexpr WordTree createRoot(){
        return WordTree(new LetterNode('.', 0));
    }

};


#endif //SCRABBLE_WORDTREE_H
