#ifndef SCRABBLE_DICTIONARY_H
#define SCRABBLE_DICTIONARY_H

#include <map>
#include <vector>

#include "WordTree.h"

class Dictionary {

    std::vector<WordTree> wordsTrees{};
    std::map<std::string_view, std::string_view> definitions{};

public:

};


#endif //SCRABBLE_DICTIONARY_H
