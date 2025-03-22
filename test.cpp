// 這是用gpt測試看可不可以用函數指標。

#include <iostream>
#include <map>
#include <vector>
#include <sstream>
#include <functional>
enum SexpType { NUMBER, ERROR, FUNCTION };
enum tokenType { INT, FLOAT };
struct Sexp {
    SexpType type;
    tokenType mtype;
    std::string value;
    std::function<Sexp(const std::vector<Sexp>&)> fun;
    Sexp(){}
    Sexp(SexpType t, tokenType mt, const std::string &v)
        : type(t), mtype(mt), value(v) {}
    Sexp(std::function<Sexp(const std::vector<Sexp>&)> f)
        : type(FUNCTION), fun(std::move(f)) {}
};

Sexp add(const std::vector<Sexp> &selist) {
    bool allInt = true;
    int sumInt = 0;
    double sumFloat = 0;
    for (auto &s : selist) {
        if (s.mtype == INT) {
            sumInt += std::stoi(s.value);
            sumFloat += std::stoi(s.value);
        } else {
            allInt = false;
            sumFloat += std::stod(s.value);
        }
    }
    std::ostringstream ss;
    if (allInt) {
        ss << sumInt;
        return Sexp(NUMBER, INT, ss.str());
    } else {
        ss << sumFloat;
        return Sexp(NUMBER, FLOAT, ss.str());
    }
}

int main() {
    std::map<std::string, Sexp> env;
    // Register “+”
    env["+"] = Sexp(add);
    // Test call:
    std::vector<Sexp> args = { Sexp(NUMBER, INT, "3"), Sexp(NUMBER, INT, "4") };
    Sexp result = env["+"].fun(args);
    std::cout << "+(3,4) = " << result.value << std::endl;  // prints 7
    return 0;
}