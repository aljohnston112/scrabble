cmake_minimum_required(VERSION 4.0.2)

set(CMAKE_CXX_STANDARD 23)
add_compile_options(-fconstexpr-steps=10485760)

add_library(
        native-lib
        SHARED
        native-lib.cpp
        models/Board.cpp
        models/Dictionary.cpp
        models/DictionaryUtility.cpp
        models/LetterNode.cpp
        models/Tile.cpp
        models/WordTree.cpp
        models/WordLocation.cpp
)

target_link_libraries(
        native-lib
        log
)
