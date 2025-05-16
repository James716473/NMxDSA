package src;

class Tuple<T, U> {
    T first;
    U second;

    // Constructor that accepts any two types
    Tuple(T first, U second) {
        this.first = first;
        this.second = second;
    }

    @Override
    public String toString() {
        return "(" + first + ", " + second + ")";
    }
}