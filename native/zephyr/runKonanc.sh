rm -f program.o

mkdir -p $DIR/build/kotlin

konanc $DIR/../src/main/kotlin/com/jwoolston/kt2048/native/main.kt \
        $DIR/../src/main/kotlin/com/jwoolston/kt2048/native/STM32F4Board.kt \
        $DIR/../../core/src/main/kotlin/com/jwoolston/kt2048/core/GameBoard.kt \
        -target zephyr_$BOARD \
        -r $DIR/c_interop/platforms/build \
        -l $BOARD \
        -opt -g -o $DIR/build/kotlin/program || exit 1